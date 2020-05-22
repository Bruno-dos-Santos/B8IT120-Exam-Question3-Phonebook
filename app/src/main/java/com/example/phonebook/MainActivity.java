package com.example.phonebook;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public EditText firstName;
    public EditText phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request permisstion to write contacts.

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    1);
        }

        findViewById(R.id.btnSave).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                firstName  = (EditText)findViewById(R.id.editFirstName);
                phoneNumber    = (EditText)findViewById(R.id.editPhonenumber);

                //adding contacts from the inputs, as long as they are not empty

                ArrayList<ContentProviderOperation> contentProviderOperation = new ArrayList<ContentProviderOperation>();

                contentProviderOperation.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());

                if(!firstName.getText().toString().trim().equals(""))  {
                    contentProviderOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, firstName.getText().toString().trim()).build());
                }
                if(!phoneNumber.getText().toString().trim().equals(""))  {
                    contentProviderOperation.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber.getText().toString().trim())
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }

                try  {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, contentProviderOperation);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                }  catch (Exception e)  {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("onClick: ",  e.getMessage());
                }

            }
        });

    }
}
