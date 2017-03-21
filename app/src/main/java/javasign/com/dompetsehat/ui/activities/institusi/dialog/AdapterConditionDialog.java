package javasign.com.dompetsehat.ui.activities.institusi.dialog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 12/28/16.
 */

public class AdapterConditionDialog extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private String[] descriptions;

  public AdapterConditionDialog(String[] descriptions) {
    this.descriptions = descriptions;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_condition_dialog, parent, false);
    return new ConditionHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    ConditionHolder ch = (ConditionHolder) holder;
    ch.tv_numbering.setText(String.valueOf(getNumbering(position)));
    ch.tv_desc.setText(descriptions[position]);
  }

  @Override public int getItemCount() {
    return descriptions.length;
  }

  private int getNumbering(int position) {
    return position + 1;
  }

  class ConditionHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_numbering) TextView tv_numbering;
    @Bind(R.id.tv_desc) TextView tv_desc;

    public ConditionHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
