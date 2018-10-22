/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.samples.vision.ocrreader;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;


public class OcrDetectorProcessor implements Detector.Processor<TextBlock> {
    private GraphicOverlay<OcrGraphic> graphicOverlay;
    Context context;

    public OcrDetectorProcessor(Context contextTemp, GraphicOverlay<OcrGraphic> ocrGraphicOverlay) {
        graphicOverlay = ocrGraphicOverlay;
        context = contextTemp;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        graphicOverlay.clear();
        final SparseArray<TextBlock> items = detections.getDetectedItems();
        if (items.size() != 0) {
//            textView.post(new Runnable() {
//                @Override
//                public void run() {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++) {
//                TextBlock item = items.valueAt(i);
//                stringBuilder.append(item.getValue());
//                stringBuilder.append("\n");
//                OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
//                graphicOverlay.add(graphic);

                TextBlock item = items.valueAt(i);
                if (item != null && item.getValue() != null) {
                    Log.d("Processor", "Text detected! " + item.getValue());
//                    OcrGraphic graphic = new OcrGraphic(graphicOverlay, item);
                    OcrGraphic graphic;
                    if(item.getValue().contains("for")){
//                        graphic = new OcrGraphic(graphicOverlay, item);
                    }else{
//                        graphic = new OcrGraphic(graphicOverlay, item);
                    }
//                    graphicOverlay.add(graphic);
                }
            }

           // textView.setText(stringBuilder.toString());
//                }
//            });
        }
    }


    @Override
    public void release() {
        graphicOverlay.clear();
    }
}