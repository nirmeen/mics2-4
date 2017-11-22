package com.example.dell.notebook;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler db;
    private List<WordTranslation> WordTranslations = new ArrayList();
    private ListView wordsListView;
    private ListView letterListView;
    ArrayAdapter<String> letterListAdapter;
    private WordsListArrayAdapter wordsListAdapter;
    private List<String> alphabet = Arrays.asList("*","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");
    private int selectedAlphabetPosition = 0;

    public static final String newWord = "new";
    public static final String translatedWord = "translation";
    public static final String wordID = "id";
    private static final java.lang.String SEPARATOR = ",";
    private static final int IMPORT_FILE = 0;
    private static final int EXPORT_FILE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = db.getInstance(this);
        WordTranslations = db.getAllWordTranslation();
        letterListView = (ListView) findViewById(R.id.lettersList);
        letterListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,alphabet);
        letterListView.setAdapter(letterListAdapter);

        wordsListView = (ListView) findViewById(R.id.wordsList);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        wordsListView.setEmptyView(emptyText);

        wordsListAdapter = new WordsListArrayAdapter(this, R.layout.list_view_items,(ArrayList<WordTranslation>) WordTranslations);
        wordsListView.setAdapter(wordsListAdapter);



        letterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                selectedAlphabetPosition = position;
                String l = alphabet.get(Integer.parseInt(""+id));
                if(l.equals("*")){
                    wordsListAdapter.getFilter().filter("");
                }else{
                    wordsListAdapter.getFilter().filter(l);
                }
                wordsListAdapter.notifyDataSetChanged();
            }

        });


        wordsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
                Intent intent = new Intent(MainActivity.this, EditWordListItemActivity.class);
                int actualId;
                for(int i = 0; i < WordTranslations.size();i++){
                    if(WordTranslations.get(i).getNewWord().equals(wordsListAdapter.getItem(position).getNewWord())
                            && WordTranslations.get(i).getWordMeaning().equals(wordsListAdapter.getItem(position).getWordMeaning())
                            ){
                        actualId = i;
                        intent.putExtra(wordID, ""+actualId);
                        intent.putExtra(newWord, WordTranslations.get(actualId).getNewWord());
                        intent.putExtra(translatedWord, WordTranslations.get(actualId).getWordMeaning());
                        startActivity(intent);
                        return;
                    }
                }
            }

        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_word:
                addWord();
                return true;
            case R.id.import_words:
                Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
                fileintent.setType("text/csv");
                try {
                    startActivityForResult(fileintent, IMPORT_FILE);
                } catch (ActivityNotFoundException e) {
                    Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                }
                return true;
            case R.id.export_words:
                Intent fileExportIntent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
                fileExportIntent.addCategory(Intent.CATEGORY_OPENABLE);
                fileExportIntent.setType("text/csv");
                fileExportIntent.putExtra(Intent.EXTRA_TITLE, "dictionnary.csv");
                try {
                    startActivityForResult(fileExportIntent, EXPORT_FILE);
                } catch (ActivityNotFoundException e) {
                    Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
                }
                return true;
            case R.id.exam:
                exam();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addWord() {
        Intent intent = new Intent(MainActivity.this, AddWordActivity.class);
        startActivity(intent);
    }
    private void exam() {
        Intent intent = new Intent(MainActivity.this, ExamActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IMPORT_FILE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                importDB(fileUri);
            }
        } else if (requestCode == EXPORT_FILE) {
            if (resultCode == RESULT_OK) {
                Uri fileUri = data.getData();
                exportDB(fileUri);
            }
        }
    }

    public boolean importDB(Uri uri) {
        StringBuilder text = new StringBuilder();
        BufferedReader br=null;
        try {

            InputStream is= this.getContentResolver().openInputStream(uri);

            br = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields=line.split(SEPARATOR);
                WordTranslation newWord = new WordTranslation(fields[0],fields[1]);
                db.addWordTranslation(newWord);
                text.append(line);
                text.append('\n');
            }
            br.close();
            Toast.makeText(getApplicationContext(), "Successfully imported", Toast.LENGTH_SHORT).show();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();

        }
        finally{
            try {
                if (br != null)
                    br.close();
            } catch (Exception E) {

            }
        }
        return false;
    }
    public boolean exportDB(Uri file) {
        OutputStream out = null;
        //String filename= file.getPath()+".txt";

        BufferedOutputStream bos = null;

        try {
            out = this.getContentResolver().openOutputStream(file);

            bos = new BufferedOutputStream(out);
            Cursor result = db.getAllWordTranslationCursor();

            byte[] buf = new byte[1024];
            while(result.moveToNext()){
                bos.write(result.getString(1).getBytes());
                bos.write(SEPARATOR.getBytes());
                bos.write(result.getString(2).getBytes());
                bos.write(System.getProperty("line.separator").getBytes());
            }
            Toast.makeText(getApplicationContext(), "Successfully exported", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } finally {
            try {
                if (bos != null) bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        WordTranslations.clear();
        WordTranslations.addAll(db.getAllWordTranslation());
        wordsListAdapter = new WordsListArrayAdapter(this, R.layout.list_view_items,(ArrayList<WordTranslation>)WordTranslations);
        wordsListView.setAdapter(wordsListAdapter);
        letterListView.getAdapter().getView(selectedAlphabetPosition, null, null).performClick();
        letterListView.requestFocusFromTouch();
        letterListView.setSelection(selectedAlphabetPosition);

        if(selectedAlphabetPosition == 0){
            wordsListAdapter.getFilter().filter("");
        }else{
            wordsListAdapter.getFilter().filter(alphabet.get(selectedAlphabetPosition));
        }

    }

}
