package com.ryce.frugalist.view.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ryce.frugalist.R;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.list.MainListActivity;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton mloginButton;
    private TextView btnLogin;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //This code is for viewing the facebook app key Hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.ryce.frugalist",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }

        setContentView(R.layout.activity_login);

        // TODO: this is redundant, probably
        if (UserHelper.getCurrentUser(LoginActivity.this) != null) {
            UserHelper.setLoggedIn(true);
            Intent intent = new Intent(this, MainListActivity.class);
            startActivity(intent);
            finish();
        }

        callbackManager = CallbackManager.Factory.create();

        mloginButton = (LoginButton)findViewById(R.id.login_button);
        mloginButton.setReadPermissions("public_profile", "email");
        mloginButton.registerCallback(callbackManager, mCallBack);

        btnLogin= (TextView) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mloginButton.performClick();

                mloginButton.setPressed(true);

                mloginButton.invalidate();

                mloginButton.setPressed(false);

                mloginButton.invalidate();

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {

                            Log.e("response: ", response + "");
                            try {
                                user = new User();
                                user.facebookID = object.getString("id");
                                user.name = object.getString("name");
                                UserHelper.setCurrentUser(user,LoginActivity.this);
                                UserHelper.setLoggedIn(true);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            Toast.makeText(LoginActivity.this,"Welcome "+user.name,Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(LoginActivity.this, MainListActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id, name, email, gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
        }
    };

}
