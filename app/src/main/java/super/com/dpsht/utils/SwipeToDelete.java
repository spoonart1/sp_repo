package javasign.com.dompetsehat.utils;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.globaladapters.BaseRecyclerViewAdapter;

/**
 * Created by lafran on 10/21/16.
 */

public class SwipeToDelete {

  public static final int DELETE_COLOR = Color.parseColor("#f44336");

  private RecyclerView target;
  private Activity activity;
  private BaseRecyclerViewAdapter.OnSwipeToDelete onSwipeToDelete;

  public static SwipeToDelete register(Activity activity, RecyclerView target, BaseRecyclerViewAdapter.OnSwipeToDelete onSwipeToDelete) {
    SwipeToDelete swipeToDelete = new SwipeToDelete();
    swipeToDelete.target = target;
    swipeToDelete.activity = activity;
    swipeToDelete.onSwipeToDelete = onSwipeToDelete;
    swipeToDelete.registerTouch();

    BaseRecyclerViewAdapter adapter = (BaseRecyclerViewAdapter) target.getAdapter();
    adapter.setSwipeToDeleteListener(swipeToDelete.onSwipeToDelete);

    return swipeToDelete;
  }

  private void registerTouch() {
    ItemTouchHelper.SimpleCallback simpleCallback =
        new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

          Drawable background;
          Drawable xMark;
          int xMarkMargin;
          boolean initiated;

          private void init() {
            background = new ColorDrawable(SwipeToDelete.DELETE_COLOR);
            xMark = ContextCompat.getDrawable(activity, R.drawable.ic_delete_black_24dp);
            xMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
            xMarkMargin = (int) activity.getResources().getDimension(R.dimen.padding_size_medium);
          }

          @Override
          public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
              RecyclerView.ViewHolder target) {
            return false;
          }

          @Override
          public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int position = viewHolder.getAdapterPosition();
            BaseRecyclerViewAdapter adapter = (BaseRecyclerViewAdapter) recyclerView.getAdapter();
            if (adapter.isUndoOn() && adapter.isPendingRemoval(position)) {
              return 0;
            }
            return super.getSwipeDirs(recyclerView, viewHolder);
          }

          @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int swipedPosition = viewHolder.getAdapterPosition();
            BaseRecyclerViewAdapter adapter = (BaseRecyclerViewAdapter) target.getAdapter();
            boolean undoOn = adapter.isUndoOn();
            if (undoOn) {
              adapter.pendingRemoval(activity, swipedPosition);
            } else {
              adapter.remove(swipedPosition);
            }
          }

          @Override public void onChildDraw(Canvas c, RecyclerView recyclerView,
              RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState,
              boolean isCurrentlyActive) {
            View itemView = viewHolder.itemView;

            // not sure why, but this method get's called for viewholder that are already swiped away
            if (viewHolder.getAdapterPosition() == -1) {
              // not interested in those
              return;
            }

            if (!initiated) {
              init();
            }

            // draw red background
            background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                itemView.getRight(), itemView.getBottom());
            background.draw(c);

            // draw x mark
            int itemHeight = itemView.getBottom() - itemView.getTop();
            int intrinsicWidth = xMark.getIntrinsicWidth();
            int intrinsicHeight = xMark.getIntrinsicWidth();

            int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
            int xMarkRight = itemView.getRight() - xMarkMargin;
            int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
            int xMarkBottom = xMarkTop + intrinsicHeight;
            xMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

            xMark.draw(c);

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
          }
        };

    ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleCallback);
    mItemTouchHelper.attachToRecyclerView(target);
  }
}
