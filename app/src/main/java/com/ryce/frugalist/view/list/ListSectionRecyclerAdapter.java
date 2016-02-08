package com.ryce.frugalist.view.list;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.view.detail.ListingDetailActivity;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;

import java.util.List;

/**
 * Created by Tony on 2016-02-07.
 *
 * Recycler list view adapter for the main list section
 */
public class ListSectionRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<AbstractListing> mItems;
    private final ListingType mItemType;

    public ListSectionRecyclerAdapter(List<AbstractListing> items, ListingType itemType) {
        mItems = items;
        mItemType = itemType;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ListingType.DEAL.toInteger()) {

            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
            return new DealViewHolder(view);

        } else if (viewType == ListingType.FREEBIE.toInteger()) {

            // TODO: layout not supported now
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
            return new FreebieViewHolder(view);

        } else {

            // TODO: what is the default ??
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item, parent, false);
            return new FreebieViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (mItemType == ListingType.DEAL) {

            final Deal deal = (Deal) mItems.get(position);
            final DealViewHolder dealHolder = (DealViewHolder) holder;

            dealHolder.mDealTextView.setText(deal.getProduct());
            dealHolder.mPriceTextView.setText(deal.getFormattedPrice());
            dealHolder.mStoreTextView.setText(deal.getStore());
            dealHolder.mRatingTextView.setText(deal.getFormattedRating());
            dealHolder.mRatingTextView.setTextColor(deal.getRatingColour());
            // TODO: hard coded dims...
            dealHolder.mImageView.setImageBitmap(deal.getThumbnail(80, 80));

            dealHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ListingDetailActivity.class);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_TYPE, mItemType);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_DATA, position);
                    context.startActivity(intent);
                }
            });

        } else if (mItemType == ListingType.FREEBIE) {
            //TODO: not built yet
        }


    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mItemType.toInteger();
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
        public Deal mDeal;

        public DealViewHolder(View view) {
            super(view);
            mView = view;
            mDealTextView = (TextView) view.findViewById(R.id.dealText);
            mPriceTextView = (TextView) view.findViewById(R.id.priceText);
            mStoreTextView = (TextView) view.findViewById(R.id.storeText);
            mRatingTextView = (TextView) view.findViewById(R.id.ratingText);
            mImageView= (ImageView) view.findViewById(R.id.dealThumb);
        }

        @Override
        public String toString() { return super.toString() + " '" + mDeal.getProduct() + "'"; }
    }

    /**
     * Stores the view layout for a freebie list item
     */
    public class FreebieViewHolder extends RecyclerView.ViewHolder {

        // TODO: not valid ATM, still need to decide on freebie item layout
        public final View mView;
        public final TextView mDealTextView;
        public final TextView mPriceTextView;
        public final TextView mStoreTextView;
        public final TextView mRatingTextView;
        public final ImageView mImageView;
        public Deal mDeal;

        public FreebieViewHolder(View view) {
            super(view);
            mView = view;
            mDealTextView = (TextView) view.findViewById(R.id.dealText);
            mPriceTextView = (TextView) view.findViewById(R.id.priceText);
            mStoreTextView = (TextView) view.findViewById(R.id.storeText);
            mRatingTextView = (TextView) view.findViewById(R.id.ratingText);
            mImageView= (ImageView) view.findViewById(R.id.dealThumb);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDeal.getProduct() + "'";
        }
    }

}
