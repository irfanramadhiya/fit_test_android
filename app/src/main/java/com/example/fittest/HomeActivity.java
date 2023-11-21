package com.example.fittest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends AppCompatActivity {

    ArrayList<User> users;
    ArrayList<User> allUsers;
    ArrayList<User> emailVerifiedUsers;
    MyAdapter myAdapter;
    RadioGroup rg_filter;
    RadioButton rb_selected;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    ProgressBar progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Intent intent = getIntent();
        String token = intent.getStringExtra("token");


        progress_bar = findViewById(R.id.progress_bar_home);
        rg_filter = findViewById(R.id.rg_filter);

        RecyclerView rv_users = findViewById(R.id.rv_users);
        rv_users.setHasFixedSize(true);
        rv_users.setLayoutManager(new LinearLayoutManager(this));

        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        users = new ArrayList<>();
        myAdapter = new MyAdapter(HomeActivity.this, users);

        rv_users.setAdapter(myAdapter);

        progress_bar.setVisibility(View.VISIBLE);

        auth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
            @Override
            public void onSuccess(GetTokenResult getTokenResult) {
                if(token.equals(getTokenResult.getToken())){
                    firestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {

                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(error != null){
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(HomeActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            for (DocumentChange dc : value.getDocumentChanges()){
                                if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED || dc.getType() == DocumentChange.Type.REMOVED){
                                    users.add(dc.getDocument().toObject(User.class));
                                }

                                allUsers = new ArrayList<>();
                                emailVerifiedUsers = new ArrayList<>();
                                for (int i = 0; i < users.size(); i++){
                                    if(users.get(i).email_verified){
                                        emailVerifiedUsers.add(users.get(i));
                                    }
                                    allUsers.add(users.get(i));
                                }


                                myAdapter.notifyDataSetChanged();
                                progress_bar.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }
    public void checkButton(View v){
        int radioId = rg_filter.getCheckedRadioButtonId();

        rb_selected = findViewById(radioId);

        users.clear();

        if(rb_selected.getText().equals("All")){
            for (int i = 0; i < allUsers.size(); i++){
                users.add(allUsers.get(i));
            }
        }
        else {
            for (int i = 0; i < emailVerifiedUsers.size(); i++){
                users.add(emailVerifiedUsers.get(i));
            }
        }
        myAdapter.notifyDataSetChanged();
    }
}