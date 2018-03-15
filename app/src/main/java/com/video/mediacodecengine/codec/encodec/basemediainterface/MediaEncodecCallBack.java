package com.video.mediacodecengine.codec.encodec.basemediainterface;

import android.media.MediaCodec;
import android.media.MediaFormat;

import java.nio.ByteBuffer;

/**
 * Created by wb5790 on 2018/3/13.
 */

public abstract class MediaEncodecCallBack {
    public abstract void encodecOutput(int encoderStatus, ByteBuffer outputBuffer, MediaFormat bufferFormat, MediaCodec.BufferInfo bufferInfo, MediaCodec mediaCodec);
}
