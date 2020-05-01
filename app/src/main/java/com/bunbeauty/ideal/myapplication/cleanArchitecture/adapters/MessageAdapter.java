package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements.MessageOrderElement;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters.chatElements.MessageReviewElement;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private int numberItems;
    private ArrayList<Message> messageList;
    private Context context;
    private static final String ORDER_STATUS = "order status";

    public MessageAdapter(int numberItems, ArrayList<Message> messages) {
        this.numberItems = numberItems;
        messageList = messages;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // в этом методе создаем ViewHolder
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.message_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder messageViewHolder, int index) {
        messageViewHolder.bind(messageList.get(index));
    }

    @Override
    public int getItemCount() {
        //общее количество элементов в нашем списке
        return numberItems;
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        private View view;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Message message) {
         /*   if (message.status.equals(ORDER_STATUS)) {
                MessageOrderElement messageOrderElement = new MessageOrderElement(message, view, context);
                messageOrderElement.createElement();
            } else {
                MessageReviewElement messageReviewElement = new MessageReviewElement(message, view, context);
                messageReviewElement.createElement();
            }*/
        }
    }

}
