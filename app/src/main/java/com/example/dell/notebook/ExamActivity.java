package com.example.dell.notebook;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;


public class ExamActivity extends AppCompatActivity {
    DatabaseHandler db;
    private int wordsCount;
    private int maxQuestionsCount = 3;
    private int currentQuestions = 1;
    private int correctAnswer;
    private WordTranslation selectedWord;
    private int randomWordIndex;
    private int[] randomIndices = {-1,-1,-1};
    String wrongChoice1;
    String wrongChoice2;
    TextView nwText;
    RadioGroup radioGroup;
    RadioButton choice1;
    RadioButton choice2;
    RadioButton choice3;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam);
        db = db.getInstance(this);
        wordsCount = db.getWordTranslationCount();

        //if no word available in the database
        if(wordsCount == 0){
            Toast.makeText(getApplicationContext(), "Currently there is data avaiable", Toast.LENGTH_LONG).show();
            finish();
        }
        nwText = (TextView)findViewById(R.id.newWordTextView);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        choice1 = (RadioButton)findViewById(R.id.radioButton);
        choice2 = (RadioButton)findViewById(R.id.radioButton2);
        choice3 = (RadioButton)findViewById(R.id.radioButton3);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        setQuestion();
    }

    //sets question text and choice text
    private void setQuestion() {
        if(wordsCount < 4){
            maxQuestionsCount = 1;
            selectedWord = db.getWordTranslation(0);
            randomIndices[currentQuestions - 1] = 0;
            String selectedWordText = selectedWord.getWordMeaning();
            nwText.setText(selectedWordText);
            String alphabet = "abcdefghijklmnopqrstuvwxyz";
            char c =alphabet.charAt((int)(Math.random() * (alphabet.length())));
            wrongChoice1 = selectedWordText+c;
            wrongChoice2 = "none of these";
        }else{
            randomWordIndex = (int)(Math.random() * (wordsCount-3))+1;
            if(currentQuestions != 1){
                while(Arrays.asList(randomIndices).contains(randomWordIndex)){
                    randomWordIndex = (int)(Math.random() * (wordsCount-2));
                }
            }
            randomIndices[currentQuestions - 1] = randomWordIndex;
            selectedWord = db.getWordTranslation(randomWordIndex);
            nwText.setText(selectedWord.getWordMeaning());
            wrongChoice1 = db.getWordTranslation(randomWordIndex+1).getNewWord();
            wrongChoice2 = db.getWordTranslation(randomWordIndex+2).getNewWord();
        }

        int randomRadioBtn = (int)(Math.random() * (3));

        if(randomRadioBtn == 0){
            correctAnswer = R.id.radioButton;
            choice1.setText(selectedWord.getNewWord());
            choice3.setText(wrongChoice1);
            choice2.setText(wrongChoice2);
        }else{
            choice1.setText(wrongChoice1);
            if(randomRadioBtn == 1){
                correctAnswer = R.id.radioButton2;
                choice2.setText(selectedWord.getNewWord());
                choice3.setText(wrongChoice2);
            }else{
                correctAnswer = R.id.radioButton3;
                choice2.setText(wrongChoice2);
                choice3.setText(selectedWord.getNewWord());
            }
        }
    }

    //validate the answer and moved to next question if available
    public void onClickCheckBtn(View view){
        if (radioGroup.getCheckedRadioButtonId() == -1)
        {
            // no radio buttons are checked
            Toast.makeText(getApplicationContext(), "Please select an Answer", Toast.LENGTH_LONG).show();
        }
        else
        {
            progressStatus += 1;
            // Update the progress bar and display the
            //current value in the text view
            handler.post(new Runnable() {
                public void run() {
                    progressBar.setProgress(progressStatus);
                }
            });
            // one of the radio buttons is checked
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if(selectedId == correctAnswer){
                score++;
            }
            if(currentQuestions == maxQuestionsCount){
                if(score >= maxQuestionsCount - 1){
                    Toast.makeText(getApplicationContext(), "Well Done!"+"\n"+" total score is "+score+"/"+maxQuestionsCount, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Give it another try!"+"\n"+" total score is "+score+"/"+maxQuestionsCount, Toast.LENGTH_LONG).show();
                }
                finish();
            }else{
                if(selectedId == correctAnswer){
                    Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Wrong, the answer is "+selectedWord.getNewWord(), Toast.LENGTH_SHORT).show();
                }
                currentQuestions++;
                setQuestion();
            }
        }

    }


}
