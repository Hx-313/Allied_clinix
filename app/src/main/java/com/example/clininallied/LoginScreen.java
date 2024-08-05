package com.example.clininallied;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginScreen extends AppCompatActivity {
    DrawableEditText editText;
    boolean isVisible = false;

    Button signin;
    TextView signUp;
    Button google;
    Button facebook;
    TextView forgotPassword;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        editText = findViewById(R.id.password);
        editText.setOnDrawableClickListener(new DrawableEditText.OnDrawableClickListener() {
            @Override
            public void onDrawableClick() {
                if(!isVisible) {

                    isVisible=!isVisible;
                    editText.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.eye, 0);
                }else{
                    isVisible= !isVisible;
                    editText.setInputType( InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    editText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock_password_svgrepo_com, 0 ,R.drawable.hide, 0);

                }
            }
        });
        signin = findViewById(R.id.signin);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this,Dashboard.class));
            }
        });
        signUp = findViewById(R.id.signup);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginScreen.this, RegistrationScreen.class);
                startActivity(intent);

            }
        });

    }
}