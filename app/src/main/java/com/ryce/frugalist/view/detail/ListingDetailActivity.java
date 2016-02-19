package com.ryce.frugalist.view.detail;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;
import com.squareup.picasso.Picasso;

import java.util.UUID;

public class ListingDetailActivity extends AppCompatActivity {

    public static final String ARG_LISTING_TYPE = "listing_type";
    public static final String ARG_LISTING_DATA = "listing_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // get views
        final ImageView imageView = (ImageView) findViewById(R.id.detailImage);
        final TextView productText = (TextView) findViewById(R.id.productText);
        final TextView priceText = (TextView) findViewById(R.id.priceText);
        final TextView storeText = (TextView) findViewById(R.id.storeNameText);
        final TextView ratingText = (TextView) findViewById(R.id.ratingText);

        // get type of listing we are displaying
        ListingType type = (ListingType) getIntent().getExtras().get(ARG_LISTING_TYPE);

        if (type == ListingType.DEAL) {

            // fetch deal by id
            UUID id = (UUID) getIntent().getExtras().get(ARG_LISTING_DATA);
            final Deal deal = MockDatastore.getInstance().getDeal(id);

            // Load image via URL
            Picasso p = Picasso.with(this);
            p.setIndicatorsEnabled(true);
            p.load(deal.getImageUrl())
                    .error(android.R.drawable.ic_delete)
                    .placeholder(R.drawable.loader)
                    .into(imageView);

            // display data
            productText.setText(deal.getProduct());
            priceText.setText(deal.getFormattedPrice());
            storeText.setText(deal.getStore());
            ratingText.setText(deal.getFormattedRating());
            ratingText.setTextColor(deal.getRatingColour());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabBookmark);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Bookmarked!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // we want a back button in task bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
