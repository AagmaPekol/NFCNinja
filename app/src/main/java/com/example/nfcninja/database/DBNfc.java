package com.example.nfcninja.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBNfc {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo
    public String tagInfo;

    @ColumnInfo
    public String tagId;
    @ColumnInfo
    public String technologies;
    @ColumnInfo
    public String ATQA;
    @ColumnInfo
    public String SAK;
    @ColumnInfo
    public int maxTransceiveLength;
    @ColumnInfo
    public int timeout;

    @ColumnInfo
    public String name;

}
