package com.example.fallsdetect;

import android.content.Context;
import android.os.Parcelable;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by hschang on 2017/11/2.
 *
 *  This class contains all the functionality related to contacts info managing.
 *  It will be used in ContactActivity for all of its button functionality.
 *  It will also be used by Notifition when it needs contact info.
 */

public class ContactsManager implements Serializable{

    private static final String TAG = "ContactsManager"; // help with debugging message

    private Context context;
    private String filename = "FallsDetect_contacts.txt";
//    private String content = "Professor Chang,5154506371,chang@iastate.edu,false,true,false---Chang HanShu,5157085845,hschang@iastate.edu,false,false,true---";
    private String content = "";
    private ArrayMap<String, Contact> contactsMap; // map structure to store contacts
    private ArrayList<String> priority; // the index of the name in the list reflects its priority

    ContactsManager(Context context) {
//        writeFile(content); // reset YOU CAN TURN ON THIS COMMENT WHEN YOU WANT TO CLEAN THE FILE CONTENT
        this.context = context;
        this.content = readFile();
        contactsMap = new ArrayMap<String, Contact>();
        priority = new ArrayList<String>();
        if(!this.content.equals("") )
            content2Contacts();
    }

    protected void checkContact() {
        // TODO make some readable output
        for (ArrayMap.Entry<String,Contact> entry : contactsMap.entrySet()) {
            Log.i("checkContact()", "contacts data structure: \nName: "+entry.getValue().getName()+" Number: "+entry.getValue().getNumber()+" Email: "+entry.getValue().getEmail()+" call option: "+entry.getValue().getCallOption()+" sms option: "+entry.getValue().getMsgOption()+" email option: "+entry.getValue().getEmailOption());
        }
        Log.i("checkContact()"," file content: \n"+content);
    }

    protected int checkContactPriority(String name) {
        return priority.indexOf(name);
    }

    protected String[] listContactNames() {
        int n = priority.size();
        String[] contacts = new String[n];
        for (int i=0; i<n; i++) {
            contacts[i] = priority.get(i);
        }
        return contacts;
    }

    protected ArrayList<String> listMsgContacts() {
        String[] contacts = listContactNames();
        ArrayList<String> sendMsg = new ArrayList<String>();
        for (String c: contacts) {
            if (getContact(c).getMsgOption()) {
                Log.d(TAG, "getContact(c).getName() "+getContact(c).getName());
                sendMsg.add(c);
            }
        }
        return sendMsg;
    }

    protected ArrayList<String> listPhoneContacts() {
        String[] contacts = listContactNames();
        ArrayList<String> call = new ArrayList<String>();
        for (String c: contacts) {
            if (getContact(c).getCallOption()) call.add(c);
        }
        return call;
    }

    protected ArrayList<String> listEmailContacts() {
        String[] contacts = listContactNames();
        ArrayList<String> sendEmail = new ArrayList<String>();
        for (String c: contacts) {
            if (getContact(c).getEmailOption()) sendEmail.add(c);
        }
        return sendEmail;
    }

    protected void deleteContact(String name) {
        if (!contactsMap.containsKey(name)) { // O(n)
            Log.d(TAG,"The contact not exists.");
            Toast.makeText(context, "The contact not exists.", Toast.LENGTH_LONG).show();
            return;
        }
        contactsMap.remove(name); // O(1)
        priority.remove(name); // O(n)

        // update content TODO maybe can make it more efficient
        updateContent();
        Log.d(TAG,"The contact is deleted.");
        Toast.makeText(context, "The contact is deleted.", Toast.LENGTH_LONG).show();
    }

    protected void editContactName(String name, String newName) {
        if (!contactsMap.containsKey(name)) { // O(n)
            Log.d(TAG,"The contact is not exist. Please add new contact first.");
            Toast.makeText(context, "The contact is not exist. Please add new contact first.", Toast.LENGTH_LONG).show();
            return;
        }
        // edit name on contacts Map
        Contact target = contactsMap.get(name); // O(1)
        target.setName(newName);
        contactsMap.put(newName, target);
        contactsMap.remove(name);
        // edit name on priority list
        int i = priority.indexOf(name); // O(n)
        priority.set(i, newName);

        // update content TODO maybe can make it more efficient
        updateContent();
        Toast.makeText(context, "Contact name edited.", Toast.LENGTH_LONG).show();
    }

    protected void editContactNumber(String name, String newNumber) {
        if (!contactsMap.containsKey(name)) { // O(n)
            Log.i(TAG,"The contact is not exist. Please add new contact first.");
            Toast.makeText(context, "The contact is not exist. Please add new contact first.", Toast.LENGTH_LONG).show();
            return;
        }
        // edit number on contacts Map
        Contact target = contactsMap.get(name); // O(1)
        target.setNumber(newNumber);

        // update content TODO maybe can make it more efficient
        updateContent();
        Log.i(TAG,"Contact number edited.");
        Toast.makeText(context, "Contact number edited.", Toast.LENGTH_LONG).show();
    }

