package cn.easyar.samples.helloarsurfacetracking;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;

import cn.easyar.Engine;

import static android.opengl.GLSurfaceView.RENDERMODE_CONTINUOUSLY;
import static android.opengl.GLSurfaceView.RENDERMODE_WHEN_DIRTY;

public class SurfaceActivity extends Activity implements ViewTreeObserver.OnGlobalLayoutListener, SensorEventListener {
    private MyGLSurfaceView mGLSurfaceView;
    private ViewGroup mRootView;
    private SensorManager mSensorManager;
    private Render mGLRender ;

    private static String key = "4ASvQeQXt138cbzgTWd3b+zsVBGyek9XMgiHOtA2mWrkJp930CuIOp9nzSmccMgsnHHJLOU0jTbGKpE6iWeRedYxmWruIIVRwWfGKYlnkHHGIJJrwDbeIv4+3nrQK5h0wAyYa4d/p0WJZ4p51yyddtE23iL+Z593yCiJdswxhTr4ad5oySSIfso3kWuHf6c60iySfMoyjzqJZ5F5xmehNIcok3zQKZlrh3+nOtYgkmvAa7V1xCKZTNckn3PMK5s6iWePfcs2mTbmKZNtwReZe8oiknHRLJN2h2nea8Arj32LF5l7yjeYccsi3jSHNpl21iDSV8cvmXvREY55xi6VdsJn0DrWIJJrwGuvbdcjnXvAEY55xi6VdsJn0DrWIJJrwGuvaMQ3j332NZ1szCSQVcQ13jSHNpl21iDSVcoxlXfLEY55xi6VdsJn0DrWIJJrwGu4fcs2mUvVJIhxxCmxedVn0DrWIJJrwGu/WeERjnnGLpV2wmehNIcghGjMN5lMzCiZS9EkkWiHf5JtySnQOsw2sHfGJJA6nyOddNYggTTeZ55tyyGQfewhjzqfHt57yijSe8QomWrEa4h93THSfMAokzr4ad5uxDeVecsxjzqfHt57yiiRbcssiGGHGNA61SmdbMMqjnXWZ8ZDhySSfNcqlXyHGNA6yCqYbckgjzqfHt5rwCuPfYsMkXnCIKhqxCaXccsi3jSHNpl21iDSW8kqiXz3IJ93wiuVbMwqkjqJZ499yzaZNvcgn3fXIZV2wmfQOtYgkmvAa7N6zyCfbPE3nXvOLJJ/h2nea8Arj32LFolqwySfffE3nXvOLJJ/h2nea8Arj32LFox51zaZS9UkiHHEKbF51WfQOtYgkmvAa7F30SyTdvE3nXvOLJJ/h2nea8Arj32LAZl21iCvaMQxlXnJCJ1oh2nea8Arj32LBr1c8Tede84skn+HGNA6wD2McdcgqHHIIK9sxCiMOp8riXTJad5x1gmTe8Qp3iLDJJBrwDjQY4cniXbBKZlRwTbeIv5n3kWJZ4p51yyddtE23iL+Z593yCiJdswxhTr4ad5oySSIfso3kWuHf6c6zCqPOvhp3nXKIYl0wDbeIv5nj33LNpk27Cidf8ARjnnGLpV2wmfQOtYgkmvAa790yjCYSsAmk3/LLIhxyiveNIc2mXbWINJKwCaTasEskn+Had5rwCuPfYsKnnLAJohM1ySfc8wrmzqJZ499yzaZNvYwjn7EJplM1ySfc8wrmzqJZ499yzaZNvY1nWrWIK9oxDGVeckInWiHad5rwCuPfYsIk2zMKpJM1ySfc8wrmzqJZ499yzaZNuEgkmvAFox50SyddOgkjDqJZ499yzaZNuYEuEzXJJ9zzCubOvhp3n3dNZVqwBGVdcAWiHnINd4iyzCQdIlnlWvpKp95yWfGfsQpj33YGIGL7EAWW+SMHOtpC71lvQtNrMRz8tepq0vDBsVY2kFRqUzlN1orkX5mF+ziQkgusvj8IvFVDbD9V0tQHDIfLWSPSRxyWp3rfpHe+djctJonNhl5CZdh/oe9HDfedZS5oFBUkyte5jDt/RCQayw+g09Pj6Vh2F4qYq1+oxYHOHTELVFuMrgJZ10I3/+wkNvHqvciKJinYw6c3JKKswNCG/vTlRunAGXAZUjak+16JXM7A590wNdHmCMcFacirMNzK4C6O5E3DOIIaziVcIiY6ErPK+WcB3PeojI/5impyMWxjxRyqoQQbmsIJyZcIgdwi4gvM63KMxCF3Sqgi/2lRfwY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout);
        requestCameraPermission();
        mRootView = (ViewGroup) findViewById(R.id.rootView);
        mRootView.getViewTreeObserver().addOnGlobalLayoutListener(this);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(SurfaceActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }


        mGLRender  = new Render(this ,null);
        mGLRender.init();

    }


    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String[] REQUEST_PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };
    protected boolean hasPermissionsGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ActivityCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (!hasPermissionsGranted(REQUEST_PERMISSIONS)) {
                Toast.makeText(this, "We need the permission: WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    public static final int IMAGE_FORMAT_NV21 = 0x02;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return  true;
    }
    @Override
    public void onGlobalLayout() {
        mRootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        lp.addRule(RelativeLayout.CENTER_IN_PARENT);
        mGLSurfaceView = new MyGLSurfaceView(this, mGLRender ,null);
        mRootView.addView(mGLSurfaceView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
//        mGLSurfaceView.setRenderMode(RENDERMODE_WHEN_DIRTY);
        mGLRender.setParamsInt(200,   200+17, 0);
        mGLSurfaceView.requestRender();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    @TargetApi(23)
    private void requestCameraPermission(     ) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
            } else {

            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGLRender.unInit();
        mGLRender. onstop();
        /*
         * Once the EGL context gets destroyed all the GL buffers etc will get destroyed with it,
         * so this is unnecessary.
         * */
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_GRAVITY:

                    mGLRender.setGravityXY(event.values[0], event.values[1]);

                break;
        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
