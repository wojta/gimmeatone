package cz.gug.hackathon.glass.gimmeatone;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import android.app.Application;

public class App extends Application {

	static HashMap<String, Tone> tones = new HashMap<String, Tone>();
	static HashMap<Tone, String> tonesRev = new HashMap<Tone, String>();
	static ArrayList<Tone> tonesArray = new ArrayList<Tone>();

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			loadTones();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public HashMap<String, Tone> getTones() {
		return tones;
	}

	public static ArrayList<Tone> getTonesArray() {
		return tonesArray;
	}

	public static HashMap<Tone, String> getTonesRev() {
		return tonesRev;
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
			tonesRev.put(tone, name);
			tonesArray.add(tone);
			System.out.println(name + ":" + freq);

		}
		scanner.close();

	}
}
