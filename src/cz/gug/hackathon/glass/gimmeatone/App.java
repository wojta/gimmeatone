package cz.gug.hackathon.glass.gimmeatone;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;

import android.app.Application;

public class App extends Application {

	HashMap<String, Tone> tones = new HashMap<String, Tone>();

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			loadTones();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public HashMap<String, Tone> getTones() {
		return tones;
	}

	private void loadTones() throws IOException {
		InputStream is = getAssets().open("frekvence.csv");
		Scanner scanner = new Scanner(is);
		scanner.useDelimiter(";");
		while (scanner.hasNext()) {
			String name = scanner.next().replace("\"", "").trim();
			String freq = scanner.next().replace("\"", "").trim();
			Tone tone = new Tone(Integer.valueOf(freq));
			tones.put(name, tone);
			System.out.println(name + ":" + freq);

		}
		scanner.close();

	}
}
