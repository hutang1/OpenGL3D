package cn.easyar.samples.helloarsurfacetracking;

import android.content.Context;
import android.graphics.ImageFormat;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Render implements GLSurfaceView.Renderer {
    public static final int SAMPLE_TYPE  =  200;
    public static final int SAMPLE_TYPE_SET_TOUCH_LOC           = SAMPLE_TYPE + 999;
    public static final int SAMPLE_TYPE_SET_GRAVITY_XY          = SAMPLE_TYPE + 1000;

    MyNativeRender mMyNativeRender;

    private boolean initialized = false;
    private boolean finishing = false;
    int width ;
    int height ;
    ARCamera  helloAR;
    Context context ;
    Render(Context context ,ARCamera mARCamera){
        this.context =context;
        mMyNativeRender =new MyNativeRender();
        helloAR =mARCamera;

    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
     mMyNativeRender.native_OnSurfaceCreated();
        if (!initialized) {
            initialized = true;
            helloAR = new ARCamera();
            helloAR.initialize();
        } else {
            helloAR.recreate_context();
        }
        helloAR.start();
    }


    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
         mMyNativeRender.native_OnSurfaceChanged(width ,height);
         this.width =width;
         this.height =height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {


      //  helloAR.render(width,height,GetScreenRotation()  );

        mMyNativeRender.native_OnDrawFrame();

    }
    public void onstop(){
        helloAR.stop();
    }
    private int GetScreenRotation()
    {
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int orientation;
        switch(rotation) {
            case Surface.ROTATION_0:
                orientation = 0;
                break;
            case Surface.ROTATION_90:
                orientation = 90;
                break;
            case Surface.ROTATION_180:
                orientation = 180;
                break;
            case Surface.ROTATION_270:
                orientation = 270;
                break;
            default:
                orientation = 0;
                break;
        }
        return orientation;
    }

    public void init(){
        mMyNativeRender.native_Init();
    }
    public void unInit(){
        mMyNativeRender.native_UnInit();
    }

    public void setParamsInt(int paramType, int value0, int value1) {

        mMyNativeRender.native_SetParamsInt(paramType, value0, value1);
    }

    public void setTouchLoc(float x, float y)
    {
        mMyNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_TOUCH_LOC, x, y);
    }

    public void setGravityXY(float x, float y) {
        mMyNativeRender.native_SetParamsFloat(SAMPLE_TYPE_SET_GRAVITY_XY, x, y);
    }

    public void setImageData(int format, int width, int height, byte[] bytes) {
        mMyNativeRender.native_SetImageData(format, width, height, bytes);
    }

    public void setImageDataWithIndex(int index, int format, int width, int height, byte[] bytes) {
        mMyNativeRender.native_SetImageDataWithIndex(index, format, width, height, bytes);
    }

    public void setAudioData(short[] audioData) {
        mMyNativeRender.native_SetAudioData(audioData);
    }



    public void updateTransformMatrix(float rotateX, float rotateY, float scaleX, float scaleY)
    {
        mMyNativeRender.native_UpdateTransformMatrix(rotateX, rotateY, scaleX, scaleY);
    }
}
