package cz.gug.hackathon.glass.gimmeatone;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import cz.gug.hackathon.glass.gimmeatone.adapter.PageAdapter;
import cz.gug.hackathon.glass.gimmeatone.audio.AudioPlayer;
import cz.gug.hackathon.glass.gimmeatone.audio.EnvelopedSource;
import cz.gug.hackathon.glass.gimmeatone.audio.ToneGenerator;

public class InstrumentActivity2 extends FragmentActivity {

    private AudioPlayer player = new AudioPlayer(true);
    private EnvelopedSource source;

    private PagerAdapter mPageAdapter;
    private PagerTitleStrip mPagerTileStrip;
    private ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instrument);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerTileStrip = (PagerTitleStrip) findViewById(R.id.pager_title_strip);
        mPagerTileStrip.setTextSpacing(20);
        mPageAdapter = new PageAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPageAdapter);
        mPager.setCurrentItem(App.getTonesArray().size() / 2);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        onGenericMotionEvent(event); // Route via GENERIC MOTION to allow testing
        return super.dispatchTouchEvent(event);
    };

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN && event.getPointerCount() == 2) {
            System.out.println("PLAYING TONE");
            pushTone(App.getTonesArray().get(mPager.getCurrentItem()));
        } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP && event.getPointerCount() <= 2) {
            releaseTone();
        }
        return super.onGenericMotionEvent(event);
    }

    private void pushTone(ToneGenerator tone) {
        releaseTone();
        source = new EnvelopedSource(tone);
        player.addSource(source);
        player.play();
    }

    private void releaseTone() {
        if (source != null) {
            source.stop();
        }
        source = null;
    }

}
