package com.ryce.frugalist.view.list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.User;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.view.detail.ListingDetailActivity;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter.ListSection;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Tony on 2016-02-07.
 *
 * Recycler list view adapter for the main list section
 */
public class ListSectionRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ListSectionRecyclerAdapter.class.getSimpleName();

    private final Activity mActivity;
    private List<AbstractListing> mItems;
    private final ListingType mItemType;
    private final ListSection mListSection;

    public ListSectionRecyclerAdapter(Activity activity,
            List<AbstractListing> items, ListingType itemType, ListSection listSection) {
        mActivity = activity;
        mItems = items;
        mItemType = itemType;
        mListSection = listSection;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ListingType.DEAL.toInteger()) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item_deal, parent, false);
            return new DealViewHolder(view);

        } else if (viewType == ListingType.EMPTY.toInteger()) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item_empty, parent, false);
            return new EmptyViewHolder(view);

        } else {
            // TODO: what is the default ??
            return null;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof DealViewHolder) {

            final Deal deal = (Deal) mItems.get(position);
            final DealViewHolder dealHolder = (DealViewHolder) holder;

            dealHolder.mDealTextView.setText(deal.getProduct());
            dealHolder.mPriceTextView.setText(deal.getFormattedPrice());
            dealHolder.mStoreTextView.setText(deal.getStore());
            dealHolder.mRatingTextView.setText(deal.getFormattedRating());
            dealHolder.mRatingTextView.setTextColor(deal.getRatingColour());

            User user = UserHelper.getCurrentUser(mActivity);

            // mark if deal has been bookmarked
            if (user.getBookmarks().contains(deal.getId())) {
                dealHolder.mBookmarkImgView.setVisibility(View.VISIBLE);
            } else {
                dealHolder.mBookmarkImgView.setVisibility(View.INVISIBLE);
            }

            // mark if user is author or not
            if (deal.getAuthorId().equals(user.getId())) {
                dealHolder.mAuthorImgView.setVisibility(View.VISIBLE);
            } else {
                dealHolder.mAuthorImgView.setVisibility(View.INVISIBLE);
            }

            // load image via URL
            // note: we get height of picture frame (in pixels)
            final int imgHeight = (int) mActivity.getResources().getDimension(R.dimen.list_item_height);
            Picasso p = Picasso.with(dealHolder.mView.getContext());
            //p.setIndicatorsEnabled(true);
            p.load(deal.getThumbnailUrl())
                    .error(android.R.drawable.ic_delete)
                    .resize(imgHeight, imgHeight)
                    .placeholder(R.drawable.loader)
                    .into(dealHolder.mImageView);

            // on click, go to details view
            dealHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ListingDetailActivity.class);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_TYPE, mItemType);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_ID, deal.getId());
                    context.startActivity(intent);
                }
            });

            // on long click, add bookmark
            dealHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListSection == ListSection.SAVED) {
                        // in the book marks section, we delete instead
                        executeAddOrDeleteBookmark(UserHelper.getCurrentUser(mActivity).getId(),
                                deal.getId(), false);

                        // remove item from data list
                        mItems.remove(position);
                        notifyDataSetChanged();
                    } else {
                        // in a deals section, we add a bookmark
                        // check that user has not already bookmarked
                        if (!UserHelper.getCurrentUser(mActivity).getBookmarks().contains(deal.getId())) {
                            executeAddOrDeleteBookmark(UserHelper.getCurrentUser(mActivity).getId(),
                                    deal.getId(), true);
                        } else {
                            Snackbar.make(mActivity.findViewById(android.R.id.content), "Bookmarked already!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                        // make bookmark icon visible
                        dealHolder.mBookmarkImgView.setVisibility(View.VISIBLE);
                    }
                    return true;
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        // handle empty case
        if (mItems.isEmpty()) {
            return 1;
        }
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        // handle empty case
        if (mItems.isEmpty()) {
            return ListingType.EMPTY.toInteger();
        }
        return mItemType.toInteger();
    }

    /**
     * Replace data in the list
     * @param list
     */
    public void replaceData(List<? extends AbstractListing> list){
        if (mItems != null) {
            mItems.clear();
            mItems.addAll(list);
        }
        else {
            mItems = new ArrayList<>(list);
        }
        notifyDataSetChanged();
    }

    /**
     * Get parcelable array list of deals
     * @return
     */
    public ArrayList<Deal> getParcelableDealArrayList() {
        if (mItemType == ListingType.DEAL) {
            ArrayList<Deal> deals = new ArrayList<>(mItems.size());
            for (AbstractListing item : mItems) {
                deals.add((Deal) item);
            }
            return deals;
        } else {
            return null;
        }
    }

    /**
     * Stores the view layout for a deal list item
     */
    public class DealViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mDealTextView;
        public final TextView mPriceTextView;
        public final TextView mStoreTextView;
        public final TextView mRatingTextView;
        public final ImageView mImageView;
        public final ImageView mBookmarkImgView;
        public final ImageView mAuthorImgView;

        public DealViewHolder(View view) {
            super(view);
            mView = view;
            mDealTextView = (TextView) view.findViewById(R.id.productText);
            mPriceTextView = (TextView) view.findViewById(R.id.priceText);
            mStoreTextView = (TextView) view.findViewById(R.id.storeText);
            mRatingTextView = (TextView) view.findViewById(R.id.ratingText);
            mImageView = (ImageView) view.findViewById(R.id.dealThumb);
            mBookmarkImgView = (ImageView) view.findViewById(R.id.bookmarkImg);
            mAuthorImgView = (ImageView) view.findViewById(R.id.authorImg);
        }

    }

    /**
     * Stores the view layout of an empty item
     */
    public class EmptyViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView mEmptyText;

        public EmptyViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mEmptyText = (TextView) view.findViewById(R.id.emptyText);
        }

    }

    /**********************************************************************
     * Deal bookmarking
     **********************************************************************/

    /**
     * Update user model, adding/deleting a bookmark of this deal
     * @param userId
     * @param dealId
     * @param add false = delete, true = add
     */
    private void executeAddOrDeleteBookmark(String userId, long dealId, boolean add) {
        FrugalistServiceHelper.doAddOrDeleteBookmark(mFrugalistUserCallback, userId, dealId, add);
    }

    /**
     * Called after user has been updated
     * @param resUser
     */
    private void onUserUpdated(FrugalistResponse.User resUser) {
        // convert model
        User user = new User(resUser);

        // update the current user model
        UserHelper.saveCurrentUser(user, mActivity);

        // show notification for bookmark add/delete
        if (mListSection == ListSection.SAVED) {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Removed Bookmark", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        } else {
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Bookmarked!", Snackbar.LENGTH_LONG)
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
        }

        @Override
        public void onFailure(Call<FrugalistResponse.User> call, Throwable t) {
            Log.i(TAG, "Error: " + t.getMessage());
            Snackbar.make(mActivity.findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    };

}
