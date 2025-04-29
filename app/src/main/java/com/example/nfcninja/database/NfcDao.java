package com.example.nfcninja.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NfcDao {
    @Query("SELECT * FROM dbnfc")
    List<DBNfc> getAll();

    @Query("SELECT * FROM dbnfc WHERE uid IN (:nfcIds)")
    List<DBNfc> loadAllByIds(int[] nfcIds);

    @Query("SELECT * FROM dbnfc WHERE uid==:nfcId")
    public DBNfc getTagById(int nfcId);
    @Insert
    public void insert(DBNfc dbNfc);

    @Delete
    public void delete(DBNfc dbNfc);
}
