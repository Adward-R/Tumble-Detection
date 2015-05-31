package com.demohn.tumbleapp;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Created by Mingchuan on 2015/5/26.
 */
public class Beep {
    double[] rec = {};
    Beep(double[] rec){
        this.rec = rec;
    }

    public void play(){
        double[] info = rec;
        int sampleRate = 8000;
        int numSamples = 0;

        int _total =0;
        //get total samples
        for(int j =0;j<info.length;j=j+2){
            numSamples += info[j] * sampleRate;
        }

        double sample[] = new double[numSamples];
        byte generatedSnd[] = new byte[2*numSamples];

        // set sample array
        for(int k=0;k<info.length;k=k+2){
            for(int l=0;l< info[k]*sampleRate;l++){
                // info[k+1] means frequency of one fragment
                sample[_total] = Math.sin(2 * Math.PI * l / (sampleRate/info[k+1]));
                _total += 1;
            }
        }
        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length, AudioTrack.MODE_STATIC);

        audioTrack.write(generatedSnd,0,generatedSnd.length);
        audioTrack.play();
    }
}