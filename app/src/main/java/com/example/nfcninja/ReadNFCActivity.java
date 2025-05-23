package com.example.nfcninja;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.nfcninja.database.*;

import java.util.Arrays;

public class ReadNFCActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private DBNfc nfcTag;

    private boolean readerActive = false;

    private AppDatabase db;
    private NfcDao dbDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_read_nfc);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        db = AppDatabase.getDatabase(this);
        dbDao = db.nfcDao();

        int nfcId = getIntent().getIntExtra("nfcId", -1);

        if (nfcId != -1) {
            new Thread(() -> {
                DBNfc nfcTagDB = dbDao.getTagById(nfcId);

                runOnUiThread(() -> {
                    if (nfcTagDB != null) {
                        TextView tagIdText = (TextView) findViewById(R.id.tagIdText);
                        tagIdText.setText(nfcTagDB.tagId);

                        TextView technologiesText = (TextView) findViewById(R.id.technologiesText);
                        technologiesText.setText(nfcTagDB.technologies);

                        TextView ATQAText = (TextView) findViewById(R.id.ATQAText);
                        ATQAText.setText(String.valueOf(nfcTagDB.ATQA + " bytes"));

                        TextView SAKText = (TextView) findViewById(R.id.SAKText);
                        SAKText.setText(String.valueOf(nfcTagDB.SAK + " bytes"));

                        TextView maxTransceiveLengthText = (TextView) findViewById(R.id.maxTransceiveLengthText);
                        maxTransceiveLengthText.setText(String.valueOf(nfcTagDB.maxTransceiveLength + " bytes"));

                        TextView timeoutText = (TextView) findViewById(R.id.timeoutText);
                        timeoutText.setText(String.valueOf(nfcTagDB.timeout + " ms"));
                    }
                });
            }).start();
        }

        Button saveToDBbtn = findViewById(R.id.saveToDB);
        saveToDBbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDatabase(nfcTag);
            }
        });

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter ==null) {
            Log.d("NFC","NFC is not available on this device.");
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show();
//            button.setEnabled(false); // Disable the button if NFC is not available
            return;
        }

        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Setup intent filters for NfcA technology
        IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
        intentFiltersArray = new IntentFilter[]{tech};
        techListsArray = new String[][]{new String[]{NfcA.class.getName()}};

        // use those intent filters to see if NfcA was discovered
        Intent receivedIntent = getIntent();
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(receivedIntent.getAction())) {
            Tag tag = receivedIntent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                processTag(tag);
            }
        } else {
            Log.d("Scanning","Scanning for tags... (NfcA).");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (readerActive && NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                processTag(tag);
            }
        }
    }

    private void processTag(Tag tag) {

        nfcTag = new DBNfc();
        nfcTag.tagId = bytesToHexString(tag.getId());
        TextView tagIdText = (TextView) findViewById(R.id.tagIdText);
        tagIdText.setText(nfcTag.tagId);

        nfcTag.technologies = Arrays.toString(tag.getTechList());
        TextView technologiesText = (TextView) findViewById(R.id.technologiesText);
        technologiesText.setText(nfcTag.technologies);

        NfcA nfcA = NfcA.get(tag);
        nfcTag.ATQA = bytesToHexString(nfcA.getAtqa());
        TextView ATQAText = (TextView) findViewById(R.id.ATQAText);
        ATQAText.setText(String.valueOf(nfcTag.ATQA + " bytes"));

        nfcTag.SAK = String.format("%02x", nfcA.getSak());
        TextView SAKText = (TextView) findViewById(R.id.SAKText);
        SAKText.setText(String.valueOf(nfcTag.SAK + " bytes"));

        nfcTag.maxTransceiveLength = nfcA.getMaxTransceiveLength();
        TextView maxTransceiveLengthText = (TextView) findViewById(R.id.maxTransceiveLengthText);
        maxTransceiveLengthText.setText(String.valueOf(nfcTag.maxTransceiveLength + " bytes"));

        nfcTag.timeout = nfcA.getTimeout();
        TextView timeoutText = (TextView) findViewById(R.id.timeoutText);
        timeoutText.setText(String.valueOf(nfcTag.timeout + " ms"));
        //nfcInfoTextView.setText(nfcTag.tagId);
    }

    private void saveToDatabase(DBNfc nfcTag) {
        if(nfcTag != null){
            new Thread(()-> {
                dbDao.insert(nfcTag);
                runOnUiThread(() -> Toast.makeText(this, "NFC tag saved to database", Toast.LENGTH_SHORT).show());
            }).start();
        } else {
            Toast.makeText(this, "NFC tag is null", Toast.LENGTH_SHORT).show();
            Log.d("NFC", "NFC tag is null");
        }
    }

    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    } // Helper function to convert a byte array to a hexadecimal string. This is commonly used for displaying tag IDs and other binary data in a human-readable format.

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null && readerActive) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, techListsArray);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
}