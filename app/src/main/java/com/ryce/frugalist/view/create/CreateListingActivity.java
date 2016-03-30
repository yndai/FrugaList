package com.ryce.frugalist.view.create;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Settings;
import com.ryce.frugalist.network.FrugalistRequest;
import com.ryce.frugalist.network.FrugalistResponse;
import com.ryce.frugalist.network.FrugalistServiceHelper;
import com.ryce.frugalist.network.ImgurRequest;
import com.ryce.frugalist.network.ImgurResponse;
import com.ryce.frugalist.network.ImgurServiceHelper;
import com.ryce.frugalist.util.UserHelper;
import com.ryce.frugalist.util.Utils;
import com.ryce.frugalist.view.ApplicationState;
import com.ryce.frugalist.view.detail.ListingDetailActivity;
import com.ryce.frugalist.view.list.ListSectionFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by shivand on 2/15/2016.
 */
public class CreateListingActivity extends AppCompatActivity {

    private static final String TAG = CreateListingActivity.class.getSimpleName();

    // arbitrary request code for image capture
    private static final int CAPTURE_IMAGE_REQUEST_CODE = 2;
    // request code for autocompleting the address and store
    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE_STORE = 9;

    ImageView mPhotoImageView;
    TextView mImageSizeText;
    FloatingActionButton mUploadButton;
    ProgressDialog mProgressDialog;

    EditText mProductInput;
    EditText mPriceInput;
    Spinner mUnitSpinner;
    Button mInsertLocationButton;
    TextView mStoreInput;
    TextView mAddressInput;

    // current image file
    private File mImgFile;

    // currently stored  location
    LatLng mStoreLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        // get references to input widgets
        mPhotoImageView = (ImageView) findViewById(R.id.cameraView);
        mImageSizeText = (TextView) findViewById(R.id.imageSizeText);
        mProductInput = (EditText) findViewById(R.id.productInput);
        mPriceInput = (EditText) findViewById(R.id.priceInput);
        mUnitSpinner = (Spinner) findViewById(R.id.unitSpinner);
        mInsertLocationButton = (Button) findViewById(R.id.insertLocationbtn);
        mStoreInput = (TextView) findViewById(R.id.storeInput);
        mAddressInput = (TextView) findViewById(R.id.addressInput);
        mUploadButton = (FloatingActionButton) findViewById(R.id.uploadFab);

        // init progress dialog
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(getResources().getString(R.string.create_upload_load_title));
        mProgressDialog.setMessage(getResources().getString(R.string.create_upload_load_msg));
        mProgressDialog.setIndeterminate(true);

