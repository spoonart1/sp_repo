package javasign.com.dompetsehat.utils;

import android.app.Activity;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.BaseModel;

/**
 * Created by lafran on 12/14/16.
 */

public class ItemEventHelper<T extends Object> implements View.OnClickListener {

  public interface ItemEventInterface {
    ItemEventHelper getItemEventHelper();

    void setOnClickItem(ItemEventHelper.OnClickItem onClickItem);
  }

  public interface OnMenuEditorClick<T extends Object> {
    void onCancel();

    void onDelete();

    void onEdit();

    void onRename();
  }

  public interface OnClickItem<T extends Object> {
    void onClick(View v, T item,int section, int postion);

    void onLongPressed(View v, T item,int section, int postion);
  }

  public interface OnSecondaryItemClick<T extends Object> {
    void onSecondaryItemCLick(View parentView, View itemView, T item,int section,int position);
  }

  private OnMenuEditorClick onMenuEditorClick;
  private OnClickItem onClickItem;
  private OnSecondaryItemClick onSecondaryItemClick;

  private View editorLayout = null;
  private View mHiglightedItemView = null;
  private View refreshLayout;
  private ViewGroup tabContainer;
  private boolean isDeleteOnly = false;

  private boolean isAttached = false;
  private int[] menuIdRes = new int[] { R.id.btn_cancel, R.id.btn_edit, R.id.btn_delete, R.id.btn_rename };

  public ItemEventHelper(Activity rootActivity, String title) {
    prepareEditor(rootActivity, title);
  }

  public static ItemEventHelper attachToActivity(Activity rootActivity, String title) {
    ItemEventHelper itemEventHelper = new ItemEventHelper(rootActivity, title);
    return itemEventHelper;
  }

  public ItemEventHelper deleteMenuOnly(){
    isDeleteOnly = true;
    return this;
  }

  public void setAdapterOnClickItem(OnClickItem onClickItem) {
    this.onClickItem = onClickItem;
  }

  public void setRefreshLayoutDisableIfExist(View refreshLayout) {
    this.refreshLayout = refreshLayout;
  }

  public void setOnSecondaryItemClick(OnSecondaryItemClick onSecondaryItemClick){
    this.onSecondaryItemClick = onSecondaryItemClick;
  }

  protected void prepareEditor(Activity rootActivity, String title) {
    editorLayout =
        LayoutInflater.from(rootActivity).inflate(R.layout.simple_bar_editor_action_menu, null);
    editorLayout.setVisibility(View.GONE);
    LinearLayout.LayoutParams params =
        new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    TextView tv_title = ButterKnife.findById(editorLayout, R.id.tv_title);
    tv_title.setText(title);
    editorLayout.setLayoutParams(params);
    tabContainer = ButterKnife.findById(rootActivity, R.id.tab_container);
  }

  protected void initEditor() {
    setIsShowing(true);
    if(tabContainer == null){
      throw new Error("Tidak ada id 'tab_container' di Rootview");
    }
    tabContainer.addView(editorLayout);
    setListener();
    if(isDeleteOnly){
      ButterKnife.findById(editorLayout, R.id.btn_edit).setVisibility(View.GONE);
    }
  }

  public void showMenu(int id){
    if(editorLayout != null) {
      ButterKnife.findById(editorLayout,id).setVisibility(View.VISIBLE);
    }
  }

  public void hideMenu(int id){
    if(editorLayout != null) {
      ButterKnife.findById(editorLayout,id).setVisibility(View.GONE);
    }
  }

  public void showEditorBar(OnMenuEditorClick onMenuEditorClick) {
    if (isShowing()) return;

    initEditor();
    this.onMenuEditorClick = onMenuEditorClick;
    editorLayout.setAlpha(0);
    if (editorLayout.getVisibility() == View.GONE) editorLayout.setVisibility(View.VISIBLE);
    ViewCompat.animate(editorLayout).alpha(1).setDuration(200).withEndAction(() -> {
      editorLayout.setAlpha(1);
      editorLayout.setVisibility(View.VISIBLE);
    });
  }

