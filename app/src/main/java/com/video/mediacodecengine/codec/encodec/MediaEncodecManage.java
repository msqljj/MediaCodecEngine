package com.video.mediacodecengine.codec.encodec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import com.video.mediacodecengine.codec.encodec.basemediainterface.MediaEncodecCallBack;
import com.video.mediacodecengine.codec.encodec.basemediainterface.Mp4MediacEncodeInterface;
import com.video.mediacodecengine.codec.encodec.basemediainterface.StreamH264MediaEncodeInterface;
import com.video.mediacodecengine.util.MediaCodecUtil;

import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class MediaEncodecManage {
    public final static int SAVE_MP4=1000;//本地存储
    public final static int SAVE_STREAM_H264=1001;//直播流 h264
    private int meidaType;
    private Context context;
    private int mWidth,mHeight;
    private int FPS;
    private String path;

    private MediaVideoEncodec mediaVideoEncodec;
    private MediaEncodecCallBack mediaEncodecCallBack;
    public MediaEncodecManage(Context context, int width, int height, int FPS,String path,int meidaType){
        this.context=context;
        this.mWidth=width;
        this.mHeight=height;
        this.FPS=FPS;
        this.path=path;
        this.meidaType=meidaType;
        configEncode();

        mediaVideoEncodec=new MediaVideoEncodec(context,mWidth,mHeight,FPS,
                MediaCodecUtil.Quality.MIDDLE_BIT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,MediaFormat.MIMETYPE_VIDEO_AVC, mediaEncodecCallBack);
    }

    private void configEncode(){
        switch (meidaType){
            case SAVE_MP4:
                mediaEncodecCallBack=new Mp4MediacEncodeInterface(path);
                break;
            case SAVE_STREAM_H264:
                mediaEncodecCallBack=new StreamH264MediaEncodeInterface(path);
                break;
        }
    }
    public void onFrameDraw(byte[] data){
        mediaVideoEncodec.putVideoData(data);
    }
    public void startEncodec(){
        mediaVideoEncodec.startCodec();
    }

    public void stopEncodec(){
        mediaVideoEncodec.stopCodec();
    }
}
