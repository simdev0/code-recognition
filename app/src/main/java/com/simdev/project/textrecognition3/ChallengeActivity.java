package com.simdev.project.textrecognition3;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChallengeActivity extends AppCompatActivity {

    String challenge_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_challenge);

        Button backBtn = (Button) findViewById(R.id.backBtn);
        Button resetBtn = (Button) findViewById(R.id.resetBtn);

        final TextView currentChallengeTxt = (TextView) findViewById(R.id.currentChallengeText);
        final ImageView badge1 = (ImageView) findViewById(R.id.badge1);
        final ImageView badge2 = (ImageView) findViewById(R.id.badge2);
        final ImageView badge3 = (ImageView) findViewById(R.id.badge3);
        final ImageView badge4 = (ImageView) findViewById(R.id.badge4);

        final TextView badge1text = (TextView) findViewById(R.id.badge1text);
        final TextView badge2text = (TextView) findViewById(R.id.badge2text);
        final TextView badge3text = (TextView) findViewById(R.id.badge3text);
        final TextView badge4text = (TextView) findViewById(R.id.badge4text);

        int color1 = Color.parseColor("#59FF45");
        int color2 = Color.parseColor("#4583FF");
        int color3 = Color.parseColor("#FF4545");
        int color4 = Color.parseColor("#A845FF");

        badge1.setColorFilter(color1);
        badge2.setColorFilter(color2);
        badge3.setColorFilter(color3);
        badge4.setColorFilter(color4);


        ArrayList<Challenge> challenges = PopulateChallenges();
        final ArrayList<String> challengeNames = new ArrayList<>();


        for (Challenge challenge : challenges) {
            Log.d("EXECUTING.. ", challenge.name);
            challengeNames.add(challenge.number + ". " + challenge.name);
        }

        final ListView challengeList = (ListView) findViewById(R.id.challengeList);

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, challengeNames );
//        challengeList.setAdapter(arrayAdapter);


        Bundle extras = getIntent().getExtras();
        challenge_no = extras.getString("CHALLENGE_NO");
        Log.d("number is ", challenge_no);
        currentChallengeTxt.setText("Current Challenge: " + challenge_no);
        final int challengeIndex = Integer.parseInt(challenge_no);

        final ArrayAdapter<String> ListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, challengeNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                if (position + 1 < challengeIndex) {
                    row.setBackgroundColor(Color.GRAY);
                } else {
                    row.setBackgroundColor(Color.WHITE);
                }
                if (position + 1 == 10 || position + 1 == 11) {
                    row.setAlpha(0.2f);
                } else {
                    row.setAlpha(1);
                }
                return row;
            }
        };

        challengeList.setAdapter(ListAdapter);

        badge1.setAlpha(0.1f);
        badge1text.setAlpha(0.1f);
        badge2.setAlpha(0.1f);
        badge2text.setAlpha(0.1f);
        badge3.setAlpha(0.1f);
        badge3text.setAlpha(0.1f);
        badge4.setAlpha(0.1f);
        badge4text.setAlpha(0.1f);


        switch (challengeIndex) {
            //challenge 1,2,3 complete
            case 4:
                badge1.setAlpha(1.0f);
                badge1text.setAlpha(1.0f);
                break;
            //challenge 4 complete
            case 5:
                //challenge 4,5 complete
            case 6:
                badge1.setAlpha(1.0f);
//                    badge2.setAlpha(1.0f);
                badge1text.setAlpha(1.0f);
//                    badge2text.setAlpha(1.0f);
                break;
            case 7:
            case 8:
                badge1.setAlpha(1.0f);
//                    badge2.setAlpha(1.0f);
                badge3.setAlpha(1.0f);
                badge1text.setAlpha(1.0f);
//                    badge2text.setAlpha(1.0f);
                badge3text.setAlpha(1.0f);
                break;
            case 9:
                badge1.setAlpha(1.0f);
                badge2.setAlpha(1.0f);
                badge3.setAlpha(1.0f);
                badge1text.setAlpha(1.0f);
                badge2text.setAlpha(1.0f);
                badge3text.setAlpha(1.0f);
                break;
            case 10:
                badge1.setAlpha(1.0f);
                badge2.setAlpha(1.0f);
                badge3.setAlpha(1.0f);
                badge4.setAlpha(1.0f);
                badge1text.setAlpha(1.0f);
                badge2text.setAlpha(1.0f);
                badge3text.setAlpha(1.0f);
                badge4text.setAlpha(1.0f);
                currentChallengeTxt.setText("All Challenges Completed!");
                break;

        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

                setResult(RESULT_OK,
                        new Intent().putExtra("challenge_no", challenge_no + ""));
                finish();
            }
        });

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        challenge_no = 1 + "";
                        currentChallengeTxt.setText("Current Challenge: " + challenge_no);
                        SharedPreferences mPrefs = getApplicationContext().getSharedPreferences("challenges", 0);
                        SharedPreferences.Editor mEditor = mPrefs.edit();
                        mEditor.putInt("challengeNum", 1).commit();
                        badge1.setAlpha(0.1f);
                        badge1text.setAlpha(0.1f);
                        badge2.setAlpha(0.1f);
                        badge2text.setAlpha(0.1f);
                        badge3.setAlpha(0.1f);
                        badge3text.setAlpha(0.1f);
                        badge4.setAlpha(0.1f);
                        badge4text.setAlpha(0.1f);
                        challengeList.setAdapter(new ArrayAdapter<String>(ChallengeActivity.this, android.R.layout.simple_list_item_1, challengeNames) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View row = super.getView(position, convertView, parent);

                                row.setBackgroundColor(Color.WHITE);

                                if (position + 1 == 10 || position + 1 == 11) {
                                    row.setAlpha(0.2f);
                                } else {
                                    row.setAlpha(1);
                                }
                                return row;
                            }
                        });

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ChallengeActivity.this);
                builder.setMessage("Are you sure?")
                       .setPositiveButton("Yes", dialogClickListener)
                       .setNegativeButton("No", dialogClickListener).show();
            }
        });
    }


    public ArrayList<Challenge> PopulateChallenges() {
        ArrayList<Challenge> chalList = new ArrayList<>();

        // badge for 1,2,3 - variable declarations
        // badge for 4,8 - selection statement
        // badge for 5,6 - loops
        // badge for 9,10,11 - classes
        chalList.add(new Challenge(1, "Identify a variable that can be assigned a number"));
        chalList.add(new Challenge(2, "Identify a variable that can be assigned text"));
        chalList.add(new Challenge(3, "Identify a variable that can be assigned true or false"));
        chalList.add(new Challenge(4, "Identify a selection statement"));
        chalList.add(new Challenge(5, "Identify an iteration block with an implemented counter"));
        chalList.add(new Challenge(6, "Identify an iteration block type distinguished from challenge 5"));
        chalList.add(new Challenge(7, "Identify a switch-case statement"));
        chalList.add(new Challenge(8, "Identify a block in a selection statement which executes when the expression is false"));
        chalList.add(new Challenge(9, "Identify both method declaration and basic method call"));
        chalList.add(new Challenge(10, "Identify an array declaration"));
        chalList.add(new Challenge(11, "Identify a setter method"));

        return chalList;
    }


}
