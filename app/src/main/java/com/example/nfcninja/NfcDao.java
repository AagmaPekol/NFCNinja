package com.example.nfcninja;

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

    @Insert
    public void insertAll(DBNfc... DBNFC);

    @Delete
    public void delete(DBNfc dbNfc);
}
