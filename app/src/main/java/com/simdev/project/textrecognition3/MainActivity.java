package com.simdev.project.textrecognition3;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.samples.vision.ocrreader.OcrGraphic;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    View crossHair;
    View crossHairBox;
    GraphicOverlay<OcrGraphic> graphicOverlay;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    Spinner spinner;
    ArrayAdapter<String> adapter;
    ArrayList<String> arraySpinner = new ArrayList<>();
    ProgressBar progressBar;
    ;

    Button captureButton;
    Button tutorialButton;
    Button finishButton;
    Button challengeBtn;
    int currentChallengeNum = 1;

    boolean screenCapture = false;
    boolean challengeComplete = false;
    boolean dialogShown = false;
    boolean tutorial = false;

    TextView info1;
    private ViewPager pager = null;
    private MainPagerAdapter pagerAdapter = null;

    CountDownTimer countDownTimer;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);
        crossHair = (View) findViewById(R.id.crossHair);
        crossHairBox = (View) findViewById(R.id.crossHairBox);
        captureButton = (Button) findViewById(R.id.capture);
        finishButton = (Button) findViewById(R.id.finishTutorialBtn);
        tutorialButton = (Button) findViewById(R.id.tutorialBtn);
        challengeBtn = (Button) findViewById(R.id.challengebtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        graphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);

        info1 = (TextView) findViewById(R.id.info1);
//        info1.setText("TESTING");
//

        pagerAdapter = new MainPagerAdapter();
        pager = (ViewPager) findViewById (R.id.view_pager);
        pager.setAdapter (pagerAdapter);

        // Create an initial view to display; must be a subclass of FrameLayout.
        Activity context = this;
        LayoutInflater inflater = context.getLayoutInflater();
        FrameLayout v0 = (FrameLayout) inflater.inflate (R.layout.page, null);
        View v1 =  new View(getApplicationContext());
        pagerAdapter.addView (v0);
        pagerAdapter.addView (v1);
        pager.setCurrentItem (pagerAdapter.getItemPosition (v1), true);

        pagerAdapter.notifyDataSetChanged();

//once picture captured, move to first page and show summary of items
        //ISSUE:: setting text on page.xml (info1)

        progressBar.setVisibility(View.INVISIBLE);

        if(!tutorial){
            crossHair.setVisibility(View.INVISIBLE);
            crossHairBox.setVisibility(View.INVISIBLE);
            challengeBtn.setVisibility(View.INVISIBLE);
            finishButton.setVisibility(View.INVISIBLE);
        }

        spinner = (Spinner) findViewById(R.id.spinner);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector Dependencies are not yet available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    RequestCameraPermissionID);

                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
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
            cameraView.setFocusable(true);

            progressBar.setProgress(0);
            countDownTimer = new CountDownTimer(1500,100) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.d("Log_tag", "Tick of Progress"+ ((1500-millisUntilFinished)/15));
                    progressBar.setProgress((int)((1500-millisUntilFinished)/15));
                    captureButton.setEnabled(false);
                    challengeBtn.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    if(!challengeComplete){
                        captureButton.setEnabled(true);
                    }
                    challengeBtn.setEnabled(true);
                }
            };

            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenCapture = true;
                    progressBar.setVisibility(View.VISIBLE);
                    if(progressBar.getProgress() == 0){
                        progressBar.setProgress(0);
                        countDownTimer.start();
                    }
                }
            });


            tutorialButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tutorial = true;
                    crossHair.setVisibility(View.VISIBLE);
                    crossHairBox.setVisibility(View.VISIBLE);
                    challengeBtn.setVisibility(View.VISIBLE);
                    tutorialButton.setVisibility(View.INVISIBLE);
                    finishButton.setVisibility(View.VISIBLE);

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setMessage("Check the Challenges screen for a list of different challenges " +
                            "to be completed in chronological order");
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });

            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tutorial = false;
                    crossHair.setVisibility(View.INVISIBLE);
                    crossHairBox.setVisibility(View.INVISIBLE);
                    challengeBtn.setVisibility(View.INVISIBLE);
                    tutorialButton.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.INVISIBLE);
                }
            });


            challengeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(MainActivity.this, ChallengeActivity.class);
                    intent.putExtra("CHALLENGE_NO", currentChallengeNum + "");
                    MainActivity.this.startActivityForResult(intent, 1);
                }
            });




            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    graphicOverlay.clear();
                    arraySpinner.clear();
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                int crossHairLocation[] = new int[2];
                                crossHairBox.getLocationOnScreen(crossHairLocation);

