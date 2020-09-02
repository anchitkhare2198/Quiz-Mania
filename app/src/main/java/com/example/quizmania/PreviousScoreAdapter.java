package com.example.quizmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class PreviousScoreAdapter extends RecyclerView.Adapter<PreviousScoreAdapter.MyViewHolder>{

    ArrayList<PreviousScoreModel> previouslist;

    public PreviousScoreAdapter(ArrayList<PreviousScoreModel> previouslist) {
        this.previouslist = previouslist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_holder,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.categoryname.setText(previouslist.get(position).getCategoryName());
        holder.score.setText(previouslist.get(position).getScore());
        holder.total.setText(previouslist.get(position).getTotal());
        holder.date.setText(previouslist.get(position).getDate());
        holder.time.setText(previouslist.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return previouslist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView categoryname,score,total, date,time;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryname = itemView.findViewById(R.id.subject);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            score = itemView.findViewById(R.id.previousScore);
            total = itemView.findViewById(R.id.previousTotal);
        }
    }
}
