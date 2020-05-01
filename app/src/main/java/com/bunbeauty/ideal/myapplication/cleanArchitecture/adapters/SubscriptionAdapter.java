package com.bunbeauty.ideal.myapplication.cleanArchitecture.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.data.db.models.entity.User;

import java.util.ArrayList;


public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.SubscriptionViewHolder> {

    private int numberItems;
    private ArrayList<User> userList;
    private Context context;

    public SubscriptionAdapter(int numberItems, ArrayList<User> userList) {
        this.numberItems = numberItems;
        this.userList = userList;
    }

    @NonNull
    @Override
    public SubscriptionViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.subscription_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        return new SubscriptionAdapter.SubscriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionViewHolder subscriptionViewHolder, int i) {
        subscriptionViewHolder.bind(userList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class SubscriptionViewHolder extends RecyclerView.ViewHolder {

        private View view;

        SubscriptionViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(User user) {
            SubscriptionElement subscriptionElement = new SubscriptionElement(user,view,context);
            subscriptionElement.createElement();
        }
    }
}
