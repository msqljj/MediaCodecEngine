package com.video.mediacodecengine.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;

public class GlSurfaceViewManage {
	private Context context;
	private GLSurfaceView glSurfaceView;
	
	public GlSurfaceViewManage(Context context, GLSurfaceView glSurfaceView){
		this.context=context;
		this.glSurfaceView=glSurfaceView;
	}
	
	public void initSurface(Renderer renderer){
		glSurfaceView.setEGLContextClientVersion(2);
		glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		glSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
		//mGLSurfaceView.setEGLConfigChooser(new ES2ConfigChooser());
		glSurfaceView.setRenderer(renderer);
		glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		//mGLSurfaceView.getHolder().setFormat(android.graphics.PixelFormat.TRANSLUCENT);
		//glSurfaceView.setOnTouchListener(mOnTouchListener);
	}

}
