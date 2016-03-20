package com.ryce.frugalist.view.detail;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingDetailActivity extends AppCompatActivity {

    private static final String TAG = ListingDetailActivity.class.getSimpleName();

    public static final String ARG_LISTING_TYPE = "listing_type";
    public static final String ARG_LISTING_ID = "listing_id";

    private ListingType mType;
    private AbstractListing mListing;

    ImageView mImageView;
    TextView mProductText;
    TextView mPriceText;
    TextView mStoreText;
    TextView mRatingText;
    TextView mAddressText;
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // init progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.dialog_loading));

        // get views
        mImageView = (ImageView) findViewById(R.id.detailImage);
        mProductText = (TextView) findViewById(R.id.productText);
        mPriceText = (TextView) findViewById(R.id.priceText);
        mStoreText = (TextView) findViewById(R.id.storeNameText);
        mRatingText = (TextView) findViewById(R.id.ratingText);
        mAddressText = (TextView) findViewById(R.id.storeAddressText);

        // get type of listing we are displaying
        mType = (ListingType) getIntent().getExtras().get(ARG_LISTING_TYPE);

        if (mType == ListingType.DEAL) {

            // fetch deal by id
            //UUID id = (UUID) getIntent().getExtras().get(ARG_LISTING_ID);
            Long id = (Long) getIntent().getExtras().get(ARG_LISTING_ID);

            // fetch deal by id
            executeFetchDeal(id);
            mProgressDialog.show();

//            mListing = MockDatastore.getInstance().getDeal(id);
//            final Deal deal = (Deal) mListing;
//
//            // Load image via URL
//            Picasso p = Picasso.with(this);
//            p.setIndicatorsEnabled(true);
//            p.load(deal.getImageUrl()).into(mImageView);
//
//            // display data
//            mProductText.setText(deal.getProduct());
//            mPriceText.setText(deal.getFormattedPrice());
//            mStoreText.setText(deal.getStore());
//            mAddressText.setText(deal.getAddress());
//            mRatingText.setText(deal.getFormattedRating());
//            mRatingText.setTextColor(deal.getRatingColour());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabBookmark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == ListingType.DEAL) {
                    MockDatastore.getInstance().addBookmark((Deal) mListing);
                }
                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getMapViewIntent(mListing.getAddress());
                startActivity(intent);
            }
        });

        // we want a back button in task bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Create a google maps intent
     * @param address
     * @return
     */
    public static Intent getMapViewIntent(String address) {
        Intent intent = null;
        try {
            Uri url = Uri.parse(
                    String.format("geo:0,0?q=%s", URLEncoder.encode(address, "UTF-8")));
            intent = new Intent(Intent.ACTION_VIEW, url);
            intent.setPackage("com.google.android.apps.maps");
        } catch (UnsupportedEncodingException e) {
            // Not handling this because UTF-8 is always supported
            // http://developer.android.com/reference/java/nio/charset/Charset.html
        }
        return intent;
    }

    /**
     * Fetch deal by id
     * @param id
     */
    private void executeFetchDeal(Long id) {
        FrugalistServiceHelper.doGetDealById(mFrugalistDealCallback, id);
    }

    private void onFetchDealComplete(FrugalistResponse.Deal resDeal) {
        Deal deal = new Deal(resDeal);
        mListing = deal;

        // Load image via URL
        Picasso p = Picasso.with(this);
        //p.setIndicatorsEnabled(true);
        p.load(deal.getImageUrl()).into(mImageView);

        // display data
        mProductText.setText(deal.getProduct());
        mPriceText.setText(deal.getFormattedPrice());
        mStoreText.setText(deal.getStore());
        mAddressText.setText(deal.getAddress());
        mRatingText.setText(deal.getFormattedRating());
        mRatingText.setTextColor(deal.getRatingColour());

    }

    // callback for deal
    Callback<FrugalistResponse.Deal> mFrugalistDealCallback = new Callback<FrugalistResponse.Deal>() {
        @Override
        public void onResponse(
                Call<FrugalistResponse.Deal> call,
                Response<FrugalistResponse.Deal> response
        ) {

            if (response.isSuccess()) {

                FrugalistResponse.Deal deal = response.body();
                Log.i(TAG, deal.toString());
                onFetchDealComplete(deal);

            } else {
                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {/* not handling */}
            }

            mProgressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<FrugalistResponse.Deal> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            mProgressDialog.dismiss();
        }

    };
}
