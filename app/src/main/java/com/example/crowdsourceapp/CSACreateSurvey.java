package com.example.crowdsourceapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class CSACreateSurvey extends AppCompatActivity {
    private EditText surveyName,startDate,endDate;
    private Button makeSurevyBtn,backToMainPage,addQuestion;
    private LinearLayout questionLayout;
    private ArrayList<ArrayList<String>>surveyQuestions = new ArrayList<>();
    private String surveyNm="Survey Untitled";
    private DatabaseReference surveyRef,surveyRefFinal,surveyRefFinalQues;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_survey);
        surveyName = findViewById(R.id.surveyName);
        makeSurevyBtn = findViewById(R.id.makeSurevyBtn);
        addQuestion = findViewById(R.id.addQuestionBtn);
        questionLayout = findViewById(R.id.questionLayout);
        addQuestion.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"UseCompatLoadingForColorStateLists", "ResourceAsColor"})
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                LinearLayout newLinearLayout = new LinearLayout(getApplicationContext());
                newLinearLayout.setOrientation(LinearLayout.VERTICAL);
                CardView newCard = new CardView(getApplicationContext());
                newCard.setCardElevation(15);
                newCard.setContentPadding(15,15,15,15);
                newCard.setRadius(10);
                newCard.setPreventCornerOverlap(true);
                newCard.setUseCompatPadding(true);
                newCard.setMaxCardElevation(20);
                newCard.setId(view.generateViewId());
                EditText newTextInput = new EditText(getApplicationContext());
                newTextInput.setId(view.generateViewId());
                newTextInput.setHint("Question Name");
                Button addOption = new Button(getApplicationContext());
                Button deleteBtn = new Button(getApplicationContext());
                addOption.setId(view.generateViewId());
                addOption.setText("Add Option");
                deleteBtn.setId(view.generateViewId());
                deleteBtn.setText("Delete Question");
                Drawable buttonDrawable = deleteBtn.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, Color.parseColor("#f3566f"));
                deleteBtn.setBackground(buttonDrawable);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    addOption.setBackgroundTintList(getResources().getColorStateList(R.color.option));
                }
                LinearLayout innerLayout = new LinearLayout(getApplicationContext());
                innerLayout.setOrientation(LinearLayout.HORIZONTAL);
                addOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout curLayout = (LinearLayout) v.getParent().getParent();
                        EditText newTextInput = new EditText(getApplicationContext());
                        Button delBtn = new Button(getApplicationContext());
                        delBtn.setId(view.generateViewId());
                        delBtn.setText("X");
                        Drawable buttonDrawable = delBtn.getBackground();
                        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                        //the color is a direct color int and not a color resource
                        DrawableCompat.setTint(buttonDrawable, Color.parseColor("#f3566f"));
                        delBtn.setBackground(buttonDrawable);
                        delBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View curView = (View) v.getParent();
                                ((ViewGroup)curView.getParent()).removeView(curView);
                            }
                        });
                        LinearLayout optionLayout = new LinearLayout(getApplicationContext());
                        optionLayout.setOrientation(LinearLayout.HORIZONTAL);
                        newTextInput.setId(view.generateViewId());
                        newTextInput.setWidth(800);
                        newTextInput.setHint("Option Name");
                        optionLayout.addView(newTextInput);
                        optionLayout.addView(delBtn);
                        curLayout.addView(optionLayout);
                    }
                });
                deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View curView = (View) v.getParent().getParent().getParent();
                        ((ViewGroup)curView.getParent()).removeView(curView);
                    }
                });
                innerLayout.addView(addOption);
                innerLayout.addView(deleteBtn);
                newLinearLayout.addView(newTextInput);
                newLinearLayout.addView(innerLayout);
                newCard.addView(newLinearLayout);
                questionLayout.addView(newCard);
            }
        });
        backToMainPage = findViewById(R.id.backToInfoPageFromCreateElectionECA);
        backToMainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),CSAMainActivity.class));
            }
        });
        makeSurevyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup curView = (ViewGroup) v.getParent().getParent();
                ViewGroup SurveyName = (ViewGroup) curView.getChildAt(curView.getChildCount()-3);
                SurveyName = (ViewGroup) SurveyName.getChildAt(0);
                SurveyName = (ViewGroup) SurveyName.getChildAt(0);
                EditText SurveyText = (EditText) SurveyName.getChildAt(0);
                surveyNm = SurveyText.getText().toString();
                System.out.println("============"+surveyNm);
                ViewGroup questionView = (ViewGroup) curView.getChildAt(curView.getChildCount()-1);
                System.out.println(questionView); //this gives the questionLayout
                for (int i = 0; i < questionView.getChildCount(); i++) {
                    ViewGroup child = (ViewGroup) questionView.getChildAt(i); //this should give card
                    child = (ViewGroup) child.getChildAt(0); // this gives linear layout
                    //System.out.println("======="+child.getChildCount());
                    if(child!=null){
                        ArrayList<String>curOptions = new ArrayList<>();
                        for (int j = 0; j < child.getChildCount(); j++) {
                            if(j==0){
                                EditText optionText = (EditText) child.getChildAt(j);
                                curOptions.add(optionText.getText().toString());
                                continue;
                            }
                            if(j==1)continue;
                            System.out.println(child.getChildAt(j));
                            ViewGroup innerChild = (ViewGroup) child.getChildAt(j); //element of option layout
                            EditText optionText = (EditText) innerChild.getChildAt(0); // this gives option (Edittext)
                            System.out.println(optionText.getText().toString());
                            curOptions.add(optionText.getText().toString());
                        }
                        surveyQuestions.add(curOptions);
                        addToDb();
                    }
                }
            }
        });
    }

    public void addToDb(){
        surveyRef = FirebaseDatabase.getInstance().getReference();
        surveyRefFinal = surveyRef.child("surveys").child(surveyNm);
        for(int i=0;i<surveyQuestions.size();i++){
            ArrayList<String> surveyQuestion = surveyQuestions.get(i);
            surveyRefFinal.child("isDone").setValue("false");
            surveyRefFinalQues = surveyRefFinal.child("questions");
            String questionName = "Survey Untitled";
            for(int j=0;j<surveyQuestion.size();j++){
                if(j==0){
                    questionName = surveyQuestion.get(j);
                }else{
                    surveyRefFinalQues.child(questionName).child("options").child(surveyQuestion.get(j)).setValue("0");
                }
            }
        }
        Toast.makeText(getApplicationContext(),"Survey Added!",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getApplicationContext(),CSAMainActivity.class));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void addQuestionToSurvey(View view){
        CardView newCard = new CardView(getApplicationContext());
        newCard.setCardElevation(15);
        newCard.setContentPadding(15,15,15,15);
        newCard.setRadius(10);
        newCard.setPreventCornerOverlap(true);
        newCard.setUseCompatPadding(true);
        newCard.setMaxCardElevation(20);
        newCard.setId(view.generateViewId());
        EditText newTextInput = new EditText(getApplicationContext());
        newTextInput.setId(view.generateViewId());
        Button addOption = new Button(getApplicationContext());
        addOption.setId(view.generateViewId());
        addOption.setText("Add Option");
        newCard.addView(newTextInput);
        newCard.addView(addOption);
        questionLayout.addView(newCard);
    }
}