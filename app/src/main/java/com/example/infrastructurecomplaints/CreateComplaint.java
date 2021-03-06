package com.example.infrastructurecomplaints;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import io.grpc.Context;

public class CreateComplaint extends AppCompatActivity {

    private ProgressBar progressBar;
    //Image purpose variables
    private static final int PICK_IMAGE = 1;
    private Uri mImageURI;
    private ImageView imageView;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    private FirebaseFirestore db;
    private EditText sub,des;
    String email;
    private Map<String,Object> data = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressBar = findViewById(R.id.progressBar);

        setContentView(R.layout.activity_create_complaint);
        sub = (EditText)findViewById(R.id.text_subject_cc);
        des = (EditText)findViewById(R.id.text_description_cc);

        Intent intent = getIntent();
        email = intent.getStringExtra("Email");
        //Getting databse instance
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference("images");

        imageView = (ImageView)findViewById(R.id.imageView);


    }

    public void create(View view) {



        data.put("User",email);
        data.put("Subject",sub.getText().toString());
        data.put("Description",des.getText().toString());
        data.put("Feedback","Not Specified");
        data.put("Image","url://");
        data.put("Priority","Not Specified");
        data.put("Status","Pending");
        data.put("Type","Not Specified");

        //Again Setting progress to zero
        //progressBar.setProgress(0);


        //Getting current date and time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        String time = simpleTimeFormat.format(calendar.getTime());

        data.put("Time",time);
        data.put("Date",date);


        db.collection("complaints").add(data).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(CreateComplaint.this, "Complaint Lodged successfully", Toast.LENGTH_SHORT).show();

                    //Image Upload
                    DocumentReference doc = task.getResult();
                    StorageReference imageRef = storageReference.child((String)doc.getId() + "." + getImageExtension(mImageURI));
                    imageRef.putFile(mImageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(CreateComplaint.this, "Image Upload Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                    //Get user  from shared prefrences config
                    //intent.putExtra("Email",email);
                    Intent intent = new Intent(CreateComplaint.this,ListComplaints.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CreateComplaint.this, "Something gone wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //Get Image extension
    private String getImageExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    public void clickImage(View view) {
        openFileChooser();
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageURI = data.getData();
            Picasso.with(this).load(mImageURI).into(imageView);
        }
    }
}
