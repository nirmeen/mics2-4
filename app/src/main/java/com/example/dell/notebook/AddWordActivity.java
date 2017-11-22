package com.example.dell.notebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddWordActivity extends AppCompatActivity {
    DatabaseHandler db;
    EditText nwTextView;
    EditText transTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_word);
        db = db.getInstance(this);
        nwTextView = (EditText) findViewById(R.id.newWordText);
        transTextView = (EditText) findViewById(R.id.TranslatedText);
    }
    public void onButtonClickAdd(View view) {
        if( !nwTextView.getText().toString().isEmpty() && !transTextView.getText().toString().isEmpty()){
            WordTranslation w = new WordTranslation(nwTextView.getText().toString(),transTextView.getText().toString());
            db.addWordTranslation(w);
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
    public void onButtonClickCancel(View view) {
        finish();
    }
}
