package com.ryce.frugalist.view.detail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.UserHelper;
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

    public static final int UP_VOTE_COLOR = Color.parseColor("#8bc34a");
    public static final int DOWN_VOTE_COLOR = Color.parseColor("#d32f2f");
    public static final int DISABLED_VOTE_COLOR = Color.GRAY;

    private ListingType mType;
    private Deal mDeal;

    ImageView mImageView;
    TextView mProductText;
    TextView mPriceText;
    TextView mCreatedText;
    TextView mStoreText;
    TextView mRatingText;
    TextView mAddressText;
    ImageButton mUpButton;
    ImageButton mDownButton;
    Button mDeleteButton;
    FloatingActionButton mBookmarkButton;
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
        mCreatedText = (TextView) findViewById(R.id.createdText);
        mStoreText = (TextView) findViewById(R.id.storeNameText);
        mRatingText = (TextView) findViewById(R.id.ratingText);
        mAddressText = (TextView) findViewById(R.id.storeAddressText);
        mDeleteButton = (Button) findViewById(R.id.deleteButton);
        mUpButton = (ImageButton) findViewById(R.id.upButton);
        mDownButton = (ImageButton) findViewById(R.id.downButton);
        mBookmarkButton = (FloatingActionButton) findViewById(R.id.fabBookmark);

        // get type of listing we are displaying
        mType = (ListingType) getIntent().getExtras().get(ARG_LISTING_TYPE);

        if (mType == ListingType.DEAL) {

            // fetch deal by id
            //UUID id = (UUID) getIntent().getExtras().get(ARG_LISTING_ID);
            Long id = (Long) getIntent().getExtras().get(ARG_LISTING_ID);

            // fetch deal by id
            executeFetchDeal(id);
            mProgressDialog.show();

//            mDeal = MockDatastore.getInstance().getDeal(id);
//            final Deal deal = (Deal) mDeal;
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

        // init vote buttons
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeDealUpdateRating(
                        mDeal.getId(),
                        UserHelper.getCurrentUser(ListingDetailActivity.this).getId(),
                        true);
                mProgressDialog.show();
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeDealUpdateRating(
                        mDeal.getId(),
                        UserHelper.getCurrentUser(ListingDetailActivity.this).getId(),
                        false);
                mProgressDialog.show();
            }
        });

        // init bookmark button
        mBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mType == ListingType.DEAL) {
                    MockDatastore.getInstance().addBookmark(mDeal);
                }
                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // init show in map button
        Button mapButton = (Button) findViewById(R.id.mapButton);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getMapViewIntent(mDeal.getAddress());
                startActivity(intent);
            }
        });

        // we want a back button in task bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Populate view with data
     * @param deal
     */
    private void refreshData(Deal deal) {
        mDeal = deal;

        // Load image via URL
        Picasso p = Picasso.with(this);
        //p.setIndicatorsEnabled(true);
        p.load(deal.getImageUrl()).into(mImageView);

        // display data
        mProductText.setText(deal.getProduct());
        mPriceText.setText(deal.getFormattedPrice());
        mCreatedText.setText(deal.getFormattedDate());
        mStoreText.setText(deal.getStore());
        mAddressText.setText(deal.getAddress());
        mRatingText.setText(deal.getFormattedRating());
        mRatingText.setTextColor(deal.getRatingColour());

        // get user
        User user = UserHelper.getCurrentUser(this);

        // if user is author, enable delete button
        if (deal.getAuthorId().equals(user.getId())) {
            mDeleteButton.setVisibility(View.VISIBLE);
        } else {
            mDeleteButton.setVisibility(View.GONE);
        }

        // set state of vote buttons
        if (deal.getVotes() != null) {
            Boolean vote = deal.getVotes().get(user.getId());
            if (vote == null) {
                // not voted
                mUpButton.setEnabled(true);
                mUpButton.setBackgroundColor(UP_VOTE_COLOR);
                mDownButton.setEnabled(true);
                mDownButton.setBackgroundColor(DOWN_VOTE_COLOR);
            } else if (vote) {
                // up-voted
                mUpButton.setEnabled(false);
                mUpButton.setBackgroundColor(DISABLED_VOTE_COLOR);
                mDownButton.setEnabled(true);
                mDownButton.setBackgroundColor(DOWN_VOTE_COLOR);
            } else {
                // down-voted
                mUpButton.setEnabled(true);
                mUpButton.setBackgroundColor(UP_VOTE_COLOR);
                mDownButton.setEnabled(false);
                mDownButton.setBackgroundColor(DISABLED_VOTE_COLOR);
            }
        }
    }

    /**********************************************************************
     * Fetching single Deal data
     **********************************************************************/

    /**
     * Fetch deal by id
     * @param id
     */
    private void executeFetchDeal(long id) {
        FrugalistServiceHelper.doGetDealById(mFrugalistDealCallback, id);
    }

    /**
     * Called once deal has been fetched
     * @param resDeal
     */
    private void onFetchDealComplete(FrugalistResponse.Deal resDeal) {
        Deal deal = new Deal(resDeal);
        refreshData(deal);
    }

    // callback for deal
    Callback<FrugalistResponse.Deal> mFrugalistDealCallback = new Callback<FrugalistResponse.Deal>() {
        @Override
        public void onResponse(Call<FrugalistResponse.Deal> call,
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

    /**********************************************************************
     * Adding deal to bookmarks
     **********************************************************************/


    /**********************************************************************
     * Updating Deal rating
     **********************************************************************/

    /**
     * Update deal rating
     * @param id
     */
    private void executeDealUpdateRating(long id, String userId, boolean upvote) {
        FrugalistServiceHelper.doUpdateDealRating(mFrugalistDealCallback, id, userId, upvote);
    }

    /**********************************************************************
     * Deleting Deal
     **********************************************************************/

    /**
     * Display a confirm dialog for deletion
     */
    private void showConfirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage("Are you sure you wish to delete this listing?")
                .setPositiveButton("Yes",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Yes-code
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int id) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**********************************************************************
     * Showing address in Maps app
     **********************************************************************/

    /**
     * Create a google maps intent on an address
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
}
