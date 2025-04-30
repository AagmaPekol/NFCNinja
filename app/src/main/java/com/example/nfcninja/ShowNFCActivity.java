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

    private LinearLayout parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_show_nfcactivity);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        parentLayout = findViewById(R.id.button_container);

        db = AppDatabase.getDatabase(this);
        dbDao = db.nfcDao();

        spawnButton();
    }

    private void spawnButton() {
        new Thread(() -> {
            List<Integer> nfcIds = dbDao.getAllIds();

            runOnUiThread(() -> {
                if (nfcIds.isEmpty()) {
                    Toast.makeText(this, "No NFC tags found", Toast.LENGTH_SHORT).show();
                } else {
                    for (int nfcId : nfcIds) {
                        Button spawnBtn = createButton(nfcId);
                        parentLayout.addView(spawnBtn);
                    }
                }
            });
        }).start();
    }

    //Thanks to Mehmet Abak for the buttons
    // https://abakmehmet.medium.com/how-to-create-a-button-in-java-android-programmatically-a6cf0eae1027
    private Button createButton(int nfcId) {
        Button spawnBtn = new Button(this);
        spawnBtn.setText("NFC nr: " + nfcId);
        spawnBtn.setId(nfcId);
        spawnBtn.setBackgroundColor(Color.YELLOW);
        spawnBtn.setTextColor(Color.BLACK);
        spawnBtn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics());
        spawnBtn.setPadding(padding, padding, padding, padding);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 50, 0, 0);
        spawnBtn.setLayoutParams(layoutParams);

        spawnBtn.setOnClickListener(v -> {
            Intent intent = new Intent(ShowNFCActivity.this, ReadNFCActivity.class);
            intent.putExtra("nfcId", nfcId);
            startActivity(intent);
        });

        return spawnBtn;
    }
}
