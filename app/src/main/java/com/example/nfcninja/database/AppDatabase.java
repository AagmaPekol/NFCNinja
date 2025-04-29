package com.example.nfcninja.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {DBNfc.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract  NfcDao nfcDao();

    private static volatile AppDatabase INSTANCE;
    //Singleton pattern
    //Ensures that only one instance of the database is created
    public static AppDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (AppDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "clothing_database").build();
                }
            }
        }
        return INSTANCE;
    }
}
