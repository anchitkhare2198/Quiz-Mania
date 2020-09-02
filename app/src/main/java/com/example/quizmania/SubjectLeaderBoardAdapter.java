package com.example.quizmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SubjectLeaderBoardAdapter extends RecyclerView.Adapter<SubjectLeaderBoardAdapter.viewHolder> {

    List<SubjectLeaderBoardModel> subjectLeaderBoardModelList;
    int p =0;
    public SubjectLeaderBoardAdapter(List<SubjectLeaderBoardModel> subjectLeaderBoardModelList) {
        this.subjectLeaderBoardModelList = subjectLeaderBoardModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.leader_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        p++;
        holder.fullname.setText(subjectLeaderBoardModelList.get(position).getFullName());
        holder.GrandTotal.setText(subjectLeaderBoardModelList.get(position).getFinalTotal());
        holder.TotalScore.setText(subjectLeaderBoardModelList.get(position).getFinalScore());
        String b = String.valueOf(p);
        holder.rank.setText(b);

    }

    @Override
    public int getItemCount() {
        return subjectLeaderBoardModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        TextView fullname, TotalScore, GrandTotal, rank;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.LeaderName);
            TotalScore = itemView.findViewById(R.id.LeaderScore);
            GrandTotal = itemView.findViewById(R.id.LeaderTotal);
            rank = itemView.findViewById(R.id.LeaderRank);
        }
    }

}
