package com.video.mediacodecengine.util;

import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by wb5790 on 2018/3/9.
 */

public class MediaCodecUtil {

    /**
     * 比特率质量
     */
    public  enum  Quality{
        LOW_BIT(1),MIDDLE_BIT(3),HEIGHT_BIT(5);

        private int number;
        Quality(int number){
            this.number=number;
        }
        public int getNumber(){
            return number;
        }
    }
    /**
     *
     * @param width
     * @param height
     * @param quality LOW_BIT MIDDLE_BIT HEIGHT_BIT
     * @return
     */
    public  static int getBitrateSize(int width,int height,Quality quality){
        return quality.getNumber()*width*height;
    }


    public static MediaCodecInfo getEncodeMediaCodecInfo(String codecType,int colorSpace){
        //获取解码器列表
        int numCodecs = MediaCodecList.getCodecCount();
        MediaCodecInfo codecInfo = null;
        for(int i = 0; i < numCodecs && codecInfo == null ; i++){
            MediaCodecInfo info = MediaCodecList.getCodecInfoAt(i);
            if(!info.isEncoder()){
                continue;
            }
            String[] types = info.getSupportedTypes();
            boolean found = false;
            for(int j=0; j<types.length && !found; j++){
                if(types[j].equals(codecType)&&isSupportColorSpace(info,codecType,colorSpace)){
                    found = true;
                    break;
                }
            }
            if(!found){
                continue;
            }
            codecInfo = info;
        }

        return codecInfo;
    }

    public  static boolean isSupportColorSpace(MediaCodecInfo codecInfo,String codecType,int colorSpaceType){
        //检查所支持的colorspace
        int colorFormat = 0;
        MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(codecType);
        for(int i = 0; i < capabilities.colorFormats.length && colorFormat == 0 ; i++){
            int format = capabilities.colorFormats[i];
            if(format==colorSpaceType){
                colorFormat = format;
                break;
            }
        }
        if(colorFormat!=0){
            return true;
        }
        else{
            return false;
        }
    }


}
