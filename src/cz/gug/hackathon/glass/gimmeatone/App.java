package cz.gug.hackathon.glass.gimmeatone;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import cz.gug.hackathon.glass.gimmeatone.audio.ToneGenerator;
import android.app.Application;

public class App extends Application {

	static HashMap<String, ToneGenerator> tones = new HashMap<String, ToneGenerator>();
	static HashMap<ToneGenerator, String> tonesRev = new HashMap<ToneGenerator, String>();
	static ArrayList<ToneGenerator> tonesArray = new ArrayList<ToneGenerator>();
	static ArrayList<String> toneColors = new ArrayList<String>();

	@Override
	public void onCreate() {
		super.onCreate();
		try {
			loadTones();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static public HashMap<String, ToneGenerator> getTones() {
		return tones;
	}

	public static ArrayList<ToneGenerator> getTonesArray() {
		return tonesArray;
	}

	public static HashMap<ToneGenerator, String> getTonesRev() {
		return tonesRev;
	}

	public static ArrayList<String> getToneColors() {
		return toneColors;
	}

	private void loadTones() throws IOException {
		InputStream is = getAssets().open("frekvence.csv");
		Scanner scanner = new Scanner(is);
		scanner.useDelimiter(";");
		while (scanner.hasNext()) {
			String name = scanner.next().replace("\"", "").trim();
			String freq = scanner.next().replace("\"", "").trim();
			String color = scanner.next().replace("\"", "").trim();
			name = scanner.next().replace("\"", "").trim();
			ToneGenerator tone = new ToneGenerator(Integer.valueOf(freq));
			tones.put(name, tone);
			tonesRev.put(tone, name);
			tonesArray.add(tone);
			toneColors.add(color);
			System.out.println(name + ":" + freq);

		}
		scanner.close();

	}
}