    protected void editContactEmail(String name, String newEmail) {
        if (!contactsMap.containsKey(name)) { // O(n)
            Log.i(TAG,"The contact is not exist. Please add new contact first.");
            Toast.makeText(context, "The contact is not exist. Please add new contact first.", Toast.LENGTH_LONG).show();
            return;
        }
        // edit number on contacts Map
        Contact target = contactsMap.get(name); // O(1)
        target.setEmail(newEmail);

        // update content TODO maybe can make it more efficient
        updateContent();
        Log.i(TAG,"Contact email edited.");
        Toast.makeText(context, "Contact email edited.", Toast.LENGTH_LONG).show();
    }

    protected Contact getContact(String name) {
        return contactsMap.get(name);
    }

    protected Contact getContactByPriority(int p) {
        return contactsMap.get(priority.get(p));
    }

    protected void saveContact(String name, String number, String email, boolean call, boolean sendMsg, boolean sendEmail) {
        if (name.equals("")) {
            Log.i(TAG,"Name can't be empty.");
            Toast.makeText(context, "Name can't be empty.", Toast.LENGTH_LONG).show();
            return;
        }
        if (contactsMap.containsKey(name)) { // O(n)
            Log.i(TAG,"The contact is already exist. Use \"edit\" instead.");
            Toast.makeText(context, "The contact is already exist. Use \"edit\" instead.", Toast.LENGTH_LONG).show();
            return;
        }
        // update file content
        content += name+","+number+","+email+","+call+","+sendMsg+","+sendEmail+"---";
        writeFile(content);
        // update contacts map
        contactsMap.put(name, new Contact(name,number,email));
        priority.add(name); // default to be the last

        Contact newContact = contactsMap.get(name);
        newContact.setCallOption(call);
        newContact.setMsgOption(sendMsg);
        newContact.setCallOption(sendEmail);
    }

    protected void saveContact(String name, String number, String email) {
        saveContact(name, number, email, false, false, false);
    }

    protected void setContactOption(String name, boolean call, boolean sendMsg, boolean sendEmail) {
        if (!contactsMap.containsKey(name)) { // O(n)
            Log.i(TAG,"The contact is not exist. Please add new contact first.");
            Toast.makeText(context, "The contact is not exist. Please add new contact first.", Toast.LENGTH_LONG).show();
            return;
        }
        // edit number on contacts Map
        Contact target = contactsMap.get(name); // O(1)
        target.setCallOption(call);
        target.setMsgOption(sendMsg);
        target.setEmailOption(sendEmail);

        // update content TODO maybe can make it more efficient
        updateContent();
        Log.i(TAG,"Contact notification options are set.");
        Toast.makeText(context, "Contact notification options are set.", Toast.LENGTH_LONG).show();
    }

    // The contact is assumed in the list, there are 2 cases, move the contact forward when priority is smaller;
    // move the contact when the priority is set to be bigger
    protected void setContactPriority(String name, int p) {
        int size = priority.size();
        int originalIndex = priority.indexOf(name); // O(n)

        if (p < 0) {
            Log.i(TAG,"Priority cannot be less than 0");
            Toast.makeText(context, "Priority cannot be less than 0", Toast.LENGTH_LONG).show();
            return;
        }
        // inserting
        if (p > originalIndex ) {// move back
            // O(n)
            String copy;
            for (int i=originalIndex; i != p && i<size-1; i++){
                // insert the contact
                copy = priority.get(i+1);
                priority.set(i, copy);
            }
            if (p>=size)
                priority.set(size-1, name);
            else
                priority.set(p, name);
        }
        else if (p < originalIndex){ // move front
            // O(n)
            String copy;
            for (int i=originalIndex; i != p; i--){
                // insert the contact
                copy = priority.get(i-1);
                priority.set(i, copy);
            }
            priority.set(p, name);
        }
        // TODO maybe can make it more efficient
        contacts2Content();
    }

    //---------------- helper methods ------------------

    //  Creates contacts map according to file content
    private void content2Contacts() {
        if (priority.size() != 0)
            priority.clear();
        if (contactsMap.size() != 0)
            contactsMap.clear();
        // convert content to array map
        String part[] = content.split("---");
        Contact temp;
        for(String info: part) {
            String[] detail = info.split(",");
            temp = new Contact(detail[0],detail[1],detail[2]);
            temp.setCallOption(Boolean.parseBoolean(detail[3]));
            temp.setMsgOption(Boolean.parseBoolean(detail[4]));
            temp.setEmailOption(Boolean.parseBoolean(detail[5]));

            contactsMap.put(detail[0], temp ); // name -> contact
            priority.add(detail[0]); // The first contact in content has the first priority, and so on
        }
    }

    // Update file content according to contacts map
    private void contacts2Content() {
        content = "";
        Contact temp;
        // convert content to array map
        for(int i=0; i<priority.size(); i++) {
            temp = contactsMap.get( priority.get(i) );
            content += temp.getName() + "," + temp.getNumber() + "," + temp.getEmail() + "," + temp.getCallOption() + "," + temp.getMsgOption() + "," + temp.getEmailOption() + "---";
        }
    }

    private String readFile() {
//        Log.d(TAG, "readFile()");
        String ret = "";
        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return ret;
    }

    private void writeFile(String text) {
//        Log.d(TAG, "writeFile()");
        // write file
        try {
            Log.d(TAG, "context "+ context);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(text);
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateContent() {
        contacts2Content();
        writeFile(content);
    }

    // if implementing database
    private void connectDataBase() {
        //TODO
    }



}

