package com.example.imagedownloadfromurl;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    Button bDownloadImage;
    ImageView ivImage;
    EditText etUrl;
    int random;
    LinearLayout llDownload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bDownloadImage = findViewById(R.id.bDownloadFile);
        ivImage = findViewById(R.id.ivImage);
        llDownload = findViewById(R.id.llDownload);
        etUrl = findViewById(R.id.etUrl1);

        bDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownTask downTask = new DownTask();
                downTask.execute(etUrl.getText().toString());
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    class DownTask extends AsyncTask<String,String,String>{

        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("Download in Progress...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.setProgress(0);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String path = strings[0];
            int file_length;
            try {
                URL url = new URL(path);
                URLConnection urlConnection = url.openConnection();
                urlConnection.connect();
                file_length = urlConnection.getContentLength();
                File newFolder = new File(Environment.getExternalStorageDirectory().getPath() + "/Cars");
                if (!newFolder.exists()){
                    newFolder.mkdir();
                }
                random = new Random().nextInt(121) + 20;
                File inputFile = new File(newFolder,"Cars-" + random + ".jpg");
                InputStream inputStream = new BufferedInputStream(url.openStream(),8192);
                byte[] data = new byte[1024];
                int total = 0;
                int count;

                OutputStream outputStream = new FileOutputStream(inputFile);
                while ((count=inputStream.read(data))!=-1){
                    total+= count;
                    outputStream.write(data,0,count);
                    int progress = total *100/file_length;
                    publishProgress(String.valueOf(progress));
                }
                inputStream.close();
                outputStream.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Download Complete.";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            progressDialog.setProgress(Integer.parseInt(values[0]));
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.hide();
            String path = Environment.getExternalStorageDirectory().getPath() +  "/Cars/" + "Cars-" + random + ".jpg";
            ivImage.setImageDrawable(Drawable.createFromPath(path));
            Snackbar snackbar = Snackbar.make(llDownload,"Cars-" + random + ".jpg",Snackbar.LENGTH_LONG);
            snackbar.setAction("OPEN", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath() + "/Cars/" + "Cars-" + random + ".jpg")), "image/*");
                    startActivity(intent);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
