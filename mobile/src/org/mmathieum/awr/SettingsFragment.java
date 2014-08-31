package org.mmathieum.awr;

import java.lang.ref.WeakReference;

import org.mmathieum.awr.common.Commons;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, DataApi.DataListener {

	private static final String TAG = SettingsFragment.class.getSimpleName();

	private static final int REQUEST_RESOLVE_ERROR = 1001;
	private static final String DIALOG_ERROR = "dialog_error";
	private static final String STATE_RESOLVING_ERROR = "resolving_error";
	private boolean resolvingError = false;

	private GoogleApiClient googleApiClient;
	private int screenOnColorClock;
	private int screenOnColorBg;
	private int screenDimmedColorClock;
	private int screenDimmedColorBg;
	private String listSize;
	private int customSize;
	private String listDimmedSize;
	private String animations;

	public SettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.resolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);
		addPreferencesFromResource(R.xml.preferences);
		readPreferences();
		setPreferenceSummaries();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_RESOLVE_ERROR) {
			resolvingError = false;
			if (resultCode == Activity.RESULT_OK) {
				connectToGoogleAPI(getActivity());
			}
		}
	}

	@Override
	public void onAttach(Activity activity) {
		MLog.v(TAG, "onAttach()");
		super.onAttach(activity);
		connectToGoogleAPI(activity);
	}


	/**
	 * If not already connected to Google API, tries to connect to Google API.
	 */
	private void connectToGoogleAPI(Activity activity) {
		if (this.googleApiClient == null && activity != null) {
			this.googleApiClient = new GoogleApiClient.Builder(activity).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(Wearable.API)
					.build();
		}
		if (this.googleApiClient != null && !this.googleApiClient.isConnecting() && !this.googleApiClient.isConnected()) {
			this.googleApiClient.connect(); // trigger asynchronous connection
		}
	}

	@Override
	public void onConnected(Bundle bundle) {
		Wearable.DataApi.addListener(this.googleApiClient, this);
	}

	@Override
	public void onStart() {
		super.onStart();
		if (!resolvingError) {
			if (this.googleApiClient != null) {
				connectToGoogleAPI(getActivity());
			}
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		if (this.googleApiClient != null && this.googleApiClient.isConnected()) {
			Wearable.DataApi.removeListener(this.googleApiClient, this);
			this.googleApiClient.disconnect();
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		connectToGoogleAPI(getActivity());
		if (this.googleApiClient == null) {
			Toast.makeText(getActivity(), "No connection!", Toast.LENGTH_SHORT).show();
			return;
		}
		readPreferences();
		setPreferenceSummaries();
		PutDataMapRequest dataMap = getDataMap();
		PendingResult<DataApi.DataItemResult> pendingResult = Wearable.DataApi.putDataItem(this.googleApiClient, dataMap.asPutDataRequest());
		pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
			@Override
			public void onResult(DataApi.DataItemResult dataItemResult) {
				if (dataItemResult.getStatus().isSuccess()) {
					Toast.makeText(getActivity(), "New Settings applied.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), "New Settings NOT applied!", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private PutDataMapRequest getDataMap() {
		PutDataMapRequest dataMap = PutDataMapRequest.create(Commons.PREF_DATA_REQUEST_PATH);
		// colors
		dataMap.getDataMap().putInt(Commons.PREF_SCREEN_ON_COLOR_CLOCK_KEY, screenOnColorClock);
		dataMap.getDataMap().putInt(Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_KEY, screenOnColorBg);
		dataMap.getDataMap().putInt(Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_KEY, screenDimmedColorClock);
		dataMap.getDataMap().putInt(Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_KEY, screenDimmedColorBg);
		// sizes
		dataMap.getDataMap().putString(Commons.PREF_SIZE_LIST_KEY, listSize);
		dataMap.getDataMap().putString(Commons.PREF_DIMMED_SIZE_LIST_KEY, listDimmedSize);
		return dataMap;
	}

	private void setPreferenceSummaries() {
		// size
		ListPreference prefSizeList = (ListPreference) findPreference(Commons.PREF_SIZE_LIST_KEY);
		int prefSizeListIndex = prefSizeList.findIndexOfValue(listSize);
		if (prefSizeListIndex >= 0) {
			prefSizeList.setSummary(prefSizeList.getEntries()[prefSizeListIndex]);
		}
		ListPreference prefDimmedSizeList = (ListPreference) findPreference(Commons.PREF_DIMMED_SIZE_LIST_KEY);
		int prefDimmedSizeListIndex = prefDimmedSizeList.findIndexOfValue(listDimmedSize);
		if (prefDimmedSizeListIndex >= 0) {
			prefDimmedSizeList.setSummary(prefDimmedSizeList.getEntries()[prefDimmedSizeListIndex]);
		}
	}

	private void readPreferences() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		// colors
		this.screenOnColorClock = sharedPref.getInt(Commons.PREF_SCREEN_ON_COLOR_CLOCK_KEY, Commons.PREF_SCREEN_ON_COLOR_CLOCK_DEFAULT);
		this.screenOnColorBg = sharedPref.getInt(Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_KEY, Commons.PREF_SCREEN_ON_COLOR_BACKGROUND_DEFAULT);
		this.screenDimmedColorClock = sharedPref.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_KEY, Commons.PREF_SCREEN_DIMMED_COLOR_CLOCK_DEFAULT);
		this.screenDimmedColorBg = sharedPref.getInt(Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_KEY, Commons.PREF_SCREEN_DIMMED_COLOR_BACKGROUND_DEFAULT);
		// sizes
		this.listSize = sharedPref.getString(Commons.PREF_SIZE_LIST_KEY, Commons.PREF_SIZE_LIST_DEFAULT);
		this.listDimmedSize = sharedPref.getString(Commons.PREF_DIMMED_SIZE_LIST_KEY, Commons.PREF_DIMMED_SIZE_LIST_DEFAULT);
	}

	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		for (DataEvent dataEvent : dataEvents) {
			String path = dataEvent.getDataItem().getUri().getPath();
			if (path.equals(Commons.PREF_DATA_REQUEST_PATH)) {
				if (dataEvent.getType() == DataEvent.TYPE_DELETED) {
				} else if (dataEvent.getType() == DataEvent.TYPE_CHANGED) {
				}
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onConnectionSuspended(int i) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (resolvingError) {
			return;
		}
		Activity activity = getActivity();
		if (activity != null && result.hasResolution()) {
			try {
				resolvingError = true;
				result.startResolutionForResult(activity, REQUEST_RESOLVE_ERROR);
			} catch (IntentSender.SendIntentException e) {
				this.googleApiClient.connect();
			}
		} else {
			showErrorDialog(activity, this, result.getErrorCode());
			resolvingError = true;
		}
	}

	private static void showErrorDialog(Activity activity, SettingsFragment settingsFragment, int errorCode) {
		ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
		dialogFragment.setSettingsFragment(settingsFragment);
		Bundle args = new Bundle();
		args.putInt(DIALOG_ERROR, errorCode);
		dialogFragment.setArguments(args);
		dialogFragment.show(activity.getFragmentManager(), "errordialog");
	}

	public void onDialogDismissed() {
		resolvingError = false;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(STATE_RESOLVING_ERROR, resolvingError);
	}

	public static class ErrorDialogFragment extends DialogFragment {

		private static final String TAG = ErrorDialogFragment.class.getSimpleName();

		private WeakReference<SettingsFragment> settingsFragmentWR;

		public ErrorDialogFragment() {
		}

		public void setSettingsFragment(SettingsFragment settingsFragment) {
			this.settingsFragmentWR = new WeakReference<SettingsFragment>(settingsFragment);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			int errorCode = this.getArguments().getInt(DIALOG_ERROR);
			return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
		}

		@Override
		public void onDismiss(DialogInterface dialog) {
			SettingsFragment settingsFragment = this.settingsFragmentWR == null ? null : this.settingsFragmentWR.get();
			if (settingsFragment != null) {
				settingsFragment.onDialogDismissed();
			}
		}
	}
}
