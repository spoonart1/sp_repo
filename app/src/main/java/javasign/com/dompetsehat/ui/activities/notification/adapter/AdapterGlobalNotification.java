package javasign.com.dompetsehat.ui.activities.notification.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import java.util.Date;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.notification.pojo.GlobalNotifModel;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;

/**
 * Created by lafran on 3/9/17.
 */

public class AdapterGlobalNotification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private int highlightColor = Color.parseColor("#E1F5FE");
  private ArrayList<GlobalNotifModel> notifModels;
  private OnReadItem onReadItem;

  public AdapterGlobalNotification(ArrayList<GlobalNotifModel> notifModels) {
    this.notifModels = notifModels;
  }

  public AdapterGlobalNotification setOnReadItem(OnReadItem onReadItem) {
    this.onReadItem = onReadItem;
    return this;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.adapter_global_notification, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    GlobalNotifModel m = notifModels.get(position);
    h.icr_category.setIconCode(m.typeNotif.getIcon());
    h.icr_category.setBackgroundColorIcon(m.typeNotif.getColor());
    h.tv_title.setText(m.message);
    if(m.created_at != null) {
      h.tv_date.setText(Helper.setSimpleDateFormat(new Date(m.created_at),"dd MMM yyyy"));
    }

    if(m.status != null) {
      int bgColor = m.status.equalsIgnoreCase("unread") ? highlightColor : Color.WHITE;
      h.itemView.setBackgroundColor(bgColor);
    }
    h.itemView.setOnClickListener(view -> {
      if(onReadItem != null){
        onReadItem.onRead(view,m,position);
      }
    });

  }

  public interface OnReadItem{
    void onRead(View v,GlobalNotifModel model, int pos);
  }

  @Override public int getItemCount() {
    return notifModels.size();
  }

  class Holder extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_date) TextView tv_date;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
