package com.dewa.pam_tugas3;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class listViewAdapter extends ArrayAdapter<Orders> {

    public listViewAdapter(@NonNull Context context, ArrayList<Orders> orderArrayList) {
        super(context, 0, orderArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            listitemView = LayoutInflater.from(getContext()).
                    inflate(R.layout.item_listview, parent, false);
        }

        Orders orders = getItem(position);

        TextView tvName = listitemView.findViewById(R.id.tvName);
        tvName.setText(orders.getName());

        TextView tvAddress = listitemView.findViewById(R.id.tvAddress);
        tvAddress.setText(orders.getAddress());

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), orders.getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                intent.putExtra(MainActivity.EXTRA_NAME, orders.getName());
                intent.putExtra(MainActivity.EXTRA_ADDRESS, orders.getAddress());
                v.getContext().startActivity(intent);
            }
        });
        return listitemView;
    }
}
