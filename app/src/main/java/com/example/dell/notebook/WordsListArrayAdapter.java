package com.example.dell.notebook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DELL on 11/15/2017.
 */


public class WordsListArrayAdapter extends ArrayAdapter<WordTranslation>  implements Filterable {

    ArrayList<WordTranslation> wordsList = new ArrayList<>();
    CustomFilter filter;
    ArrayList<WordTranslation> filterList;
    Context context;

    public WordsListArrayAdapter(Context context, int textViewResourceId, ArrayList<WordTranslation> objects) {
        super(context, R.layout.list_view_items, objects);
        wordsList = objects;
        filterList = new ArrayList<>(objects);
        this.context = context;
    }

    @Override
    public int getCount() {
        return wordsList.size();
    }

    public WordTranslation getItem(int i) {
        return wordsList.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_view_items, null, true);
        }
        WordTranslation p = (WordTranslation) getItem(position);

        TextView nwTextView = (TextView) v.findViewById(R.id.nwTextView);
        TextView translatedtextView = (TextView) v.findViewById(R.id.translatedtextView);
        if (wordsList.size() >= 1 && position < wordsList.size()) {
            nwTextView.setText(wordsList.get(position).getNewWord());
            translatedtextView.setText(wordsList.get(position).getWordMeaning());
        }

        if (wordsList.size() < 1 && position == 0) {

            nwTextView.setText("No avaiable words");
        }
        return v;


    }
    @Override
    public Filter getFilter() {
        // TODO Auto-generated method stub
        if(filter == null)
        {
           filter  =new CustomFilter();
        }
        return filter;
    }
    //INNER CLASS
    class CustomFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            FilterResults results=new FilterResults();
            if(constraint != null && constraint.length()>0)
            {
                constraint=constraint.toString().toLowerCase();
                ArrayList<WordTranslation> filters=new ArrayList();

                //get specific items
                for(int i=0;i<filterList.size();i++)
                {

                    if(filterList.get(i).getNewWord().startsWith(constraint.toString().toLowerCase()))
                    {
                        WordTranslation p=new WordTranslation(filterList.get(i).getNewWord(), filterList.get(i).getWordMeaning());
                        filters.add(p);
                    }
                }
                results.count=filters.size();
                results.values=filters;

            }else
            {
                results.count=filterList.size();
                results.values=filterList;
            }
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            // TODO Auto-generated method stub
            // Now we have to inform the adapter about the new list filtered

            wordsList =(ArrayList<WordTranslation>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {

                notifyDataSetInvalidated();
            }


        }
    }
}
