package com.example.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btnGetID, btnScanQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGetID = findViewById(R.id.btnGetID);
        btnScanQR = findViewById(R.id.btnScanQR);

        //Display Android ID on button click
        btnGetID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Android ID
                String Id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                Toast.makeText(MainActivity.this, "Android Id: " + Id, Toast.LENGTH_LONG).show();
            }
        });

        //Scan QR code on button click
        btnScanQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CameraPreviewActivity.class);
                startActivity(intent);
            }
        });
    }

}