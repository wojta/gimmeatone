package cz.gug.hackathon.glass.gimmeatone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cz.gug.hackathon.glass.gimmeatone.audio.AudioPlayer;
import cz.gug.hackathon.glass.gimmeatone.audio.AudioSource;
import cz.gug.hackathon.glass.gimmeatone.audio.EnvelopedSource;
import cz.gug.hackathon.glass.gimmeatone.audio.ToneGenerator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class InstrumentActivity extends Activity {

    private static List<ToneGenerator> tones = new ArrayList<ToneGenerator>();

    static {
        tones.add(new ToneGenerator(262)); // C
        tones.add(new ToneGenerator(277)); // C#
        tones.add(new ToneGenerator(294)); // D
        tones.add(new ToneGenerator(311)); // D#
        tones.add(new ToneGenerator(330)); // E
        tones.add(new ToneGenerator(349)); // F
        tones.add(new ToneGenerator(370)); // F#
        tones.add(new ToneGenerator(392)); // G
        tones.add(new ToneGenerator(416)); // G#
        tones.add(new ToneGenerator(440)); // A
        tones.add(new ToneGenerator(466)); // A#
        tones.add(new ToneGenerator(494)); // H
    }

    private AudioPlayer player = new AudioPlayer(false);
    @SuppressLint("UseSparseArrays")
    private Map<Integer, EnvelopedSource> sources = new HashMap<Integer, EnvelopedSource>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onGenericMotionEvent(event); // Route TOUCH to GENERIC MOTION to allow testing on touch devices
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        // Get pointer index and identifier
        int pointerIdx = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIdx);
        // Process motion event
        if (event.getAction() == MotionEvent.ACTION_DOWN ||
                event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
            startPlaying(pointerId, getToneForPointer(event.getAxisValue(0, pointerIdx)));
        } else if (event.getAction() == MotionEvent.ACTION_UP ||
                event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            stopPlaying(pointerId);
        }
        // Start the player if needed
        if (!sources.isEmpty() && !player.isPlaying()) {
            player.play();
        }
        return super.onGenericMotionEvent(event);
    }

    /**
     * Convert pointer X-coordinate into a tone instance.
     */
    private ToneGenerator getToneForPointer(float coordinate) {
        int position = Math.min((int) (coordinate / 120 + 1), tones.size() - 1);
        System.out.println("TONE: " + position);
        return tones.get(position);
    }

    private void startPlaying(int id, AudioSource source) {
        if (sources.containsKey(id)) {
            stopPlaying(id);
        }
        EnvelopedSource envelopedSource = new EnvelopedSource(source);
        sources.put(id, envelopedSource);
        player.addSource(envelopedSource);
    }

    private void stopPlaying(int id) {
        EnvelopedSource source = sources.remove(id);
        if (source != null) {
            source.stop();
        }
    }

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

}
