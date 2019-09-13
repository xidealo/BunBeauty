package com.example.ideal.myapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.foundElements.FoundOrderElement;
import com.example.ideal.myapplication.fragments.objects.Order;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private int numberItems;
    private ArrayList<Order> orderList;
    private Context context;

    public OrderAdapter(int numberItems, ArrayList<Order> orderList) {
        this.numberItems = numberItems;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.found_order_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder orderViewHolder, int i) {
        orderViewHolder.bind(orderList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        private View view;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Order order) {
            FoundOrderElement foundOrderElement = new FoundOrderElement(order,view,context);
            foundOrderElement.createElement();
        }
    }
}
