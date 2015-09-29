package tales;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.octo.android.robospice.JacksonSpringAndroidSpiceService;
import com.octo.android.robospice.SpiceManager;

import farytale.fairytale.genius.com.fairytaleclient.R;
import tales.act.BaseActivity;
import tales.model.Tale;


public class NewTaleActivity extends BaseActivity {

    private SpiceManager mSpiceManager = new SpiceManager(
            JacksonSpringAndroidSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_story);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_tale, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void createTale(View v) {
        EditText mTaleName = (EditText) findViewById(R.id.editText_TaleName);
        EditText mTaleText = (EditText) findViewById(R.id.editText_TaleText);

        Tale taleToSend = new Tale(mTaleName.getText().toString(),
                mTaleText.getText().toString());
        Intent intent = new Intent();
        intent.putExtra(ExpandableListActivity.INTENT_TALE_KEY, taleToSend);
        setResult(RESULT_OK, intent);
        finish();
    }
}
