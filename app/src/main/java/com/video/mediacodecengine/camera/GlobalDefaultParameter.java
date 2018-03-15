package com.video.mediacodecengine.camera;

import android.graphics.ImageFormat;
import android.hardware.Camera.CameraInfo;


public class GlobalDefaultParameter {

	public static final int FRONT_CAMERA_ID = CameraInfo.CAMERA_FACING_FRONT;//1
	
	//for Camera default settings
	public static final int CAMERA_PREVIEW_IMAGE_FORMAT = ImageFormat.NV21;

	public static final int PREVIEW_WIDTH=1280;
	public static final int PREVIEW_HEIGHT=720;
	public static final int FPS=30;

}
