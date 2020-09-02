package com.example.quizmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class LeaderAdapter extends RecyclerView.Adapter<LeaderAdapter.MyViewHolder> {

    ArrayList<LeaderModel> leaderModellist;
    int p =0;

    public LeaderAdapter(ArrayList<LeaderModel> leaderModellist) {
        this.leaderModellist = leaderModellist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        p++;
        holder.fullname.setText(leaderModellist.get(position).getFullName());
        holder.GrandTotal.setText(leaderModellist.get(position).getGrandTotal());
        holder.TotalScore.setText(leaderModellist.get(position).getTotalScore());
        String b = String.valueOf(p);
        holder.rank.setText(b);
    }

    @Override
    public int getItemCount() {
        return leaderModellist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView fullname, TotalScore, GrandTotal, rank;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.LeaderName);
            TotalScore = itemView.findViewById(R.id.LeaderScore);
            GrandTotal = itemView.findViewById(R.id.LeaderTotal);
            rank = itemView.findViewById(R.id.LeaderRank);
        }
    }

}
