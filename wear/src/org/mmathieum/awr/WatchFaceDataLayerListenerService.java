package org.mmathieum.awr;

import java.util.List;

import org.mmathieum.awr.common.Commons;

import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchFaceDataLayerListenerService extends WearableListenerService {

	private static final String TAG = WatchFaceDataLayerListenerService.class.getSimpleName();

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		super.onDataChanged(dataEvents);
		final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
		for (DataEvent event : events) {
			final Uri uri = event.getDataItem().getUri();
			final String path = uri != null ? uri.getPath() : null;
			if (Commons.PREF_DATA_REQUEST_PATH.equals(path)) {
				// read your values from map:
				final DataMap dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
				// colors
				int screenOnColorClock = dataMap.getInt(Commons.KEY_PREF_SCREEN_ON_COLOR_CLOCK);
				int screenOnColorBg = dataMap.getInt(Commons.KEY_PREF_SCREEN_ON_COLOR_BACKGROUND);
				int screenDimmedColorClock = dataMap.getInt(Commons.KEY_PREF_SCREEN_DIMMED_COLOR_CLOCK);
				int screenDimmedColorBg = dataMap.getInt(Commons.KEY_PREF_SCREEN_DIMMED_COLOR_BACKGROUND);
				// sizes
				String listSize = dataMap.getString(Commons.KEY_PREF_SIZE_LIST);
				boolean dimmedFullScreen = dataMap.getBoolean(Commons.KEY_PREF_SIZE_FULL_DIMMED);
				// save into preferences
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
				// colors
				sharedPrefEditor.putInt(Commons.KEY_PREF_SCREEN_ON_COLOR_CLOCK, screenOnColorClock);
				sharedPrefEditor.putInt(Commons.KEY_PREF_SCREEN_ON_COLOR_BACKGROUND, screenOnColorBg);
				sharedPrefEditor.putInt(Commons.KEY_PREF_SCREEN_DIMMED_COLOR_CLOCK, screenDimmedColorClock);
				sharedPrefEditor.putInt(Commons.KEY_PREF_SCREEN_DIMMED_COLOR_BACKGROUND, screenDimmedColorBg);
				// sizes
				sharedPrefEditor.putString(Commons.KEY_PREF_SIZE_LIST, listSize);
				sharedPrefEditor.putBoolean(Commons.KEY_PREF_SIZE_FULL_DIMMED, dimmedFullScreen);
				sharedPrefEditor.apply();
			}
		}
	}
}
