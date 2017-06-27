package com.score.rahasak.utils;

public class OpusDecoder {

    public native boolean nativeInitDecoder(int samplingRate, int numberOfChannels, int frameSize);

    public native int nativeDecodeBytes(byte[] in, short[] out);

    public native boolean nativeReleaseDecoder();

    static {
        System.loadLibrary("senz");
    }

    public void init(int sampleRate, int channels, int frameSize) {
        this.nativeInitDecoder(sampleRate, channels, frameSize);
    }

    public int decode(byte[] encodedBuffer, short[] buffer) {
        int decoded = this.nativeDecodeBytes(encodedBuffer, buffer);

        return decoded;
    }

    public void close() {
        this.nativeReleaseDecoder();
    }

}
