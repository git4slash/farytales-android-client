package tales;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import farytale.fairytale.genius.com.fairytaleclient.R;

/**
 * Created by Aleksandr Subbotin on 29.09.2015.
 */
public class Util {

    // method returns current server address:port
    public static String currentServerProperty(Activity activity) {
        SharedPreferences SP = PreferenceManager
                .getDefaultSharedPreferences(activity.getBaseContext());

        return "http://" +
                SP.getString(
                    activity.getResources().getString(R.string.key_server_address),
                    activity.getResources().getString(R.string.default_value_server_address))
                + ":" +
                SP.getString(
                    activity.getResources().getString(R.string.title_server_port),
                    activity.getResources().getString(R.string.default_value_server_port));
    }
}
