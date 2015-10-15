package tales.act;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nhaarman.listviewanimations.itemmanipulation.expandablelistitem.ExpandableListItemAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.undo.UndoAdapter;

import java.util.ArrayList;

import farytale.fairytale.genius.com.fairytaleclient.R;
import tales.model.Tale;


/**
 * Created by Aleksandr Subbotin on 28.09.2015.
 */
public class MyExpandableListItemAdapter
        extends ExpandableListItemAdapter<Tale>
        implements UndoAdapter{

    private final Context mContext;
    private static final boolean IS_SHOW_ALL = true;

    /*
     * This will create a new ExpandableListItemAdapter, providing a custom layout resource,
     * and the two child ViewGroups' id's. If you don't want this, just pass either just the
     * Context, or the Context and the List<T> up to super.
     */
    public MyExpandableListItemAdapter(@NonNull final Context context,
                                       ArrayList<Tale> items) {
        super(context, R.layout.activity_expandablelistitem_card,
                R.id.activity_expandablelistitem_card_title,
                R.id.activity_expandablelistitem_card_content, items);
        this.mContext = context;
    }

    @NonNull
    @Override
    public View getTitleView(final int position, final View convertView,
                             @NonNull final ViewGroup parent) {

        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(mContext);
        }

        if (IS_SHOW_ALL) {
            final Tale tale = getItem(position);
            tv.setText("id:" + tale.getId()
                    + " name:" + tale.getName()
                    + " \nuri:" + tale.getUri()
            );
            return tv;
        }

        tv.setText(getItem(position).getName());
        return tv;
    }

    @NonNull
    @Override
    public View getContentView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        TextView textView = (TextView) convertView;
        if (textView == null) {
            textView = new TextView(mContext);
        }
        final Tale tale = getItem(position);
        textView.setText(tale.getText());

        return textView;
    }

    @NonNull
    @Override
    public View getUndoView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.undo_row, parent, false);
        }
        return view;
    }

    @NonNull
    @Override
    public View getUndoClickView(@NonNull final View view) {
        return view.findViewById(R.id.undo_row_undobutton);
    }
}
