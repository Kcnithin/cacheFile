package com.example.myapplicationnew;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    static Bitmap bitmap;
    ImageView imageView;
    File file;
    Button save, load;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save = findViewById(R.id.button2);
        load = findViewById(R.id.button);

        //for drawable images

       /* @SuppressLint("UseCompatLoadingForDrawables")
        Drawable drawable = getResources().getDrawable(R.drawable.login);
        bitmap = ((BitmapDrawable) drawable).getBitmap();*/


        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "https://media.geeksforgeeks.org/wp-content/cdn-uploads/gfg_200x200-min.png";
               // String url = "vacations/pictures/eiffel.jpg";
                //    url = "https://m.media-amazon.com/images/G/01/APS/api-reference/APS_Integration_Guide_-_Android.pdf";
                DownloadImage(url);

                //for drawable images

            /*    if (!file.exists()) {
                    Log.e("path", file.toString());
                    Log.e("bitmap", bitmap.toString());
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        fos.flush();
                        fos.close();
                        Toast.makeText(getApplicationContext(), "file saved in :"
                                + file, Toast.LENGTH_LONG).show();

                    } catch (java.io.IOException e) {
                        e.printStackTrace();
                    }
                }*/
            }
        });

        load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView = findViewById(R.id.image);
                @SuppressLint("SdCardPath") File imgFile = new File(file.toString());
                if (imgFile.exists()) {
                    Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

                    imageView.setImageBitmap(myBitmap);

                }
                Toast.makeText(getApplicationContext(), "Loaded "
                        + file, Toast.LENGTH_SHORT).show();
            }
        });
    }

    void DownloadImage(String ImageUrl) {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);

            Toast.makeText(getApplicationContext(), "Need Permission to access storage for Downloading Image", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Downloading Image...", Toast.LENGTH_SHORT).show();
            //Asynctask to create a thread to downlaod image in the background
            new DownloadsImage().execute(ImageUrl);
        }
    }

    class DownloadsImage extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            URL url = null;
            try {
                url = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bm = null;
            try {
                bm = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Create Path to save Image
            //   File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES + "/AndroidDvlpr"); //Creates app specific folder
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            file = new File(directory, "NewImage" + ".jpg");
        //    file = new File(directory, "Pdf" + ".pdf");

            FileOutputStream out = null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                bm.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
                out.flush();
                out.close();
                // Tell the media scanner about the new file so that it is
                // immediately available to the user.
                MediaScannerConnection.scanFile(MainActivity.this, new String[]{file.getAbsolutePath()}, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                        // Log.i("ExternalStorage", "Scanned " + path + ":");
                        //    Log.i("ExternalStorage", "-> uri=" + uri);
                    }
                });
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("path", file.toString());
            Toast.makeText(getApplicationContext(), "Image Saved!" + file, Toast.LENGTH_SHORT).show();
        }
    }
}