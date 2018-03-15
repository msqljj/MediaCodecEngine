package com.video.mediacodecengine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;
import android.widget.Toast;


import com.video.mediacodecengine.util.ScreenUtill;

import java.util.ArrayList;

/**
 * Created by wb5790 on 2018/3/6.
 */

public class WelcomeActivity extends Activity{

    private static final  int INIT_PERMISSION=1000;

    private String[] permssions={
            Manifest.permission.CAMERA,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private Handler handler;
    private TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        textView=(TextView) findViewById(R.id.apk_version);
        textView.setText(ScreenUtill.getLocalVersionName(this)+"");
        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    initPermission();
                }
                else{
                    startActivity();
                }
            }
        },1000);

    }


    private void initPermission(){
        ArrayList<String> currentPermission=new ArrayList<String>();
        for(int i=0;i<permssions.length;i++){
            if(ContextCompat.checkSelfPermission(this, permssions[i])!= PackageManager.PERMISSION_GRANTED){
                currentPermission.add(permssions[i]);
            }
        }

        String [] usedPermissions=new String[currentPermission.size()];
        currentPermission.toArray(usedPermissions);
        if(usedPermissions.length>0) {
            ActivityCompat.requestPermissions(this, usedPermissions, INIT_PERMISSION);
        }
        else{
            startActivity();
        }
    }


    private void startActivity(){
        Intent intent=new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INIT_PERMISSION: {
                for(int i=0;i<grantResults.length;i++){
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "必要权限未开启", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                startActivity();

                return;
            }
        }
    }


}
