package com.example.nfcninja;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NfcaTagDao {
    @Query("SELECT * FROM NfcaTag")
    List<NfcaTag> getALL();

    @Query("SELECT * FROM NfcaTag WHERE Name LIKE :name")
    NfcaTag findByName(String name);

    @Insert
    void insertAll(NfcaTag... NfcaTags);

    @Delete
    void delete (NfcaTag nfcaTag);
}
