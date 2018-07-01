package com.example.tmahlangu3.journalapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddNotesActivity extends AppCompatActivity {
    private Button mSubmitButton;
    private ImageButton mPostImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private DatabaseReference mPostDatabase;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private static final int GALLERY_CODE = 1;
    private Uri mImageUrl;
    private StorageReference mStorageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);
        mProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("MBlog");
        mSubmitButton =(Button)findViewById(R.id.submitPost);
        mPostImage = (ImageButton)findViewById(R.id.imageButton);
        mPostTitle = (EditText)findViewById(R.id.postTitleEt);
        mPostDesc = (EditText)findViewById(R.id.descriptionEt);
        mPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //posting to databse
                startPosting();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode==RESULT_OK){
            mImageUrl=data.getData();
            mPostImage.setImageURI(mImageUrl);
        }
    }

    private void startPosting() {
        mProgress.setMessage("Posting to blog..");
        mProgress.show();
        final String titleVal=mPostTitle.getText().toString().trim();
        final String descVal =mPostDesc.getText().toString().trim();
        Uri downloadUrl;
        if(!TextUtils.isEmpty(titleVal) && !TextUtils.isEmpty(descVal) && mImageUrl !=null){
            //start the uploading..
           final StorageReference filepath=mStorageReference.child("MBlog_imagies").child(mImageUrl.getLastPathSegment());
           filepath.putFile(mImageUrl).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           Uri downloadUrl=uri;
                           DatabaseReference newPost = mPostDatabase.push();
                           Map<String,String> dataToSave =new HashMap<>();

                           dataToSave.put("title",titleVal);
                           dataToSave.put("desc",descVal);
                           dataToSave.put("image",downloadUrl.toString());
                           dataToSave.put("timestamp",String.valueOf(java.lang.System.currentTimeMillis()));
                           dataToSave.put("userid",mUser.getUid());
                           newPost.setValue(dataToSave);
                           mProgress.dismiss();
                           startActivity(new Intent(AddNotesActivity.this,NotesListActivity.class));
                           finish();
                       }
                   });

               }
           });


        }else{
            mProgress.dismiss();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Oops");
            alertDialogBuilder.setMessage("Make Sure You Put In Your The Title,Description & Image");
            alertDialogBuilder.setPositiveButton("yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            Toast.makeText(getApplicationContext(),"Try Again",Toast.LENGTH_LONG).show();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
