package cz.gug.hackathon.glass.gimmeatone.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import cz.gug.hackathon.glass.gimmeatone.App;
import cz.gug.hackathon.glass.gimmeatone.audio.ToneGenerator;
import cz.gug.hackathon.glass.gimmeatone.fragment.ToneFragment;

public class PageAdapter extends FragmentPagerAdapter {

	public PageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		ToneGenerator tone = App.getTonesArray().get(arg0);
		Fragment frag = new ToneFragment();
		Bundle bundle = new Bundle();
		bundle.putString(ToneFragment.EXTRA_TONE, App.getTonesRev().get(tone));
		bundle.putString(ToneFragment.EXTRA_COLOR, App.getToneColors()
				.get(arg0));
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	public int getCount() {
		return App.getTonesArray().size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return App.getTonesRev().get(App.getTonesArray().get(position));
	}

}
