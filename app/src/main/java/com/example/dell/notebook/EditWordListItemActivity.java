package com.example.dell.notebook;

import android.content.DialogInterface;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditWordListItemActivity extends AppCompatActivity {
    DatabaseHandler db;
    EditText nwTextView;
    EditText transTextView;
    int wID;
    TextToSpeech toSpeech;
    private List<WordTranslation> WordTranslations = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word_list_item);
        db = db.getInstance(this);
        WordTranslations = db.getAllWordTranslation();
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String wordId = intent.getStringExtra(MainActivity.wordID);
        wID = Integer.parseInt(wordId);
        String newWord = intent.getStringExtra(MainActivity.newWord);
        String translatedWord = intent.getStringExtra(MainActivity.translatedWord);

        // Capture the layout's TextView and set the string as its text
        nwTextView = (EditText) findViewById(R.id.newWordText);
        nwTextView.setText(newWord);
        transTextView = (EditText) findViewById(R.id.TranslatedText);
        transTextView.setText(translatedWord);

        toSpeech=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    toSpeech.setLanguage(Locale.FRANCE);
                }
            }
        });

    }

    @SuppressWarnings("deprecation")
    public  void onButtonClickListen(View view){
        String w = nwTextView.getText().toString();
        toSpeech.speak(w, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void onButtonClickSave(View view) {
        if( !nwTextView.getText().toString().isEmpty() && !transTextView.getText().toString().isEmpty()){
            WordTranslations.get(wID).setNewWord(nwTextView.getText().toString());
            WordTranslations.get(wID).setWordMeaning(transTextView.getText().toString());
            db.updateWordTranslation(WordTranslations.get(wID));
            finish();
        }else{
            if( nwTextView.getText().toString().isEmpty()){
                nwTextView.setError( "New word is required!" );
            }
            if( transTextView.getText().toString().isEmpty()){
                transTextView.setError( "Word Translation is required!" );

            }
        }

    }
    public void onButtonClickDelete(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(
                EditWordListItemActivity.this);
        alert.setTitle("Alert!!");
        alert.setMessage("Are you sure to delete this word");
        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                //do your work here
                dialog.dismiss();
                db.deleteWordTranslation(WordTranslations.get(wID));
                finish();
            }
        });
        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();



    }
    public void onButtonClickCancel(View view) {
        finish();
    }

    public void onPause(){
        if(toSpeech !=null){
            toSpeech.stop();
            toSpeech.shutdown();
        }
        super.onPause();
    }
}
