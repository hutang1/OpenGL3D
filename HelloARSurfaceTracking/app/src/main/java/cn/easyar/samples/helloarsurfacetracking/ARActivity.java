//================================================================================================================================
//
// Copyright (c) 2015-2021 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloarsurfacetracking;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import cn.easyar.CameraDevice;
import cn.easyar.Engine;
import cn.easyar.SurfaceTracker;

public class ARActivity extends Activity
{
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloARSurfaceTracking
    *      Package Name: cn.easyar.samples.helloarsurfacetracking
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "4ASvQeQXt138cbzgTWd3b+zsVBGyek9XMgiHOtA2mWrkJp930CuIOp9nzSmccMgsnHHJLOU0jTbGKpE6iWeRedYxmWruIIVRwWfGKYlnkHHGIJJrwDbeIv4+3nrQK5h0wAyYa4d/p0WJZ4p51yyddtE23iL+Z593yCiJdswxhTr4ad5oySSIfso3kWuHf6c60iySfMoyjzqJZ5F5xmehNIcok3zQKZlrh3+nOtYgkmvAa7V1xCKZTNckn3PMK5s6iWePfcs2mTbmKZNtwReZe8oiknHRLJN2h2nea8Arj32LF5l7yjeYccsi3jSHNpl21iDSV8cvmXvREY55xi6VdsJn0DrWIJJrwGuvbdcjnXvAEY55xi6VdsJn0DrWIJJrwGuvaMQ3j332NZ1szCSQVcQ13jSHNpl21iDSVcoxlXfLEY55xi6VdsJn0DrWIJJrwGu4fcs2mUvVJIhxxCmxedVn0DrWIJJrwGu/WeERjnnGLpV2wmehNIcghGjMN5lMzCiZS9EkkWiHf5JtySnQOsw2sHfGJJA6nyOddNYggTTeZ55tyyGQfewhjzqfHt57yijSe8QomWrEa4h93THSfMAokzr4ad5uxDeVecsxjzqfHt57yiiRbcssiGGHGNA61SmdbMMqjnXWZ8ZDhySSfNcqlXyHGNA6yCqYbckgjzqfHt5rwCuPfYsMkXnCIKhqxCaXccsi3jSHNpl21iDSW8kqiXz3IJ93wiuVbMwqkjqJZ499yzaZNvcgn3fXIZV2wmfQOtYgkmvAa7N6zyCfbPE3nXvOLJJ/h2nea8Arj32LFolqwySfffE3nXvOLJJ/h2nea8Arj32LFox51zaZS9UkiHHEKbF51WfQOtYgkmvAa7F30SyTdvE3nXvOLJJ/h2nea8Arj32LAZl21iCvaMQxlXnJCJ1oh2nea8Arj32LBr1c8Tede84skn+HGNA6wD2McdcgqHHIIK9sxCiMOp8riXTJad5x1gmTe8Qp3iLDJJBrwDjQY4cniXbBKZlRwTbeIv5n3kWJZ4p51yyddtE23iL+Z593yCiJdswxhTr4ad5oySSIfso3kWuHf6c6zCqPOvhp3nXKIYl0wDbeIv5nj33LNpk27Cidf8ARjnnGLpV2wmfQOtYgkmvAa790yjCYSsAmk3/LLIhxyiveNIc2mXbWINJKwCaTasEskn+Had5rwCuPfYsKnnLAJohM1ySfc8wrmzqJZ499yzaZNvYwjn7EJplM1ySfc8wrmzqJZ499yzaZNvY1nWrWIK9oxDGVeckInWiHad5rwCuPfYsIk2zMKpJM1ySfc8wrmzqJZ499yzaZNuEgkmvAFox50SyddOgkjDqJZ499yzaZNuYEuEzXJJ9zzCubOvhp3n3dNZVqwBGVdcAWiHnINd4iyzCQdIlnlWvpKp95yWfGfsQpj33YGIGL7EAWW+SMHOtpC71lvQtNrMRz8tepq0vDBsVY2kFRqUzlN1orkX5mF+ziQkgusvj8IvFVDbD9V0tQHDIfLWSPSRxyWp3rfpHe+djctJonNhl5CZdh/oe9HDfedZS5oFBUkyte5jDt/RCQayw+g09Pj6Vh2F4qYq1+oxYHOHTELVFuMrgJZ10I3/+wkNvHqvciKJinYw6c3JKKswNCG/vTlRunAGXAZUjak+16JXM7A590wNdHmCMcFacirMNzK4C6O5E3DOIIaziVcIiY6ErPK+WcB3PeojI/5impyMWxjxRyqoQQbmsIJyZcIgdwi4gvM63KMxCF3Sqgi/2lRfwY";

    private GLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(ARActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        if (!CameraDevice.isAvailable()) {
            Toast.makeText(ARActivity.this, "CameraDevice not available.", Toast.LENGTH_LONG).show();
            return;
        }
        if (!SurfaceTracker.isAvailable()) {
            Toast.makeText(ARActivity.this, "SurfaceTracker not available.", Toast.LENGTH_LONG).show();
            return;
        }

        glView = new GLView(this);



        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ViewGroup preview = ((ViewGroup) findViewById(R.id.preview));
                preview.addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });
    }


    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }

}
