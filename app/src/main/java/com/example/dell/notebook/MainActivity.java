package com.example.dell.notebook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler db;
    private List<WordTranslation> WordTranslations = new ArrayList();
    private ListView wordsListView;
    private ListView letterListView;

    private List<String> alphabet = Arrays.asList("*","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z");


    public static final String newWord = "new";
    public static final String translatedWord = "translation";
    public static final String wordID = "id";
    private WordsListArrayAdapter wordsListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db = new DatabaseHandler(this);
        WordTranslations = db.getAllWordTranslation();
        letterListView = (ListView) findViewById(R.id.lettersList);
        ArrayAdapter<String> letterListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,alphabet);
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
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

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
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        WordTranslations.clear();
        WordTranslations.addAll(db.getAllWordTranslation());
        wordsListAdapter = new WordsListArrayAdapter(this, R.layout.list_view_items,(ArrayList<WordTranslation>)WordTranslations);
        wordsListView.setAdapter(wordsListAdapter);

    }

}
