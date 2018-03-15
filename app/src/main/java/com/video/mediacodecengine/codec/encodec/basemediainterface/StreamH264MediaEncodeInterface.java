package com.video.mediacodecengine.codec.encodec.basemediainterface;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class StreamH264MediaEncodeInterface extends MediaEncodecCallBack{
    public String path;

    private byte[] m_info;
    public StreamH264MediaEncodeInterface(String path){
        this.path=path;
    }

    public void encodecOutput(int encoderStatus, ByteBuffer outputBuffer, MediaFormat bufferFormat, MediaCodec.BufferInfo bufferInfo, MediaCodec mediaCodec){
        if (encoderStatus >= 0) {
            byte[] outData = new byte[bufferInfo.size];
            outputBuffer.get(outData);
            ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
            spsPpsBuffer.position(0);

            if (outputBuffer == null) {
                throw new RuntimeException("encoderOutputBuffer " + outputBuffer + " was null");
            }


            //
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0) {
                //bufferInfo.size = 0;
                MediaFormat format = mediaCodec.getOutputFormat();
                String MINE=format.getString(MediaFormat.KEY_MIME);

                //
                if(m_info==null&&MINE.equals(MediaFormat.MIMETYPE_VIDEO_AVC)){

                    ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData);
                    spsPpsBuffer.position(0);
                    if (spsPpsBuffer.getInt() == 0x00000001)
                    {
                        m_info = new byte[outData.length];
                        System.arraycopy(outData, 0, m_info, 0, outData.length);

                    }

                }
            }
            else{
                if(outData[4] == 0x65) //key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
                {
                    System.arraycopy(output, 0,  yuv420, 0, pos);
                    System.arraycopy(m_info, 0,  output, 0, m_info.length);
                    System.arraycopy(yuv420, 0,  output, m_info.length, pos);
                    pos += m_info.length;
                }


            }
            mediaCodec.releaseOutputBuffer(encoderStatus,false);
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED) {
            //拿到输出缓冲区,用于取到编码后的数据
            //outputBuffers = mediaCodec.getOutputBuffers();
        } else if (encoderStatus == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
            // Subsequent data will conform to new format.
            //MediaFormat format = mediaCodec.getOutputFormat();
        } else if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER){

        }else{

        }
    }


}
