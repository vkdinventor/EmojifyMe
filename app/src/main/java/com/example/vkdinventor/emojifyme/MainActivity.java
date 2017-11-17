package com.example.vkdinventor.emojifyme;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

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
                writeToScCard();
                break;
            case R.id.clear_button:
                break;
            case R.id.save_button:
                break;
            case R.id.share_button:
                break;
        }
    }
}
