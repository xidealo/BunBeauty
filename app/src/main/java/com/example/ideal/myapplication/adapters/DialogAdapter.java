package com.example.ideal.myapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.chatElements.DialogElement;
import com.example.ideal.myapplication.fragments.objects.Dialog;
import com.example.ideal.myapplication.fragments.objects.Message;
import com.example.ideal.myapplication.other.DBHelper;

import java.util.ArrayList;

public class DialogAdapter extends RecyclerView.Adapter<DialogAdapter.DialogViewHolder> {

    private int numberItems;
    private ArrayList<Dialog> dialogList;
    private Context context;
    private DBHelper dbHelper;
    private static final String TAG = "DBInf";

    public DialogAdapter(int numberItems, ArrayList<Dialog> dialogList, DBHelper dbHelper) {
        this.numberItems = numberItems;
        this.dialogList = dialogList;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public DialogViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.dialog_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        DialogViewHolder viewHolder = new DialogViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DialogViewHolder dialogViewHolder, int i) {
        dialogViewHolder.bind(dialogList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class DialogViewHolder extends RecyclerView.ViewHolder {

        View view;

        DialogViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Dialog dialog) {
            DialogElement dialogElement = new DialogElement(dialog,view,context, dbHelper);
        }
    }
}
