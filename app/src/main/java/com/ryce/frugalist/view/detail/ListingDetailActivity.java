package com.ryce.frugalist.view.detail;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.ApplicationState;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import bolts.AppLinks;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListingDetailActivity extends AppCompatActivity {

    private static final String TAG = ListingDetailActivity.class.getSimpleName();

    public static final String ARG_LISTING_TYPE = "listing_type";
    public static final String ARG_LISTING_ID = "listing_id";
    public static final String ARG_LISTING_INDEX = "listing_index";

    public static final int UP_VOTE_COLOR = Color.parseColor("#8bc34a");
    public static final int DOWN_VOTE_COLOR = Color.parseColor("#d32f2f");
    public static final int DISABLED_VOTE_COLOR = Color.GRAY;

    private ListingType mType;
    private Deal mDeal;
    private boolean mBookmarked;

    RelativeLayout mDetailsLayout;
    ImageView mImageView;
    ImageView mBookmarkIconImg;
    ImageView mAuthorIconImg;
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

    // for the facebook sharing
    CallbackManager callbackManager;
    ShareDialog shareDialog;
    ShareButton mShareButton;

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
        mDetailsLayout = (RelativeLayout) findViewById(R.id.detailContentLayout);
        mBookmarkIconImg = (ImageView) findViewById(R.id.bookmarkImg);
        mAuthorIconImg = (ImageView) findViewById(R.id.authorImg);
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
        mShareButton = (ShareButton) findViewById(R.id.fb_share_button);

        // hide layout initially
        mDetailsLayout.setVisibility(View.INVISIBLE);

        // get type of listing we are displaying
        mType = (ListingType) getIntent().getExtras().get(ARG_LISTING_TYPE);

        // fetch deal by ID
        if (mType == ListingType.DEAL) {

            // get id from extras
            Long id = (Long) getIntent().getExtras().get(ARG_LISTING_ID);

            // fetch deal by id
            mProgressDialog.show();
            executeFetchDeal(id);
        }

        // init vote buttons
        mUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeDealUpdateRating(true);
            }
        });
        mDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeDealUpdateRating(false);
            }
        });

        // init bookmark button listener
        mBookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                User user = UserHelper.getCurrentUser(ListingDetailActivity.this);
                if (!user.getBookmarks().contains(mDeal.getId())) {
                    // if user has not already added bookmark, add it
                    executeAddOrDeleteBookmark(true);
                } else {
                    // user has already added this bookmark, delete it
                    executeAddOrDeleteBookmark(false);
                }
            }
        });

        // init facebook SDK for the share button's dialog
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this); // initialize facebook shareDialog

        // catches the app requests for individual deals
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(this, getIntent());
        if (targetUrl != null) {
            Log.i("Activity", "App Link Target URL: " + targetUrl.toString());
        }

        // init delete button
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDeleteDialog();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // when menu back button pressed, just finish()
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Toggle bookmark flag
     * @param saved true to indicate bookmark saved
     */
    private void toggleBookmark(boolean saved) {

        final ColorStateList BOOKMARK_ADD_COLOR = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorAccent));
        final ColorStateList BOOKMARK_DEL_COLOR = ContextCompat.getColorStateList(this, android.R.color.holo_red_light);
        final Drawable BOOKMARK_ADD_ICON = ContextCompat.getDrawable(this, android.R.drawable.ic_input_add);
        final Drawable BOOKMARK_DEL_ICON = ContextCompat.getDrawable(this, android.R.drawable.ic_delete);

        if (saved) {

            mBookmarkIconImg.setVisibility(View.VISIBLE);
            mBookmarkButton.setBackgroundTintList(BOOKMARK_DEL_COLOR);
            mBookmarkButton.setImageDrawable(BOOKMARK_DEL_ICON);
            mBookmarked = true;

        } else {

            mBookmarkIconImg.setVisibility(View.INVISIBLE);
            mBookmarkButton.setBackgroundTintList(BOOKMARK_ADD_COLOR);
            mBookmarkButton.setImageDrawable(BOOKMARK_ADD_ICON);
            mBookmarked = false;

        }
    }

    /**
     * Populate view with data
     * @param deal
     */
    private void refreshData(Deal deal) {
        mDeal = deal;

        // if image has changed, then reload
        if (!deal.getImageUrl().equals(mImageView.getTag(R.id.tag_image_url))) {
            // Load image via URL
            Picasso p = Picasso.with(this);
            //p.setIndicatorsEnabled(true);
            p.load(deal.getImageUrl()).into(mImageView);
            // store image url as a tag
            mImageView.setTag(R.id.tag_image_url, deal.getImageUrl());
        }

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

        // if user is author, enable delete button and show author icon
        if (deal.getAuthorId().equals(user.getId())) {
            mDeleteButton.setVisibility(View.VISIBLE);
            mAuthorIconImg.setVisibility(View.VISIBLE);
        } else {
            mDeleteButton.setVisibility(View.GONE);
            mAuthorIconImg.setVisibility(View.INVISIBLE);
        }

        // if user has bookmarked, show bookmark icon
        if (user.getBookmarks().contains(deal.getId())) {
            mBookmarkIconImg.setVisibility(View.VISIBLE);
        } else {
            mBookmarkIconImg.setVisibility(View.INVISIBLE);
        }

        // set bookmark button state
        toggleBookmark(user.getBookmarks().contains(deal.getId()));

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

        // activates the fb share button
        String contentDescription =
                mDeal.getStore() + " @ " + mDeal.getAddress() + "\n" +
                        mDeal.getProduct() + ": " + mDeal.getPrice() + "/" + mDeal.getUnit();
        String imgurPage = mDeal.getImageUrl().substring(0,mDeal.getImageUrl().lastIndexOf("."));

        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setContentTitle("FrugaList - Share your favourite deals!")
                .setContentDescription(Uri.decode(contentDescription))
                .setImageUrl(Uri.parse(mDeal.getImageUrl()))
                .setContentUrl(Uri.parse(imgurPage))
                .build();

        mShareButton.setShareContent(linkContent); // enables the button

        // unhide view
        mDetailsLayout.setVisibility(View.VISIBLE);
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

    /** callback for Frugalist API deal fetch */
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

    /**
     * Update user model, adding or deleting a bookmark of this deal
     */
    private void executeAddOrDeleteBookmark(boolean add) {
        FrugalistServiceHelper.doAddOrDeleteBookmark(mFrugalistUserCallback,
                UserHelper.getCurrentUser(this).getId(), mDeal.getId(), add);
    }

    /**
     * Called after user has been updated
     * @param resUser
     */
    private void onUserUpdated(FrugalistResponse.User resUser) {
        // convert model
        User user = new User(resUser);

        // update the current user model
        UserHelper.saveCurrentUser(user, this);

        if (mBookmarked) {
            // now NOT bookmarked
            toggleBookmark(false);

            // show notification for bookmark
            Snackbar.make(findViewById(android.R.id.content), "Removed bookmark", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            // now bookmarked
            toggleBookmark(true);

            // show notification for bookmark
            Snackbar.make(findViewById(android.R.id.content), "Bookmarked!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

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
                onUserUpdated(user);

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
     * Updating Deal rating
     **********************************************************************/

    /**
     * Update Deal rating
     * @param upvote
     */
    private void executeDealUpdateRating(boolean upvote) {
        FrugalistServiceHelper.doUpdateDealRating(mFrugalistDealUpdateCallback,
                mDeal.getId(), UserHelper.getCurrentUser(this).getId(), upvote);
    }


    /**
     * Called once deal has been updated
     * @param resDeal
     */
    private void onDealUpdateComplete(FrugalistResponse.Deal resDeal) {
        Deal deal = new Deal(resDeal);
        refreshData(deal);

        // mark main list as stale
        ((ApplicationState) getApplicationContext()).setMainListDataIsStale(true);
    }

    /** callback for Frugalist API deal fetch */
    Callback<FrugalistResponse.Deal> mFrugalistDealUpdateCallback = new Callback<FrugalistResponse.Deal>() {
        @Override
        public void onResponse(Call<FrugalistResponse.Deal> call,
                               Response<FrugalistResponse.Deal> response
        ) {
            if (response.isSuccess()) {

                FrugalistResponse.Deal deal = response.body();
                Log.i(TAG, deal.toString());
                onDealUpdateComplete(deal);

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
     * Deleting Deal
     **********************************************************************/

    /**
     * Display a confirm dialog for deletion
     */
    private void showConfirmDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you wish to delete this listing?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // do delete
                    mProgressDialog.show();
                    executeDealDelete();
                    dialog.cancel();
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            })
            .show();
    }

    /**
     * Delete this deal
     */
    private void executeDealDelete() {
        FrugalistServiceHelper.doDeleteDeal(mFrugalistDeleteCallback, mDeal.getId());
    }

    /**
     * Called after deal deletion complete
     */
    private void onDealDeleted() {

        // show deleted message
        Toast.makeText(this, "Deal deleted", Toast.LENGTH_LONG).show();

        // mark main list as stale
        ((ApplicationState) getApplicationContext()).setMainListDataIsStale(true);

        // go back to main list
        onBackPressed();

    }

    /** Callback for Frugalist API User fetch */
    Callback<FrugalistResponse.ResponseMsg> mFrugalistDeleteCallback = new Callback<FrugalistResponse.ResponseMsg>() {
        @Override
        public void onResponse(Call<FrugalistResponse.ResponseMsg> call,
                               Response<FrugalistResponse.ResponseMsg> response
        ) {
            if (response.isSuccess()) {

                // User fetched
                FrugalistResponse.ResponseMsg msg = response.body();
                Log.i(TAG, msg.msg);
                onDealDeleted();

            } else {
                try {
                    Log.i(TAG, "Error: " + response.errorBody().string());
                } catch (IOException e) {/* not handling */}
            }

            mProgressDialog.dismiss();
        }

        @Override
        public void onFailure(Call<FrugalistResponse.ResponseMsg> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            mProgressDialog.dismiss();
        }
    };

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
