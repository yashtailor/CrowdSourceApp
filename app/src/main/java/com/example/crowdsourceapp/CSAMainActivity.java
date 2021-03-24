package com.example.crowdsourceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CSAMainActivity extends AppCompatActivity {
    private Button makeElectionBtn,reviewVoterBtn,reviewCandidateBtn,calculateResultsBtn,viewResultsBtn,logout;
    protected  void onCreate(Bundle savedInstateState){
        super.onCreate(savedInstateState);
        setContentView(R.layout.activity_csa_main);
        makeElectionBtn = findViewById(R.id.makeElectionPage);
        reviewVoterBtn = findViewById(R.id.reviewVoterDetails);
        calculateResultsBtn = findViewById(R.id.viewAndCalculateResults);
        viewResultsBtn = findViewById(R.id.viewResultsBtn);
        reviewVoterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), CSAUserReviewActivity.class));
            }
        });
        logout = findViewById(R.id.logoutECA);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        makeElectionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CSACreateSurvey.class));
            }
        });
        calculateResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SurveyCalculateResults.class));
            }
        });
        viewResultsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ResultsActivity.class));
            }
        });
    }
}
