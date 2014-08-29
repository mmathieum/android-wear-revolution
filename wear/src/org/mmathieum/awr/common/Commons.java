package org.mmathieum.awr.common;

/**
 * Fake shared lib between mobile & wearable app. #ashamed
 */
public class Commons {

	// COLORs
	public static final String KEY_PREF_SCREEN_ON_COLOR_CLOCK = "pref_key_screen_on_color_clock";
	public static final int KEY_PREF_SCREEN_ON_COLOR_CLOCK_DEFAULT = 0xFFF2F200;

	public static final String KEY_PREF_SCREEN_ON_COLOR_BACKGROUND = "pref_key_screen_on_color_bg";
	public static final int KEY_PREF_SCREEN_ON_COLOR_BACKGROUND_DEFAULT = 0xFF000000;

	public static final String KEY_PREF_SCREEN_DIMMED_COLOR_CLOCK = "pref_key_screen_dimmed_color_clock";
	public static final int KEY_PREF_SCREEN_DIMMED_COLOR_CLOCK_DEFAULT = 0xFFFFFFFF;

	public static final String KEY_PREF_SCREEN_DIMMED_COLOR_BACKGROUND = "pref_key_screen_dimmed_color_bg";
	public static final int KEY_PREF_SCREEN_DIMMED_COLOR_BACKGROUND_DEFAULT = 0xFF000000;

	// SIZEs
	public static final String KEY_PREF_SIZE_LIST = "pref_clock_size_list";
	public static final String KEY_PREF_SIZE_LIST_SMALL = "small";
	public static final String KEY_PREF_SIZE_LIST_MEDIUM = "medium";
	public static final String KEY_PREF_SIZE_LIST_FULL = "full";
	public static final String KEY_PREF_SIZE_LIST_DEFAULT = KEY_PREF_SIZE_LIST_SMALL;


	public static final String KEY_PREF_SIZE_FULL_DIMMED = "pref_dimmed_screen_full";
	public static final boolean KEY_PREF_SIZE_FULL_DIMMED_DEFAULT = false;


	public static final String PREF_DATA_REQUEST_PATH = "/pref";
}
