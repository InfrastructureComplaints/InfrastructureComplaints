package com.example.infrastructurecomplaints;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.Collection;

public class Complaint extends AppCompatActivity {

    private FirebaseFirestore db;
    private TextView text_subject;
    private String description;
    private String status;
    private String user;
    private TextView text_description;
    private TextView text_user;
    private TextView text_status;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaint);
        text_subject = (TextView)findViewById(R.id.text_subject_c);
        Intent intent = getIntent();
        String subject = intent.getStringExtra("Subject");
        text_subject.setText(subject);
        text_description = (TextView)findViewById(R.id.text_description_c);
        text_user = (TextView)findViewById(R.id.text_user_c);
        text_status = (TextView)findViewById(R.id.text_status);

        //Getting databse instance
        db = FirebaseFirestore.getInstance();

        db.collection("complaints").whereEqualTo("Subject",subject).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    //Toast.makeText(Complaint.this, "Query Successfull", Toast.LENGTH_SHORT).show();

                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                    description = (String) doc.get("Description");
                    status = (String) doc.get("Status");
                    user = (String) doc.get("User");

                    //Setting data in interface
                    text_description.setText(description);
                    text_status.setText(status);
                    text_user.setText(user);
                    docId = doc.getId();


                }
                else {
                    Toast.makeText(Complaint.this, "Failed to fetch", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    //Feedback
    public void feedback(View view) {

        String status = text_status.getText().toString();
        if(status.equals("Pending")) {
            Toast.makeText(this, "Complaint still pending", Toast.LENGTH_SHORT).show();
        }
        else if(status.equals("Approved")) {
            Toast.makeText(this, "Complaint forwarded", Toast.LENGTH_SHORT).show();
        }
        else if(status.equals("Rejected")){
            Toast.makeText(this, "Complaint Rejected", Toast.LENGTH_SHORT).show();
        }
        else {
            Intent intent = new Intent(this,Feedback.class);
            intent.putExtra("ComplaintId",docId);
            startActivity(intent);
        }
    }


    //Image display in seprate interface
    public void openImage(View view) {

        Intent intent = new Intent(this,CmpImage.class);
        intent.putExtra("ComplaintId",docId);
        startActivity(intent);


    }
}
