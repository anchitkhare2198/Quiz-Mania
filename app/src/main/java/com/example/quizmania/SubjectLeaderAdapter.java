package com.example.quizmania;

import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SubjectLeaderAdapter extends RecyclerView.Adapter<SubjectLeaderAdapter.viewHolder> {

    private List<CategoryModel> categoryModelList;
    private long mLastClickTime = 0;

    public SubjectLeaderAdapter(List<CategoryModel> categoryModelList) {
        this.categoryModelList = categoryModelList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.setData(categoryModelList.get(position).getUrl(),categoryModelList.get(position).getName(),position);
    }

    @Override
    public int getItemCount() {
        return categoryModelList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder{

        private CircleImageView imageView;
        private TextView title;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.circular_image);
            title = itemView.findViewById(R.id.Title);
        }

        private void setData(String url, final String title, final int position){

            Glide.with(itemView.getContext()).load(url).into(imageView);
            this.title.setText(title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();
                    Intent i = new Intent(itemView.getContext(),LeaderBoard_Subject.class);
                    i.putExtra("title", title);
                    i.putExtra("position",  position);
                    itemView.getContext().startActivity(i);
                }
            });

        }
    }
}
