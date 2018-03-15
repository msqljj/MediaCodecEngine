package com.video.mediacodecengine.codec.encodec;

import android.content.Context;
import android.media.AudioFormat;
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

public class MediaAudioEncodec extends BaseMediaEncodec{
    // 44.1[KHz] is only setting guaranteed to be available on all devices.
    private static final int SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 64000;
    private MediaCodecUtil.Quality vbitrateType = MediaCodecUtil.Quality.MIDDLE_BIT;
    private int intputType= MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar;
    private String outputType= MediaFormat.MIMETYPE_AUDIO_AAC;



    public MediaAudioEncodec(Context context, int width, int height, int FPS,
                             MediaCodecUtil.Quality vbitrateType, int intputType, String outputType, MediaEncodecCallBack mediaEncodecCallBack){
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

        // mediaFormat配置
        final MediaFormat audioFormat = MediaFormat.createAudioFormat(outputType, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AudioFormat.CHANNEL_IN_MONO);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
        audioFormat.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
        //vformat.setByteBuffer("csd-0"  , ByteBuffer.wrap(sps));
        //vformat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));

        mediaCodec.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
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
