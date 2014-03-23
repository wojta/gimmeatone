package cz.gug.hackathon.glass.gimmeatone;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

public class AudioPlayer {

    public static final int SAMPLE_RATE = 44100;
    public static final int OUTPUT_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BUFFER_SIZE = 10000;
                                                                            // latency

    private volatile boolean playing = false;
    private AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
            SAMPLE_RATE, OUTPUT_CHANNELS, AUDIO_FORMAT, BUFFER_SIZE,
            AudioTrack.MODE_STREAM);

    private byte[] audioBuffer = new byte[BUFFER_SIZE];
    private short[] sampleBuffer = new short[BUFFER_SIZE / 2];
    private short[] mergeBuffer = new short[BUFFER_SIZE / 2];

    private Set<Playback> playbacks = new HashSet<Playback>();

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
        synchronized (playbacks) {
            playbacks.clear();
            playbacks.add(tone.createPlayback());
        }
    }

    public void addPlayback(Playback playback) {
        synchronized (playbacks) {
            playbacks.add(playback);
        }
    }

    public void removePlayback(Playback playback) {
        synchronized (playback) {
            playbacks.remove(playback);
        }
    }

    public boolean isPlaying() {
        return playing;
    }

    private class PlayerWorker implements Runnable {
        @Override
        public void run() {
            while (playing) {
                Playback[] currentPlaybacks;
                synchronized (playbacks) {
                    currentPlaybacks = playbacks.toArray(new Playback[playbacks.size()]);
                }
                Arrays.fill(mergeBuffer, (short) 0);
                for (Playback playback : currentPlaybacks) {
                    fillSampleBuffer(playback);
                    mergeSampleBuffer();
                }
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
                int mergedSample = mergeBuffer[i] + (sampleBuffer[i] / 10);
                mergeBuffer[i] = (short) (
                        mergedSample > Short.MAX_VALUE ? Short.MAX_VALUE :
                            (mergedSample < Short.MIN_VALUE ? Short.MIN_VALUE : mergedSample));
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
