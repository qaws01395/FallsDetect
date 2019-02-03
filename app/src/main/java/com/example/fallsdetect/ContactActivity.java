package com.example.fallsdetect;

/**
 * Created by slyu on 2017/11/4.
 */

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.TextView;
import android.view.View.OnClickListener;

public class ContactActivity extends AppCompatActivity {
    private Button addBtn;
    private ContactsManager cm;
    private String[] showName;
    private TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_8;
    private TextView[] tvArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        addBtn = (Button) findViewById(R.id.btn_saveAdd);
        tv_1 = (TextView) findViewById(R.id.tv1);
        tv_2 = (TextView) findViewById(R.id.tv2);
        tv_3 = (TextView) findViewById(R.id.tv3);
        tv_4 = (TextView) findViewById(R.id.tv4);
        tv_5 = (TextView) findViewById(R.id.tv5);
        tv_6 = (TextView) findViewById(R.id.tv6);
        tv_7 = (TextView) findViewById(R.id.tv7);
        tv_8 = (TextView) findViewById(R.id.tv8);
        tvArray = new TextView[]{tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_8};

        final Intent intentItem = new Intent(ContactActivity.this, ContactItemActivity.class);

        OnClickListener listener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                intentItem.putExtra("mode", 1);
                switch (v.getId()) {
                    case R.id.tv1:
                        intentItem.putExtra("name", tv_1.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv2:
                        intentItem.putExtra("name", tv_2.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv3:
                        intentItem.putExtra("name", tv_3.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv4:
                        intentItem.putExtra("name", tv_4.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv5:
                        intentItem.putExtra("name", tv_5.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv6:
                        intentItem.putExtra("name", tv_6.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv7:
                        intentItem.putExtra("name", tv_7.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                    case R.id.tv8:
                        intentItem.putExtra("name", tv_8.getText().toString());
                        startActivity(intentItem);
                        finish();
                        break;
                }
            }
        };
        tv_1.setOnClickListener(listener);
        tv_2.setOnClickListener(listener);
        tv_3.setOnClickListener(listener);
        tv_4.setOnClickListener(listener);
        tv_5.setOnClickListener(listener);
        tv_6.setOnClickListener(listener);
        tv_7.setOnClickListener(listener);
        tv_8.setOnClickListener(listener);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactActivity.this, ContactItemActivity.class);
                intent.putExtra("mode", 0);// 1 -> edit mode
                startActivity(intent);

            }
        });

        cm = new ContactsManager(getBaseContext());
        showName = cm.listContactNames();
        int count = 0;
        int lengthContral;
        if (showName.length >= 8) {
            lengthContral = 8;
        } else {
            lengthContral = showName.length;
        }
        for (int i = showName.length - lengthContral; i < showName.length; i++) {
            tvArray[count].setText(showName[i]);
            count++;
        }

    }

}

//public class ContactActivity extends AppCompatActivity {
//
//    private Button btnaddInfo;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_contact);
//        initView();
//    }
//
//    private void initView() {
//        btnaddInfo = (Button) findViewById(R.id.fab);
//        btnaddInfo.setOnClickListener(this);
//    }
//
//    public void onClick(View v) {
//        Intent contactItem = new Intent(this, ContactItemActivity.class);
//        startService(contactItem);
//    }
//
//}
