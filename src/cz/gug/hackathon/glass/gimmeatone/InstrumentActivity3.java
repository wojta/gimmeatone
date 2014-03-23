package cz.gug.hackathon.glass.gimmeatone;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import cz.gug.hackathon.glass.gimmeatone.adapter.PageAdapter;

public class InstrumentActivity3 extends FragmentActivity {

	private AudioPlayer player = new AudioPlayer();
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Map<Integer, Playback> playbacks = new HashMap<Integer, Playback>();

	private PagerAdapter mPageAdapter;
	private PagerTitleStrip mPagerTileStrip;
	protected float[] matrixR, matrixI, accelVals, magVals;
	protected float[] orientation;
	private Sensor mAccelerometer;
	private Sensor mMagnetic;
	private boolean mSensorChange;
	private double mInitialOrientation;
	private boolean initial = true;
	private double lastOrientation;
	private int lastPos;
	private ViewPager mPager;
	private static final String TAG = InstrumentActivity3.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetic = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

		setContentView(R.layout.activity_instrument);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerTileStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
		mPagerTileStrip.setTextSpacing(20);
		mPageAdapter = new PageAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPageAdapter);
		mPager.setCurrentItem(App.getTonesArray().size() / 2);

		orientation = new float[3];
		matrixR = new float[16];
		matrixI = new float[16];

		magVals = new float[] { 1, 1, 1 };
	}

	@Override
	protected void onStart() {
		super.onStart();
		mSensorManager.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				magVals = event.values.clone();
				// Log.v(TAG,"Mag: "+magVals[0]+","+magVals[1]+","+magVals[2]);
				mSensorChange = true;
				checkOrientation();
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		}, mMagnetic, SensorManager.SENSOR_DELAY_UI);

		mSensorManager.registerListener(new SensorEventListener() {

			@Override
			public void onSensorChanged(SensorEvent event) {
				accelVals = event.values.clone();
				// Log.v(TAG,"Accel: "+accelVals[0]+","+accelVals[1]+","+accelVals[2]);
				mSensorChange = true;
				checkOrientation();
			}

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {

			}
		}, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
	}

	private void checkOrientation() {
		if (accelVals != null && magVals != null && mSensorChange) {

			SensorManager.getRotationMatrix(matrixR, matrixI, accelVals,
					magVals);
			SensorManager.getOrientation(matrixR, orientation);

			if (initial) {
				mInitialOrientation = Math.toDegrees(orientation[0]);
				initial = false;
				lastOrientation = mInitialOrientation;
			}

			double testOrientation = calculateFilteredAngle(
					(float) Math.toDegrees(orientation[0]),
					(float) lastOrientation);

			double newOrientation = Math.toDegrees(orientation[0]);

			double smoothFactorCompass = 0.7;
			double smoothThresholdCompass = 50.0;

			if (Math.abs(newOrientation - lastOrientation) < 180) {
				if (Math.abs(newOrientation - lastOrientation) > smoothThresholdCompass) {
					lastOrientation = newOrientation;
				} else {
					lastOrientation = lastOrientation + smoothFactorCompass
							* (newOrientation - lastOrientation);
				}
			} else {
				if (360.0 - Math.abs(newOrientation - lastOrientation) > smoothThresholdCompass) {
					lastOrientation = newOrientation;
				} else {
					if (lastOrientation > newOrientation) {
						lastOrientation = (lastOrientation
								+ smoothFactorCompass
								* ((360 + newOrientation - lastOrientation) % 360) + 360) % 360;
					} else {
						lastOrientation = (lastOrientation
								- smoothFactorCompass
								* ((360 - newOrientation + lastOrientation) % 360) + 360) % 360;
					}
				}
			}

			int orientation = (int) Math.min(360,
					Math.max(0, 180 + lastOrientation));

			int pos = (int) ((orientation / 360.0f) * App.getTonesArray()
					.size());
			if (pos > App.getTonesArray().size() - 1 || pos < 0)
				return;

			if (lastPos != pos) {
				mPager.setCurrentItem(pos);
			}

			lastPos = pos;
			// Log.v(TAG, "O:" + pos);

			mSensorChange = false;
		}
	}

	// @Override
	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// // TODO Auto-generated method stub
	// return onGenericMotionEvent(ev);
	// }

	@Override
	public boolean onGenericMotionEvent(MotionEvent event) {
		int pointerIdx = event.getActionIndex();
		int pointerId = event.getPointerId(pointerIdx);

		System.out.println(event.getAction() + ", " + event.getPointerCount());
		if (event.getAction() == MotionEvent.ACTION_POINTER_2_DOWN
				&& event.getPointerCount() == 2) {
			System.out.println("down");
			// Playback playback = App.getTonesArray()
			// .get(mPager.getCurrentItem()).createPlayback();
			// playbacks.put(pointerId, playback);
			// player.addPlayback(playback);
			player.changeTone(App.getTonesArray().get(mPager.getCurrentItem()));
			player.play();
		} else if ((event.getAction() == MotionEvent.ACTION_POINTER_2_UP && event
				.getPointerCount() == 2)
				|| event.getAction() == MotionEvent.ACTION_UP) {
			player.stop();
		}
		// if (!playbacks.isEmpty() && !player.isPlaying()) {
		// player.play();
		// } else if (playbacks.isEmpty() && player.isPlaying()) {
		// player.stop();
		// }

		return super.onGenericMotionEvent(event);
	}

	private float restrictAngle(float tmpAngle) {
		while (tmpAngle >= 180)
			tmpAngle -= 360;
		while (tmpAngle < -180)
			tmpAngle += 360;
		return tmpAngle;
	}

	// x is a raw angle value from getOrientation(...)
	// y is the current filtered angle value
	private float calculateFilteredAngle(float x, float y) {
		final float alpha = 0.1f;
		float diff = x - y;

		// here, we ensure that abs(diff)<=180
		diff = restrictAngle(diff);

		y += alpha * diff;
		// ensure that y stays within [-180, 180[ bounds
		y = restrictAngle(y);

		return y;
	}

}
