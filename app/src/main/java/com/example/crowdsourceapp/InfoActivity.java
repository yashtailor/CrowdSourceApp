package com.example.crowdsourceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InfoActivity extends AppCompatActivity {
    private Button goToPendingElectionsPage,goToEditPage,vGoToResultsPage,logout;
    private Boolean isVerified;
    private TextView messageText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        goToPendingElectionsPage = findViewById(R.id.goToPendingElectionsActivity);
        goToEditPage = findViewById(R.id.goToMainPage);
        vGoToResultsPage = findViewById(R.id.vGoToResultsPage);
        messageText = findViewById(R.id.messageTxt);
        logout = findViewById(R.id.logout);
        FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
        DatabaseReference voterRef = FirebaseDatabase.getInstance().getReference();
        voterRef = voterRef.child("user-data").child(curUser.getUid());
        voterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("isVerified").getValue().toString().equals("true")){
                    messageText.setText("Profile verified. Authorized for surveys.");
                    isVerified = true;
                }else{
                    String fmsg = snapshot.child("message").getValue().toString();
                    if(fmsg.length()==0)fmsg="Profile is under verification. Not yet authorized for Surveys.";
                    else fmsg += "Status is Declined.";
                    messageText.setText(fmsg);
                    isVerified = false;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        goToPendingElectionsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isVerified)startActivity(new Intent(getApplicationContext(),ShowAllCurrentSurveys.class));
                else{
                    Toast.makeText(getApplicationContext(),"Not verified for surveys",Toast.LENGTH_LONG).show();
                }
            }
        });
        goToEditPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        vGoToResultsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ResultsActivity.class));
            }
        });
    }
}
