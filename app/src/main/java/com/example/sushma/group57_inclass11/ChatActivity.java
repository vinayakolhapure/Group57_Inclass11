package com.example.sushma.group57_inclass11;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class ChatActivity extends AppCompatActivity {

    ImageButton imageSend;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private EditText sendText;
    private TextView textViewUser;
    ArrayList<Message> messages;
    ListView lv;
    ListViewAdapter adapter;
    String firstName = null;
    String lastName = null;

    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    Uri selectedImageUri, file;

    StorageReference storageRef;
    FirebaseStorage storage;
    ProgressDialog pd;
    ImageButton imageGallery, logout;
    UploadTask uploadTask;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        imageSend = (ImageButton) findViewById(R.id.imageSend);
        sendText = (EditText) findViewById(R.id.editText);
        textViewUser = (TextView) findViewById(R.id.tvUserName);
        messages = new ArrayList<Message>();
        lv = (ListView) findViewById(R.id.listView);
        pd = new ProgressDialog(this);

        firebaseUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://group57inclass11.appspot.com");
        imageGallery = (ImageButton) findViewById(R.id.imageGallery);
        logout = (ImageButton)findViewById(R.id.imageLogout);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent loginIntent = new Intent(ChatActivity.this, MainActivity.class);
                startActivity(loginIntent);
            }
        });

        imageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(v);
            }
        });

        if(firebaseUser!=null) {
            String uid = firebaseUser.getUid();
            DatabaseReference userFromDb = mDatabase.child("users").child(uid);
            userFromDb.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    firstName = user.getFirstName();
                    lastName = user.getLastName();
                    textViewUser.setText(firstName + " " + lastName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            DatabaseReference messagesRef = mDatabase.child("messages").child(uid);
            messagesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot msgSnapshot : dataSnapshot.getChildren()) {
                        Message message = new Message();
                        Log.d("demo",msgSnapshot.getRef().toString());
                        message.setFname((String) msgSnapshot.child("fname").getValue());
                        message.setLname((String) msgSnapshot.child("lname").getValue());
//                        message.setChatDate((Date) msgSnapshot.child("chatDate").getValue());
                        message.setMessageText((String) msgSnapshot.child("messageText").getValue());
                        message.setThumbNail((String) msgSnapshot.child("thumbNail").getValue());
                        messages.add(message);
                    }
                    if(messages.size()>0) {
                        adapter = new ListViewAdapter(ChatActivity.this,R.layout.item_row_layout, messages);
                        adapter.notifyDataSetChanged();
                        if (lv!=null) {
                            lv.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }



        imageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uid = firebaseUser.getUid();
                String messageText = sendText.getText().toString();
                final Message message = new Message();
                message.setMessageText(messageText);
                message.setChatDate(new Date());

                message.setFname(firstName);
                message.setLname(lastName);
                String key = mDatabase.child("messages").child(uid).push().getKey();
                mDatabase.child("messages").child(uid).child(key).
                        child("chatDate").setValue(message.getChatDate());
                mDatabase.child("messages").child(uid).child(key).
                        child("messageText").setValue(message.getMessageText());
                mDatabase.child("messages").child(uid).child(key).
                        child("fname").setValue(message.getFname());
                mDatabase.child("messages").child(uid).child(key).
                        child("lname").setValue(message.getLname());
                mDatabase.child("messages").child(uid).child(key).
                        child("thumbNail").setValue(null);
                messages.add(message);
                if(adapter!=null){
                    Log.d("demo","adapter not null");
                    //adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void openGallery(View view){
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        selectedImageUri = data.getData();
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(selectedImageUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String imgDecodableString = cursor.getString(columnIndex);
        cursor.close();
        Log.d("Image", imgDecodableString);
        file = Uri.fromFile(new File(imgDecodableString));
        pd.setMessage("Loading..");
        pd.show();
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        uploadTask = storageRef.child("images/"+file.getLastPathSegment()).putFile(file, metadata);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Log.d("Upload Failure","Couldn't upload image");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();

                final String uid = firebaseUser.getUid();
//                String messageText = sendText.getText().toString();
                final Message message = new Message();
                message.setMessageText("");
                message.setChatDate(new Date());
                message.setThumbNail(downloadUrl.toString());
                DatabaseReference userFromDb = mDatabase.child("users").child(uid);
                userFromDb.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        message.setFname(user.getFirstName());
                        message.setLname(user.getLastName());
                        Log.d("demo",message.toString());
                        String key = mDatabase.child("messages").child(uid).push().getKey();
                        mDatabase.child("messages").child(uid).child(key).
                                child("chatDate").setValue(message.getChatDate());
                        mDatabase.child("messages").child(uid).child(key).
                                child("messageText").setValue(message.getMessageText());
                        mDatabase.child("messages").child(uid).child(key).
                                child("fname").setValue(message.getFname());
                        mDatabase.child("messages").child(uid).child(key).
                                child("lname").setValue(message.getLname());
                        mDatabase.child("messages").child(uid).child(key).
                                child("thumbNail").setValue(message.getThumbNail());

                        messages.add(message);

                        pd.dismiss();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
}
