package cz.gug.hackathon.glass.gimmeatone;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import cz.gug.hackathon.glass.gimmeatone.audio.AudioPlayer;
import cz.gug.hackathon.glass.gimmeatone.audio.AudioSource;
import cz.gug.hackathon.glass.gimmeatone.audio.EnvelopedSource;
import cz.gug.hackathon.glass.gimmeatone.audio.WaveGenerator;

public class InstrumentActivity extends Activity {

    private static int[] TONE_FREQUENCIES = new int[] {
        262 /* C */, 277 /* C# */, 294 /* D */, 311 /* D# */, 330 /* E */,
        349 /* F */, 370 /* F# */, 392 /* G */, 416 /* G# */, 440 /* A */,
        466 /* A# */, 494 /* H */
    };

    private AudioPlayer player = new AudioPlayer(false);
    @SuppressLint("UseSparseArrays")
    private Map<Integer, SourceHolder> sources = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onGenericMotionEvent(event); // Route thtough GENERIC MOTION to allow testing
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            startPlaying(event);
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            startPlaying(event);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            stopPlaying(event.getPointerId(event.getActionIndex()));
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            stopPlaying(event.getPointerId(event.getActionIndex()));
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            shiftTone(event);
        }
        return super.onGenericMotionEvent(event);
    }

    /**
     * Shift playing tone's frequency.
     */
    private void shiftTone(MotionEvent event) {
        // Check the history size
        if (event.getHistorySize() == 0) {
            return;
        }
        // Go through the pointers and apply pitch shift
        for (int pointerIdx = 0; pointerIdx < event.getPointerCount(); pointerIdx++) {
            int pointerId = event.getPointerId(pointerIdx);
            // Get the shift from relative movement
            double shift = event.getAxisValue(0, pointerIdx) - event.getHistoricalAxisValue(0, pointerIdx, 0);
            // Shift frequency of the wave generator
            SourceHolder holder = sources.get(pointerId);
            if (holder != null) {
                holder.shift((int) shift);
                holder.effect(getEffectForPointer(event.getAxisValue(1, pointerIdx)));
            }
        }
    }

    /**
     * Start playing a new sound (POINTER_DOWN).
     */
    private void startPlaying(MotionEvent event) {
        int pointerIdx = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIdx);
        // Stop playing sound with the same identifier
        if (sources.containsKey(pointerId)) {
            stopPlaying(pointerId);
        }
        // Start playing new sound
        SourceHolder holder = new SourceHolder(
                getFrequencyForPointer(event.getAxisValue(0, pointerIdx)),
                getEffectForPointer(event.getAxisValue(1, pointerIdx)));
        sources.put(pointerId, holder);
        player.addSource(holder.getSource());
        // Start the player if needed
        if (!player.isPlaying()) {
            player.play();
        }
    }

    /**
     * Convert pointer X-coordinate into a frequency.
     */
    private int getFrequencyForPointer(float coordinate) {
        return TONE_FREQUENCIES[Math.min((int) (coordinate / 65), TONE_FREQUENCIES.length - 1)];
    }

    /**
     * Get effect value for Y-coordinate.
     */
    private double getEffectForPointer(float coordinate) {
        double effect = 1.0 * (coordinate - 80) / 500;
        return effect < 0 ? 0 : (effect > 1 ? 1 : effect);
    }

    /**
     * Stop playing sound with the given identifier.
     */
    private void stopPlaying(int id) {
        SourceHolder holder = sources.remove(id);
        if (holder != null) {
            holder.stop();
        }
    }

    /**
     * Stop the playback completely.
     */
    private void stopPlayback() {
        sources.clear();
        player.stop();
        player.changeSources();
    }

    @Override
    protected void onPause() {
        stopPlayback();
        super.onPause();
    }

    //~ Internal Classes

    private static class SourceHolder {

        private final int frequency;
        private final WaveGenerator wave;
        private final EnvelopedSource<?> enveloped;

        private int shift;

        public SourceHolder(int frequency, double effect) {
            this.frequency = frequency;
            this.wave = new WaveGenerator(frequency);
            this.enveloped = new EnvelopedSource<AudioSource>(wave);
            this.effect(effect);
        }

        public void shift(int amount) {
            if (shift + amount + frequency > 20) { // No need to go deeper than 20
                wave.changeFrequency((shift += amount) + frequency);
            }
        }

        public void effect(double effect) {
            wave.changeEffect(effect);
        }

        public AudioSource getSource() {
            return enveloped;
        }

        public void stop() {
            enveloped.stop();
        }

    }

}
