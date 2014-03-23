package cz.gug.hackathon.glass.gimmeatone;

import java.util.Arrays;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayer {

    public static final int SAMPLE_RATE = 44100;
    public static final int OUTPUT_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BUFFER_SIZE = SAMPLE_RATE * AUDIO_FORMAT / 100; // ~20ms
                                                                            // latency

    private volatile boolean playing = false;
    private AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
            SAMPLE_RATE, OUTPUT_CHANNELS, AUDIO_FORMAT, BUFFER_SIZE,
            AudioTrack.MODE_STREAM);

    private byte[] audioBuffer = new byte[BUFFER_SIZE];
    private short[] sampleBuffer = new short[BUFFER_SIZE / 2];
    private short[] mergeBuffer = new short[BUFFER_SIZE / 2];

    private Playback playback;

    public void play() {
        if (playing) {
            return;
        }
        playing = true;
        new Thread(new PlayerWorker()).start();
        track.play();
    }

    public void stop() {
        if (!playing) {
            return;
        }
        playing = false;
        track.pause();
        track.flush();
    }

    public void changeTone(Tone tone) {
        this.playback = tone.createPlayback();
    }

    public boolean isPlaying() {
        return playing;
    }

    private class PlayerWorker implements Runnable {
        @Override
        public void run() {
            while (playing) {
                Arrays.fill(mergeBuffer, (short) 0);
                fillSampleBuffer(AudioPlayer.this.playback);
                mergeSampleBuffer();
                convertAudioBuffer();
                track.write(audioBuffer, 0, audioBuffer.length);
            }
        }

        private void fillSampleBuffer(Playback playback) {
             if (playback == null) {
                 Arrays.fill(sampleBuffer, (short) 0);
             } else {
                 playback.fillBuffer(sampleBuffer);
             }
        }

        private void mergeSampleBuffer() {
            for (int i = 0; i < sampleBuffer.length; i++) {
                mergeBuffer[i] = (short) (sampleBuffer[i] / 4); // Normalization
            }
        }

        private void convertAudioBuffer() {
            for (int i = 0; i < mergeBuffer.length; i++) {
                audioBuffer[2 * i] = (byte) (mergeBuffer[i] & 0x00ff);
                audioBuffer[2 * i + 1] = (byte) ((mergeBuffer[i] & 0xff00) >>> 8);
            }
        }

    }

}
