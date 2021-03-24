package com.example.crowdsourceapp;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SurveyActivity extends AppCompatActivity {
    private Button chooseElectionCandidateBtn,goToAllElections;
    private String curSurveyName;
    private DatabaseReference candidateDbRef,surveyDbRef,fsureveyRef,userRef;
    private FirebaseUser curUser;
    private String uid,result;
    private RadioGroup electionCandidatesRadioGroup;
    private TextView curSurveyTxtView;
    private LinearLayout curQuestionsLayout;
    private Integer curValue = 0;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        try{
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }catch(Exception e){
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
        curSurveyName = getIntent().getStringExtra("surveyName");
        candidateDbRef = FirebaseDatabase.getInstance().getReference();
        surveyDbRef = candidateDbRef.child("surveys").child(curSurveyName).child("questions");
        String uuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try{
            fsureveyRef = candidateDbRef.child("surveys").child(curSurveyName);
            userRef = candidateDbRef.child("user-data").child(uuid);
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        goToAllElections = findViewById(R.id.goToAllSurveys);
        curSurveyTxtView= findViewById(R.id.curSurveyName);
        curSurveyTxtView.setText(curSurveyName);
        curQuestionsLayout = findViewById(R.id.surveyQuestions);
        goToAllElections.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ShowAllCurrentSurveys.class));
            }
        });
        surveyDbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    CardView newCard = new CardView(getApplicationContext());
                    LinearLayout curCardLayout = new LinearLayout(getApplicationContext());
                    curCardLayout.setOrientation(LinearLayout.VERTICAL);
                    TextView surveyQuestion = new TextView(getApplicationContext());
                    surveyQuestion.setText(questionSnapshot.getKey());
                    curCardLayout.addView(surveyQuestion);
                    surveyQuestion.setTextSize(20);
                    newCard.setCardElevation(20);
                    newCard.setContentPadding(15,15,15,15);
                    newCard.setRadius(10);
                    newCard.setPreventCornerOverlap(true);
                    newCard.setUseCompatPadding(true);
                    newCard.setMaxCardElevation(20);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        newCard.setId(View.generateViewId());
                    }
                    questionSnapshot = questionSnapshot.child("options");
                    RadioGroup newRadioGrp = new RadioGroup(getApplicationContext());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        newRadioGrp.setId(View.generateViewId());
                    }
                    for (DataSnapshot optionSnapshot : questionSnapshot.getChildren()) {
                        RadioButton curOption = new RadioButton(getApplicationContext());
                        curOption.setText(optionSnapshot.getKey());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            curOption.setId(View.generateViewId());
                        }
                        newRadioGrp.addView(curOption);
                    }
                    curCardLayout.addView(newRadioGrp);
                    newCard.addView(curCardLayout);
                    curQuestionsLayout.addView(newCard);
                }
                Button submitBtn = new Button(getApplicationContext());
                submitBtn.setText("Submit");
                submitBtn.setId(View.generateViewId());
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewGroup vg = (ViewGroup)v.getParent();
                        for(int i=0;i<vg.getChildCount();i++){
                            if(i==vg.getChildCount()-1)continue;
                            ViewGroup rdGrp = (ViewGroup) vg.getChildAt(i);
                            rdGrp = (ViewGroup) rdGrp.getChildAt(0);
                            TextView curQuesNameTxt = (TextView)rdGrp.getChildAt(0);
                            String curQuesName = curQuesNameTxt.getText().toString();
                            rdGrp = (ViewGroup) rdGrp.getChildAt(1);
                            for(int j=0;j<rdGrp.getChildCount();j++){
                                RadioButton curBtn = (RadioButton)rdGrp.getChildAt(j);
                                if(curBtn.isChecked()){
                                    final String optionSelected  = curBtn.getText().toString();
//                                    System.out.println(curQuesName+optionSelected);
                                    fsureveyRef.child("questions").child(curQuesName).child("options").child(optionSelected).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot innermostsnapshot) {
                                            curValue = Integer.parseInt(innermostsnapshot.getValue().toString());
                                            fsureveyRef.child("questions").child(curQuesName).child("options").child(optionSelected).setValue(String.valueOf(curValue+1));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                                if(j==rdGrp.getChildCount()-1){
                                    userRef.child("surveys").child(curSurveyName).setValue("done");
                                    startActivity(new Intent(getApplicationContext(),ShowAllCurrentSurveys.class));
                                }
                            }
                        }
                    }
                });
                curQuestionsLayout.addView(submitBtn);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
