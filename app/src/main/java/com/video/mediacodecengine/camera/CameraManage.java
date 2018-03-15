package com.video.mediacodecengine.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import java.io.IOException;

/**
 * Created by wb5790 on 2018/1/30.
 */

public class CameraManage {

    private int mCameraId = GlobalDefaultParameter.FRONT_CAMERA_ID;//前置1
    private static CameraManage instance;

    private CameraManage(){
    }

    public static CameraManage getInstance(){

        if(instance==null){
            instance= new CameraManage();
        }
        return instance;
    }


    private Camera mCamera;

    protected int previewSizeWidth;
    protected int previewSizeHeight;
    protected byte[] mPreviewFrameBuffer;
    protected byte[] mPreviewFrameBuffer2; //fix bug: for fps
    protected byte[] mPreviewFrameBuffer3; //fix bug: render on native layer.


    public boolean openCamera(){
        int cameras = Camera.getNumberOfCameras();
        if(cameras<=0){
            return false;
        }

        try {
            mCamera = Camera.open(mCameraId);
        } catch (RuntimeException e) {
            mCamera =null;
            e.printStackTrace();
        }

        if (mCamera == null) {
            return false;
        }
        return true;
    }


    public void setCameraDefultParameters(){
        if(mCamera==null)
            return;
        Camera.Parameters parameters=mCamera.getParameters();
        CameraParametersUtil.setFlashMode(parameters,null);
        CameraParametersUtil.setExposureCompensation(parameters,-1);
        CameraParametersUtil.setSenceMode(parameters,null);
        CameraParametersUtil.setWhiteBalance(parameters,null);
        CameraParametersUtil.setISOModeAuto(parameters);
        CameraParametersUtil.setFouceMode(parameters,Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPreviewFormat(ImageFormat.NV21);
        CameraParametersUtil.setVideoStabilization(parameters,null);
        mCamera.setParameters(parameters);
        lockFps(GlobalDefaultParameter.FPS,GlobalDefaultParameter.FPS);
      /*  CameraParametersUtil.setFPS(parameters,30,30);
        Camera.Size previewSize=CameraParametersUtil.setPreviewSize(parameters,PREVIEW_WIDTH,PREVIEW_HEIGHT);
        previewSizeWidth=previewSize.width;
        previewSizeHeight=previewSize.height;*/
        
        mCamera.stopPreview();
    }
    public void lockFps(int startFps,int endFps){
    	Camera.Parameters parameters=mCamera.getParameters();
    	CameraParametersUtil.setFPS(parameters,startFps,endFps);
    	Camera.Size previewSize=CameraParametersUtil.setPreviewSize(parameters,GlobalDefaultParameter.PREVIEW_WIDTH,GlobalDefaultParameter.PREVIEW_HEIGHT);
        previewSizeWidth=previewSize.width;
        previewSizeHeight=previewSize.height;
        mCamera.setParameters(parameters);
        
    }
    private int frameidx = 0;
    public void startPreviewWithBuffer(SurfaceTexture surfaceTexture, final Camera.PreviewCallback cb) {
        if (mCamera != null) {
            try {
                if (mPreviewFrameBuffer == null) {
                    Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                    if (previewSize == null)
                        return;
                    int size = previewSize.width
                            * previewSize.height
                            * ImageFormat
                            .getBitsPerPixel(GlobalDefaultParameter.CAMERA_PREVIEW_IMAGE_FORMAT)
                            / 8;
                    mPreviewFrameBuffer = new byte[size];
                }
                if (mPreviewFrameBuffer2 == null) {
                    Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                    if (previewSize == null)
                        return;
                    int size = previewSize.width
                            * previewSize.height
                            * ImageFormat
                            .getBitsPerPixel(GlobalDefaultParameter.CAMERA_PREVIEW_IMAGE_FORMAT)
                            / 8;
                    mPreviewFrameBuffer2 = new byte[size];
                }
                if (mPreviewFrameBuffer3 == null) {
                    Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
                    if (previewSize == null)
                        return;
                    int size = previewSize.width
                            * previewSize.height
                            * ImageFormat
                            .getBitsPerPixel(GlobalDefaultParameter.CAMERA_PREVIEW_IMAGE_FORMAT)
                            / 8;
                    mPreviewFrameBuffer3 = new byte[size];
                }
                mCamera.addCallbackBuffer(mPreviewFrameBuffer);
                mCamera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
                    @Override
                    public void onPreviewFrame(byte[] data, Camera camera) {
                        if(frameidx==0)
                        {
                            mCamera.addCallbackBuffer(mPreviewFrameBuffer2);
                            frameidx = 1;
                        }
                        else if(frameidx==1)
                        {
                            mCamera.addCallbackBuffer(mPreviewFrameBuffer3);
                            frameidx = 2;
                        }
                        else
                        {
                            mCamera.addCallbackBuffer(mPreviewFrameBuffer);
                            frameidx = 0;
                        }
                        cb.onPreviewFrame(data, camera);
                    }
                });
                mCamera.setPreviewTexture(surfaceTexture);// Pass a fully initialized  SurfaceHolder to  setPreviewDisplay(SurfaceHolder).

                startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }


    public Camera getmCamera(){
        return mCamera;
    }
    public int getmCameraId(){
        return mCameraId;
    }
    public void startPreview() {
        if (mCamera != null) {
            /*Camera.Parameters parameters=mCamera.getParameters();
            CameraParametersUtil.setPreviewSize(parameters,PREVIEW_WIDTH,PREVIEW_HEIGHT);
            mCamera.setParameters(parameters);*/
            mCamera.startPreview();
            //to avoid dark face problem
            if(mCameraId == GlobalDefaultParameter.FRONT_CAMERA_ID){
                try {
                    mCamera.startFaceDetection();
                } catch (Exception e) {

                }
            }
        }
    }
    public void stopPreview() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mPreviewFrameBuffer = null;
            mPreviewFrameBuffer2 = null;
            mPreviewFrameBuffer3 = null;

            if(mCameraId == GlobalDefaultParameter.FRONT_CAMERA_ID)
                try {
                    mCamera.stopFaceDetection();
                } catch (Exception e) {
                    // TODO: handle exception
                }

            mCamera.stopPreview();

        }
    }

    public void closeCamera() {

        stopPreview();
        releaseCamera();
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }
    public int getPreviewSizeWidth(){
        return previewSizeWidth;
    }

    public int getPreviewSizeHeight(){
        return previewSizeHeight;
    }

    public int getFPS(){
        return GlobalDefaultParameter.FPS;
    }
}
