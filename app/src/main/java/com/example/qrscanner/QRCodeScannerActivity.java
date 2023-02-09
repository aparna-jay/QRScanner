package com.example.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import java.io.IOException;


public class QRCodeScannerActivity extends AppCompatActivity {

    SurfaceView scannerView;
    TextView txtQrCodeContent, txtQRText;
    private BarcodeDetector qrCodeDetector;
    private CameraSource cameraSource;
    private static final int REQUEST_CAMERA_PERMISSION = 201;
    Button btnAction;
    String intentData = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_q_r_code_scanner);

        //Call initViews method to initialize QR code scanner view
        initViews();
    }

    //Method to initialize QR Code Scanner View
    private void initViews() {

        txtQrCodeContent = findViewById(R.id.txtQRCodeContent);
        txtQRText = findViewById(R.id.txtQRText);
        scannerView = findViewById(R.id.scannerView);
        btnAction = findViewById(R.id.btnAction);

        btnAction.setVisibility(View.INVISIBLE);
        txtQRText.setVisibility(View.INVISIBLE);

        //Open URL on button click
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intentData.length() > 0) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(intentData)));
                }
            }
        });
    }

    //Method to initialize camera resource and scan QR code
    private void initialiseDetectorsAndSources() {

        //Toast message displaying scanner initialization
        Toast.makeText(getApplicationContext(), getString(R.string.startScanner), Toast.LENGTH_SHORT).show();

        //Build new qr code detector
        qrCodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        //Build new camera source
        cameraSource = new CameraSource.Builder(this, qrCodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true)
                .build();


        //Get permission to use camera and open camera resource
        scannerView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(QRCodeScannerActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(scannerView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(QRCodeScannerActivity.this, new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        //Process qr code content
        qrCodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                //Release camera resource to prevent memory leakages
                Toast.makeText(getApplicationContext(), getString(R.string.stopScanner), Toast.LENGTH_SHORT).show();
            }

            //Take action on qr code content
            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {

                final SparseArray<Barcode> codeContent = detections.getDetectedItems();

                if (codeContent.size() != 0) {
                    txtQrCodeContent.post(new Runnable() {
                        @Override
                        public void run() {

                            //If qr code contains URL
                            if (codeContent.valueAt(0).url != null) {
                                btnAction.setVisibility(View.VISIBLE);
                                txtQRText.setVisibility(View.INVISIBLE);
                                btnAction.setText(getString(R.string.openURL));
                                intentData = codeContent.valueAt(0).displayValue;
                                txtQrCodeContent.setText(intentData);

                            } else {
                                btnAction.setVisibility(View.INVISIBLE);
                                txtQRText.setVisibility(View.VISIBLE);
                                intentData = codeContent.valueAt(0).displayValue;
                                txtQrCodeContent.setText(intentData);
                                txtQRText.setText(intentData);
                            }
                        }
                    });
                }
            }
        });
    }

    //Release camera resource  on activity pause
    @Override
    protected void onPause() {
        super.onPause();
        cameraSource.release();
    }

    //Initialize qr code scanner on activity resume
    @Override
    protected void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }
}