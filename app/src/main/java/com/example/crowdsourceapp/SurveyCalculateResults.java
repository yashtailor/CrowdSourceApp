package com.example.crowdsourceapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SurveyCalculateResults extends AppCompatActivity {
    private LinearLayout showAllRunningElectionsToCalculate;
    private DatabaseReference electionDbRef;
    private FirebaseUser curUser;
    private Button backToMainPage;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculate_results);
        showAllRunningElectionsToCalculate = findViewById(R.id.showAllRunningElectionsToCalculate);
        electionDbRef = FirebaseDatabase.getInstance().getReference();
        electionDbRef = electionDbRef.child("surveys");
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        backToMainPage = findViewById(R.id.backToInfoPageFromMainECA);
        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CSAMainActivity.class));
            }
        });
        electionDbRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot candidateSnapshot : snapshot.getChildren()) {
                    String electionName = candidateSnapshot.getKey();
                    if(candidateSnapshot.child("isDone").getValue().toString().equals("true")){
                        //don't add
                    }else{
                        Button curElectionBtn = new Button(getApplicationContext());
                        curElectionBtn.setId(View.generateViewId());
                        curElectionBtn.setText(electionName);
                        curElectionBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                electionDbRef.child(electionName).child("isDone").setValue("true");
                                Toast.makeText(getApplicationContext(),"Survey Ended",Toast.LENGTH_SHORT).show();
                                ((ViewGroup)v.getParent()).removeView(v);
                            }
                        });
                        showAllRunningElectionsToCalculate.addView(curElectionBtn);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}