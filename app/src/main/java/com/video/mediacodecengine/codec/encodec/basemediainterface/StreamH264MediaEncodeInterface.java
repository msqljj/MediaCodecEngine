package com.video.mediacodecengine.codec.encodec.basemediainterface;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wb5790 on 2018/3/13.
 */

public class StreamH264MediaEncodeInterface extends MediaEncodecCallBack{
    public String path;

    private byte[] m_info;
    protected ArrayBlockingQueue<byte[]> YUVQueue,encodeQueue;
    public StreamH264MediaEncodeInterface(String path){
        this.path=path;
        YUVQueue=new ArrayBlockingQueue<byte[]>(MAC_QUEUE);

    }

    public void encodecOutput(int encoderStatus, ByteBuffer outputBuffer, MediaFormat bufferFormat, MediaCodec.BufferInfo bufferInfo, MediaCodec mediaCodec){
        if (encoderStatus >= 0) {


            if (outputBuffer == null) {
                throw new RuntimeException("encoderOutputBuffer " + outputBuffer + " was null");
            }


            //
            if ((bufferInfo.flags & MediaCodec.BUFFER_FLAG_CODEC_CONFIG) != 0 ) {
                //bufferInfo.size = 0;
                MediaFormat format = mediaCodec.getOutputFormat();
                String MINE=format.getString(MediaFormat.KEY_MIME);
                byte[] outData = new byte[bufferInfo.size];
                outputBuffer.get(outData);
                ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData,0,bufferInfo.size);
                spsPpsBuffer.position(0);
                //
                if(m_info==null&&MINE.equals(MediaFormat.MIMETYPE_VIDEO_AVC)){
                    if (spsPpsBuffer.getInt() == 0x00000001||spsPpsBuffer.getInt() == 0x000001){
                        m_info = new byte[outData.length];
                        System.arraycopy(outData, 0, m_info, 0, outData.length);
                    }
                }
            }
            else{
                int m_info_len=0;
                if(m_info!=null){
                    m_info_len=m_info.length;
                }
                byte[] outData = new byte[bufferInfo.size+m_info_len];
                outputBuffer.get(outData);
                ByteBuffer spsPpsBuffer = ByteBuffer.wrap(outData,m_info_len,bufferInfo.size);
                spsPpsBuffer.position(m_info_len);
                if((spsPpsBuffer.getInt() == 0x00000001&&outData[4] == 0x65) ||(spsPpsBuffer.getInt() == 0x000001&&outData[3] == 0x65))//key frame   编码器生成关键帧时只有 00 00 00 01 65 没有pps sps， 要加上
                {
                    System.arraycopy(m_info, 0,  outData, 0, m_info.length);
                    YUVQueue.offer(outData);
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


    public byte[] getEncodeData(){
        if(YUVQueue!=null){
            return YUVQueue.poll();
        }
        return null;
    }
}
