package com.video.mediacodecengine.codec.encodec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Build;
import android.view.Surface;

import com.video.mediacodecengine.codec.encodec.basemediainterface.MediaEncodecCallBack;
import com.video.mediacodecengine.util.MediaCodecUtil;

import java.io.IOException;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class MediaVideoEncodec extends BaseMediaEncodec {


    private Surface mSurface;// 由MediaCodec创建的输入surface
    private MediaCodecUtil.Quality vbitrateType = MediaCodecUtil.Quality.MIDDLE_BIT;
    private int intputType=MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
    private String outputType=MediaFormat.MIMETYPE_VIDEO_AVC;
    public MediaVideoEncodec(Context context,int width,int height,int FPS,
                             MediaCodecUtil.Quality vbitrateType,int intputType,String outputType,MediaEncodecCallBack mediaEncodecCallBack){
        super(context);
        this.mWidth=width;
        this.mHeight=height;
        this.FPS=FPS;
        if(vbitrateType!=null) {
            this.vbitrateType = vbitrateType;
        }
        if(intputType!=-1) {
            this.intputType = intputType;
        }
        if(outputType!=null) {
            this.outputType = outputType;
        }
        this.mediaEncodecCallBack=mediaEncodecCallBack;
    }

    private void choesEncodeMediaCode(int intputType,String outputType){
        mediaCodecInfo=MediaCodecUtil.getEncodeMediaCodecInfo(outputType,intputType);
        if(mediaCodecInfo==null){
            return ;
        }

        try {
            mediaCodec= MediaCodec.createByCodecName(mediaCodecInfo.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaFormat vformat = MediaFormat.createVideoFormat(outputType, mWidth, mHeight);
        vformat.setInteger(MediaFormat.KEY_COLOR_FORMAT, intputType);
        vformat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, 0);
        vformat.setInteger(MediaFormat.KEY_BIT_RATE, MediaCodecUtil.getBitrateSize(mWidth,mHeight,vbitrateType));
        vformat.setInteger(MediaFormat.KEY_FRAME_RATE, FPS);
        vformat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, VGOP);
        //vformat.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
        //vformat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));

        mediaCodec.configure(vformat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        if(intputType== MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface) {
            mSurface = mediaCodec.createInputSurface();
        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            AsynchronousProcessingUsingBuffers();
        }
    }
    public Surface getmSurface(){
        return mSurface;
    }
    public boolean startCodec(){
        choesEncodeMediaCode(intputType,outputType);
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

    public void putVideoData(byte[] data){
        super.putVideoData(data);
    }
}
