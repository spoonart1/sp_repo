package javasign.com.dompetsehat.ui.globaladapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.BaseModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.SwipeToDelete;

/**
 * Created by lafran on 10/21/16.
 */

public class BaseRecyclerViewAdapter<T extends BaseModel>
    extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnSwipeToDelete<T extends BaseModel> {
    void onDelete(T item, int position);
    void onCancel(T item, int position);
  }

  private static final int PENDING_REMOVAL_TIMEOUT = 2000;
  private boolean isUndoOn = true;
  private List<T> items;
  private List<T> itemsPendingRemoval;
  private OnSwipeToDelete swipeToDelete;

  private Handler handler = new Handler();
  HashMap<T, Runnable> pendingRunnables = new HashMap<>();

  public void setListToSwipe(List<T> items) {
    this.items = items;
    this.itemsPendingRemoval = new ArrayList<T>();
  }

  public void setSwipeToDeleteListener(OnSwipeToDelete swipeToDeleteListener) {
    this.swipeToDelete = swipeToDeleteListener;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @CallSuper @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    final T item = items.get(position);
    BaseViewHolder viewHolder = null;
    try {
      viewHolder = (BaseViewHolder) holder;
    } catch (ClassCastException exception) {
      throw new ClassCastException("Did your viewHolder already inherit from BaseViewHolder ?");
    }

    viewHolder.undoButton.setTag(position);

    if (itemsPendingRemoval.contains(item)) {
      // we need to show the "undo" state of the row
      viewHolder.itemView.setBackgroundColor(SwipeToDelete.DELETE_COLOR);
      viewHolder.rootView.setVisibility(View.INVISIBLE);
      viewHolder.undoButton.setVisibility(View.VISIBLE);
      viewHolder.undoButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          int pos = (int) v.getTag();
          // user wants to undo the removal, let's cancel the pending task
          T objectToDelete = item;
          Runnable pendingRemovalRunnable = pendingRunnables.get(item);
          pendingRunnables.remove(item);
          if (pendingRemovalRunnable != null) handler.removeCallbacks(pendingRemovalRunnable);
          itemsPendingRemoval.remove(item);
          // this will rebind the row in "normal" state
          notifyItemChanged(items.indexOf(item));

          if (Helper.isEmptyObject(swipeToDelete)) return;

          swipeToDelete.onCancel(objectToDelete, pos);
        }
      });
    } else {
      // we need to show the "normal" state
      viewHolder.itemView.setBackgroundColor(Color.WHITE);
      viewHolder.rootView.setVisibility(View.VISIBLE);
      viewHolder.undoButton.setVisibility(View.GONE);
      viewHolder.undoButton.setOnClickListener(null);
    }
  }

  @Override public int getItemCount() {
    return items != null ? items.size() : 0;
  }

  public boolean isUndoOn() {
    return isUndoOn;
  }

  public void pendingRemoval(Context context, int position) {
    if (!itemsPendingRemoval.contains(items.get(position))) {
      itemsPendingRemoval.add(items.get(position));
      // this will redraw row in "undo" state
      notifyItemChanged(position);
      // let's create, store and post a runnable to remove the item
      Runnable pendingRemovalRunnable = new Runnable() {
        @Override public void run() {
          T objectToRemove = null;
          try {
            objectToRemove = items.get(position);
            remove(items.indexOf(objectToRemove));
          } catch (IndexOutOfBoundsException exception) {
            Toast.makeText(context,
                context.getString(R.string.error_source_unknown) + ", " + context.getString(
                    R.string.please_try_again).toLowerCase(), Toast.LENGTH_SHORT).show();
          }
          if (Helper.isEmptyObject(swipeToDelete)) return;

          swipeToDelete.onDelete(objectToRemove, position);
        }
      };
      handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
      pendingRunnables.put(items.get(position), pendingRemovalRunnable);
    }
  }

  public void remove(int position) {
    if (itemsPendingRemoval.contains(items.get(position))) {
      itemsPendingRemoval.remove(items.get(position));
    }
    if (items.contains(items.get(position))) {
      items.remove(position);
      notifyItemRemoved(position);
    }
  }

  public boolean isPendingRemoval(int position) {
    final BaseModel item = items.get(position);
    return itemsPendingRemoval.contains(item);
  }

  public static class BaseViewHolder extends RecyclerView.ViewHolder {
    Button undoButton;
    View rootView;

    public BaseViewHolder(View itemView) {
      super(itemView);
      rootView = itemView.findViewById(R.id.rootview);
      undoButton = (Button) itemView.findViewById(R.id.undo_button);
    }
  }
}
