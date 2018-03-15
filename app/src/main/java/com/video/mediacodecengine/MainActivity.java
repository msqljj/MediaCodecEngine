package com.video.mediacodecengine;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.video.mediacodecengine.camera.CameraManage;
import com.video.mediacodecengine.codec.MediaCodecManage;
import com.video.mediacodecengine.codec.encodec.MediaEncodecManage;
import com.video.mediacodecengine.util.GlUtil;
import com.video.mediacodecengine.util.ScreenUtill;
import com.video.mediacodecengine.view.GlSurfaceViewManage;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class MainActivity extends Activity {


    private GLSurfaceView surfaceView;
    private Button startVideo;

    protected int previewSizeWidth;
    protected int previewSizeHeight;
    protected int mCameraId;
    protected int outlineOrientation = 270;
    private int textureOESId;
    private SurfaceTexture surfaceTexture;
    private Camera.PreviewCallback previewCallback;
    private MediaEncodecManage mediaEncodecManage;
    private boolean startCodec;
    private GlSurfaceViewManage glSurfaceViewManage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView=(GLSurfaceView) findViewById(R.id.surfaceView);
        startVideo=(Button) findViewById(R.id.startVideo);
        CameraManage.getInstance().openCamera();

        initData();
        onClickView();
    }

    private void initData(){
        glSurfaceViewManage=new GlSurfaceViewManage(this,surfaceView);
        startCodec=false;
        previewCallback=new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if(startCodec){
                    mediaEncodecManage.onFrameDraw(data);
                }

            }
        };
        glSurfaceViewManage.initSurface(new GLSurfaceView.Renderer() {
            @Override
            public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            }

            @Override
            public void onSurfaceChanged(GL10 gl, int width, int height) {

            }

            @Override
            public void onDrawFrame(GL10 gl) {

            }
        });
    }

    private void onClickView(){
        startVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!startCodec) {
                    if (mediaEncodecManage == null) {
                        mediaEncodecManage = new MediaEncodecManage(MainActivity.this, previewSizeWidth, previewSizeHeight, CameraManage.getInstance().getFPS(),
                                null,MediaEncodecManage.SAVE_STREAM_H264);
                    }
                    mediaEncodecManage.startEncodec();

                }
                else{
                    mediaEncodecManage.stopEncodec();
                }
                startCodec=!startCodec;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(surfaceView==null){
            return ;
        }
        if(CameraManage.getInstance().getmCamera()==null){
            if(!CameraManage.getInstance().openCamera()){
                return;
            }
        }

        CameraManage.getInstance().setCameraDefultParameters();
        previewSizeWidth=CameraManage.getInstance().getPreviewSizeWidth();
        previewSizeHeight=CameraManage.getInstance().getPreviewSizeHeight();
        mCameraId=CameraManage.getInstance().getmCameraId();
        Camera.CameraInfo cameraInfo=new Camera.CameraInfo();
        Camera.getCameraInfo(CameraManage.getInstance().getmCameraId(), cameraInfo);
        outlineOrientation=(cameraInfo.orientation+ ScreenUtill.getDisplayRotation(this))%360;
        surfaceView.queueEvent(new Runnable() {
            @Override
            public void run() {
                textureOESId= GlUtil.createOESTextureID();
                surfaceTexture=new SurfaceTexture(textureOESId);
                CameraManage.getInstance().startPreviewWithBuffer(surfaceTexture, previewCallback);
            }
        });

        surfaceView.requestRender();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(surfaceView==null){
            return;
        }
        CameraManage.getInstance().closeCamera();

    }
}
