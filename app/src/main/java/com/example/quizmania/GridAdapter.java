package com.example.quizmania;

import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GridAdapter extends BaseAdapter {


    private List<String> sets;
    private String category;
    private long mLastClickTime = 0;

    public GridAdapter(List<String> sets, String category)
    {
        this.sets = sets;
        this.category = category;
    }

    @Override
    public int getCount() {
        return sets.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        View view;

        if(convertView == null)
        {
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.set_item,parent,false);
        }
        else
        {
            view = convertView;
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent i = new Intent(parent.getContext(), Questions.class);
                i.putExtra("category",category);
                i.putExtra("setId",sets.get(position));
                parent.getContext().startActivity(i);
            }
        });

        ((TextView)view.findViewById(R.id.chapter)).setText(String.valueOf(position+1));

        return view;
    }
}
