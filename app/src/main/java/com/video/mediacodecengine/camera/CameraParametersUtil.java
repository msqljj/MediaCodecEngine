package com.video.mediacodecengine.camera;

import android.hardware.Camera;

import java.util.List;

/**
 * Created by wb5790 on 2018/1/30.
 */

public class CameraParametersUtil {


    //闪光灯配置参数
    public static void setFlashMode(Camera.Parameters parameters,String mode){

        List<String> array= parameters.getSupportedFlashModes();
        if(array!=null){
            for(int i=0;i<array.size();i++){
                if(array.get(i).equals(mode)){
                    parameters.setFlashMode(mode);
                    break;
                }
            }
        }
    }
    /**
     * 曝光
     * @param parameters
     * @param exp_compen   <0  is auto
      */
    public static void setExposureCompensation(Camera.Parameters parameters,int exp_compen){
        if(exp_compen<0) {
            if (parameters.isAutoExposureLockSupported()) {
            	//parameters.setExposureCompensation(10);
                parameters.setAutoExposureLock(false);
            }
        }
        else{
            parameters.setExposureCompensation(exp_compen);
            parameters.setAutoExposureLock(true);
        }
    }

    /**
     * 场景
     * @param parameters
     * @param mode
     */
    public static void setSenceMode(Camera.Parameters parameters,String mode){
        if (mode == null) {
            mode = Camera.Parameters.SCENE_MODE_AUTO;
        }
        List<String> array= parameters.getSupportedSceneModes();
        if(array!=null){
            for(int i=0;i<array.size();i++){
                if(array.get(i).equals(mode)){
                    parameters.setSceneMode(mode);
                    break;
                }
            }
        }

    }


    public static void setWhiteBalance(Camera.Parameters parameters,String mode){
        if (mode == null) {
            mode = Camera.Parameters.WHITE_BALANCE_AUTO;
        }
        List<String> array=  parameters.getSupportedWhiteBalance();
        if(array!=null){
            for(int i=0;i<array.size();i++){
                if(array.get(i).equals(mode)){
                    parameters.setWhiteBalance(mode);
                    break;
                }
            }
        }
    }

    public static void setVideoStabilization(Camera.Parameters parameters,String mode){

        boolean useVideoSta =false;
        if(mode!=null&& mode.length()>0)
            useVideoSta = Boolean.valueOf(mode);
        if(parameters.isVideoStabilizationSupported()){
            parameters.setVideoStabilization(useVideoSta);
        }

    }

    public static void setFPS(Camera.Parameters parameters, int startFps,int endFps){
        startFps*=1000;
        endFps*=1000;
        List<int[]> array=parameters.getSupportedPreviewFpsRange();
        if(array!=null){
            for(int i=0;i<array.size();i++){
                if(array.get(i)[0]==startFps&&array.get(i)[1]==endFps){
                    parameters.setPreviewFpsRange(startFps,endFps);
                    break;
                }
            }
        }
    }

    public static Camera.Size setPreviewSize(Camera.Parameters parameters,int width,int height){

        List<Camera.Size> array=parameters.getSupportedPreviewSizes();
        if(array!=null){
            Camera.Size size=array.get(0);
            int maxW=array.get(0).width;
            int maxH=array.get(0).height;
            for(int i=0;i<array.size();i++){
                if(array.get(i).width==width&&array.get(i).height==height){
                    parameters.setPreviewSize(array.get(i).width,array.get(i).height);
                    return array.get(i);
                }
                else{
                    if(Math.abs(maxW-width)>Math.abs(array.get(i).width-width)&&array.get(i).width*1.0/array.get(i).height==width*1.0/height){
                        maxW=array.get(i).width;
                        maxH=array.get(i).height;
                        size=array.get(i);
                    }
                }
            }
            parameters.setPreviewSize(maxW,maxH);
            return size;
        }
        return null;
    }

    public static void setFouceMode(Camera.Parameters parameters,String mode){

        List<String> array=parameters.getSupportedFocusModes();
        if(array!=null){
            for(int i=0;i<array.size();i++){
                if(array.get(i).equals(mode)){
                    parameters.setFocusMode(mode);
                }
            }
        }
    }
    //key for getting iso values
    public final String[] KEY_ISO_VALUES = {"iso-values", "iso-speed-values"};
    //key for setting iso value
    public static final String KEY_ISO = "iso";

    //Lenovo Z90 ISO values
    public static final String ISO_AUTO = "auto";
/*     public static final String ISO_HJR = "ISO_HJR";
    public static final String ISO_100 = "ISO100";
 	public static final String ISO_200 = "ISO200";
    public static final String ISO_400 = "ISO400";
    public static final String ISO_800 = "ISO800";
    public static final String ISO_1600 = "ISO1600";
    public static final String ISO_3200 = "ISO3200";
*/
    public static void setISOModeAuto(Camera.Parameters parameters){
    	
    	parameters.set(KEY_ISO, ISO_AUTO);
    	
    }
    
  
}
