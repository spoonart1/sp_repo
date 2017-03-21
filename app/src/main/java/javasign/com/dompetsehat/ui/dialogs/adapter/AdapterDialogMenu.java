package javasign.com.dompetsehat.ui.dialogs.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import javasign.com.dompetsehat.R;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/11/16.
 */
public class AdapterDialogMenu extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private String[] labels;
  private OnMenuClick onMenuClick;
  private View clicked;
  private boolean hideCheckList = false;
  private int mDefaultCheckedPos = 1;

  public interface OnMenuClick {
    void onMenuClick(View v, String label, int pos);
  }

  public AdapterDialogMenu(String[] labels) {
    this.labels = labels;
  }

  public void hideCheckList(boolean hideCheckList){
    this.hideCheckList = hideCheckList;
  }

  public void setOnMenuClick(OnMenuClick onMenuClick) {
    this.onMenuClick = onMenuClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_dialog_menu, parent, false);
    return new Holder(view);
  }

  public void setDefaultCheckedView(int pos){
    mDefaultCheckedPos = pos;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    final Holder h = (Holder) holder;

    h.label.setText(labels[position]);
    h.label.setTag(position);

    if(mDefaultCheckedPos == position){
      if(!hideCheckList){
        setCheckedItemView(h.label);
      }
    }

    h.label.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (onMenuClick == null) return;

        if(!hideCheckList) {
          setCheckedItemView(v);
        }

        Timber.e("Heloo "+labels[position]);

        //int pos = (int) v.getTag();
        onMenuClick.onMenuClick(v, labels[position], position);
      }
    });
  }

  private void setCheckedItemView(View v){
    if (clicked != null) {
      View icon = (View) clicked.getTag(R.id.icon);
      icon.setVisibility(View.GONE);
      clicked = null;
    }

    View icon = (View) v.getTag(R.id.icon);
    icon.setVisibility(View.VISIBLE);
    clicked = v;
  }

  @Override public int getItemCount() {
    return labels.length;
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.label) TextView label;
    @Bind(R.id.icon) IconicsTextView icon;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      label.setTag(R.id.icon, icon);

      if(hideCheckList){
        icon.setVisibility(View.GONE);
      }
    }
  }
}
