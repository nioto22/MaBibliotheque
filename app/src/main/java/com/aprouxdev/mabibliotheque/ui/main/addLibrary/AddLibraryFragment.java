package com.aprouxdev.mabibliotheque.ui.main.addLibrary;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aprouxdev.mabibliotheque.R;
import com.aprouxdev.mabibliotheque.tools.camera.CameraSourcePreview;
import com.aprouxdev.mabibliotheque.tools.camera.GraphicOverlay;

import java.util.Objects;

public class AddLibraryFragment extends Fragment {
    private static final String TAG = "AddLibraryFragment";

    private AddLibraryViewModel mViewModel;

    public static final String ARRAY_LIST_INTENT_EXTRA = "ARRAY_LIST_INTENT_EXTRA";

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

    public static AddLibraryFragment newInstance() {
        return new AddLibraryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.add_library_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(AddLibraryViewModel.class);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViews(view);
        observeOrientation();
    }

    private void setupViews(View view) {
        tvInstructionText = view.findViewById(R.id.tvInstructionText);
        cameraPreview = view.findViewById(R.id.cameraPreview);
        graphicOverlay = view.findViewById(R.id.graphicOverlay);
        tvCountDown = view.findViewById(R.id.tvCountDown);
        captureButton = view.findViewById(R.id.captureButton);
        finishButton = view.findViewById(R.id.finishButton);
        orientationLayout = view.findViewById(R.id.orientationLayout);
    }

    private void observeOrientation() {
        SensorManager sensorManager = (SensorManager) Objects.requireNonNull(getContext()).getSystemService(Context.SENSOR_SERVICE);
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

}
