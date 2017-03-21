package javasign.com.dompetsehat.ui.fragments.more.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.DSFont;
import javasign.com.dompetsehat.utils.Helper;

/**
 * Created by bastianbentra on 8/5/16.
 */
public class MoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnClickMenu {
    void onMenuClick(View view, int position);
  }

  private final String RIGHT_ARROW_THIN = DSFont.Icon.dsf_right_chevron_thin.getFormattedName();

  private ArrayList<String> list;
  private ArrayList<String> botLabels;
  private ArrayList<String> icons;
  private OnClickMenu onClickMenu;

  public MoreAdapter(ArrayList<String> labels, ArrayList<String> botLabels) {
    list = labels;
    this.botLabels = botLabels;
  }

  public MoreAdapter withListener(OnClickMenu onClickMenu) {
    this.onClickMenu = onClickMenu;
    return this;
  }

  public MoreAdapter withIcons(ArrayList<String> icons) {
    this.icons = icons;
    return this;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_more, parent, false);
    Holder h = new Holder(view);
    return h;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
    Holder h = (Holder) holder;
    String labeltext = list.get(position);
    String botLabel = botLabels.get(position);

    h.v_divider.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    h.label.setTextColor(position == getItemCount() - 1 ? Helper.GREEN_DOMPET_COLOR
        : ContextCompat.getColor(h.itemView.getContext(), R.color.grey_800));

    h.icon.setText(RIGHT_ARROW_THIN);

    if (icons != null && icons.size() > 0) {
      h.left_icon.setText(icons.get(position));
    }
    h.label.setText(labeltext);
    h.label2.setText(botLabel);
    h.itemView.setOnClickListener(v -> {
      if (onClickMenu != null) {
        onClickMenu.onMenuClick(v, position);
      }
    });
  }

  @Override public int getItemCount() {
    return list.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.label) TextView label;
    @Bind(R.id.label2) TextView label2;
    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.left_icon) IconicsTextView left_icon;
    @Bind(R.id.v_divider) View v_divider;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
