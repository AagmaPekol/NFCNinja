package com.example.nfcninja;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.util.List;

public class nikolas extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nikolas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private int[] array = {1,2,3,4};

    //Create instance of database
    AppDatabase db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "AppDatabase").build();

    //Create instance of Data access object
    NfcDao nfcDao = db.nfcDao();

    //returns list of all entities
    List<DBNfc> tags = nfcDao.getAll();
    List<DBNfc> tagsOne = nfcDao.loadAllByIds(array);

    public void ReadNfc(int primaryKey){
        DBNfc nfcTag = nfcDao.getTagById(primaryKey);
        String nfcInfo = nfcTag.tagInfo;
        String nfcId = nfcTag.tagId;
        String nfcTech = nfcTag.technologies;
        String nfcATQA = nfcTag.ATQA;
        int nfcMaxTransLength = nfcTag.maxTransceiveLength;
        int nfcTimeout = nfcTag.timeout;
    }




}