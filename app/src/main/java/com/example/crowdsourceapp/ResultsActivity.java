        package com.example.crowdsourceapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

        public class ResultsActivity extends AppCompatActivity {
            private LinearLayout showAllDoneElections;
            private DatabaseReference electionDbRef, electionCandidatesRef;
            private FirebaseUser curUser;
            private ArrayList<String> candidates = new ArrayList<>();
            private Button goBackToInfoPageResults;

            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_results);
                ArrayList<Integer> colors = new ArrayList<>();
                colors.add(Color.parseColor("#304567"));
                colors.add(Color.parseColor("#309967"));
                colors.add(Color.parseColor("#476567"));
                colors.add(Color.parseColor("#890567"));
                colors.add(Color.parseColor("#a35567"));
                colors.add(Color.parseColor("#ff5f67"));
                colors.add(Color.parseColor("#3ca567"));
                //Toast.makeText(getApplicationContext(),LoginActivity.curUserType,Toast.LENGTH_LONG).show();
                goBackToInfoPageResults = findViewById(R.id.backToInfoPageFromResults);
                goBackToInfoPageResults.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LoginActivity.curUserType == "user") {
                            startActivity(new Intent(getApplicationContext(), InfoActivity.class));
                        } else if (LoginActivity.curUserType == "csa") {
                            startActivity(new Intent(getApplicationContext(), CSAMainActivity.class));
                        }
                    }
                });
                showAllDoneElections = findViewById(R.id.allDoneElections);
                electionDbRef = FirebaseDatabase.getInstance().getReference();
                electionCandidatesRef = electionDbRef.child("surveys");
                electionCandidatesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot candidateSnapshot : snapshot.getChildren()) {
                            CardView newCard = new CardView(getApplicationContext());
                            newCard.setCardElevation(15);
                            newCard.setContentPadding(15,15,15,15);
                            newCard.setRadius(10);
                            newCard.setPreventCornerOverlap(true);
                            newCard.setUseCompatPadding(true);
                            newCard.setMaxCardElevation(20);
                            TextView surveyNameTxt = new TextView(getApplicationContext());
                            surveyNameTxt.setText("Survey Name: "+candidateSnapshot.getKey());
                            surveyNameTxt.setTextSize(25);
                            LinearLayout newLinearLayout = new LinearLayout(getApplicationContext());
                            newLinearLayout.setOrientation(LinearLayout.VERTICAL);
                            newLinearLayout.addView(surveyNameTxt);
                            //candidates.add(candidateSnapshot.child("pName").getValue().toString());
                            candidateSnapshot = candidateSnapshot.child("questions");
                            for (DataSnapshot questionSnapshot : candidateSnapshot.getChildren()) {
                                PieChart pieChart = new PieChart(getApplicationContext());
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                    pieChart.setId(View.generateViewId());
                                }
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(800, 500);
                                pieChart.setLayoutParams(layoutParams);
                                pieChart.getDescription().setEnabled(false);
                                pieChart.setRotationEnabled(true);
                                pieChart.setDragDecelerationFrictionCoef(0.9f);
                                pieChart.setRotationAngle(0);
                                pieChart.setHighlightPerTapEnabled(true);
                                //adding animation so the entries pop up from 0 degree
                                pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
                                //setting the color of the hole in the middle, default white
                                pieChart.setHoleColor(Color.parseColor("#0f0f0f"));
                                TextView electionNameTxt = new TextView(getApplicationContext());
                                electionNameTxt.setText(questionSnapshot.getKey());
                                electionNameTxt.setTextSize(30);
                                ArrayList<PieEntry> pieEntries = new ArrayList<>();
                                String label = "Options";
                                Map<String, Integer> typeAmountMap = new HashMap<>();
                                newLinearLayout.addView(electionNameTxt);
                                //Toast.makeText(getApplicationContext(),candidates.size(),Toast.LENGTH_SHORT).show();
                                questionSnapshot = questionSnapshot.child("options");
                                for (DataSnapshot optionSnapshot : questionSnapshot.getChildren()) {
                                    candidates.add(optionSnapshot.getKey());
                                    TextView candidateTxtView = new TextView(getApplicationContext());
                                    String votes = optionSnapshot.getValue().toString();
                                    votes = String.valueOf(Integer.parseInt(votes)+1);
                                    candidateTxtView.setText(optionSnapshot.getKey() + " -" + votes);
                                    typeAmountMap.put(optionSnapshot.getKey(), Integer.parseInt(votes));
                                    //collecting the entries with label name
                                    PieDataSet pieDataSet = new PieDataSet(pieEntries, label);
                                    //setting text size of the value
                                    pieDataSet.setValueTextSize(12f);
                                    //providing color list for coloring different entries
                                    pieDataSet.setColors(colors);
                                    //grouping the data set from entry to chart
                                    PieData pieData = new PieData(pieDataSet);
                                    //showing the value of the entries, default true if not set
                                    pieData.setDrawValues(true);
                                    pieChart.setData(pieData);
                                    pieChart.invalidate();
                                    candidateTxtView.setTextSize(20);
                                    newLinearLayout.addView(candidateTxtView);
                                }
                                for (String type : typeAmountMap.keySet()) {
                                    pieEntries.add(new PieEntry(typeAmountMap.get(type).floatValue(), type));
                                }
                                newLinearLayout.addView(pieChart);
                                newLinearLayout.setPadding(5, 5, 5, 5);
                            }
                            newCard.addView(newLinearLayout);
                            newLinearLayout.setBackgroundColor(Color.parseColor("#f1a4ee"));
                            showAllDoneElections.addView(newCard);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }