package com.ryce.frugalist.view.list;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.AbstractListing;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.view.detail.ListingDetailActivity;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter.ListSection;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Tony on 2016-02-07.
 *
 * Recycler list view adapter for the main list section
 */
public class ListSectionRecyclerAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final List<AbstractListing> mItems;
    private final ListingType mItemType;
    private final ListSection mListSection;

    public ListSectionRecyclerAdapter(Context context,
            List<AbstractListing> items, ListingType itemType, ListSection listSection) {
        mContext = context;
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

        } else if (viewType == ListingType.FREEBIE.toInteger()) {

            // TODO: layout not supported now
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item_deal, parent, false);
            return new FreebieViewHolder(view);

        } else {

            // TODO: what is the default ??
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_list_item_deal, parent, false);
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

            // load image via URL
            // note we get height of picture frame (in pixels)
            int height = (int) mContext.getResources().getDimension(R.dimen.list_item_height);
            Picasso p = Picasso.with(dealHolder.mView.getContext());
            //p.setIndicatorsEnabled(true);
            p.load(deal.getThumbnailUrl())
                    .error(android.R.drawable.ic_delete)
                    .resize(height, height)
                    .placeholder(R.drawable.loader)
                    .into(dealHolder.mImageView);

            dealHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, ListingDetailActivity.class);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_TYPE, mItemType);
                    intent.putExtra(ListingDetailActivity.ARG_LISTING_DATA, deal.getId());
                    context.startActivity(intent);
                }
            });
            dealHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (mListSection == ListSection.SAVED) {
                        // in the book marks section, we delete instead
                        MockDatastore.getInstance().removeBookmark(deal);
                        Snackbar.make(v, "Removed bookmark", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    } else {
                        // in a deals section, we add a bookmark
                        MockDatastore.getInstance().addBookmark(deal);
                        Snackbar.make(v, "Bookmarked!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    return true;
                }
            });

        } else if (mItemType == ListingType.FREEBIE) {
            //TODO: not handling
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

    public void addItem(Deal deal) {
        mItems.add(deal);
        notifyDataSetChanged();
    }

    public void removeItem(Deal deal) {
        mItems.remove(deal);
        notifyDataSetChanged();
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
            mDealTextView = (TextView) view.findViewById(R.id.productText);
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

    /**
     * Stores the view layout for a freebie list item
     *
     * TODO: Not used
     */
    public class FreebieViewHolder extends RecyclerView.ViewHolder {

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
            mDealTextView = (TextView) view.findViewById(R.id.productText);
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
