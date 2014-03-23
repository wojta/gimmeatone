package cz.gug.hackathon.glass.gimmeatone;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;

public class InstrumentActivity extends Activity {

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
            Playback playback = getToneForPointer(event.getAxisValue(0, pointerIdx)).createPlayback();
            playbacks.put(pointerId, playback);
            player.addPlayback(playback);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            player.removePlayback(playbacks.remove(pointerId));
        }
        if (!playbacks.isEmpty() && !player.isPlaying()) {
            player.play();
        } else if (playbacks.isEmpty() && player.isPlaying()) {
            player.stop();
        }
        return super.onGenericMotionEvent(event);
    }

    private Tone getToneForPointer(float coordinate) {
        int position = (int) (coordinate / 300 + 1);
        System.out.println("POZICE: " + position);
        return new Tone(position > 4 ? 880 : 220 * position);
    }

}
