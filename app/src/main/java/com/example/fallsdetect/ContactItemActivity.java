package com.example.fallsdetect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.content.Intent;
import android.widget.TextView;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

public class ContactItemActivity extends AppCompatActivity{
    private static final String TAG = "ContactItemActivity"; // help with debugging message


    private EditText edName, edPhone, edEmail;
    private Button saveContact, editContact, deleteContact;
    private ContactsManager contactsManager;
    private Switch chzcall, chzSMS, chzEmail;
    private int mode=0;
    private String name;
    private Contact contact;
    private Spinner spinner;
    private List<Integer> dataList;
    private ArrayAdapter<Integer> adapter;
    private String[] nameList;
    private TextView priority;
    boolean iscall= false;
    boolean isMSG = false;
    boolean isEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.mode = intent.getIntExtra("mode",0);
        contactsManager = new ContactsManager(getBaseContext());

        if (mode == 0) {
            setContentView(R.layout.activity_contact_item);
            initView();
        }else {
            Intent intentName = getIntent();
            name = intentName.getStringExtra("name");
            if (!name.isEmpty()) {
                setContentView(R.layout.edit_contact);
                editView();
            } else {
                Intent intentBack = new Intent(ContactItemActivity.this,ContactActivity.class);
                startActivity(intentBack);
                finish();
            }
        }

    }

    private void initView() {
        edName = (EditText) findViewById(R.id.editTextName);
        edPhone = (EditText) findViewById(R.id.editTextMobile);
        edEmail = (EditText) findViewById(R.id.editTextEmail);
        chzcall = (Switch) findViewById(R.id.callsw);
        chzSMS = (Switch) findViewById(R.id.messagesw);
        chzEmail = (Switch) findViewById(R.id.emailsw);


        chzcall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !edName.getText().toString().isEmpty()){ iscall=true;}
            }
        });

        chzSMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !edName.getText().toString().isEmpty()){ isMSG=true;}
            }
        });

        chzEmail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked && !edName.getText().toString().isEmpty()){ isEmail=true;}
            }
        });



        saveContact = (Button) findViewById(R.id.btn_saveItem);

        saveContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsManager.saveContact(edName.getText().toString(),edPhone.getText().toString(), edEmail.getText().toString(),iscall, isMSG, isEmail);
                Intent intent = new Intent(ContactItemActivity.this, ContactActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void editView() {
        edName = (EditText) findViewById(R.id.editTextNameed);
        edPhone = (EditText) findViewById(R.id.editTextMobileed);
        edEmail = (EditText) findViewById(R.id.editTextEmailed);

        chzcall = (Switch) findViewById(R.id.callswed);
        chzSMS = (Switch) findViewById(R.id.messageswed);
        chzEmail = (Switch) findViewById(R.id.emailswed);

        editContact = (Button) findViewById(R.id.btn_editItem);
        deleteContact = (Button) findViewById(R.id.btn_deletItem);
        priority = (TextView) findViewById(R.id.setPrio);
        spinner = (Spinner) findViewById(R.id.spinner);
        dataList = new ArrayList<Integer>();



        contact = contactsManager.getContact(name);
        edName.setText(contact.getName());
        edPhone.setText(contact.getNumber());
        edEmail.setText(contact.getEmail());

        chzcall.setChecked(contact.getCallOption());
        chzSMS.setChecked(contact.getMsgOption());
        chzEmail.setChecked(contact.getEmailOption());
        priority.setText("Set Priority ( " + contactsManager.checkContactPriority(name) + " )");

        editContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contact.getName().equals(edName.getText().toString())) {
                    contactsManager.editContactName(contact.getName(), edName.getText().toString());
                }
                if (!contact.getNumber().equals(edPhone.getText().toString())) {
                    contactsManager.editContactNumber(contact.getName(), edPhone.getText().toString());
                }
                if (!contact.getEmail().equals(edEmail.getText().toString())) {
                    contactsManager.editContactEmail(contact.getName(), edEmail.getText().toString());
                }
                contactsManager.setContactOption(edName.getText().toString(), chzcall.isChecked(), chzSMS.isChecked(), chzEmail.isChecked());

                //test
                Log.d(TAG, "set options");
                contactsManager.checkContact();

                Intent intent = new Intent(ContactItemActivity.this, ContactActivity.class);
                startActivity(intent);
                finish();
            }
        });

        deleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contactsManager.deleteContact(edName.getText().toString());
                Intent intent = new Intent(ContactItemActivity.this, ContactActivity.class);
                startActivity(intent);
                finish();
            }
        });

        nameList = contactsManager.listContactNames();
        for (int i = 0; i < nameList.length; i++) {
            dataList.add(i);
        }

        adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, dataList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(contactsManager.checkContactPriority(name),true);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                contactsManager.setContactPriority(edName.getText().toString(), adapter.getItem(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

}
