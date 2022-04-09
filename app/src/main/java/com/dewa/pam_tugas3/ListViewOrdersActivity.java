package com.dewa.pam_tugas3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ListViewOrdersActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Orders> orderArrayList;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_orders);

        listView = findViewById(R.id.list_view);
        orderArrayList = new ArrayList<>();

        db = FirebaseFirestore.getInstance();

        loadDatainListview();
    }

    private void loadDatainListview() {
        db.collection("orders").orderBy("createdDate").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot d : list) {
                                Orders orders = d.toObject(Orders.class);
                                orderArrayList.add(orders);
                            }
                            listViewAdapter adapter = new listViewAdapter(
                                    ListViewOrdersActivity.this, orderArrayList);

                            listView.setAdapter(adapter);
                        } else {
                            Toast.makeText(ListViewOrdersActivity.this,
                                    "No data found in Database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ListViewOrdersActivity.this, "Fail to load data..",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}