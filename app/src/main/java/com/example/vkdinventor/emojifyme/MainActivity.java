package com.example.vkdinventor.emojifyme;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;


public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.image_view)
    ImageView imageView;
    @BindView(R.id.emojify_button)
    Button emojifyButton;
    @BindView(R.id.clear_button)
    FloatingActionButton clearButton;
    @BindView(R.id.save_button)
    FloatingActionButton saveButton;
    @BindView(R.id.share_button)
    FloatingActionButton shareButton;

    private String mTempPhotoPath;
   private Bitmap mResultsBitmap;

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int RC_STORAGE_PERMISSION  = 100;
    private static final String[] STORAGE_PERMISSION =
            {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    boolean hasSDCardPermission(){
        return EasyPermissions.hasPermissions(this, STORAGE_PERMISSION);
    }

    @AfterPermissionGranted(RC_STORAGE_PERMISSION)
    void writeToScCard(){
        if(hasSDCardPermission()){
            Toast.makeText(this, "Camera Permission available", Toast.LENGTH_SHORT).show();
        }else {
            EasyPermissions.requestPermissions(
                    this,
                    "Read and Write to external storage premission required",
                    RC_STORAGE_PERMISSION,
                    STORAGE_PERMISSION);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        writeToScCard();
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }

    @OnClick({R.id.emojify_button, R.id.clear_button, R.id.save_button, R.id.share_button})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.emojify_button:
                captureImage();
                break;
            case R.id.clear_button:
                clearImage();
                break;
            case R.id.save_button:
                saveImage();
                break;
            case R.id.share_button:
                shareImage();
                break;
        }
    }

    private void shareImage() {
        BitmapUtils.deleteImageFile(this, mTempPhotoPath);
        // Save the image
        BitmapUtils.saveImage(this, mResultsBitmap);
        // Share the image
        BitmapUtils.shareImage(this, mTempPhotoPath);
    }

    private void saveImage() {
        BitmapUtils.saveImage(this,mResultsBitmap);
        BitmapUtils.deleteImageFile(this,mTempPhotoPath);
    }

    private void clearImage() {
        imageView.setImageBitmap(null);
        imageView.setVisibility(View.GONE);
        clearButton.setVisibility(View.GONE);
        saveButton.setVisibility(View.GONE);
        shareButton.setVisibility(View.GONE);
        emojifyButton.setVisibility(View.VISIBLE);
    }

    private void captureImage() {
            // Create the capture image intent
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the temporary File where the photo should go
                File photoFile = null;
                try {
                    photoFile = BitmapUtils.createTempImageFile(this);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    ex.printStackTrace();
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {

                    // Get the path of the temporary file
                    mTempPhotoPath = photoFile.getAbsolutePath();

                    // Get the content URI for the image file
                    Uri photoURI = FileProvider.getUriForFile(this,BitmapUtils.FILE_PROVIDER_AUTHORITY,photoFile);

                    // Add the URI so the camera can store the image
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                    // Launch the camera activity
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            processAndSetImage();
        } else {

            // Otherwise, delete the temporary image file
            BitmapUtils.deleteImageFile(this, mTempPhotoPath);
        }
    }

    private void processAndSetImage() {
        mResultsBitmap = BitmapUtils.resamplePic(this, mTempPhotoPath);
        Log.d("BitmapUtils", "witdth X heifht of image after sample "+mResultsBitmap.getWidth()+" x "+mResultsBitmap.getHeight());

        imageView.setVisibility(View.VISIBLE);
        clearButton.setVisibility(View.VISIBLE);
        saveButton.setVisibility(View.VISIBLE);
        shareButton.setVisibility(View.VISIBLE);
        emojifyButton.setVisibility(View.GONE);

        // Detect the faces and overlay the appropriate emoji
        //mResultsBitmap = Emojifier.detectFacesandOverlayEmoji(this, mResultsBitmap);

        // Set the new bitmap to the ImageView
//        Matrix matrix = new Matrix();
//        matrix.postRotate(90);
//        mResultsBitmap =  Bitmap.createBitmap(mResultsBitmap, 0, 0, mResultsBitmap.getWidth(), mResultsBitmap.getHeight(), matrix, true);

        imageView.setImageBitmap(mResultsBitmap);
        EmojiFy.detectFacesandOverlayEmoji(this,mResultsBitmap);
        //Toast.makeText(this,"Totaol no of people in this photo :"+EmojiFy.detectFaces(this,mResultsBitmap), Toast.LENGTH_SHORT).show();
    }
}
