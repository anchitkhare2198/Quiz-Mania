package com.example.quizmania;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.viewHolder> {

    List<BookmarkModel> list;
    private DeleteListener deleteListener;

    public BookmarkAdapter(List<BookmarkModel> list, DeleteListener deleteListener) {
        this.list = list;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookmark_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
//        holder.question.setText(list.get(position).getQuestion());
//        holder.answer.setText(list.get(position).getAnswer());

        holder.setData(list.get(position).getQuestion(),list.get(position).getAnswer(),list.get(position).getKey(),position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{
        TextView question,answer;
        ImageButton delete;
        public viewHolder(@NonNull View itemView) {
            super(itemView);

            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            delete = itemView.findViewById(R.id.delete_btn);
        }

        public void setData(String question, String answer, final String key, final int position){
            this.question.setText("Ques.) " + question);
            this.answer.setText("Ans.) "+ answer);

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteListener.onDelete(key, position);
                }
            });

        }
    }

    public interface DeleteListener{
        public void onDelete(String key, int position);
    }

}
