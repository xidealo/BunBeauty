package com.bunbeauty.ideal.myapplication.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.ideal.myapplication.R;
import com.bunbeauty.ideal.myapplication.adapters.foundElements.FoundServiceProfileElement;
import com.bunbeauty.ideal.myapplication.cleanArchitecture.models.entity.Service;

import java.util.ArrayList;

public class ServiceProfileAdapter extends RecyclerView.Adapter<ServiceProfileAdapter.ServiceProfileViewHolder> {

    private static final String TAG = "DBInf";

    private int numberItems;
    private ArrayList<Service> serviceList;
    private Context context;

    public ServiceProfileAdapter(int numberItems, ArrayList<Service> serviceList) {
        this.numberItems = numberItems;
        this.serviceList = serviceList;
    }

    @NonNull
    @Override
    public ServiceProfileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.found_service_profile_element;
        //Класс, который позволяет создавать представления из xml файла
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // откуда, куда, необходимо ли помещать в родителя
        View view = layoutInflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ServiceProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceProfileViewHolder serviceProfileViewHolder, int i) {
        serviceProfileViewHolder.bind(serviceList.get(i));
    }

    @Override
    public int getItemCount() {
        return numberItems;
    }

    class ServiceProfileViewHolder extends RecyclerView.ViewHolder {

        private View view;

        ServiceProfileViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
        }

        void bind(Service service) {
            FoundServiceProfileElement foundServiceProfileElement = new FoundServiceProfileElement(service, view, context);
            foundServiceProfileElement.createElement();
        }
    }
}
