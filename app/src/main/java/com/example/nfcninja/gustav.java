package com.example.nfcninja;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Arrays;

public class gustav extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private String[][] techListsArray;
    private TextView nfcInfoTextView;
    private boolean readerActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gustav);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Button button = (Button) findViewById(R.id.readerButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the ReaderButton");

                if (!readerActive) {
                    button.setText("Stop reading");
                } else {
                    button.setText("Start reading");
                }
                readerActive = !readerActive;
                Log.d("BUTTONS", "readerActive: " + readerActive);
            }
        });

//        if (readerActive) {
//            readTag();
//        }

        nfcInfoTextView = findViewById(R.id.showReadNfc);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter ==null) {
            Log.d("NFC","NFC is not available on this device.");
            Toast.makeText(this, "NFC is not available on this device.", Toast.LENGTH_LONG).show();
            button.setEnabled(false); // Disable the button if NFC is not available
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
            nfcInfoTextView.setText("Tap the button to start reading (NfcA).");
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

    // gusNOTE: I don't know what the FUCK this is, so I made chat write comments
    private void processTag(Tag tag) {
        StringBuilder tagInfo = new StringBuilder();

        tagInfo.append("Student Card Tag Found!\n");
        tagInfo.append("Tag ID (Hex): ").append(bytesToHexString(tag.getId())).append("\n");
        // The `tag.getId()` method returns the unique identifier of the NFC tag as a byte array. We convert it to a hexadecimal string for easier readability.

        String[] techList = tag.getTechList();
        tagInfo.append("Technologies: ").append(Arrays.toString(techList)).append("\n");
        // `tag.getTechList()` returns an array of strings representing the technologies supported by the discovered tag. For an NfcA card, this list will contain "android.nfc.tech.NfcA".

        for (String tech : techList) {
            if (tech.equals(NfcA.class.getName())) {
                NfcA nfcA = NfcA.get(tag);
                if (nfcA != null) {
                    tagInfo.append("\nNfcA Information:\n");
                    tagInfo.append("  ATQA (Answer to Request, Type A): ").append(bytesToHexString(nfcA.getAtqa())).append("\n");
                    tagInfo.append("  SAK (Select Acknowledge): ").append(String.format("%02x", nfcA.getSak())).append("\n");
                    tagInfo.append("  Maximum Transceive Length: ").append(nfcA.getMaxTransceiveLength()).append(" bytes\n");
                    tagInfo.append("  Timeout: ").append(nfcA.getTimeout()).append(" ms\n");
                    // Here, we specifically check if the "android.nfc.tech.NfcA" technology is present in the tag's technology list.
                    // If it is, we get an `NfcA` object from the `Tag`. This object allows us to access NfcA-specific properties of the tag, such as:
                    // - `getAtqa()`: Returns the ATQA (Answer to Request, Type A) bytes. This is part of the initial anti-collision and identification process.
                    // - `getSak()`: Returns the SAK (Select Acknowledge) byte. This provides information about the tag's capabilities and conformance.
                    // - `getMaxTransceiveLength()`: Returns the maximum number of bytes that can be sent to and received from the tag in a single transceive operation.
                    // - `getTimeout()`: Returns the current timeout value in milliseconds used for transceive operations.
                    // We then format these values and append them to the `tagInfo` StringBuilder.
                }
            }
        }

        nfcInfoTextView.setText(tagInfo.toString()); // Finally, we set the text of the `nfcInfoTextView` to display the collected tag information.
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


//    private void readTag(Intent intent) {
//
//    }

}