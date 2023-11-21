package com.example.fittest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText et_email = findViewById(R.id.et_login_email);
        EditText et_password = findViewById(R.id.et_login_password);
        ProgressBar progress_bar = findViewById(R.id.progress_bar_login);
        Button btn_login = findViewById(R.id.btn_login);
        TextView tv_forgot_pass = findViewById(R.id.tv_forgot_pass);
        TextView tv_create_new_account = findViewById(R.id.tv_create_new_account);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(et_email.getText());
                String password = String.valueOf(et_password.getText());

                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                Pattern pat = Pattern.compile(emailRegex);

                boolean upperCaseCheck = false;
                boolean lowerCaseCheck = false;
                boolean numberCheck = false;

                for (int i = 0; i < password.length(); i++){
                    char ch = password.charAt(i);
                    if(Character.isUpperCase(ch)){
                        upperCaseCheck = true;
                    }
                    else if(Character.isLowerCase(ch)){
                        lowerCaseCheck = true;
                    }
                    else if(Character.isDigit(ch)){
                        numberCheck = true;
                    }
                }

                if(email.isEmpty()){
                    Toast.makeText(MainActivity.this, "E-mail can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!pat.matcher(email).matches()){
                    Toast.makeText(MainActivity.this, "E-mail is not valid", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(MainActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 8){
                    Toast.makeText(MainActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!upperCaseCheck || !lowerCaseCheck || !numberCheck){
                    Toast.makeText(MainActivity.this, "Password must contain uppercase, lowercase and number", Toast.LENGTH_SHORT).show();
                }
                else {
                    progress_bar.setVisibility(View.VISIBLE);
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                firestore.collection("users").document(auth.getCurrentUser().getUid()).update("email_verified", auth.getCurrentUser().isEmailVerified()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            auth.getCurrentUser().getIdToken(true).addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                                                @Override
                                                public void onSuccess(GetTokenResult getTokenResult) {
                                                    progress_bar.setVisibility(View.GONE);
                                                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                                    intent.putExtra("token", getTokenResult.getToken());
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        }
                                        else {
                                            progress_bar.setVisibility(View.GONE);
                                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            else {
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        tv_forgot_pass.setPaintFlags(tv_forgot_pass.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tv_forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_create_new_account.setPaintFlags(tv_create_new_account.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tv_create_new_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}