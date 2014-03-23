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
	private byte[] buffer = new byte[BUFFER_SIZE];
	private AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC,
			SAMPLE_RATE, OUTPUT_CHANNELS, AUDIO_FORMAT, BUFFER_SIZE,
			AudioTrack.MODE_STREAM);

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
		// track.stop();
		track.pause();
		track.flush();
	}

	public void changeTone(Tone tone) {
		this.playback = tone.createPlayback();
	}

	private class PlayerWorker implements Runnable {
		@Override
		public void run() {
			while (playing) {
				Playback playback = AudioPlayer.this.playback;
				if (playback == null) {
					Arrays.fill(buffer, (byte) 0);
				} else {
					playback.fillBuffer(buffer);
				}
				track.write(buffer, 0, buffer.length);
			}
		}
	}

	public boolean isPlaying() {
		return playing;
	}

}
