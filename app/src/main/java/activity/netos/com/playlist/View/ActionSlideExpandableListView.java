package activity.netos.com.playlist.View;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

/**
 * A more specific expandable listview in which the expandable area
 * consist of some buttons which are context actions for the item itself.
 * <p/>
 * It handles event binding for those buttons and allow for adding
 * a listener that will be invoked if one of those buttons are pressed.
 *
 * @author tjerk
 * @date 6/26/12 7:01 PM
 */
public class
        ActionSlideExpandableListView extends SlideExpandableListView {
    private OnActionClickListener listener;
    private int[] buttonIds = null;

    public ActionSlideExpandableListView(Context context) {
        super(context);
    }

    public ActionSlideExpandableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActionSlideExpandableListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setItemActionListener(OnActionClickListener listener, int... buttonIds) {
        this.listener = listener;
        this.buttonIds = buttonIds;
    }

    /**
     * Interface for callback to be invoked whenever an action is clicked in
     * the expandle area of the list item.
     */
    public interface OnActionClickListener {
        /**
         * Called when an action item is clicked.
         *
         * @param itemView    the view of the list item
         * @param clickedView the view clicked
         * @param position    the position in the listview
         */
        public void onClick(View itemView, View clickedView, int position);
    }
}