//                                StringBuilder stringBuilder = new StringBuilder();
                                if (challengeComplete && !dialogShown) {
                                    captureButton.setEnabled(false);
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.INVISIBLE);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    if(currentChallengeNum != 8){
                                        builder.setMessage("Check Challenges screen for next challenge.")
                                                .setTitle("Challenge " + (currentChallengeNum - 1) + " complete!");
                                    }else{
                                        builder.setMessage("Congratulation! All implemented challenges completed." +
                                                " Await application update for more challenges to come...");
                                    }
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            Intent intent = new Intent(MainActivity.this, ChallengeActivity.class);
                                            intent.putExtra("CHALLENGE_NO", currentChallengeNum + "");
                                            MainActivity.this.startActivityForResult(intent, 1);
                                        }
                                    });

                                    AlertDialog dialog = builder.create();

                                    dialog.show();
                                    dialogShown = true;
                                }
                                for (int i = 0; i < items.size(); i++) {

                                    TextBlock item = items.valueAt(i);

                                    if (item != null && item.getValue() != null && screenCapture && !challengeComplete) {

                                        if ((item.getBoundingBox().top >= crossHairLocation[1] - 130 &&
                                                item.getBoundingBox().bottom <= crossHairLocation[1] + 150) || !tutorial)  {
                                            OcrGraphic graphic = new OcrGraphic(graphicOverlay, null, false,false, false, false);

                                            boolean integerFound = item.getValue().startsWith("int");
                                            boolean doubleFound = item.getValue().startsWith("double");
                                            boolean floatFound = item.getValue().startsWith("float");
                                            boolean stringFound = item.getValue().startsWith("String");
                                            boolean charFound = item.getValue().startsWith("char");
                                            boolean booleanFound = item.getValue().startsWith("boolean");

                                            if (integerFound || doubleFound || floatFound || stringFound || charFound || booleanFound) {
                                                graphic = new OcrGraphic(graphicOverlay, item, false, false, false, true);
//                                                if (!arraySpinner.contains("Variable Declaration")) {
//                                                    arraySpinner.add("Variable Declaration");
//                                                }
                                                if(integerFound){
//                                                    info1.setText("Integer Found! - Scanned line: " + item.getValue());
                                                }

                                                if ((currentChallengeNum == 1 && (integerFound || doubleFound || floatFound)) && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                } else if ((currentChallengeNum == 2 && (stringFound || charFound)) && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                } else if ((currentChallengeNum == 3 && (booleanFound)) && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                }

                                            } else if (item.getValue().startsWith("if")) {
                                                graphic = new OcrGraphic(graphicOverlay, item, false, false, true, false);
//                                                if (!arraySpinner.contains("if Statement")) {
//                                                    arraySpinner.add("if Statement");
//                                                }
                                                if (currentChallengeNum == 4 && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                }

                                            } else if (item.getValue().startsWith("for") || (item.getValue().startsWith("while"))) {
                                                graphic = new OcrGraphic(graphicOverlay, item, false, true, false, false);
//                                                if (!arraySpinner.contains("for Loop")) {
//                                                    arraySpinner.add("for Loop");
//                                                }
                                                if ((currentChallengeNum == 5 && (item.getValue().startsWith("for"))) && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                } else if ((currentChallengeNum == 6 && (item.getValue().startsWith("while"))) && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                }

                                            } else if (item.getValue().startsWith("switch")) {
                                                graphic = new OcrGraphic(graphicOverlay, item, true, false, false, false);

                                                if (currentChallengeNum == 7 && tutorial) {
                                                    currentChallengeNum++;
                                                    challengeComplete = true;
                                                }
                                            }

                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    screenCapture = false;
                                                }
                                            }, 1500);
                                            //1.5 sec to find item
                                            graphicOverlay.add(graphic);
                                        }
                                    }
                                }
                                adapter.notifyDataSetChanged();
//                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String number = data.getStringExtra("challenge_no");
                currentChallengeNum = Integer.parseInt(number);
                Log.d("PICKING UP ON NUMBER ", currentChallengeNum + "");
                challengeComplete = false;
                captureButton.setEnabled(true);
                textView.setText("");
                dialogShown = false;
            }
        }
    }


}


// CURRENT BUG: selecting item in spinner is difficult since the adapter continuously set
// FOR SERIOUS GAMES: button to different screen to show count for variable dec, for loop, if statement and challenge
// -- compare item with array of foundCode. if the item isn't in that array, count++ and add item to array,
// if code found, make DING noise and either surround code with box or show a tick
// to avoid infinite count incrementation, have an array for each type and compare the text to whatever's in the array