package com.video.mediacodecengine.codec;

import android.content.Context;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by wb5790 on 2018/3/9.
 */

public class BaseMediaCodecManage {




    protected static final String MIME_TYPE = "video/avc";
    private static final int QUEUE_SIZE=3;
    private static final int timeoutUs=11000;
    public ArrayBlockingQueue<byte[]>YUVQueue;
    protected MediaCodecInfo mediaCodecInfo;
    protected MediaCodec mediaCodec;
    protected Context context;
    protected int mWidth,mHeight;
    protected int FPS;
    protected int VGOP=1;//关键帧
    protected int generateIndex=0;
    public BaseMediaCodecManage(Context context){
        this.context=context;
        YUVQueue = new ArrayBlockingQueue<byte[]>(QUEUE_SIZE);
    }


    protected void AsynchronousProcessingUsingBuffers(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaCodec.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(MediaCodec mc, int inputBufferId) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ByteBuffer inputBuffer = mc.getInputBuffer(inputBufferId);
                        encodecInput(inputBuffer,inputBufferId);
                    }
                }

                @Override
                public void onOutputBufferAvailable(MediaCodec mc, int outputBufferId, @NonNull MediaCodec.BufferInfo info) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        ByteBuffer outputBuffer = mc.getOutputBuffer(outputBufferId);
                        MediaFormat bufferFormat = mc.getOutputFormat(outputBufferId); // option A
                        encodecOutput(outputBufferId,outputBuffer,bufferFormat,info);
                    }

                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {

                }


                @Override
                public void onOutputFormatChanged(MediaCodec mc, MediaFormat format) {
                    // Subsequent data will conform to new format.
                    // Can ignore if using getOutputFormat(outputBufferId)
                   // mOutputFormat = format; // option B
                }


            });
        }
    }

    protected void SynchronousProcessingUsingBuffers(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int inputBufferId = mediaCodec.dequeueInputBuffer(timeoutUs);
            if (inputBufferId >= 0) {
                ByteBuffer inputBuffer = mediaCodec.getInputBuffer(inputBufferId);
                encodecInput(inputBuffer,inputBufferId);
            }
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo,timeoutUs);
            ByteBuffer outputBuffer=null;
            MediaFormat bufferFormat=null;
            if (outputBufferId >= 0) {
                outputBuffer = mediaCodec.getOutputBuffer(outputBufferId);
                bufferFormat = mediaCodec.getOutputFormat(outputBufferId);
            }
            encodecOutput(outputBufferId,outputBuffer,bufferFormat,bufferInfo);
        }
        else {
            SynchronousProcessingUsingBufferArrays();
        }
    }

    protected void SynchronousProcessingUsingBufferArrays(){
        ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
        ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
        int inputBufferId = mediaCodec.dequeueInputBuffer(timeoutUs);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = inputBuffers[inputBufferId];
            encodecInput(inputBuffer,inputBufferId);
        }
        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferId = mediaCodec.dequeueOutputBuffer(bufferInfo,timeoutUs);
        ByteBuffer outputBuffer=null;
        if(outputBufferId>=0){
            outputBuffer = outputBuffers[outputBufferId];
        }
        encodecOutput(outputBufferId,outputBuffer,null,bufferInfo);

    }

    private void encodecInput(ByteBuffer inputBuffer,int inputBufferId){
        byte[] input= null;
        input = YUVQueue.poll();
        if(input==null){
            return;
        }
        inputBuffer.clear();
        inputBuffer.put(input);
        long pts = computePresentationTime(generateIndex);
        mediaCodec.queueInputBuffer(inputBufferId, 0, input.length, pts, 0);
        generateIndex++;
    }

    private void encodecOutput(int encoderStatus,ByteBuffer outputBuffer,MediaFormat bufferFormat,MediaCodec.BufferInfo bufferInfo){
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
            //MediaFormat format = codec.getOutputFormat();
        } else if (encoderStatus == MediaCodec.INFO_TRY_AGAIN_LATER){

        }else{

        }
    }
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / FPS;
    }


    public void onFrameDraw(byte[] data){
        if(YUVQueue!=null){
            YUVQueue.offer(data);
        }
    }
}
