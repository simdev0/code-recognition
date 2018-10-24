package com.simdev.project.textrecognition3;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ChallengeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_challenge);

        Button backBtn = (Button) findViewById(R.id.backBtn);
        TextView currentChallengeTxt = (TextView) findViewById(R.id.currentChallengeText);

        ArrayList<Challenge> challenges = PopulateChallenges();
        ArrayList<String> challengeNames = new ArrayList<>();


        for (Challenge challenge : challenges) {
            Log.d("EXECUTING.. ", challenge.name);
            challengeNames.add(challenge.number + ". " + challenge.name);
        }

        ListView challengeList = (ListView) findViewById(R.id.challengeList);

//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, challengeNames );
//        challengeList.setAdapter(arrayAdapter);

        Bundle extras = getIntent().getExtras();
        final String challenge_no = extras.getString("CHALLENGE_NO");
        Log.d("number is ", challenge_no);
        currentChallengeTxt.setText("Current Challenge: " + challenge_no);
        final int challengeIndex = Integer.parseInt(challenge_no);

        challengeList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, challengeNames) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row = super.getView(position, convertView, parent);

                if (position + 1 < challengeIndex) {
                    // do something change color
                    row.setBackgroundColor(Color.GRAY); // some color
                } else {
                    // default state
                    row.setBackgroundColor(Color.WHITE); // default color
                }
                return row;
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

                setResult(RESULT_OK,
                        new Intent().putExtra("challenge_no", challenge_no + ""));
                finish();
            }
        });
    }

    public ArrayList<Challenge> PopulateChallenges() {
        ArrayList<Challenge> chalList = new ArrayList<>();

        chalList.add(new Challenge(1, "Identify a variable that returns a number"));
        chalList.add(new Challenge(2, "Identify a variable that returns text"));
        chalList.add(new Challenge(3, "Identify a variable that returns true or false"));
        chalList.add(new Challenge(4, "Identify a selection statement"));
        chalList.add(new Challenge(5, "Identify an iteration block with an incrementing or decrementing condition (for)"));
        chalList.add(new Challenge(6, "Identify an iteration block with just a condition (while)"));
        chalList.add(new Challenge(7, "Identify a switch-case statement"));
        chalList.add(new Challenge(8, "Identify a selection statement with both conditions"));
        chalList.add(new Challenge(9, "Identify a declaration of a new class"));
        chalList.add(new Challenge(10, "Identify a getter method"));
        chalList.add(new Challenge(11, "Identify a setter method"));

        return chalList;
    }


}
