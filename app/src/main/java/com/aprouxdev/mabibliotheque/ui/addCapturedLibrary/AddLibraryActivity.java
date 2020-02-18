package com.aprouxdev.mabibliotheque.ui.addCapturedLibrary;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.tools.camera.CameraSource;
import com.aprouxdev.mabibliotheque.tools.camera.CameraSourcePreview;
import com.aprouxdev.mabibliotheque.tools.camera.GraphicOverlay;
import com.aprouxdev.mabibliotheque.tools.ocr.OcrDetectorProcessor;
import com.aprouxdev.mabibliotheque.tools.ocr.OcrGraphic;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProviders;

public class AddLibraryActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "AddLibraryActivity";


    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";
    // Constants used for orientation control
    private static final int LANDSCAPE = 1;
    private static final int PORTRAIT = 0;
    private static final int OUT = -1;

    // UI Vars
    private TextView tvInstructionText;
    private CameraSourcePreview cameraPreview;
    private GraphicOverlay graphicOverlay;
    private TextView tvCountDown;
    private RelativeLayout orientationLayout;
    private ImageButton captureButton;
    private Button finishButton;

    // DATA VARS
    private CameraSource cameraSource;
    private ScaleGestureDetector scaleGestureDetector;
    private TextRecognizer textRecognizer;
    private AddLibraryViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_library);
        Log.d(TAG, "onCreate: ");

        viewModel = ViewModelProviders.of(this).get(AddLibraryViewModel.class);
        setupViews();
        setupActionBar();
        setupCamera();
        setupGestureDetectors();
        observeOrientation();
        setupListeners();

    }


    private void setupViews() {
        tvInstructionText = findViewById(R.id.tvInstructionText);
        cameraPreview = findViewById(R.id.cameraPreview);
        graphicOverlay = findViewById(R.id.graphicOverlay);
        tvCountDown = findViewById(R.id.tvCountDown);
        captureButton = findViewById(R.id.addLibraryCaptureButton);
        finishButton = findViewById(R.id.addLibraryFinishButton);
        orientationLayout = findViewById(R.id.orientationLayout);
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupCamera() {
        // Set good defaults for capturing text.
        boolean autoFocus = true;
        boolean useFlash = false;

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }
    }

    private void setupGestureDetectors() {
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupListeners() {
        captureButton.setOnClickListener(this);
        finishButton.setOnClickListener(this);
    }

    private void observeOrientation() {
        SensorManager sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;
        sensorManager.registerListener(new SensorEventListener() {
            int orientation = OUT;

            @Override
            public void onSensorChanged(SensorEvent event) {
                if(event.values[1] < 6.5 && event.values[1] > -6.5){
                    if(orientation != LANDSCAPE){
                        orientationUpdated(LANDSCAPE);
                    }
                    orientation = LANDSCAPE;
                } else {
                    if (orientation != PORTRAIT){
                        orientationUpdated(PORTRAIT);
                    }
                    orientation = PORTRAIT;
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        }, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
    }

    private void orientationUpdated(int orientation) {
        if (orientation == PORTRAIT){
            orientationLayout.setVisibility(View.VISIBLE);
        } else {
            orientationLayout.setVisibility(View.GONE);
        }
    }


    // -------------------------
    //     CAPTURE ON GOING
    // -------------------------

    /**
     * Start a five seconds capture
     * Save all texts every half seconds : 10 List<String>
     * At the end analyze all list and save the books
     */
    private void startCountDownCapture() {
        // freezeCaptureButton()
        captureButton.setEnabled(false);
        // showCountdown()
        tvCountDown.setVisibility(View.VISIBLE);
        //TODO showMessageText()

        new CountDownTimer(5000, 500) {
            @Override
            public void onTick(long millisUntilFinished) {
                //TODO updateCountDown TextView
                updateCountDownTextView(millisUntilFinished);
                //TODO each 500millis viewModel.startCaptureSession
                Log.d(TAG, "onTick: millis until finished " + millisUntilFinished);
                Set<OcrGraphic> graphicsOverlay = graphicOverlay.getGraphics();
                if (graphicsOverlay.size() != 0){
                    viewModel.saveGraphicsTexts(graphicsOverlay);
                }
            }

            @Override
            public void onFinish() {

                //TODO viewModel.analyzeAllText()
                viewModel.analyzeAllText();
                // !freezeCaptureButton()
                captureButton.setEnabled(true);
                // hideCountdown()
                tvCountDown.setVisibility(View.GONE);

                //TODO changeMessageText()
            }
        }.start();


    }

    private void updateCountDownTextView(long millisUntilFinished) {
        int millisInSecond =  (int)(millisUntilFinished / 1000);
        tvCountDown.setText(String.valueOf(millisInSecond));
    }


    // ---------------------
    //        ACTIONS
    // ---------------------

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case (R.id.addLibraryCaptureButton):
                startCountDownCapture();
                break;
            case (R.id.addLibraryFinishButton):
                // TODO finish scanning
                break;
        }
    }


    // ---------------------
    //    CAMERA METHODS
    // ---------------------
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = this;

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(graphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        return b || super.onTouchEvent(e);
    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getApplicationContext();

        // Create the TextRecognizer
        textRecognizer = new TextRecognizer.Builder(context).build();
        // Set the TextRecognizer's Processor.
        textRecognizer.setProcessor(new OcrDetectorProcessor(graphicOverlay));
        // Check if the TextRecognizer is operational.
        if (!textRecognizer.isOperational()) {
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Create the cameraSource using the TextRecognizer.
        cameraSource =
                new CameraSource.Builder(getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(15.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO : null)
                        .build();

    }

    /**
     * Restarts the camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (cameraPreview != null) {
            cameraPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraPreview != null) {
            cameraPreview.release();
        }
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus,false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            createCameraSource(autoFocus, useFlash);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (cameraSource != null) {
            try {
                cameraPreview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }




    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            if (cameraSource != null) {
                cameraSource.doZoom(detector.getScaleFactor());
            }
        }
    }

}
