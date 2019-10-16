package com.bunbeauty.ideal.myapplication.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.foundElements.FoundServiceElement;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.User;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder> {

    private static final String TAG = "DBInf";

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

        return new ServiceViewHolder(view);
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
            foundServiceElement.createElement();
        }
    }
}
