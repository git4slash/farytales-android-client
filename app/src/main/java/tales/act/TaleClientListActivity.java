package tales.act;

import android.os.Bundle;
import android.widget.ListView;

import farytale.fairytale.genius.com.fairytaleclient.R;


/**
 * Created by Aleksandr Subbotin on 28.09.2015.
 */
public class TaleClientListActivity extends BaseActivity {

    private ListView mListView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mylist_listview);
        mListView = (ListView) findViewById(R.id.activity_mylist_listview);
    }

    public ListView getListView() {
        return mListView;
    }

    protected MyListAdapter createListAdapter() {
        return new MyListAdapter(this);
    }
}
