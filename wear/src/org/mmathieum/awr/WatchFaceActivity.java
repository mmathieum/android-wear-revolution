package org.mmathieum.awr;

import java.util.Calendar;

import org.mmathieum.awr.common.Commons;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.view.WatchViewStub;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class WatchFaceActivity extends Activity implements SurfaceHolder.Callback, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String TAG = WatchFaceActivity.class.getSimpleName();

	private final static IntentFilter intentFilter;
	static {
		intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_TIME_TICK);
		intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
		// TODO use com.google.android.wearable.home.action.WEARABLE_TIME_TICK?
	}

	private static final int SMALL_NOTIFICATION_HEIGHT_IN_PX = 60;
	private static final int LARGE_NOTIFICATION_HEIGHT_IN_PX = 146; // 144; //100; //144; // 166

	private final BroadcastReceiver mIntentTimeTickReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateUI(false);
		}
	};

	private WatchViewStub mStub;
	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private Canvas mDrawCanvas;
	private boolean screenDimmed = false;

	private int screenOnColorClock;
	private int screenOnColorBg;
	private int screenDimmedColorClock;
	private int screenDimmedColorBg;
	private String clockSizeList;
	private int clockSize = -1;
	private String dimmedClockSizeList;
	private int dimmedClockSize = -1;
	private String animationList;
	private boolean screenOnAnimations;
	private boolean screenDimmedAnimations;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_watchface);
		loadSharedPreferences(PreferenceManager.getDefaultSharedPreferences(this));
		this.mStub = (WatchViewStub) findViewById(R.id.watch_view_stub);
		this.mStub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
			@Override
			public void onLayoutInflated(WatchViewStub stub) {
				mSurfaceView = (SurfaceView) stub.findViewById(R.id.surfaceView);
				mSurfaceView.setZOrderOnTop(true);
				mSurfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);
				mSurfaceView.getHolder().addCallback(WatchFaceActivity.this);
				createLayout();
			}
		});

		mIntentTimeTickReceiver.onReceive(this, registerReceiver(null, intentFilter));
		registerReceiver(mIntentTimeTickReceiver, intentFilter);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs, String key) {
		loadSharedPreferences(sharedPrefs);
		updateUI(true); // TODO only if changed?
	}

	private void loadSharedPreferences(SharedPreferences sharedPrefs) {
		// colors
		this.screenOnColorClock = sharedPrefs.getInt(Commons.PREF_SCREEN_ON_COLOR_CLOCK_KEY, Commons.PREF_SCREEN_ON_COLOR_CLOCK_DEFAULT);
		this.screenOnColorBg = sharedPrefs.getInt(Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_KEY, Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_DEFAULT);
		this.screenDimmedColorClock = sharedPrefs.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_KEY, Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_DEFAULT);
		this.screenDimmedColorBg = sharedPrefs.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_KEY, Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_DEFAULT);
		// sizes
		this.clockSizeList = sharedPrefs.getString(Commons.PREF_SIZE_LIST_KEY, Commons.PREF_SIZE_LIST_DEFAULT);
		generateClockSize();
		this.dimmedClockSizeList = sharedPrefs.getString(Commons.PREF_DIMMED_SIZE_LIST_KEY, Commons.PREF_DIMMED_SIZE_LIST_DEFAULT);
		generateDimmedClockSize();
	}


	private void generateClockSize() {
		if (Commons.PREF_SIZE_LIST_SMALL.equals(this.clockSizeList)) {
			if (this.mStub == null) {
				this.clockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.clockSize = size - LARGE_NOTIFICATION_HEIGHT_IN_PX;
		} else if (Commons.PREF_SIZE_LIST_MEDIUM.equals(this.clockSizeList)) {
			if (this.mStub == null) {
				this.clockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.clockSize = size - SMALL_NOTIFICATION_HEIGHT_IN_PX;
		} else if (Commons.PREF_SIZE_LIST_FULL.equals(this.clockSizeList)) {
			if (this.mStub == null) {
				this.clockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.clockSize = size;
		}
	}

	private void generateDimmedClockSize() {
		if (Commons.PREF_DIMMED_SIZE_LIST_SAME.equals(this.dimmedClockSizeList)) {
			this.dimmedClockSize = this.clockSize;
		} else if (Commons.PREF_DIMMED_SIZE_LIST_SMALL.equals(this.dimmedClockSizeList)) {
			if (this.mStub == null) {
				this.dimmedClockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.dimmedClockSize = size - LARGE_NOTIFICATION_HEIGHT_IN_PX;
		} else if (Commons.PREF_DIMMED_SIZE_LIST_MEDIUM.equals(this.dimmedClockSizeList)) {
			if (this.mStub == null) {
				this.dimmedClockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.dimmedClockSize = size - SMALL_NOTIFICATION_HEIGHT_IN_PX;
		} else if (Commons.PREF_DIMMED_SIZE_LIST_FULL.equals(this.dimmedClockSizeList)) {
			if (this.mStub == null) {
				this.dimmedClockSize = -1; // force generate
				return;
			}
			int size = Math.min(this.mStub.getWidth(), this.mStub.getHeight());
			this.dimmedClockSize = size;
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mSurfaceHolder = holder;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mSurfaceHolder = holder;
		updateUI(false); // only update if time different
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mDrawCanvas = null;
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.screenDimmed = true;
		updateUI(true); // force to change color
		PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		this.screenDimmed = false;
		updateUI(true); // force to change color
		PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(this.mIntentTimeTickReceiver);
	}

	private void createLayout() {
		updateUI(true); // force 1st display
	}

	private int oldMinute = -1;

	private void updateUI(boolean force) {
		Calendar now = Calendar.getInstance();
		if (this.mSurfaceView == null) {
			return;
		}
		if (this.mSurfaceHolder == null) {
			return;
		}
		// read minute
		int minute = now.get(Calendar.MINUTE);
		if (!force && minute == oldMinute) {
			return; // same time, nothing to display
		}
		// read hour
		int hour;
		if (DateFormat.is24HourFormat(this)) {
			hour = now.get(Calendar.HOUR_OF_DAY);
		} else {
			hour = now.get(Calendar.HOUR);
			if (hour == 0) {
				hour = 12;
			}
		}
		// extract time digits
		int[] timeDigits = new int[4];
		timeDigits[0] = hour / 10;
		timeDigits[1] = hour % 10;
		timeDigits[2] = minute / 10;
		timeDigits[3] = minute % 10;
		// draw time
		drawTime(timeDigits);
		// save current minute to not redraw same hh:mm
		oldMinute = minute;
		Log.d(TAG, "this.oldMinute: " + this.oldMinute);
	}

	private/* static final */int paddingInPx = 4; // 4; //2; // 8;// 4; //2; // 1;
	private static final int marginInPx = 0; // 2;

	private static final float NUMBER_SIZE_IN_STROKE = 5f;
	private static final float PADDING_SIZE_IN_STROKE = 0.33f;
	private static final float CLOCK_SIZE_IN_STROKE = NUMBER_SIZE_IN_STROKE + PADDING_SIZE_IN_STROKE + PADDING_SIZE_IN_STROKE + NUMBER_SIZE_IN_STROKE;
	private static final float PADDING_SIZE_IN_PERCENT = PADDING_SIZE_IN_STROKE / CLOCK_SIZE_IN_STROKE;
	private static final float NUMBER_SIZE_IN_PERCENT = NUMBER_SIZE_IN_STROKE / CLOCK_SIZE_IN_STROKE;

	private void drawTime(int[] timeDigits) {
		if (this.clockSize < 0) {
			generateClockSize();
		}
		if (this.dimmedClockSize < 0) {
			generateDimmedClockSize();
		}
		int size = this.screenDimmed ? this.dimmedClockSize : this.clockSize;
		// this.paddingInPx = size - (2 * Math.round(size * NUMBER_SIZE_IN_PERCENT));
		this.paddingInPx = (int) (size * PADDING_SIZE_IN_PERCENT); // ((size / (NUMBER_SIZE_IN_STROKE + PADDING_SIZE_IN_STROKE + NUMBER_SIZE_IN_STROKE)) *
		int timeLeftStart = 0;
		int timeTopStart = 0;
		// center the squared clock horizontally
		int bottomMargin = this.mStub.getHeight() - size;
		timeLeftStart += /* bottomMargin == 0 ? 0 : */bottomMargin / 2; // LARGE_NOTIFICATION_HEIGHT_IN_PX / 2;
		int middleSize = size / 2;
		int numberSize = middleSize - (marginInPx + paddingInPx);
		float numberStokeSize = numberSize / NUMBER_SIZE_IN_STROKE;
		mDrawCanvas = mSurfaceHolder == null ? null : mSurfaceHolder.lockCanvas();
		if (mDrawCanvas == null) {
			return;
		}
		// set background color
		mDrawCanvas.drawColor(this.screenDimmed ? this.screenDimmedColorBg : this.screenOnColorBg);// Color.BLACK); // not really useful?
		int startLeft;
		int startTop;
		// set clock color
		Paint myPaint = new Paint();
		myPaint.setColor(this.screenDimmed ? this.screenDimmedColorClock : this.screenOnColorClock);
		// Hh:mm
		startLeft = timeLeftStart + 0 + marginInPx;
		startTop = timeTopStart + 0 + marginInPx;
		drawTimeDigit(timeDigits[0], numberStokeSize, numberStokeSize, startLeft, startTop, myPaint);
		// hH:mm
		startLeft = timeLeftStart + middleSize + paddingInPx;
		startTop = timeTopStart + 0 + marginInPx;
		drawTimeDigit(timeDigits[1], numberStokeSize, numberStokeSize, startLeft, startTop, myPaint);
		// hh:Mm
		startLeft = timeLeftStart + 0 + marginInPx;
		startTop = timeTopStart + middleSize + paddingInPx;
		drawTimeDigit(timeDigits[2], numberStokeSize, numberStokeSize, startLeft, startTop, myPaint);
		// hhLmM
		startLeft = timeLeftStart + middleSize + paddingInPx;
		startTop = timeTopStart + middleSize + paddingInPx;
		drawTimeDigit(timeDigits[3], numberStokeSize, numberStokeSize, startLeft, startTop, myPaint);
		mSurfaceHolder.unlockCanvasAndPost(mDrawCanvas);
	}

	private void drawTimeDigit(int timeDigit, float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		switch (timeDigit) {
		case 0:
			drawZero(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 1:
			drawOne(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 2:
			drawTwo(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 3:
			drawThree(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 4:
			drawFour(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 5:
			drawFive(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 6:
			drawSix(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 7:
			drawSeven(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 8:
			drawEight(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		case 9:
			drawNine(numberStokeWidth, numberStokeHeight, startLeft, startTop, myPaint);
			break;
		default:
			Log.w(TAG, "Unexpected digit " + timeDigit + " !");
		}
	}

	private void drawZero(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
	}

	private void drawNine(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
	}

	private void drawEight(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
	}

	private void drawSeven(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
	}

	private void drawSix(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 2, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
	}

	private void drawFive(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 2, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
	}

	private void drawFour(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 1, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
	}

	private void drawThree(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, /* 0 *//* 1 */2, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
	}

	private void drawTwo(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 0, 5, 1, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 5, 3, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 2, 1, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 0, 4, 5, 5, myPaint);
	}

	private void drawOne(float numberStokeWidth, float numberStokeHeight, int startLeft, int startTop, Paint myPaint) {
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 4, 0, 5, 5, myPaint);
		drawRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, 3, 0, 5, 1, myPaint);
	}

	private void drawRect(int startLeft, int startTop, float numberStokeWidth, float numberStokeHeight, int left, int top, int right, int bottom, Paint myPaint) {
		mDrawCanvas.drawRect(getNewRect(startLeft, startTop, numberStokeWidth, numberStokeHeight, left, top, right, bottom), myPaint);
		// float radius = 4f; // 6f;
		// mDrawCanvas.drawRoundRect(getNewRectF(startLeft, startTop, numberStokeWidth, numberStokeHeight, left, top, right, bottom), radius, radius, myPaint);
	}

	public static Rect getNewRect(int startLeft, int startTop, float numberStokeWidth, float numberStokeHeight, int left, int top, int right, int bottom) {
		return new Rect( //
				(int) (startLeft + /* paddingInPx + */(left * numberStokeWidth)), // left
				(int) (startTop + /* paddingInPx + */(top * numberStokeHeight)), // top
				(int) (startLeft + /* paddingInPx + */(right * numberStokeWidth)), // right
				(int) (startTop + /* paddingInPx + */(bottom * numberStokeHeight)) // bottom
		);
	}

	public static RectF getNewRectF(int startLeft, int startTop, float numberStokeWidth, float numberStokeHeight, int left, int top, int right, int bottom) {
		return new RectF( //
				startLeft + /* paddingInPx + */(left * numberStokeWidth), // left
				startTop + /* paddingInPx + */(top * numberStokeHeight), // top
				startLeft + /* paddingInPx + */(right * numberStokeWidth), // right
				startTop + /* paddingInPx + */(bottom * numberStokeHeight) // bottom
		);
	}

}
