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
				int screenOnColorClock = dataMap.getInt(Commons.PREF_SCREEN_ON_COLOR_CLOCK_KEY);
				int screenOnColorBg = dataMap.getInt(Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_KEY);
				int screenDimmedColorClock = dataMap.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_KEY);
				int screenDimmedColorBg = dataMap.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_KEY);
				// sizes
				String listSize = dataMap.getString(Commons.PREF_SIZE_LIST_KEY);
				String listDimmedSize = dataMap.getString(Commons.PREF_DIMMED_SIZE_LIST_KEY);
				// save into preferences
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
				SharedPreferences.Editor sharedPrefEditor = sharedPref.edit();
				// colors
				sharedPrefEditor.putInt(Commons.PREF_SCREEN_ON_COLOR_CLOCK_KEY, screenOnColorClock);
				sharedPrefEditor.putInt(Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_KEY, screenOnColorBg);
				sharedPrefEditor.putInt(Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_KEY, screenDimmedColorClock);
				sharedPrefEditor.putInt(Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_KEY, screenDimmedColorBg);
				// sizes
				sharedPrefEditor.putString(Commons.PREF_SIZE_LIST_KEY, listSize);
				sharedPrefEditor.putString(Commons.PREF_DIMMED_SIZE_LIST_KEY, listDimmedSize);
				sharedPrefEditor.apply();
			}
		}
	}
}
