package com.example.fallsdetect;

public class Contact {
    private String name;
    private String number;
    private String email;
    private boolean call;
    private boolean sendMsg;
    private boolean sendEmail;

    Contact(String name, String number, String email) {
        this.name = name;
        this.number = number;
        this.email = email;
        this.call = false;
        this.sendMsg = false;
        this.sendEmail = false;
    }

    protected String getName(){
        return this.name;
    }

    protected String getNumber(){
        return this.number;
    }

    protected String getEmail(){
        return this.email;
    }

    protected boolean getCallOption() {
        return this.call;
    }

    protected boolean getMsgOption() {
        return this.sendMsg;
    }

    protected boolean getEmailOption() {
        return this.sendEmail;
    }

    protected void setName(String n){
        this.name = n;
    }

    protected void setNumber(String n){
        this.number = n;
    }

    protected void setEmail(String e){
        this.email = e;
    }

    protected void setCallOption(boolean b) {
        this.call = b;
    }

    protected void setMsgOption(boolean b) {
        this.sendMsg = b;
    }

    protected void setEmailOption(boolean b) {
        this.sendEmail = b;
    }

}
