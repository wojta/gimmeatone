package cz.gug.hackathon.glass.gimmeatone;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	AudioPlayer player;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		player = new AudioPlayer();
		if (intent.getExtras() != null) {
			ArrayList<String> voiceResults = getIntent().getExtras()
					.getStringArrayList(RecognizerIntent.EXTRA_PARTIAL_RESULTS);
			System.out.println(voiceResults);
			TextView tvTest = (TextView) findViewById(R.id.tvTest);
			if (voiceResults != null && !voiceResults.isEmpty())
				tvTest.setText(voiceResults.get(0));
		} else {
			startActivity(new Intent(this, InstrumentActivity2.class));
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
			System.out.println("keydown center");
			if (!player.isPlaying()) {
				player.changeTone(new Tone(440));
				player.play();
			} else
				player.stop();

			return true;
		} else
			return super.onKeyDown(keyCode, event);
	}

	//
	// @Override
	// public boolean onKeyUp(int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
	// System.out.println("keyup center");
	// player.stop();
	// return true;
	// } else
	// return super.onKeyUp(keyCode, event);
	// }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
