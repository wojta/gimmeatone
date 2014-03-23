package cz.gug.hackathon.glass.gimmeatone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class InstrumentActivity extends Activity {

    private static List<Tone> tones = new ArrayList<Tone>();

    static {
        tones.add(new Tone(262)); // C
        tones.add(new Tone(277)); // C#
        tones.add(new Tone(294)); // D
        tones.add(new Tone(311)); // D#
        tones.add(new Tone(330)); // E
        tones.add(new Tone(349)); // F
        tones.add(new Tone(370)); // F#
        tones.add(new Tone(392)); // G
        tones.add(new Tone(416)); // G#
        tones.add(new Tone(440)); // A
        tones.add(new Tone(466)); // A#
        tones.add(new Tone(494)); // H
    }

    private AudioPlayer player = new AudioPlayer();
    private Map<Integer, Playback> playbacks = new HashMap<Integer, Playback>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        int pointerIdx = event.getActionIndex();
        int pointerId = event.getPointerId(pointerIdx);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            playTone(pointerId, getToneForPointer(event.getAxisValue(0, pointerIdx)));
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_DOWN) {
            playTone(pointerId, getToneForPointer(event.getAxisValue(0, pointerIdx)));
        } else if (event.getAction() == MotionEvent.ACTION_POINTER_UP) {
            stopPlaying(pointerId);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            stopPlayback();
        }
        if (!playbacks.isEmpty() && !player.isPlaying()) {
            player.play();
        } else if (playbacks.isEmpty() && player.isPlaying()) {
            player.stop();
        }
        return super.onGenericMotionEvent(event);
    }

    private Tone getToneForPointer(float coordinate) {
        int position = Math.min((int) (coordinate / 120 + 1), tones.size() - 1);
        System.out.println("TONE: " + position);
        return tones.get(position);
    }

    private void playTone(int id, Tone tone) {
        if (playbacks.containsKey(id)) {
            stopPlaying(id);
        }
        playbacks.put(id, tone.createPlayback());
        player.addPlayback(playbacks.get(id));
        logStats();
    }

    private void stopPlaying(int id) {
        Playback playback = playbacks.get(id);
        if (playback != null) {
            player.removePlayback(playback);
        }
        logStats();
    }

    private void stopPlayback() {
        for (Playback playback : playbacks.values()) {
            player.removePlayback(playback);
        }
        playbacks.clear();
        player.stop();
    }

    @Override
    protected void onPause() {
        stopPlayback();
        super.onPause();
    }

    private void logStats() {
        System.out.println("SAMPLES: " + playbacks.size());
    }

}
