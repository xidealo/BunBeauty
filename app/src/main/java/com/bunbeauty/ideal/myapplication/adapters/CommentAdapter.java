package com.bunbeauty.ideal.myapplication.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Comment;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private int numberItems;
    private ArrayList<Comment> commentList;
    private Context context;
    private static final String TAG = "DBInf";
    public void refreshData(int numberItems, ArrayList<Comment> commentList){
        this.numberItems = numberItems;
        this.commentList = commentList;
        notifyDataSetChanged();
    }

    public CommentAdapter(int numberItems, ArrayList<Comment> commentList) {
        this.numberItems = numberItems;
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.comment_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        return new CommentAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {
        //Log.d(TAG, "onBindViewHolder: " + i);
        //Log.d(TAG, "SIZE "+ commentList.size());
        commentViewHolder.bind(commentList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        private View view;
        CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Comment comment) {
            CommentElement commentElement = new CommentElement(comment,view,context);
            commentElement.createElement();
        }
    }

}
