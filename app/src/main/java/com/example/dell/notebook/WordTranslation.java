package com.example.dell.notebook;

/**
 * Created by DELL on 11/14/2017.
 */

class WordTranslation {
    private int id;
    private String newWord;
    private String wordMeaning;

    public WordTranslation (){
    }

    public WordTranslation (int id,String word,String translation){
        this.id = id;
        this.newWord = word;
        this.wordMeaning = translation;
    }

    public WordTranslation (String word,String translation){
        this.newWord = word;
        this.wordMeaning = translation;
    }

    public String getNewWord() {
        return newWord;
    }

    public void setNewWord(String newWord) {
        this.newWord = newWord;
    }

    public String getWordMeaning() {
        return wordMeaning;
    }

    public void setWordMeaning(String wordMeaning) {
        this.wordMeaning = wordMeaning;
    }
    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return  newWord + " <---> " + wordMeaning;
    }


}
