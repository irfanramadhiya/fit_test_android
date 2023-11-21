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
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        EditText et_email = findViewById(R.id.et_forgot_pass_email);
        ProgressBar progress_bar = findViewById(R.id.progress_bar_forgot_pass);
        Button btn_reset_pass = findViewById(R.id.btn_reset_pass);
        TextView tv_back_to_login = findViewById(R.id.tv_back_to_login_forgot_pass);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        btn_reset_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = String.valueOf(et_email.getText());

                String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                        "[a-zA-Z0-9_+&*-]+)*@" +
                        "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                        "A-Z]{2,7}$";
                Pattern pat = Pattern.compile(emailRegex);

                if(email.isEmpty()){
                    Toast.makeText(ForgotPasswordActivity.this, "E-mail can't be empty", Toast.LENGTH_SHORT).show();
                }
                else if(!pat.matcher(email).matches()){
                    Toast.makeText(ForgotPasswordActivity.this, "E-mail is not valid", Toast.LENGTH_SHORT).show();
                }
                else{
                    progress_bar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(ForgotPasswordActivity.this, "E-mail sent", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progress_bar.setVisibility(View.GONE);
                                Toast.makeText(ForgotPasswordActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
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
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }
}