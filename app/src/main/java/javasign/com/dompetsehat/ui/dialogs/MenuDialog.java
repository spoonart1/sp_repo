package javasign.com.dompetsehat.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.dialogs.adapter.AdapterDialogMenu;

/**
 * Created by bastianbentra on 8/11/16.
 */
public class MenuDialog extends DialogFragment {

  private String[] labels;
  private String title = "Menu";
  private AdapterDialogMenu adapter;
  private OnMenuDialogClick onMenuDialogClick;
  private String clickedMenu;
  private int clickedPos;

  private boolean hideOkBtn = false;
  private boolean hideChecklist = false;

  private int titleTextColor = Color.BLACK;
  private int mCheckedPosition = 0;

  public MenuDialog() {

  }

  public interface OnMenuDialogClick {
    /* leave onOkClick blank if simpleModeOn() */
    void onOkClick(String item, int pos);
    void onMenuClick(String label, int pos, Dialog dialog);
  }

  public void setOnMenuDialogClick(OnMenuDialogClick onMenuDialogClick) {
    this.onMenuDialogClick = onMenuDialogClick;
  }

  public static MenuDialog newInstance(String title, String[] labels) {
    MenuDialog menuDialog = new MenuDialog();
    menuDialog.init(title, labels);
    return menuDialog;
  }

  public void setDefaultCheckedItem(int pos){
    mCheckedPosition = pos;
  }

  public int getLastSelectedPosition(){
    return clickedPos;
  }

  public void init(String title, String[] labels) {
    this.labels = labels;
    this.title = title;
  }

  public void setLabels(String[] labels) {
    this.labels = labels;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void hideOkButton(boolean hide){
    this.hideOkBtn = hide;
  }

  public void hideCheckList(boolean hideChecklist){
    this.hideChecklist = hideChecklist;
  }

  public void setTitleTextColor(int titleTextColor){
    this.titleTextColor = titleTextColor;
  }

  public void simpleModeOn(){
    hideCheckList(true);
    hideOkButton(true);
  }

  @Bind(R.id.ok) TextView ok;
  @Bind(R.id.title) TextView tv_title;
  @Bind(R.id.recycleview) RecyclerView recycleview;

  @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_menu, null);
    ButterKnife.bind(this, view);
    clickedMenu = "";
    clickedPos = -1;

    tv_title.setText(title);
    tv_title.setTextColor(titleTextColor);
    adapter = new AdapterDialogMenu(labels);
    adapter.setDefaultCheckedView(mCheckedPosition);
    recycleview.setLayoutManager(new LinearLayoutManager(getContext()));
    recycleview.setAdapter(adapter);

    final Dialog dialog = new Dialog(getActivity());
    dialog.setContentView(view);
    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    adapter.setOnMenuClick((v, label, pos) -> {
      if (onMenuDialogClick == null) return;

      clickedMenu = label;
      clickedPos = pos;
      onMenuDialogClick.onMenuClick(label, pos, dialog);
    });

    if(hideOkBtn){
      ok.setVisibility(View.INVISIBLE);
      ok.setEnabled(false);
    }

    adapter.hideCheckList(hideChecklist);
    ok.setOnClickListener(v -> {
      if(onMenuDialogClick == null) return;

      onMenuDialogClick.onOkClick(clickedMenu, clickedPos);
      onDismiss(dialog);
    });
    return dialog;
  }

  @Override public void onDismiss(DialogInterface dialog) {
    super.onDismiss(dialog);
    dismissAllowingStateLoss();
  }
}
