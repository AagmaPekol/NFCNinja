package com.example.nfcninja;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBNfc {
    @PrimaryKey
    public int uid;

    @ColumnInfo
    public String tagInfo;

    @ColumnInfo
    public String name;

}
