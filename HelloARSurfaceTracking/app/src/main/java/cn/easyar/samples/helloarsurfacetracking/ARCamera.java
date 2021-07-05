package cn.easyar.samples.helloarsurfacetracking;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;

import cn.easyar.Buffer;
import cn.easyar.CameraDevice;
import cn.easyar.CameraDeviceFocusMode;
import cn.easyar.CameraDevicePreference;
import cn.easyar.CameraDeviceSelector;
import cn.easyar.CameraDeviceType;
import cn.easyar.CameraParameters;
import cn.easyar.DelayedCallbackScheduler;
import cn.easyar.FrameFilterResult;
import cn.easyar.Image;
import cn.easyar.InputFrame;
import cn.easyar.InputFrameThrottler;
import cn.easyar.Matrix44F;
import cn.easyar.OutputFrame;
import cn.easyar.OutputFrameBuffer;
import cn.easyar.SurfaceTracker;
import cn.easyar.SurfaceTrackerResult;
import cn.easyar.Vec2F;
import cn.easyar.Vec2I;


public class ARCamera {

    private DelayedCallbackScheduler scheduler;
    private CameraDevice camera;
    private InputFrameThrottler inputFrameThrottler;
    private SurfaceTracker tracker;
    private OutputFrameBuffer outputFrameBuffer;
    private  BGRenderer bgRenderer;
    private int previousInputFrameIndex = -1;
    private byte[] imageBytes = null;

    public ARCamera()
    {
        bgRenderer =new BGRenderer();
    }

    public void recreate_context()
    {

        previousInputFrameIndex = -1;

    }

    public void initialize()
    {
        recreate_context();

        scheduler = new DelayedCallbackScheduler();
        inputFrameThrottler = InputFrameThrottler.create();
        camera = CameraDeviceSelector.createCameraDevice(CameraDevicePreference.PreferSurfaceTracking);
        camera.setSize(new Vec2I(40000, 20000));
        camera.openWithPreferredType(CameraDeviceType.Back);;
        camera.setFocusMode(CameraDeviceFocusMode.Infinity);

        outputFrameBuffer = OutputFrameBuffer.create();
        inputFrameThrottler = InputFrameThrottler.create();
        tracker = SurfaceTracker.create();
        camera.inputFrameSource().connect(inputFrameThrottler.input());
        inputFrameThrottler.output().connect(tracker.inputFrameSink());
        tracker.outputFrameSource().connect(outputFrameBuffer.input());
        outputFrameBuffer.signalOutput().connect(inputFrameThrottler.signalInput());

        //CameraDevice and rendering each require an additional buffer
        camera.setBufferCapacity(inputFrameThrottler.bufferRequirement() + outputFrameBuffer.bufferRequirement() + tracker.bufferRequirement() + 2);
    }

    public void dispose()
    {
        if (tracker != null) {
            tracker.dispose();
            tracker = null;
        }


        if (camera != null) {
            camera.dispose();
            camera = null;
        }
        if (scheduler != null) {
            scheduler.dispose();
            scheduler = null;
        }
    }

    public boolean start()
    {
        boolean status = true;
        if (camera != null) {
            status &= camera.start();
        } else {
            status = false;
        }
        if (tracker != null) {
            status &= tracker.start();
        } else {
            status = false;
        }
        return status;
    }

    public void stop()
    {
        if (camera != null) {
            camera.stop();
        }
        if (tracker != null) {
            tracker.stop();
        }
    }

    public void alignTargetToCameraImagePoint(Vec2F vec2F){
        tracker.alignTargetToCameraImagePoint(vec2F);

    }


    public byte[] render(int width, int height, int screenRotation  )
    {
//        while (scheduler.runOne())
//        {
//        }

        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0.f, 0.f, 0.f, 1.f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        OutputFrame oframe = outputFrameBuffer.peek();
        if (oframe == null) { return null ;}
        InputFrame iframe = oframe.inputFrame();
        if (iframe == null) { oframe.dispose(); return null; }
        CameraParameters cameraParameters = iframe.cameraParameters();
        if (cameraParameters == null) { oframe.dispose(); iframe.dispose(); return null; }
        float viewport_aspect_ratio = (float)width / (float)height;

        Matrix44F imageProjection = cameraParameters.imageProjection(viewport_aspect_ratio, screenRotation, true, false);
        Image image = iframe.image();

        try {
            if (iframe.index() != previousInputFrameIndex) {
                Buffer buffer = image.buffer();
                try {
                    if ((imageBytes == null) || (imageBytes.length != buffer.size())) {
                        imageBytes = new byte[buffer.size()];
                    }
                    buffer.copyToByteArray(imageBytes);
                    bgRenderer.upload(image.format(), image.width(), image.height(),
                            ByteBuffer.wrap(imageBytes));
                } finally {
                    buffer.dispose();
                }
                previousInputFrameIndex = iframe.index();
            }
            bgRenderer.render(imageProjection);

        Vec2F vec2F = cameraParameters.imageCoordinatesFromScreenCoordinates(viewport_aspect_ratio,
                screenRotation,true, false ,  new Vec2F(width,   height));
        alignTargetToCameraImagePoint(vec2F);
            Log.e("","tanghutanghutanghutanghutanghutanghu"+vec2F.data[0] +" "+vec2F.data[1]);
            Matrix44F projectionMatrix = cameraParameters.projection(0.01f, 1000.f, viewport_aspect_ratio, screenRotation, true,false);
            for (FrameFilterResult oResult : oframe.results()) {
                if (oResult instanceof SurfaceTrackerResult) {
                    SurfaceTrackerResult result = (SurfaceTrackerResult)oResult;
                    Matrix44F transform = result.transform();
              //      boxRenderer.render(projectionMatrix, gluInvertMatrix(transform),
               //             new Vec3F(0.5f, 0.5f, 0.5f));
                }
                if (oResult != null) {
                    oResult.dispose();
                }
            }
        } finally {
            iframe.dispose();
            oframe.dispose();
            if (cameraParameters != null) {
                cameraParameters.dispose();
            }
            image.dispose();
        }
        return imageBytes;
    }
}



