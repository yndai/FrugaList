package com.ryce.frugalist.view.settings;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Settings;
import com.ryce.frugalist.util.UserHelper;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private Settings mSettings;

    SeekBar mRadiusSeek;
    SeekBar mRatingSeek;
    SeekBar mQualitySeek;
    TextView mRadiusValue;
    TextView mRatingValue;
    TextView mQualityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // get controls
        mRadiusSeek = (SeekBar) findViewById(R.id.radiusSeek);
        mRatingSeek = (SeekBar) findViewById(R.id.ratingSeek);
        mQualitySeek = (SeekBar) findViewById(R.id.qualitySeek);
        mRadiusValue = (TextView) findViewById(R.id.radiusValue);
        mRatingValue = (TextView) findViewById(R.id.ratingValue);
        mQualityValue = (TextView) findViewById(R.id.qualityValue);

        // set seek maximums
        mRadiusSeek.setMax(Settings.RADIUS_MAX - Settings.RADIUS_MIN);
        mRatingSeek.setMax(Settings.RATING_MAX - Settings.RATING_MIN);
        mQualitySeek.setMax((Settings.QUALITY_MAX - Settings.QUALITY_MIN) / 10);

        // get user settings
        mSettings = UserHelper.getUserSettings(this);

        // set initial values
        mRadiusSeek.setProgress(mSettings.getSearchRadius() - Settings.RADIUS_MIN);
        mRadiusValue.setText(mSettings.getFormattedSearchRadius());

        mRatingSeek.setProgress(mSettings.getRatingThreshold() - Settings.RATING_MIN);
        mRatingValue.setText(mSettings.getFormattedRatingThreshold());

        mQualitySeek.setProgress((mSettings.getUploadQuality() - Settings.QUALITY_MIN) / 10);
        mQualityValue.setText(mSettings.getFormattedUploadQuality());

        // radius seek bar listener
        mRadiusSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer radius = progress + Settings.RADIUS_MIN;
                mSettings.setSearchRadius(radius);
                mRadiusValue.setText(mSettings.getFormattedSearchRadius());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Integer radius = seekBar.getProgress() + Settings.RADIUS_MIN;
                UserHelper.saveUserSettings(mSettings, SettingsActivity.this);
                Log.i(TAG, "Commit radius " + radius.toString());
            }
        });

        // rating seek bar listener
        mRatingSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer rating = progress + Settings.RATING_MIN;
                mSettings.setRatingThreshold(rating);
                mRatingValue.setText(mSettings.getFormattedRatingThreshold());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Integer rating = seekBar.getProgress() + Settings.RATING_MIN;
                UserHelper.saveUserSettings(mSettings, SettingsActivity.this);
                Log.i(TAG, "Commit rating " + rating.toString());
            }
        });

        // rating seek bar listener
        mQualitySeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Integer quality = progress * 10 + Settings.QUALITY_MIN;
                mSettings.setUploadQuality(quality);
                mQualityValue.setText(mSettings.getFormattedUploadQuality());
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Integer quality = seekBar.getProgress() * 10 + Settings.QUALITY_MIN;
                UserHelper.saveUserSettings(mSettings, SettingsActivity.this);
                Log.i(TAG, "Commit quality " + quality.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // when menu back button pressed, just finish()
            onBackPressed();
            return true;
        }
        return false;
    }

}
