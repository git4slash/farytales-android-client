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
import java.util.List;

import farytale.fairytale.genius.com.fairytaleclient.R;
import tales.model.Tale;


/**
 * Created by Aleksandr Subbotin on 28.09.2015.
 */
public class MyExpandableListItemAdapter
        extends ExpandableListItemAdapter<Tale>
        implements UndoAdapter{

    private final Context mContext;
    private List<Tale> tales;

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
//        if (items != null) {
//            this.tales = items;
//        } else {
//            this.tales = new ArrayList<>();
//            tales.add(new Tale("Waiting for stories from you", ""));
//        }
//        addAll(tales);
    }

    @NonNull
    @Override
    public View getTitleView(final int position, final View convertView, @NonNull final ViewGroup parent) {
        TextView tv = (TextView) convertView;
        if (tv == null) {
            tv = new TextView(mContext);
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

        textView.setText(getItem(position).getText());

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
