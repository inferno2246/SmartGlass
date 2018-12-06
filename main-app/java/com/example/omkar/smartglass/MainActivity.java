package com.example.omkar.smartglass;

import android.content.IntentFilter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private TextView textView;
    private ListView listView;
    List<String> listItem = new ArrayList<>();

    private NotificationReceiver nReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView= findViewById(R.id.listView);
        textView= findViewById(R.id.textView);


        if(ContextCompat.checkSelfPermission(getBaseContext(), "android.permission.READ_SMS")
                == PackageManager.PERMISSION_GRANTED) {
            readsms();
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, listItem);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    // TODO Auto-generated method stub
                    String value=adapter.getItem(position);
                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference("StoredMessage");
                    mDatabase.setValue(value);
                    Toast.makeText(MainActivity.this,value,Toast.LENGTH_SHORT).show();

                    DatabaseReference SMIndDatabase = FirebaseDatabase.getInstance().getReference("SMIndicator");
                    SMIndDatabase.setValue(1);

                }
            });
        }else{
            final int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_SMS"},REQUEST_CODE_ASK_PERMISSIONS);
        }

        nReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.kpbird.nlsexample.NOTIFICATION_LISTENER_EXAMPLE");
        registerReceiver(nReceiver,filter);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nReceiver);
    }

    private void readsms(){
        String[] reqCols = new String[] { "address", "body" };
        String final_msgData;
        Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"),
                reqCols,
                null,
                null, null);

        if ( cursor.moveToFirst() ) {
            do {
                String msgData = "";
                for(int idx=0;idx<cursor.getColumnCount();idx++) {
                    msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                }
                final_msgData=msgData.replaceAll(".\\baddress.\\b","");
                final_msgData=final_msgData.replaceAll(".\\baddress:.\\b","");
                final_msgData=final_msgData.replaceAll(".\\bbody.\\b","\n");
                listItem.add(final_msgData);
            } while (cursor.moveToNext());
        } else {
            Log.w("Myapp","INBOX EMPTY!!");
        }
    }

    class NotificationReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getStringExtra("notification_event");

            DatabaseReference NotifTxtDatabase = FirebaseDatabase.getInstance().getReference("Notification");
            NotifTxtDatabase.setValue(temp);
            Toast.makeText(MainActivity.this,temp,Toast.LENGTH_SHORT).show();

            DatabaseReference NotifIndDatabase = FirebaseDatabase.getInstance().getReference("NotifIndicator");
            NotifIndDatabase.setValue(1);
        }
    }
}


