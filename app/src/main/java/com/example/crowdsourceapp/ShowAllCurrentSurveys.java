package com.example.crowdsourceapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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

public class ShowAllCurrentSurveys extends AppCompatActivity {
    private LinearLayout showAllRunningSurveys;
    private DatabaseReference surveyDbRef,userRef;
    private FirebaseUser curUser;
    private Button goBackToInfoPageFromAllElections;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_surveys);
        showAllRunningSurveys = findViewById(R.id.showAllRunningSurveys);
        surveyDbRef = FirebaseDatabase.getInstance().getReference();
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = surveyDbRef.child("user-data").child(uuid).child("surveys");
        surveyDbRef = surveyDbRef.child("surveys");
        curUser = FirebaseAuth.getInstance().getCurrentUser();
        goBackToInfoPageFromAllElections = findViewById(R.id.backToInfoPageFromAllElections);
        goBackToInfoPageFromAllElections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),InfoActivity.class));
            }
        });
        surveyDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot surveySnapshot : snapshot.getChildren()) {
                    System.out.println(surveySnapshot.child("isDone").getValue());
                    if(surveySnapshot.child("isDone").getValue().equals("true")){
                        //don't add
                    }else{
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot insnapshot) {
                                if(!insnapshot.hasChild(surveySnapshot.getKey())){
                                    Button curElectionBtn = new Button(getApplicationContext());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        curElectionBtn.setElevation(2);
                                    }
                                    curElectionBtn.setId(View.generateViewId());
                                    String surveyNane = surveySnapshot.getKey();
                                    curElectionBtn.setText(surveyNane);
                                    curElectionBtn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(getApplicationContext(),SurveyActivity.class);
                                            intent.putExtra("surveyName",surveyNane);
                                            startActivity(intent);
                                        }
                                    });
                                    showAllRunningSurveys.addView(curElectionBtn);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }
}
