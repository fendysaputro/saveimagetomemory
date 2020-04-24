package com.save.saveimagetomemory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button takePhoto;
    ImageView imageView;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    File photoFile = null;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePhoto = (Button) findViewById(R.id.button);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeMedia(MediaStore.ACTION_IMAGE_CAPTURE, REQUEST_IMAGE_CAPTURE);
            }
        });

        imageView = (ImageView) findViewById(R.id.idImageView);
    }

    public void takeMedia (String actionType, int codeRequest){
        Intent takeMediaIntent = new Intent(actionType);
        if (takeMediaIntent.resolveActivity(this.getPackageManager()) != null){
            try {
                if (codeRequest == REQUEST_IMAGE_CAPTURE){
                    photoFile = createImageFile();
                    if (photoFile != null){
                        Uri photoURI = FileProvider.getUriForFile(this,
                                "com.save.saveimagetomemory",
                                photoFile);
                        takeMediaIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takeMediaIntent, codeRequest);
                    }
                }
            } catch (IOException ex){
                Log.e("newmms", ex.getMessage());
            }
        }
    }

    String mCurrentPhotoPath;
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HH:mm:ss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/FileFoto");
        if (!storageDir.exists()){
            storageDir.mkdirs();
        }
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
        try {
            FileOutputStream out = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void setImagePreview(){
        Bitmap bmp = BitmapFactory.decodeFile(photoFile.getPath());
        Point point = new Point();
        point.set(20, 150);
        imageView.setImageBitmap(bmp);
        imageView.setVisibility(View.VISIBLE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//            appMms.setImageFile(mediaFile);
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            Point point = new Point();
            point.set(20, 150);
            OutputStream fOut = null;
            try {
                fOut = new FileOutputStream(photoFile);
            } catch (FileNotFoundException fex){
                fex.printStackTrace();
            }
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            try {
                fOut.flush();
                fOut.close();
            } catch (IOException j){
                j.printStackTrace();
            }
            setImagePreview();
        }
    }
}
