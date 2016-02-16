package com.ryce.frugalist.view.detail;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.service.FetchImageTask;
import com.ryce.frugalist.view.list.ListSectionFragment;
import com.ryce.frugalist.view.list.ListSectionFragment.ListingType;

public class ListingDetailActivity extends AppCompatActivity {


    public static final String ARG_LISTING_TYPE = "listing_type";
    public static final String ARG_LISTING_DATA = "listing_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView text = (TextView) findViewById(R.id.detailText);
        final ImageView imageView = (ImageView) findViewById(R.id.detailImage);

        ListingType type = (ListingType) getIntent().getExtras().get(ARG_LISTING_TYPE);

        if (type == ListingType.DEAL) {
            int pos = (int) getIntent().getExtras().get(ARG_LISTING_DATA);
            final Deal deal = (Deal) ListSectionFragment.items.get(pos);
            if (deal.getImage() == null) {

                new FetchImageTask() {
                    @Override
                    protected void onPostExecute(Bitmap result) {
                        if (result != null) {
                            // cache image in model
                            deal.setImage(result,
                                    ListSectionFragment.THUMBNAIL_WIDTH,
                                    ListSectionFragment.THUMBNAIL_HEIGHT);
                            imageView.setImageBitmap(deal.getImage());
                        }
                    }
                }.execute(deal.getImageUrl());

            } else{
                imageView.setImageBitmap(deal.getImage());
            }
            Log.i("deal", deal.toString());
            //image.setImageBitmap(deal.getImage());
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
