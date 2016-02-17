package com.ryce.frugalist.view.create;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ryce.frugalist.R;
import android.graphics.Bitmap;

import java.io.File;
import java.util.Date;

import static android.graphics.Movie.decodeFile;

/**
 * Created by shivand on 2/15/2016.
 */
public class CreateListingActivity extends AppCompatActivity {

    ImageView iv;
    final private int CAPTURE_IMAGE = 2;
    private String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posting);

        if (savedInstanceState == null) {
            //getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
        iv = (ImageView) findViewById(R.id.cameraView);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, setImageUri());
                startActivityForResult(intent, CAPTURE_IMAGE);
            }
        });
    }

    public Uri setImageUri() {
        // Store image in dcim
        File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/", "image" + new Date().getTime() + ".png");
        Uri imgUri = Uri.fromFile(file);
        this.imgPath = file.getAbsolutePath();
        return imgUri;
    }


    public String getImagePath() {
        return imgPath;
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == CAPTURE_IMAGE) {
                String selectedImagePath = getImagePath();
                iv.setImageBitmap(BitmapFactory.decodeFile(selectedImagePath));
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
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

    /**
     +     * A placeholder fragment containing a simple view.
     +     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
             View rootView = inflater.inflate(R.layout.activity_posting, container, false);
                return rootView;
             }
    }


}