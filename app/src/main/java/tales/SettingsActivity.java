package tales;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import farytale.fairytale.genius.com.fairytaleclient.R;


public class SettingsActivity extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
