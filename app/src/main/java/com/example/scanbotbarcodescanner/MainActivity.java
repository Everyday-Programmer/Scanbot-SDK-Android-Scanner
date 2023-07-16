package com.example.scanbotbarcodescanner;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.MessageFormat;
import java.util.ArrayList;

import io.scanbot.sdk.AspectRatio;
import io.scanbot.sdk.SdkLicenseError;
import io.scanbot.sdk.barcode.BarcodeDetectorFrameHandler;
import io.scanbot.sdk.barcode.ScanbotBarcodeDetector;
import io.scanbot.sdk.barcode.entity.BarcodeScanningResult;
import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDK;
import io.scanbot.sdk.camera.CameraOpenCallback;
import io.scanbot.sdk.camera.FrameHandlerResult;
import io.scanbot.sdk.ui.camera.FinderOverlayView;
import io.scanbot.sdk.ui.camera.ScanbotCameraXView;

public class MainActivity extends AppCompatActivity {

    private ScanbotCameraXView cameraView;
    private BarcodeDetectorFrameHandler barcodeDetectorFrameHandler;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Camera permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = findViewById(R.id.camera);


        cameraView.setCameraOpenCallback(new CameraOpenCallback() {
            @Override
            public void onCameraOpened() {
                cameraView.postDelayed(() -> {
                    cameraView.continuousFocus();
                }, 300);
            }
        });
        final ScanbotBarcodeDetector barcodeDetector = new ScanbotBarcodeScannerSDK(this).createBarcodeDetector();

        barcodeDetectorFrameHandler = BarcodeDetectorFrameHandler.attach(cameraView, barcodeDetector);
        barcodeDetectorFrameHandler.setDetectionInterval(100);
        barcodeDetectorFrameHandler.addResultHandler(new BarcodeDetectorFrameHandler.ResultHandler() {
            @Override
            public boolean handle(@NonNull FrameHandlerResult<? extends BarcodeScanningResult, ? extends SdkLicenseError> frameHandlerResult) {
                final BarcodeScanningResult result = ((FrameHandlerResult.Success<BarcodeScanningResult>) frameHandlerResult).getValue();
                if (result != null) {
                    barcodeDetectorFrameHandler.setEnabled(false);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
                            View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.result_bottomsheet, null, false);
                            bottomSheetDialog.setContentView(view);
                            bottomSheetDialog.show();

                            ImageView imageView = view.findViewById(R.id.imageView);
                            imageView.setImageBitmap(result.getBarcodeItems().get(0).getImage());

                            TextView format = view.findViewById(R.id.formatTV);
                            TextView data = view.findViewById(R.id.dataTV);

                            format.setText(MessageFormat.format("Code Format: {0}", result.getBarcodeItems().get(0).getBarcodeFormat().name()));
                            data.setText(MessageFormat.format("Text: {0}", result.getBarcodeItems().get(0).getText()));

                            bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    barcodeDetectorFrameHandler.setEnabled(true);
                                }
                            });
                        }
                    });
                }
                return false;
            }
        });

        FinderOverlayView finderOverlayView = findViewById(R.id.my_finder_overlay);
        final ArrayList<AspectRatio> aspectRatios = new ArrayList<>();
        aspectRatios.add(new AspectRatio(200, 200) );
        finderOverlayView.setRequiredAspectRatios(aspectRatios);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.startPreview();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stopPreview();
    }
}