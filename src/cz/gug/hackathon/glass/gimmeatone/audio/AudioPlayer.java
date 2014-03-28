package cz.gug.hackathon.glass.gimmeatone.audio;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

/**
 * Audio player facade capable of playing multiple {@link AudioSource}.
 */
public class AudioPlayer {

    public static final int SAMPLE_RATE = 44100; // Hz
    public static final int OUTPUT_CHANNELS = AudioFormat.CHANNEL_OUT_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    public static final int BUFFER_LATENCY = 24; // ms
    public static final int BUFFER_SIZE = BUFFER_LATENCY * SAMPLE_RATE / 1000; // samples
    public static final int MIXIN_DIVISOR = 8;


    private AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, SAMPLE_RATE,
            OUTPUT_CHANNELS, AUDIO_FORMAT, BUFFER_SIZE, AudioTrack.MODE_STREAM);

    private short[] sourceBuffer = new short[BUFFER_SIZE];
    private short[] mergeBuffer = new short[BUFFER_SIZE];
    private byte[] outputBuffer = new byte[BUFFER_SIZE * 2];

    private Collection<AudioSource> sources = Collections.synchronizedCollection(new HashSet<AudioSource>());

    private boolean autoStop = false;
    private long time = 0;
    private volatile Thread worker;

    /**
     * Create new audio player.
     */
    public AudioPlayer(boolean autoStop) {
        this.autoStop = autoStop;
    }

    /**
     * Start or continue the playback.
     */
    public synchronized void play() {
        if (!isPlaying()) {
            worker = new Thread(new PlayerWorker());
            worker.start();
            track.play();
        }
    }

    /**
     * Pause the playback.
     */
    public synchronized void stop() {
        if (isPlaying()) {
            track.stop();
        }
    }

    /**
     * Check whether the player is actually in PLAYING state.
     */
    public synchronized boolean isPlaying() {
        return worker != null && worker.isAlive();
    }

    /**
     * Replace current sources with a set of new ones.
     */
    public void changeSources(AudioSource ... sources) {
        this.sources.clear();
        this.sources.addAll(Arrays.asList(sources));
    }

    /**
     * Add additional audio source.
     */
    public void addSource(AudioSource source) {
        this.sources.add(source);
    }

    /**
     * Get number of currently playing sources.
     */
    public int sourceCount() {
        return sources.size();
    }


    //~ Inner Classes

    /**
     * Audio player worker responsible for keeping the audio queue full.
     */
    private class PlayerWorker implements Runnable {

        @Override
        public void run() {
            while (true) {
                // Get copy of the current sources (preventing ConcurrentModificationException)
                AudioSource[] currentSources = sources.toArray(new AudioSource[0]);
                // Clear the merge buffer
                Arrays.fill(mergeBuffer, (short) 0);
                // Process each source
                for (AudioSource source : currentSources) {
                    processSource(source);
                }
                // Convert merge buffer into output buffer
                for (int i = 0; i < mergeBuffer.length; i++) {
                    outputBuffer[2 * i] = (byte) (mergeBuffer[i] & 0x00ff);
                    outputBuffer[2 * i + 1] = (byte) ((mergeBuffer[i] & 0xff00) >>> 8);
                }
                // Write output buffer
                int written = track.write(outputBuffer, 0, outputBuffer.length);
                if (written < outputBuffer.length) {
                    break; // Audio has been stopped
                }
                // Increment time
                time = (time + sourceBuffer.length) % (Long.MAX_VALUE / 2);
                // Check auto-stop
                if (autoStop && currentSources.length == 0) {
                    stop();
                }
            }
        }

        private void processSource(AudioSource source) {
            int sampleCount = source.fillBuffer(sourceBuffer, time);
            for (int i = 0; i < sampleCount; i++) {
                int mergedSample = mergeBuffer[i] + (sourceBuffer[i] / MIXIN_DIVISOR);
                mergeBuffer[i] = (short) (mergedSample > Short.MAX_VALUE ? Short.MAX_VALUE :
                        (mergedSample < Short.MIN_VALUE ? Short.MIN_VALUE : mergedSample));
            }
            if (sampleCount < sourceBuffer.length) {
                sources.remove(source);
            }
        }

    }

}
