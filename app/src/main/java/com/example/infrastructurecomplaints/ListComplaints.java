package com.example.infrastructurecomplaints;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListComplaints extends AppCompatActivity {

    ArrayList personNames = new ArrayList<>(Arrays.asList("Person 1","Person 2","Person 3","Person 4","Person 5", "Person 6", "Person 7","Person 8", "Person 9", "Person 10", "Person 11", "Person 12", "Person 13", "Person 14"));

    private FirebaseFirestore db;
    ArrayList<Complaints> cmplist;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_complaints);


        //Making a list for complaints
        cmplist = new ArrayList<Complaints>();


        //Array List of strings
        final ArrayList<String> subjects = new ArrayList<>();


        //Getting data from Firebase
        db = FirebaseFirestore.getInstance();
        CollectionReference complaints = db.collection("complaints");
        complaints.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {

                    Toast.makeText(ListComplaints.this, "Fetching Successfull", Toast.LENGTH_SHORT).show();

                    //Working absolutely fine but after this for diaplaying taking too much time 
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String subject = (String) document.get("Subject");
                        subjects.add(subject);
                    }
                }
                else {
                    Toast.makeText(ListComplaints.this, "Failed to fetch Complaints", Toast.LENGTH_SHORT).show();
                }
            }
        });


        //Initializing Recyler View
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        MyAdapter mAdapter = new MyAdapter(this,subjects);
        recyclerView.setAdapter(mAdapter);


    }

    public void createComplaint(View view) {
        Toast.makeText(this, "Button Pressed", Toast.LENGTH_SHORT).show();
    }
}
