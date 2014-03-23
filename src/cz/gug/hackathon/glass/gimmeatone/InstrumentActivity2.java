package cz.gug.hackathon.glass.gimmeatone;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import cz.gug.hackathon.glass.gimmeatone.adapter.PageAdapter;

public class InstrumentActivity2 extends FragmentActivity {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private PagerAdapter mPageAdapter;
	private PagerTitleStrip mPagerTileStrip;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

		setContentView(R.layout.activity_instrument);

		ViewPager mPager = (ViewPager) findViewById(R.id.pager);
		mPagerTileStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		mPagerTileStrip.setTextSpacing(20);
		mPageAdapter = new PageAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPageAdapter);
		mPager.setCurrentItem(App.getTonesArray().size() / 2);

	}

	@Override
	protected void onStart() {
		super.onStart();

	}
}
