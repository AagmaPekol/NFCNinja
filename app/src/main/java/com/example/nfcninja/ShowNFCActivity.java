package com.example.nfcninja;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nfcninja.database.*;

import com.example.nfcninja.database.AppDatabase;

import java.util.List;

public class ShowNFCActivity extends AppCompatActivity {

    private AppDatabase db;
    private NfcDao dbDao;

    private DBNfc nfcTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_nfcactivity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        db = AppDatabase.getDatabase(this);
        dbDao = db.nfcDao();

        new Thread(()-> {
            List<Integer> nfcIds = dbDao.getAllIds();

            for (int nfcId : nfcIds) {

                runOnUiThread(()-> {
                    Button spawnBtn = new Button(this);
                    spawnBtn.setText("NFC nr: " + nfcId);
                    spawnBtn.setId(nfcId);
                    spawnBtn.setBackgroundColor(Color.YELLOW);
                    spawnBtn.setTextColor(Color.BLACK);

                    int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
                    spawnBtn.setPadding(padding, padding, padding, padding);

                    spawnBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    layoutParams.setMargins(0, 100, 0, 0);

                    spawnBtn.setLayoutParams(layoutParams);
                    LinearLayout parentLayout = (LinearLayout) findViewById(R.id.button_container);
                    parentLayout.addView(spawnBtn);

                    spawnBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ShowNFCActivity.this, ReadNFCActivity.class);

                            

                            startActivity(intent);
                        }
                    });
                });

            }
        }).start();

    }
}