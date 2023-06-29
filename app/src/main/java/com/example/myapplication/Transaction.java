package com.example.myapplication;

public class Transaction {

    String transaction_title = "Title";
    String note = "note";
    int amount = 500;


    public Transaction(String transaction_title, int amount, String note) {
        this.transaction_title = transaction_title;
        this.amount = amount;
        this.note = note;

    }
}
