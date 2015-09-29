package tales;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

import farytale.fairytale.genius.com.fairytaleclient.R;
import tales.act.MyExpandableListItemAdapter;
import tales.act.MyListAct;
import tales.model.Tale;
import tales.model.TaleList;
import tales.request.GetUserTalesRequest;
import tales.request.PostTaleRequest;

public class ExpandableListActivity extends MyListAct {

    private static final String LOG_TAG = "ExpandableListActivity";
    private static final int REQUEST_CODE_CREATE_TALE = 1913;
    public static final String INTENT_TALE_KEY = "send_this_tale_please";

    private static final int INITIAL_DELAY_MILLIS = 500;
    // amount active tales in moment
    private static final int OPENED_TALES_LIMIT = 1;
    private MyExpandableListItemAdapter mExpandableListItemAdapter;

    // request/response vars
    private static final String KEY_LAST_REQUEST_CACHE_KEY = "ExpandableListActivityRequestCacheKey";
    private SpiceManager mSpiceManager =
            new SpiceManager(JacksonSpringAndroidSpiceService.class);
    private String mLastRequestCacheKey;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // creating main adapter and setting maximum opened tales in one moment
        mExpandableListItemAdapter = new MyExpandableListItemAdapter(this, new ArrayList<Tale>());
        mExpandableListItemAdapter.setLimit(OPENED_TALES_LIMIT);

        // setting the wrapping animation adapter and quantity of the opened elements
        AnimationAdapter animationAdapter =
                new SwingRightInAnimationAdapter(mExpandableListItemAdapter);
        animationAdapter.setAbsListView(getListView());
        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        getListView().setAdapter(animationAdapter);

        performGetUserTalesRequest();
        Toast.makeText(this, "Tap on cards to expand or collapse them", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        mSpiceManager.start(this);
        super.onStart();
    }

    @Override
    protected void onStop() {
        mSpiceManager.shouldStop();
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (!TextUtils.isEmpty(mLastRequestCacheKey)) {
            outState.putString(KEY_LAST_REQUEST_CACHE_KEY, mLastRequestCacheKey);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey(KEY_LAST_REQUEST_CACHE_KEY)) {
            mLastRequestCacheKey = savedInstanceState
                    .getString(KEY_LAST_REQUEST_CACHE_KEY);
            mSpiceManager.addListenerIfPending(TaleList.class,
                    mLastRequestCacheKey, new UserTalesRequestListener());
            mSpiceManager.getFromCache(TaleList.class,
                    mLastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                    new UserTalesRequestListener());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                Log.i(LOG_TAG, "Starting SettingsActivity");
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.action_new_story:
                Log.i(LOG_TAG, "Starting NewTaleActivity");
                startActivityForResult(new Intent(this, NewTaleActivity.class),
                        REQUEST_CODE_CREATE_TALE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(LOG_TAG, "onActivityResult(" + requestCode + ", " + resultCode + ")");
        if (data == null) return;
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CREATE_TALE:
                    Tale tale = (Tale) data.getSerializableExtra(INTENT_TALE_KEY);
                    performPostTaleRequest(tale);
            }
        }
    }

    private void performGetUserTalesRequest() {
        // todo load from context user name
        final String serverAddress = Util.currentServerProperty(this) + "/dsyer/tales";
        GetUserTalesRequest request = new GetUserTalesRequest(serverAddress);
        mLastRequestCacheKey = request.createCacheKey();
        mSpiceManager.execute(request, mLastRequestCacheKey,
                DurationInMillis.ONE_MINUTE, new UserTalesRequestListener());
    }

    private void performPostTaleRequest(Tale tale) {
        final String serverAddress = Util.currentServerProperty(this);
        PostTaleRequest postTaleRequest = new PostTaleRequest(serverAddress, tale);
        mSpiceManager.execute(postTaleRequest, new PostTaleListener());
    }

    private class UserTalesRequestListener implements
            RequestListener<TaleList> {
        @Override
        public void onRequestFailure(SpiceException e) {
            Toast.makeText(ExpandableListActivity.this,
                    "Error during request: " + e.getLocalizedMessage(), Toast.LENGTH_LONG)
                    .show();
            Log.e(LOG_TAG, "Error during request: " + e.getLocalizedMessage());
            ExpandableListActivity.this.setProgressBarIndeterminateVisibility(false);
        }

        @Override
        public void onRequestSuccess(TaleList taleList) {
            // userList could be null just if contentManager.getFromCache(...)
            // doesn't return anything.
            if (taleList == null) return;
            mExpandableListItemAdapter.clear();
            mExpandableListItemAdapter.addAll(taleList);
            mExpandableListItemAdapter.notifyDataSetChanged();
        }
    }

    private class PostTaleListener implements RequestListener<Tale>{

        @Override
        public void onRequestFailure(SpiceException e) {
            Toast.makeText(ExpandableListActivity.this, "Failed to create new tale"
                            + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error during request: " + e.getLocalizedMessage());
        }

        // user hat created tale, reload tales list
        @Override
        public void onRequestSuccess(Tale tale) {
            Toast.makeText(ExpandableListActivity.this, "Tale created", Toast.LENGTH_SHORT).show();
            performGetUserTalesRequest();
        }
    }
}
