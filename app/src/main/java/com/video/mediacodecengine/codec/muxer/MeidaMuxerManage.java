package com.video.mediacodecengine.codec.muxer;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class MeidaMuxerManage {
    // 输出文件路径
    private String mOutputPath;
    private MediaMuxer mediaMuxer=null;
    private int trackNumber=0;
    public MeidaMuxerManage(String path){
        this.mOutputPath=path;
        trackNumber=0;
        try {
            mediaMuxer=new MediaMuxer(path,MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startMediaMuxer(){
        if(mediaMuxer!=null){
            mediaMuxer.start();
        }
    }

    public void stopMediaMuxer(){
        if(mediaMuxer!=null){
            mediaMuxer.stop();
            mediaMuxer.release();
        }
        trackNumber=0;

    }
    public synchronized  int addTrack(MediaFormat mediaFormat){
        trackNumber++;
        int trackIndex=mediaMuxer.addTrack(mediaFormat);
        return trackIndex;
    }

    public synchronized  void writeData(int trackIndex, ByteBuffer byteBuffer, MediaCodec.BufferInfo bufferInfo){
        if(trackNumber>0) {
            mediaMuxer.writeSampleData(trackIndex, byteBuffer, bufferInfo);
        }
    }
}
