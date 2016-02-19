package com.ryce.frugalist.view.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ryce.frugalist.R;
import com.ryce.frugalist.util.UserHelper;

public class LoginActivity extends AppCompatActivity {

    Button mLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserHelper.setLoggedIn(true);
                finish();
            }
        });

    }

}
