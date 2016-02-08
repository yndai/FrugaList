package com.ryce.frugalist.view.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;

import java.util.List;

/**
 * Created by Tony on 2016-02-06.
 */
public class ListSectionListAdapter extends ArrayAdapter<Deal> {

    private Context context = null;
    private List<Deal> values = null;


    public ListSectionListAdapter(Context context, List<Deal> values) {
        super(context, R.layout.main_list_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Reuse previous list item to save memory!
        View itemView = convertView;

        ItemViewHolder viewHolder = null;

        if (itemView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            itemView = inflater.inflate(R.layout.main_list_item, parent, false);

            viewHolder = new ItemViewHolder();
            viewHolder.dealTextView = (TextView) itemView.findViewById(R.id.dealText);
            viewHolder.priceTextView = (TextView) itemView.findViewById(R.id.priceText);
            viewHolder.storeTextView = (TextView) itemView.findViewById(R.id.storeText);
            viewHolder.ratingTextView = (TextView) itemView.findViewById(R.id.ratingText);
            viewHolder.imageView = (ImageView) itemView.findViewById(R.id.dealThumb);

            // cache reference to subviews in tag of row layout
            itemView.setTag(viewHolder);

        } else {
            // null check on itemView.getTag?
            viewHolder = (ItemViewHolder) itemView.getTag();

        }

        // refactor this into item view holder...
        Deal deal = values.get(position);
        viewHolder.dealTextView.setText(deal.getProduct());
        viewHolder.priceTextView.setText(deal.getFormattedPrice());
        viewHolder.storeTextView.setText(deal.getStore());
        viewHolder.ratingTextView.setText(deal.getFormattedRating());
        viewHolder.ratingTextView.setTextColor(deal.getRatingColour());
        viewHolder.imageView.setImageBitmap(deal.getThumbnail(80, 80));

        return itemView;
    }

    /**
     * For caching references to layout views to avoid calling findViewById
     */
    private static class ItemViewHolder {
        public TextView dealTextView = null;
        public TextView priceTextView = null;
        public TextView storeTextView = null;
        public TextView ratingTextView = null;
        public ImageView imageView = null;
    }

}
