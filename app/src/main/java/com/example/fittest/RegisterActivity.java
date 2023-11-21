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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText et_name = findViewById(R.id.et_name);
        EditText et_email = findViewById(R.id.et_regis_email);
        EditText et_password = findViewById(R.id.et_regis_password);
        EditText et_confirm_password = findViewById(R.id.et_regis_confirm_password);
        ProgressBar progress_bar = findViewById(R.id.progress_bar_regis);
        Button btn_register = findViewById(R.id.btn_register);
        TextView tv_back_to_login = findViewById(R.id.tv_back_to_login_regis);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = String.valueOf(et_name.getText());
                String email = String.valueOf(et_email.getText());
                String password = String.valueOf(et_password.getText());
                String confirmPassword = String.valueOf(et_confirm_password.getText());

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

                if(name.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Name can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(name.length() < 3 || name.length() > 50){
                    Toast.makeText(RegisterActivity.this, "Name must be between 3 to 50 characters", Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "E-mail can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!pat.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "E-mail is not valid", Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Password can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 8){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show();
                }
                else if(!upperCaseCheck || !lowerCaseCheck || !numberCheck){
                    Toast.makeText(RegisterActivity.this, "Password must contain uppercase, lowercase and number", Toast.LENGTH_SHORT).show();
                }
                else if(confirmPassword.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Confirm password can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this, "Confirm password and password does not match", Toast.LENGTH_SHORT).show();
                }
                else {
                    progress_bar.setVisibility(View.VISIBLE);
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Map<String, Object> map = new HashMap<>();
                                map.put("email", email);
                                map.put("email_verified", auth.getCurrentUser().isEmailVerified());
                                map.put("name", name);
                                map.put("uid", auth.getCurrentUser().getUid());
                                firestore.collection("users").document(auth.getCurrentUser().getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            auth.getCurrentUser().sendEmailVerification();
                                            progress_bar.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, "Register success", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                        else {
                                            progress_bar.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            }
                            else {
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(RegisterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        tv_back_to_login.setPaintFlags(tv_back_to_login.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        tv_back_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}