package tales;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingRightInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.SimpleSwipeUndoAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.TimedUndoAdapter;
import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.Arrays;

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
    private MyExpandableListItemAdapter mTaleAdapter;

    // request/response vars
    private static final String KEY_LAST_REQUEST_CACHE_KEY = "ExpandableListActivityRequestCacheKey";
    private SpiceManager mSpiceManager =
            new SpiceManager(JacksonSpringAndroidSpiceService.class);
    private String mLastRequestCacheKey;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamiclistview);

        DynamicListView listView =
                (DynamicListView) findViewById(R.id.activity_dynamiclistview_listview);

        // Adding header to listview
//        listView.addHeaderView(LayoutInflater.from(this)
//                .inflate(R.layout.activity_dynamiclistview_header, listView, false));

        // creating main adapter and setting maximum opened tales in one moment
        mTaleAdapter = new MyExpandableListItemAdapter(this, new ArrayList<Tale>());
        mTaleAdapter.setLimit(OPENED_TALES_LIMIT);

        // creating the wrapping swipe/delete/undo adapter
        SimpleSwipeUndoAdapter swipeUndoAdapter =
                new TimedUndoAdapter(mTaleAdapter,
                                    this,
                                    new MyOnDismissCallback(mTaleAdapter));

        // setting the wrapping animation adapter and quantity of the opened elements
        AnimationAdapter animationAdapter =
                new SwingRightInAnimationAdapter(swipeUndoAdapter);
        animationAdapter.setAbsListView(listView);
        assert animationAdapter.getViewAnimator() != null;
        animationAdapter.getViewAnimator().setInitialDelayMillis(INITIAL_DELAY_MILLIS);
        listView.setAdapter(animationAdapter);

        // Enabling swipe to dismiss
        listView.enableSimpleSwipeUndo();

        // Loading Tales
        performGetUserTalesRequest(true);

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
                    mLastRequestCacheKey, new GetUserTalesRequestListener());
            mSpiceManager.getFromCache(TaleList.class,
                    mLastRequestCacheKey, DurationInMillis.ONE_MINUTE,
                    new GetUserTalesRequestListener());
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
                    Log.d(this.getClass().getSimpleName(), "Tale: " + tale.getName() + " | " + tale.getText());
                    performPostTaleRequest(tale);
            }
        }
    }

    private void performGetUserTalesRequest(boolean isUsingCachedRequest) {
        // todo load from context user name
        final String serverAddress = Util.currentServerProperty(this) + "/dsyer/tales";
        GetUserTalesRequest request = new GetUserTalesRequest(serverAddress);
        mLastRequestCacheKey = isUsingCachedRequest ? request.createCacheKey() : null;
        mSpiceManager.execute(request, mLastRequestCacheKey,
                DurationInMillis.ONE_MINUTE, new GetUserTalesRequestListener());
    }

    private void performPostTaleRequest(Tale tale) {
        final String serverAddress = Util.currentServerProperty(this);
        final String request = serverAddress + "/dsyer/tales/";
        PostTaleRequest postTaleRequest = new PostTaleRequest(request, tale);
        mSpiceManager.execute(postTaleRequest, new PostTaleListener());
    }

    private class GetUserTalesRequestListener implements
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
            // taleList could be null just if contentManager.getFromCache(...)
            // doesn't return anything.
            if (taleList == null) return;
            mTaleAdapter.clear();
            mTaleAdapter.addAll(taleList);
            mTaleAdapter.notifyDataSetChanged();
        }
    }

    private class PostTaleListener implements RequestListener<Tale>{

        @Override
        public void onRequestFailure(SpiceException e) {
            Toast.makeText(ExpandableListActivity.this, "Failed to create new tale"
                            + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            Log.e(LOG_TAG, "Error during request: " + e.getLocalizedMessage());
        }

        // user hat created new tale, reload tales list
        @Override
        public void onRequestSuccess(Tale tale) {
            Toast.makeText(ExpandableListActivity.this, "Tale created", Toast.LENGTH_SHORT).show();
            performGetUserTalesRequest(false);
        }
    }

    private class MyOnDismissCallback implements OnDismissCallback {

        private final ExpandableListItemAdapter mAdapter;

        @Nullable
        private Toast mToast;

        public MyOnDismissCallback(ExpandableListItemAdapter adapter) {
            mAdapter = adapter;
        }

        @Override
        public void onDismiss(@NonNull ViewGroup viewGroup, @NonNull int[] positions) {
            for (int position : positions) {
                mAdapter.remove(position);
            }

            if (mToast != null) {
                mToast.cancel();
            }

            mToast = Toast.makeText(
                    ExpandableListActivity.this,
                    getString((R.string.removed_positions), Arrays.toString(positions)),
                    Toast.LENGTH_LONG);
            mToast.show();
        }
    }
}
