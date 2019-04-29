package com.simdev.project.textrecognition3;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.samples.vision.ocrreader.OcrGraphic;
import com.google.android.gms.samples.vision.ocrreader.ui.camera.GraphicOverlay;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    SharedPreferences mPrefs;

    SurfaceView cameraView;
    TextView textView;
    TextView methodFoundText;
    View crossHair;
    View crossHairBox;
    View linetest;
    GraphicOverlay<OcrGraphic> graphicOverlay;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;

    ProgressBar progressBar;
    ProgressBar infoProgress;

    Button captureButton;
    Button tutorialButton;
    Button finishButton;
    Button challengeBtn;
    int captureCount = 0;
    int currentChallengeNum = 1;

    boolean screenCapture = false;
    boolean challengeComplete = false;
    boolean dialogShown = false;
    boolean methodDialogShown = false;
    boolean methodNameSelected = false;
    boolean tutorial = false;
    boolean appStart = true;
    boolean switchPage = false;
    boolean publicOrPrivate = false;

    String methodName;

    TextView info1;
    private ViewPager pager = null;
    private MainPagerAdapter pagerAdapter = null;

    AlertDialog methodDialog;

    CountDownTimer countDownTimer;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    this,
                    PERMISSIONS_STORAGE,
                    1
            );
        }

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
        methodFoundText = (TextView) findViewById(R.id.methodFoundText);
        crossHair = (View) findViewById(R.id.crossHair);
        crossHairBox = (View) findViewById(R.id.crossHairBox);

        linetest = (View) findViewById(R.id.linetest);
        linetest.setVisibility(View.VISIBLE);

        captureButton = (Button) findViewById(R.id.capture);
        finishButton = (Button) findViewById(R.id.finishTutorialBtn);
        tutorialButton = (Button) findViewById(R.id.tutorialBtn);
        challengeBtn = (Button) findViewById(R.id.challengebtn);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        graphicOverlay = (GraphicOverlay<OcrGraphic>) findViewById(R.id.graphicOverlay);



        pagerAdapter = new MainPagerAdapter();
        pager = (ViewPager) findViewById(R.id.view_pager);
        pager.setAdapter(pagerAdapter);

        // Create an initial view to display; must be a subclass of FrameLayout.
        final Activity context = this;
        LayoutInflater inflater = context.getLayoutInflater();
        FrameLayout v0 = (FrameLayout) inflater.inflate(R.layout.page, null);
        View v1 = new View(getApplicationContext());
        pagerAdapter.addView(v0);
        pagerAdapter.addView(v1);
        pager.setCurrentItem(pagerAdapter.getItemPosition(v1), true);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    captureButton.setVisibility(View.INVISIBLE);
                    tutorialButton.setVisibility(View.INVISIBLE);
                    challengeBtn.setVisibility(View.INVISIBLE);
                    finishButton.setVisibility(View.INVISIBLE);
                    graphicOverlay.setVisibility(View.INVISIBLE);
                } else if (position == 1 && !tutorial) {
                    captureButton.setVisibility(View.VISIBLE);
                    tutorialButton.setVisibility(View.VISIBLE);
                    finishButton.setVisibility(View.INVISIBLE);
                    challengeBtn.setVisibility(View.INVISIBLE);
                    graphicOverlay.setVisibility(View.VISIBLE);
                } else if (position == 1 && tutorial) {
                    captureButton.setVisibility(View.VISIBLE);
                    tutorialButton.setVisibility(View.INVISIBLE);
                    finishButton.setVisibility(View.VISIBLE);
                    challengeBtn.setVisibility(View.VISIBLE);
                    graphicOverlay.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        infoProgress = (ProgressBar) v0.findViewById(R.id.infoProgress);
        info1 = (TextView) v0.findViewById(R.id.info1);
        info1.setText("This screen shows components of the code you scan. Click capture to identify different items and they will show up here." +
                "\nGreen Boxes show lines with variables declarations" +
                "\nRed Boxes show lines with iterations" +
                "\nBlue Boxes show lines with conditional statements" +
                "\nYellow Boxes show lines with switch statements");
        info1.setMovementMethod(new ScrollingMovementMethod());
        pagerAdapter.notifyDataSetChanged();

        //once picture captured, move to first page and show summary of items
        //ISSUE:: setting text on page.xml (info1)

        progressBar.setVisibility(View.INVISIBLE);
        infoProgress.setVisibility(View.INVISIBLE);

        if (!tutorial) {
            crossHair.setVisibility(View.INVISIBLE);
            crossHairBox.setVisibility(View.INVISIBLE);
            challengeBtn.setVisibility(View.INVISIBLE);
            finishButton.setVisibility(View.INVISIBLE);
        }




        //STORING CHALLENGE NUMBER
        mPrefs = context.getSharedPreferences("challenges", 0);
        int savedChallengeNum = mPrefs.getInt("challengeNum", 1);
        currentChallengeNum = savedChallengeNum;

        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Not operational - Detector Dependencies are not yet available");
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
            countDownTimer = new CountDownTimer(1500, 100) {

                @Override
                public void onTick(long millisUntilFinished) {
                    progressBar.setProgress((int) ((1500 - millisUntilFinished) / 15));
                    captureButton.setEnabled(false);
                    challengeBtn.setEnabled(false);
                }

                @Override
                public void onFinish() {
                    progressBar.setProgress(0);
                    progressBar.setVisibility(View.INVISIBLE);
                    infoProgress.setVisibility(View.INVISIBLE);
                    switchPage = true;

                    if (!challengeComplete) {
                        captureButton.setEnabled(true);
                    }
                    challengeBtn.setEnabled(true);
                    screenCapture = false;

                }
            };


            captureButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    screenCapture = true;
                    appStart = false;
                    progressBar.setVisibility(View.VISIBLE);
                    info1.setText("");
                    infoProgress.setVisibility(View.VISIBLE);
                    infoProgress.setProgress(50);

                    if (progressBar.getProgress() == 0) {
                        progressBar.setProgress(0);
                        countDownTimer.start();
                    }

                    captureCount++;
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

            methodFoundText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    methodName = null;
                    methodFoundText.setText("");
                    methodNameSelected = false;
                    publicOrPrivate = false;
                    Log.d("ITS BEEN CLICKED ", "");

                }
            });



            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    graphicOverlay.clear();
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("The Method Name is ", ""+methodName);

                                int crossHairLocation[] = new int[2];
                                crossHairBox.getLocationOnScreen(crossHairLocation);
                                Log.d("box location",crossHairLocation[1]+"");
                                final StringBuilder stringBuilder = new StringBuilder();
                                int variableCount = 0;
                                int ifCount = 0;
                                int elseCount = 0;
                                int forCount = 0;
                                int whileCount = 0;
                                int switchCount = 0;
                                if (challengeComplete && !dialogShown) {
                                    currentChallengeNum++;
                                    captureButton.setEnabled(false);
                                    progressBar.setProgress(0);
                                    progressBar.setVisibility(View.INVISIBLE);

                                    try {
                                        File myFile = new File("/sdcard/log.txt");
                                        myFile.createNewFile();
                                        FileOutputStream fOut = new FileOutputStream(myFile, true);
                                        OutputStreamWriter myOutWriter =
                                                new OutputStreamWriter(fOut);
                                        Date currentTime = Calendar.getInstance().getTime();
                                        myOutWriter.append("Challenge "+(currentChallengeNum-1) + ": "+captureCount +" attempts - time: "+currentTime+"\n\r");
                                        myOutWriter.close();
                                        fOut.close();
//                                        Log.d("****************",MainActivity.this.getFilesDir().getAbsolutePath()+"");

                                    }
                                    catch (Exception e) {
                                        Log.e("Exception", "File write failed: " + e.toString());
                                    }
                                    captureCount = 0;


                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                                    if (currentChallengeNum != 10) {
                                        builder.setMessage("Check Challenges screen for next challenge.")
                                                .setTitle("Challenge " + (currentChallengeNum - 1) + " complete!");
                                        SharedPreferences.Editor mEditor = mPrefs.edit();
                                        mEditor.putInt("challengeNum", currentChallengeNum).commit();
                                    } else {
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

                                    for (Text line : item.getComponents()) {

                                        if (line != null && line.getValue() != null && screenCapture && !challengeComplete) {
                                            // - 130    +150   for xiaomi
                                            if ((line.getBoundingBox().top >= crossHairLocation[1] - 200 &&
                                                    line.getBoundingBox().bottom <= crossHairLocation[1] -50) || !tutorial) {
                                                OcrGraphic graphic = new OcrGraphic(graphicOverlay, null, false, false, false, false);

                                                boolean integerFound = line.getValue().startsWith("int");
                                                boolean doubleFound = line.getValue().startsWith("double");
                                                boolean floatFound = line.getValue().startsWith("float");
                                                boolean stringFound = line.getValue().startsWith("String");
                                                boolean charFound = line.getValue().startsWith("char");
                                                boolean booleanFound = line.getValue().startsWith("boolean");
                                                boolean arrayFound = line.getValue().startsWith("Array");

                                                boolean ifFound = line.getValue().startsWith("if");
                                                boolean elseFound = line.getValue().startsWith("else") || line.getValue().startsWith("}else")
                                                        || line.getValue().startsWith("} else");
                                                boolean forFound = line.getValue().startsWith("for");
                                                boolean whileFound = line.getValue().startsWith("while");
                                                boolean switchFound = line.getValue().startsWith("switch");
                                                publicOrPrivate = line.getValue().startsWith("public") || line.getValue().startsWith("private")
                                                        || line.getValue().startsWith("public static") || line.getValue().startsWith("private static");

                                                //VARIABLE DECLARATIONS
                                                if (integerFound || doubleFound || floatFound || stringFound || charFound || booleanFound || arrayFound || publicOrPrivate) {
                                                    graphic = new OcrGraphic(graphicOverlay, line, false, false, false, true);

                                                    String[] sp = line.getValue().split(" ");
                                                    if (sp.length >= 2) {
                                                        if (sp[0].contains("Array") && sp[0].length() < 8) {
                                                            sp[0] = "Array";
                                                        }
                                                        stringBuilder.append(" - variable type: " + sp[0] + ", variable name: " + sp[1] + "\n");
                                                        variableCount++;
                                                    }

                                                    if(publicOrPrivate) {
                                                        try {
                                                            switch (sp[1]) {
                                                                case "int":
                                                                    integerFound = true;
                                                                    break;
                                                                case "double":
                                                                    doubleFound = true;
                                                                    break;
                                                                case "float":
                                                                    floatFound = true;
                                                                    break;
                                                                case "String":
                                                                    stringFound = true;
                                                                    break;
                                                                case "char":
                                                                    charFound = true;
                                                                    break;
                                                                case "boolean":
                                                                    arrayFound = true;
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                            switch (sp[2]) {
                                                                case "int":
                                                                    integerFound = true;
                                                                    break;
                                                                case "double":
                                                                    doubleFound = true;
                                                                    break;
                                                                case "float":
                                                                    floatFound = true;
                                                                    break;
                                                                case "String":
                                                                    stringFound = true;
                                                                    break;
                                                                case "char":
                                                                    charFound = true;
                                                                    break;
                                                                case "boolean":
                                                                    arrayFound = true;
                                                                    break;
                                                                default:
                                                                    break;
                                                            }
                                                        }catch(ArrayIndexOutOfBoundsException ex){

                                                        }
                                                    }
                                                    if ((currentChallengeNum == 1 && (integerFound || doubleFound || floatFound)) && tutorial) {
                                                        challengeComplete = true;
                                                    } else if ((currentChallengeNum == 2 && (stringFound || charFound)) && tutorial) {
                                                        challengeComplete = true;
                                                    } else if ((currentChallengeNum == 3 && (booleanFound)) && tutorial) {
                                                        challengeComplete = true;
                                                    }

                                                    // IF CONDITIONS
                                                } else if (ifFound) {
                                                    graphic = new OcrGraphic(graphicOverlay, line, false, false, true, false);
                                                    ifCount++;
                                                    if (currentChallengeNum == 4 && tutorial) {
                                                        challengeComplete = true;
                                                    }

                                                } else if(elseFound){
                                                    graphic = new OcrGraphic(graphicOverlay, line, false, false, true, false);
                                                    elseCount++;
                                                    if(currentChallengeNum == 8 && tutorial){
                                                        challengeComplete = true;
                                                    }

                                                    //LOOPS
                                                }else if (forFound || whileFound) {
                                                    graphic = new OcrGraphic(graphicOverlay, line, false, true, false, false);

                                                    if (forFound) {
                                                        forCount++;
                                                        if (currentChallengeNum == 5 && tutorial) {
                                                            challengeComplete = true;
                                                        }
                                                    }

                                                    if (whileFound) {
                                                        whileCount++;
                                                        if (currentChallengeNum == 6 && tutorial) {
                                                            challengeComplete = true;
                                                        }
                                                    }

                                                    //SWITCH STATEMENT
                                                } else if (switchFound) {
                                                    graphic = new OcrGraphic(graphicOverlay, line, true, false, false, false);
                                                    switchCount++;
                                                    if (currentChallengeNum == 7 && tutorial) {
                                                        challengeComplete = true;
                                                    }
                                                } if(currentChallengeNum == 9 && tutorial){
                                                    String[] sp = line.getValue().split(" ");
                                                    try{
                                                        if(line.getValue().contains("static") && publicOrPrivate && line.getValue().endsWith(")")){
                                                            methodName = sp[3];
                                                        }else if((!line.getValue().contains("static")) && publicOrPrivate && line.getValue().endsWith(")")){
                                                            methodName = sp[2];
                                                        }
                                                        if(!methodNameSelected && methodName != null) {
                                                            AlertDialog.Builder methodDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                                                            methodDialogBuilder.setMessage("The method to look for is \"" + methodName + "\". Is this correct?")
                                                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            methodNameSelected = true;
                                                                        }
                                                                    })
                                                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                                        public void onClick(DialogInterface dialog, int id) {
                                                                            methodName = null;
                                                                            methodFoundText.setText("");
                                                                            methodNameSelected = false;
                                                                            publicOrPrivate = false;

                                                                        }
                                                                    });
                                                            methodDialog = methodDialogBuilder.create();
                                                            methodDialogShown = true;
                                                        }

                                                    }catch(ArrayIndexOutOfBoundsException ex){

                                                    }
                                                    try{//marktolba can code like crqzy hells ye dude i respect that
                                                        methodFoundText.setText(methodName);
                                                        //capture again to find method call
                                                        if(line.getValue().startsWith(methodName) && methodNameSelected){
                                                            challengeComplete = true;
                                                        }
                                                    }catch(Exception ex){

                                                    }

                                                }

                                                graphicOverlay.add(graphic);

                                                Handler handler = new Handler();
                                                handler.postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        screenCapture = false;
                                                        if(!challengeComplete){
                                                            Toast toast=Toast.makeText(getBaseContext(),"Line to complete challenge not found, try again!",Toast.LENGTH_SHORT);
                                                            toast.show();
                                                        }
                                                        info1.setText(stringBuilder.toString());
                                                        if (switchPage) {
                                                            pager.setCurrentItem(0, true);
                                                            switchPage = false;
                                                        }
                                                        if(methodDialogShown && !methodNameSelected){
                                                            methodDialog.show();
                                                        }

                                                    }
                                                }, 1500);
                                                //1.5 sec to find item
                                            }
                                        }
                                    }
                                }

                                stringBuilder.insert(0, variableCount + "" +
                                        " Variable Declarations Found\n");
                                stringBuilder.append("\n" + ifCount + " If Conditions Found\n");
                                stringBuilder.append(elseCount + " else/else if statements Found\n");
                                stringBuilder.append(forCount + " For Loops Found\n");
                                stringBuilder.append(whileCount + " While Loops Found\n");
                                stringBuilder.append(switchCount + " Switch Statements Found\n");

                                stringBuilder.append("\n\nTips: ");
                                if (ifCount >= 1) {
                                    stringBuilder.append("\n-If conditions are statements that execute only IF the condition is true." +
                                            "The code within the curly brackets {} is what is executed");
                                }
                                if(elseCount >= 1){
                                    stringBuilder.append("\n-When the if condition falls true, " +
                                            "the code beneath is executed. Inserting an \"else\" afterwards will execute the other condition.");
                                }
                                if (forCount >= 1 || whileCount >= 1) {
                                    stringBuilder.append("\n-Loops are used to execute a set of statements repeatedly until a particular condition is satisfied.");
                                }
                                if (forCount >= 1) {
                                    stringBuilder.append("\n-For loops are recommended if there is a certain number of times you want the code to execute (either by incrementing or decrementing).");
                                }
                                if (whileCount >= 1) {
                                    stringBuilder.append("\n-While loops are recommended if you don't know how many times the code will execute");
                                }
                                if (switchCount >= 1) {
                                    stringBuilder.append("\n-This works similar to an else-if statement. It \"switches\" the variable and shows a variety of cases." +
                                            " If a specific case is true, the code underneath it is executed.");
                                }

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
                challengeComplete = false;
                captureButton.setEnabled(true);
                textView.setText("");
                dialogShown = false;
            }
        }
    }


}

// if code found, make DING noise and either surround code with box or show a tick

//
//int thisIsATest;
//    int seventeen = 17;
//int another=231;


//              while
//              switch
//              }else


/*
test(fgh );
                                    public void huy (){
hi();
        }

hyrr()

String
boolean
if






for











 while(true){

}





no();


 */
