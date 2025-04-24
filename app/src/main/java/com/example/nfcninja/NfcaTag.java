package com.example.nfcninja;

import android.nfc.tech.NfcA;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class NfcaTag {
    @PrimaryKey
    public int uid;

    @ColumnInfo
    public NfcA NFCA;

    @ColumnInfo
    public String Name;

}
