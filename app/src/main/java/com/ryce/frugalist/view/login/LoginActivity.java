package com.ryce.frugalist.view.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.ryce.frugalist.R;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.list.MainListActivity;

import java.io.IOException;
import java.util.HashSet;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private CallbackManager callbackManager;
    private LoginButton mloginButton;
    private ProgressDialog mProgressDialog;
    private ProfileTracker mProfileTracker;

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

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_loading));

        callbackManager = CallbackManager.Factory.create();

        mloginButton = (LoginButton)findViewById(R.id.login_button);
        mloginButton.setReadPermissions("public_profile", "email");
        mloginButton.registerCallback(callbackManager, mCallBack);

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

    /**********************************************************************
     * Frugalist User fetch/add
     **********************************************************************/

    /**
     * Called after User is fetched from Frugalist API
     * @param responseUser
     */
    private void onUserFetched(FrugalistResponse.User responseUser) {
        User user = new User(responseUser);

        // set User in preferences
        UserHelper.saveCurrentUser(user, LoginActivity.this);
        UserHelper.setLoggedIn(true);

        // show welcome message
        Toast.makeText(LoginActivity.this, "Welcome " + user.getName(), Toast.LENGTH_LONG).show();

        // switch to main list view
        Intent intent = new Intent(LoginActivity.this, MainListActivity.class);
        startActivity(intent);
        finish();
    }

    /** Callback for Frugalist API User fetch */
    Callback<FrugalistResponse.User> mFrugalistUserCallback = new Callback<FrugalistResponse.User>() {

        @Override
        public void onResponse(Call<FrugalistResponse.User> call,
                               Response<FrugalistResponse.User> response
        ) {
            if (response.isSuccess()) {

                // User fetched
                FrugalistResponse.User user = response.body();
                Log.i(TAG, user.toString());
                onUserFetched(user);

            } else {
                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {/* not handling */}
            }

            mProgressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<FrugalistResponse.User> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            mProgressDialog.dismiss();
        }
    };

    /**********************************************************************
     * Facebook Profile fetch
     **********************************************************************/

    /**
     * Called after Facebook Profile is ready to use
     * @param profile
     */
    private void onProfileReady(Profile profile) {

        // immediately call Frugalist API to get user data (or create)
        FrugalistServiceHelper.doGetOrCreateUser(mFrugalistUserCallback, profile.getId(), profile.getName());

    }

    /**
     * For testing, no remote DB connection
     * @param profile
     */
    private void onProfileReadyTesting(Profile profile) {
        User user = new User(profile.getId(), profile.getName(), new HashSet<Long>());

        // set User in preferences
        UserHelper.saveCurrentUser(user, LoginActivity.this);
        UserHelper.setLoggedIn(true);

        // show welcome message
        Toast.makeText(LoginActivity.this,"Welcome " + user.getName(), Toast.LENGTH_LONG).show();

        mProgressDialog.dismiss();

        // switch to main list view
        Intent intent = new Intent(LoginActivity.this, MainListActivity.class);
        startActivity(intent);
        finish();
    }

    /** Callback for Facebook login */
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            mProgressDialog.show();

            if(Profile.getCurrentProfile() == null) {

                // start profile tracker to wait for facebook profile to be ready
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                        // profile2 is the new profile
                        onProfileReady(profile2);
                        //onProfileReadyTesting(profile2);
                        mProfileTracker.stopTracking();
                    }
                };
                mProfileTracker.startTracking();

            }
            else {
                // Profile is ready, just continue
                Profile profile = Profile.getCurrentProfile();
                onProfileReady(profile);
                //onProfileReadyTesting(profile);
            }


// Switched to using the Profile API for simplicity, graph requests may come in handy later though

//            GraphRequest request = GraphRequest.newMeRequest(
//                    loginResult.getAccessToken(),
//                    new GraphRequest.GraphJSONObjectCallback() {
//                        @Override
//                        public void onCompleted(
//                                JSONObject object,
//                                GraphResponse response) {
//
//                            FrugalistServiceHelper.doGetOrCreateUser(
//                                    mFrugalistUserCallback,
//                                    object.getString("id"),
//                                    object.getString("name"));
//
//                            Log.i(TAG, response + "");
//                            try {
//                                user = new User(
//                                        object.getString("id"),
//                                        object.getString("name"),
//                                        new HashSet<Long>());
//                                UserHelper.saveCurrentUser(user, LoginActivity.this);
//                                UserHelper.setLoggedIn(true);
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                            Toast.makeText(LoginActivity.this,"Welcome " + user.getName(), Toast.LENGTH_LONG).show();
//
//                            Intent intent = new Intent(LoginActivity.this, MainListActivity.class);
//                            startActivity(intent);
//                            mProgressDialog.dismiss();
//                            finish();
//                        }
//                    });
//
//            Bundle parameters = new Bundle();
//            parameters.putString("fields", "id, name, email, gender, birthday");
//            request.setParameters(parameters);
//            request.executeAsync();

        }

        @Override
        public void onCancel() {
            Log.i(TAG, "FB - cancelled");
        }

        @Override
        public void onError(FacebookException e) {
            Log.i(TAG, "FB - error: " + e.getMessage());
        }
    };

}