        // initialize TextWatcher for price input
        mPriceInput.addTextChangedListener(new MoneyTextWatcher(mPriceInput));

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(this, R.array.unitArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mUnitSpinner.setAdapter(adapter);

        // init image size text
        mImageSizeText.setText("");

        // setup listener for camera button
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchImageCaptureIntent();
            }
        });

        // setup listener for upload button
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyInputOk()) {
                    executeUpload();
                }
            }
        });

        // set up the autocomplete intents for store and address
        mInsertLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchPlaceAutocompleteIntent();
            }
        });

    }

    /**
     * Verify that user has entered data into required fields
     * @return true if everything is OK
     */
    private boolean verifyInputOk() {

        if (mImgFile == null || !mImgFile.exists()) {
            Utils.showAlertDialog(this, "Image not chosen, please take a photo!");
            return false;
        } else if (mProductInput.getText().toString().isEmpty()) {
            Utils.showAlertDialog(this, "Please enter a product name!");
            return false;
        } else if (mPriceInput.getText().toString().isEmpty()) {
            Utils.showAlertDialog(this, "Please enter a price!");
            return false;
        } else if (mStoreLocation == null ||
                mStoreInput.getText().toString().isEmpty() ||
                mAddressInput.getText().toString().isEmpty()) {
            Utils.showAlertDialog(this, "Please pick a store location!");
            return false;
        } else {
            return true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // process results from external activities

        if (requestCode == CAPTURE_IMAGE_REQUEST_CODE) { // image capture result

            if (resultCode == RESULT_OK) {

                // we want to scale down image file from camera
                mImgFile = scaleDownImage(mImgFile);

                // show file size
                mImageSizeText.setText(mImgFile.length() / 1024 + " KB");

                // load image into image view
                Picasso.with(getBaseContext())
                        .load(mImgFile)
                        .fit()
                        .into(mPhotoImageView);

            } else if (resultCode == RESULT_CANCELED) {

                // if user cancels out of the camera without taking a picture, we need to remove the temporary file
                if (mImgFile != null && mImgFile.exists()) {
                    mImgFile.delete();
                    mImgFile = null;
                }

            }

        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE_STORE) { // place autocomplete result

            if (resultCode == RESULT_OK) {

                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getLatLng());
                mStoreInput.setText(place.getName());
                mAddressInput.setText(place.getAddress());
                mStoreLocation = place.getLatLng();

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {

                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.i(TAG, "Failed Autocomplete Result: " + status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    protected void onDestroy() {
        // cleanup image temp files, etc
        cleanupImageRequest();
        super.onDestroy();
    }

    /**********************************************************************
     * Frugalist Deal upload
     **********************************************************************/

    /**
     * Called after deal has been uploaded
     * @param resDeal
     */
    private void onDealUploaded(FrugalistResponse.Deal resDeal) {

        // show delete message
        Toast.makeText(this, "Deal posted, thanks for sharing!", Toast.LENGTH_LONG).show();

        // clean up temp files
        cleanupImageRequest();

        // mark main list as stale
        ((ApplicationState) getApplicationContext()).setMainListDataIsStale(true);

        // go to detail view of posted deal
        Intent intent = new Intent(this, ListingDetailActivity.class);
        intent.putExtra(ListingDetailActivity.ARG_LISTING_TYPE, ListSectionFragment.ListingType.DEAL);
        intent.putExtra(ListingDetailActivity.ARG_LISTING_ID, resDeal.id);
        startActivity(intent);
        finish();
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
                onDealUploaded(deal);

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
     * Imgur Image Upload
     **********************************************************************/

    /**
     * Execute Imgur image upload request
     */
    private void executeUpload() {

        if (mImgFile != null && mImgFile.exists()) {

            ImgurRequest request = new ImgurRequest();
            request.image = mImgFile;

            ImgurServiceHelper.doPostImage(getBaseContext(), request, mImgurResponseCallback);

            mProgressDialog.show();

        } else {
            Snackbar.make(this.findViewById(android.R.id.content), "No image to upload/File no longer exists", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    /**
     * Called after image has been uploaded to Imgur
     * @param imgurRes
     */
    private void onImageUploaded(ImgurResponse imgurRes) {

        // create new deal obj
        FrugalistRequest.Deal deal = new FrugalistRequest.Deal();
        deal.authorId = UserHelper.getCurrentUser(this).getId();
        deal.product = mProductInput.getText().toString();
        deal.imageUrl = imgurRes.data.link;
        deal.address = mAddressInput.getText().toString();
        deal.store = mStoreInput.getText().toString();
        deal.latitude = (float) mStoreLocation.latitude;
        deal.longitude = (float) mStoreLocation.longitude;
        deal.price = mPriceInput.getText().toString();
        deal.unit = mUnitSpinner.getSelectedItem().toString();
        deal.description = "";

        // post deal
        FrugalistServiceHelper.doPostDeal(mFrugalistDealCallback, deal);

    }

    /** callback for imgur image upload request */
    Callback<ImgurResponse> mImgurResponseCallback = new Callback<ImgurResponse>() {

        @Override
        public void onResponse(Call<ImgurResponse> call, Response<ImgurResponse> response) {

            if (response.isSuccess()) {

                ImgurResponse imgurRes = response.body();
                Log.i(TAG, imgurRes.toString());
                onImageUploaded(imgurRes);

            } else {

                try {
                    Log.i(TAG, "Imgur upload error: " + response.errorBody().string());
                } catch (IOException e) {/*not handling */}

                // something went wrong!
                Snackbar.make(findViewById(android.R.id.content), "Something went wrong! Please try again", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                mProgressDialog.dismiss();
            }
        }

        @Override
        public void onFailure(Call<ImgurResponse> call, Throwable t) {
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            mProgressDialog.dismiss();
        }

    };

    /**
     * Clean up previous request, delete temp img, etc
     */
    private void cleanupImageRequest() {
        // delete image after we are done with it!
        if (mImgFile != null && mImgFile.exists()) {
            mImgFile.delete();
            mImgFile = null;
        }
        mProgressDialog.dismiss();
    }

    /**********************************************************************
     * Image Capture
     **********************************************************************/

    /**
     * Initiate camera capture intent
     */
    private void dispatchImageCaptureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // clean up previous request
            cleanupImageRequest();

            // Create the File where the photo should go
            try {
                mImgFile = createTempImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (mImgFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImgFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST_CODE);
            }

        } else {
            Log.i(TAG, "No camera hardware??");
            // TODO: no camera... what
        }
    }

    /**
     * Create a temp image file
     * @return
     * @throws IOException
     */
    private File createTempImageFile() throws IOException {

        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // create a temporary image file in DCIM folder
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        return image;
    }

    /**
     * Scale down and compress an image (from camera)
     * Will delete the old image
     *
     * @param oldImageFile
     * @return
     */
    private File scaleDownImage(File oldImageFile) {
        if (oldImageFile == null || !oldImageFile.exists()) {
            return null;
        }

        // get quality setting
        Settings settings = UserHelper.getUserSettings(this);

        // get reduced image
        Bitmap image = Utils.decodeSampledBitmapFromFile(oldImageFile.getAbsolutePath(), 720, 1280);

        File newImageFile = null;
        try {
            // create new image file
            newImageFile = createTempImageFile();

            FileOutputStream fos = new FileOutputStream(newImageFile);

            // store compressed bitmap in file
            image.compress(Bitmap.CompressFormat.JPEG, settings.getUploadQuality(), fos);

            fos.close();

            // delete old file
            oldImageFile.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newImageFile;
    }

    /**********************************************************************
     * Location service
     **********************************************************************/

    /**
     * Initiate place autocomplete intent
     */
    private void dispatchPlaceAutocompleteIntent() {
        try {

            AutocompleteFilter storeFilter = new AutocompleteFilter.Builder()
                    .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ESTABLISHMENT)
                    .build();

            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                    .setFilter(storeFilter)
                    .build(CreateListingActivity.this);

            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE_STORE);

        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
            Log.i(TAG, "Failed Place autocomplete!");
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            Log.i(TAG, "Failed Place autocomplete - Service not available");
        }
    }

}