  public void hideEditor() {
    ViewGroup parent = (ViewGroup) editorLayout.getParent();
    ViewCompat.animate(editorLayout).alpha(0).setDuration(200).withEndAction(() -> {
      if (Helper.isEmptyObject(parent) || Helper.isEmptyObject(editorLayout)) return;
      parent.removeView(editorLayout);
      setIsShowing(false);
    });
    highlight(false);
  }

  public void attach(View view, View viewSecondary,int section,int position) {
    final T object = (T) view.getTag();
    triggerOnclick(view, viewSecondary, object, section,position);
    triggerOnLongClick(view, viewSecondary, object,section,position);
  }

  protected void triggerOnclick(View view, View viewSecondary, T object,int section,int position) {
    if (view != null) {
      view.setOnClickListener(v -> {
        if (onClickItem != null) {
          if (!isShowing()) {
            onClickItem.onClick(v, object,section,position);
          } else {
            hideEditor();
          }
        }
      });
    }

    if (viewSecondary != null) {
      viewSecondary.setOnClickListener(v -> {
        if(onSecondaryItemClick == null){
          System.out.println("ItemEventHelper.triggerOnclick onSecondaryItemClick is null");
        }
        if (onClickItem != null && onSecondaryItemClick == null) {
          if (!isShowing()) {
            mHiglightedItemView = view;
            onClickItem.onLongPressed(view, object,section,position);
            highlight(true);
          } else {
            hideEditor();
          }
        }

        else if (onSecondaryItemClick != null) {
          System.out.println("ItemEventHelper.triggerOnclick masuk bawah");
          if (!isShowing()) {
            //mHiglightedItemView = view;
            onSecondaryItemClick.onSecondaryItemCLick(view, viewSecondary, object,section,position);
            //highlight(true);
          } else {
            hideEditor();
          }
        }
      });
    }
  }

  protected void changeBackground(View target) {
    target.setBackground(target.getResources().getDrawable(R.drawable.item_transition_background));
  }

  protected void triggerOnLongClick(View view, View viewSecondary, T object,int section,int position) {
    view.setOnLongClickListener(v -> {
      if (onClickItem != null && !isShowing()) {
        mHiglightedItemView = v;
        onClickItem.onLongPressed(v, object,section,position);
        highlight(true);
        return true;
      }
      return false;
    });
  }

  protected void highlight(boolean higlight) {
    if (mHiglightedItemView == null) return;
    final int duration = 200;
    if (!(mHiglightedItemView.getBackground() instanceof TransitionDrawable)) {
      changeBackground(mHiglightedItemView);
    }
    TransitionDrawable transition = (TransitionDrawable) mHiglightedItemView.getBackground();
    if (higlight) {
      transition.startTransition(duration);
    } else {
      transition.reverseTransition(duration);
      mHiglightedItemView = null;
    }
  }

  protected void setListener() {
    for (int id : menuIdRes) {
      ButterKnife.findById(editorLayout, id).setOnClickListener(this);
    }
  }

  @Override public void onClick(View v) {
    if (onMenuEditorClick == null) return;
    switch (v.getId()) {
      case R.id.btn_cancel:
        onMenuEditorClick.onCancel();
        break;

      case R.id.btn_edit:
        onMenuEditorClick.onEdit();
        break;

      case R.id.btn_delete:
        onMenuEditorClick.onDelete();
        break;
      case R.id.btn_rename:
        onMenuEditorClick.onRename();
        break;
    }
    hideEditor();
  }

  private void setIsShowing(boolean isShowing) {
    this.isAttached = isShowing;
    if (refreshLayout != null) {
      refreshLayout.setEnabled(!isShowing);
    }
  }

  public boolean isShowing() {
    return isAttached;
  }

  public static class SimpleMenuEditorClick implements OnMenuEditorClick {

    @Override public void onCancel() {

    }

    @Override public void onDelete() {

    }

    @Override public void onEdit() {

    }

    @Override public void onRename() {

    }
  }
}
