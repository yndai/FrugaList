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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.ryce.frugalist.R;
import com.ryce.frugalist.model.Deal;
import com.ryce.frugalist.model.MockDatastore;
import com.ryce.frugalist.network.ImgurRequest;
import com.ryce.frugalist.network.ImgurResponse;
import com.ryce.frugalist.network.ImgurServiceHelper;
import com.ryce.frugalist.util.Utils;
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

    // arbitrary request code for image capture
    private static final int CAPTURE_IMAGE = 2;

    ImageView mPhotoImageView;
    FloatingActionButton mUploadButton;
    ProgressDialog mProgressDialog;

    EditText mProductInput;
    EditText mPriceInput;
    EditText mUnitInput;
    EditText mStoreInput;
    EditText mAddressInput;

    // current image file
    private File mImgFile;

    private boolean mImgIsReady;

    Callback<ImgurResponse> mImgurResponseCallback = new Callback<ImgurResponse>() {
        @Override
        public void onResponse(Call<ImgurResponse> call, Response<ImgurResponse> response) {

            Deal deal = new Deal(
                    response.body().data.link,
                    mPriceInput.getText().toString(),
                    mProductInput.getText().toString(),
                    0,
                    mUnitInput.getText().toString(),
                    mStoreInput.getText().toString());

            MockDatastore.getInstance().addDeal(deal);

            Snackbar.make(findViewById(android.R.id.content), "Deal posted, thanks for sharing!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            cleanupRequest();
        }

        @Override
        public void onFailure(Call<ImgurResponse> call, Throwable t) {
            Snackbar.make(findViewById(android.R.id.content), "Failed! " + t.getMessage(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            cleanupRequest();
        }

    };

    /**
     * Clean up previous request, delete temp img, clear progress dialog, etc
     */
    private void cleanupRequest() {
        // delete image after we are done with it!
        if (mImgFile != null && mImgFile.exists()) {
            mImgFile.delete();
            mImgFile = null;
        }
        // dismiss progress dialog
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_listing);

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        // make sure we can write to disk
        Utils.verifyStoragePermissions(this);

        mProductInput = (EditText) findViewById(R.id.productInput);
        mPriceInput = (EditText) findViewById(R.id.priceInput);
        mUnitInput = (EditText) findViewById(R.id.unitInput);
        mStoreInput = (EditText) findViewById(R.id.storeInput);
        mAddressInput = (EditText) findViewById(R.id.addressInput);

        // set default unit
        mUnitInput.setText("ea");

        // setup listener for camera button
        mPhotoImageView = (ImageView) findViewById(R.id.cameraView);
        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchImageCaptureIntent();
            }
        });

        // setup listener for upload button
        mUploadButton = (FloatingActionButton) findViewById(R.id.uploadFab);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeUpload(view);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CAPTURE_IMAGE && resultCode == RESULT_OK) {

            // we want to scale down image file from camera
            mImgFile = scaleDownImage(mImgFile);

            // load image into image view
            Picasso.with(getBaseContext())
                    .load(mImgFile)
                    .placeholder(R.drawable.ic_photo_library_black)
                    .fit()
                    .into(mPhotoImageView);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Initiate camera capture intent
     */
    private void dispatchImageCaptureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            // clean up previous request
            cleanupRequest();
            try {
                mImgFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (mImgFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mImgFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE);
            }
        }
    }
    /**
     * Create a temp image file
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * Execute image upload request
     * @param parentView
     */
    private void executeUpload(View parentView) {
        if (mImgFile != null) {

            ImgurRequest request = new ImgurRequest();
            request.image = mImgFile;

            // start wait dialog
            mProgressDialog = ProgressDialog.show(parentView.getContext(), "Uploading deal...", "Please wait", true);
            // do request
            ImgurServiceHelper.getInstance().doPostImage(getBaseContext(), request, mImgurResponseCallback);
        } else {
            Snackbar.make(parentView, "No image to upload/File no longer exists", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
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

        // get reduced image
        Bitmap image = Utils.decodeSampledBitmapFromFile(oldImageFile.getAbsolutePath(), 720, 1280);

        File newImageFile = null;
        try {
            // create new image file
            newImageFile = createImageFile();

            FileOutputStream fos = new FileOutputStream(newImageFile);

            // store compressed bitmap in file
            image.compress(Bitmap.CompressFormat.JPEG, 50, fos);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}