package com.video.mediacodecengine.codec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.util.Log;
import android.view.Surface;

import com.video.mediacodecengine.util.MediaCodecUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/9.
 */

public class MediaCodecManage extends BaseMediaCodecManage{

    private Surface mSurface;// 由MediaCodec创建的输入surface
    private MediaCodecUtil.Quality vbitrateType = MediaCodecUtil.Quality.MIDDLE_BIT;
    public MediaCodecManage(Context context,int width,int height,int FPS){
        super(context);
        this.mWidth=width;
        this.mHeight=height;
        this.FPS=FPS;

    }

    private void choesEncodeMediaCode(int intputType,String outputType){
        mediaCodecInfo=MediaCodecUtil.getEncodeMediaCodecInfo(outputType,intputType);
        if(mediaCodecInfo==null){
            return ;
        }

        try {
            mediaCodec=MediaCodec.createByCodecName(mediaCodecInfo.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat vformat = MediaFormat.createVideoFormat(MediaFormat.MIMETYPE_VIDEO_AVC, mWidth, mHeight);
        vformat.setInteger(MediaFormat.KEY_COLOR_FORMAT, intputType);
        vformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        vformat.setInteger(MediaFormat.KEY_BIT_RATE, MediaCodecUtil.getBitrateSize(mWidth,mHeight,vbitrateType));
        vformat.setInteger(MediaFormat.KEY_FRAME_RATE, FPS);
        vformat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VGOP);
        //vformat.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
        //vformat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));

        mediaCodec.configure(vformat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        if(intputType==MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface) {
            mSurface = mediaCodec.createInputSurface();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AsynchronousProcessingUsingBuffers();
        }
        else{
            SynchronousProcessingUsingBufferArrays();
        }


    }

    public boolean startCodec(){
        choesEncodeMediaCode(MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,MIME_TYPE);
        if(mediaCodec!=null){
            mediaCodec.start();
            return true;
        }
        return false;
    }

    public boolean stopCodec(){
        try {
            mediaCodec.stop();
            mediaCodec.release();
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public void putData(byte[] data){
        onFrameDraw(data);
    }

}
