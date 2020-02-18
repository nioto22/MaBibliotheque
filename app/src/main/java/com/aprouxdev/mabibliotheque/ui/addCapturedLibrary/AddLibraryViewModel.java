package com.aprouxdev.mabibliotheque.ui.addCapturedLibrary;

import android.util.Log;

import com.aprouxdev.mabibliotheque.tools.ocr.OcrGraphic;
import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.Set;

import androidx.lifecycle.ViewModel;

public class AddLibraryViewModel extends ViewModel {
    private static final String TAG = "AddLibraryViewModel";

    private ArrayList<ArrayList<String>> allTextArrays = new ArrayList<>();

    public void saveGraphicsTexts(final Set<OcrGraphic> graphics) {
        ArrayList<String> textArray = new ArrayList<>();
        for (OcrGraphic graphic : graphics) {
            TextBlock text = null;
            if (graphic != null) {
                text = graphic.getTextBlock();
                if (text != null && text.getValue() != null) {
                    textArray.add(text.getValue());
                    //Log.d(TAG, "saveTheText: Text is " + text.getValue());
                } else {
                    Log.d(TAG, "text data is null");
                }
            } else {
                Log.d(TAG, "no text detected");
            }
        }
        allTextArrays.add(textArray);
    }

    public void analyzeAllText() {

    }
}
