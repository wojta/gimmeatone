package cz.gug.hackathon.glass.gimmeatone.fragment;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cz.gug.hackathon.glass.gimmeatone.R;

public class ToneFragment extends Fragment {

	public static final String EXTRA_TONE = "EXTRA_TONE";
	public static final String EXTRA_COLOR = "EXTRA_COLOR";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		String tone = getArguments().getString(EXTRA_TONE);
		View v = inflater.inflate(R.layout.fragment_tone, null);
		TextView tvText = (TextView) v.findViewById(R.id.tvTone);
		tvText.setText(tone);

		GradientDrawable drawable = (GradientDrawable) tvText.getBackground();
		drawable.setColor(Color.parseColor(getArguments()
				.getString(EXTRA_COLOR)));
		return v;
	}
}
