package javasign.com.dompetsehat.ui.activities.reminder.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.presenter.reminder.ReminderPresenter;
import javasign.com.dompetsehat.ui.activities.reminder.pojo.ReminderModel;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.ItemEventHelper;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.utils.Words;

/**
 * Created by bastianbentra on 8/29/16.
 */
public class AdapterReminder extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    implements ItemEventHelper.ItemEventInterface {

  private List<ReminderModel> models;
  private List<ReminderModel> backups;
  private Context context;
  private RupiahCurrencyFormat format = RupiahCurrencyFormat.getInstance();
  private ItemEventHelper itemEventHelper;
  private ReminderPresenter presenter;

  public AdapterReminder(List<ReminderModel> models) {
    setModels(models);
  }

  public void setModels(List<ReminderModel> models) {
    this.models = models;
    this.backups = new ArrayList<>(this.models);
  }

  public AdapterReminder setItemEventHelper(ItemEventHelper itemEventHelper) {
    this.itemEventHelper = itemEventHelper;
    return this;
  }

  public AdapterReminder setPresenter(ReminderPresenter presenter) {
    this.presenter = presenter;
    return this;
  }

  public void remove(ReminderModel model) {
    int index = models.indexOf(model);
    if(index >= 0) {
      models.remove(index);
      notifyItemRemoved(index);
    }
  }

  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view =
        LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_reminder, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    ReminderModel model = models.get(position);
    h.itemView.setTag(model);
    h.model = model;

    h.cv_background.setCircleColor(model.bgColor);
    h.tv_category_icon.setText(model.icon);
    h.tv_header.setText(Words.toSentenceCase(model.alarm.getDeskripsi_alarm()));

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
      h.tv_repeat.setText(
          Html.fromHtml("&bull; " + context.getString(R.string.every_date) + " " + model.repeatDate,
              1));
    } else {
      h.tv_repeat.setText(Html.fromHtml(
          "&bull; " + context.getString(R.string.every_date) + " " + model.repeatDate));
    }

    h.tv_value.setText(format.toRupiahFormatSimple(model.billAmount));
    h.setReminderActive(model.isActive);
    h.v_divider.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);

    h.ll_status.setOnClickListener(v -> {
      if (presenter.setActiveReminder(model)) {
        h.setReminderActive(!model.isActive);
      } else {
        Toast.makeText(context, context.getString(R.string.error_source_unknown),
            Toast.LENGTH_LONG).show();
      }
    });

    itemEventHelper.attach(h.itemView, h.cv_background,0,position);
  }

  @Override public int getItemCount() {
    return models.size();
  }

  @Override public ItemEventHelper getItemEventHelper() {
    return null;
  }

  @Override public void setOnClickItem(ItemEventHelper.OnClickItem onClickItem) {

  }

  public static class Holder extends ViewHolder {

    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.tv_category_icon) IconicsTextView tv_category_icon;
    @Bind(R.id.tv_header) TextView tv_header;
    @Bind(R.id.tv_repeat) TextView tv_repeat;
    @Bind(R.id.tv_value) TextView tv_value;
    @Bind(R.id.tv_status) TextView tv_status;
    @Bind(R.id.ll_status) View ll_status;
    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.indicator) View indicator;

    GradientDrawable drawable;

    private final int strokeColor = Color.parseColor("#BDBDBD");
    private final int activeColor = Helper.GREEN_DOMPET_COLOR;
    private final int inactiveColor = Color.TRANSPARENT;
    private ReminderModel model;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      drawable = (GradientDrawable) indicator.getBackground();
    }

    public void setReminderActive(boolean active) {
      String status = "";
      if (active) {
        status = itemView.getResources().getString(R.string.active);
        drawable.setColor(activeColor);
        drawable.setStroke(0, strokeColor);
      } else {
        status = itemView.getResources().getString(R.string.not_active);
        drawable.setColor(inactiveColor);
        drawable.setStroke(1, strokeColor);
      }

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        indicator.setBackground(drawable);
      } else {
        indicator.setBackgroundDrawable(drawable);
      }
      model.isActive = active;
      tv_status.setText(status);
      tv_status.setEnabled(model.isActive);
    }
  }
}
