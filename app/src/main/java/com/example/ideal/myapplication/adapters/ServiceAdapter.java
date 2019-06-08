package com.example.ideal.myapplication.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ideal.myapplication.R;
import com.example.ideal.myapplication.adapters.foundElements.FoundServiceElement;
import com.example.ideal.myapplication.fragments.objects.Service;
import com.example.ideal.myapplication.fragments.objects.User;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private int numberItems;
    private ArrayList<Service> serviceList;
    private ArrayList<User> userList;

    private Context context;

    public ServiceAdapter(int numberItems, ArrayList<Service> serviceList, ArrayList<User> userList) {
        this.numberItems = numberItems;
        this.serviceList = serviceList;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.found_service_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);

        ServiceViewHolder viewHolder = new ServiceViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder serviceViewHolder, int i) {
        serviceViewHolder.bind(serviceList.get(i), userList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class ServiceViewHolder extends RecyclerView.ViewHolder {

        private View view;

        ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Service service, User user) {
            FoundServiceElement foundServiceElement = new FoundServiceElement(service,user,view,context);
        }
    }
}
