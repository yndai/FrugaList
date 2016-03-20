package com.ryce.frugalist.view.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.ryce.frugalist.R;
import com.ryce.frugalist.view.list.ListSectionPagerAdapter;

/**
 * Created by Roger_Wang on 2016-03-14.
 */
public class SearchListingActivity extends AppCompatActivity {

    private Spinner mSortSpinner;
    private Spinner mTypeSpinner;
    private EditText mInputSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_listing);

        mInputSearch = (EditText) findViewById(R.id.inputSearch);
        mSortSpinner = (Spinner) findViewById(R.id.inputSortSpinner);
        mTypeSpinner = (Spinner) findViewById(R.id.inputTypeSpinner);

        ArrayAdapter<CharSequence> adapter;
        adapter = ArrayAdapter.createFromResource(this, R.array.searchSortListArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSortSpinner.setAdapter(adapter);

        adapter = ArrayAdapter.createFromResource(this, R.array.searchTypeListArray, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTypeSpinner.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
