package com.video.mediacodecengine.codec.encodec.basemediainterface;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;

import com.video.mediacodecengine.codec.encodec.MediaVideoEncodec;
import com.video.mediacodecengine.codec.muxer.MeidaMuxerManage;

import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class Mp4MediacEncodeInterface extends MediaEncodecCallBack{

    private MeidaMuxerManage meidaMuxerManage;
    private String path;
    private int videoTrackIndex=-1;
    private int audioTrackIndex=-1;
    public Mp4MediacEncodeInterface(String path){
        this.path=path;
        meidaMuxerManage=new MeidaMuxerManage(path);
    }

    @Override
    public void encodecOutput(int encoderStatus, ByteBuffer outputBuffer, MediaFormat bufferFormat, MediaCodec.BufferInfo bufferInfo, MediaCodec mediaCodec) {
        if (encoderStatus >= 0) {
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);
            if (outputBuffer == null) {
                throw new RuntimeException("encoderOutputBuffer " + outputBuffer + " was null");
            }
            //
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                //bufferInfo.size = 0;
            }
            else{
                //use outData
            }
            mediaCodec.releaseOutputBuffer(encoderStatus,false);
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            //拿到输出缓冲区,用于取到编码后的数据
            //outputBuffers = mediaCodec.getOutputBuffers();
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            // Subsequent data will conform to new format.
            MediaFormat format = mediaCodec.getOutputFormat();

            int trackIndex=meidaMuxerManage.addTrack(format);
        } else if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER){

        }else{

        }
    }
}
