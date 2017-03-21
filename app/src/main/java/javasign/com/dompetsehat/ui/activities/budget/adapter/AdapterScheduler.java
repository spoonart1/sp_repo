package javasign.com.dompetsehat.ui.activities.budget.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.github.jjobes.slidedatetimepicker.SlideDateTimeListener;
import com.github.jjobes.slidedatetimepicker.SlideDateTimePicker;
import java.util.Date;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.VeryFund.GeneralHelper;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.ui.event.BudgetSchedulerEvent;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.RxBus;
import javasign.com.dompetsehat.utils.Words;

/**
 * Created by lafran on 10/3/16.
 */

public class AdapterScheduler extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  private Context context;
  private Date initialDate;
  private FragmentManager fragmentManager;
  private List<SHModel> models;
  private int mCheckedPos = -1;
  private RxBus rxBus = MyCustomApplication.getRxBus();
  private Activity activity;

  public AdapterScheduler(Activity activity, FragmentManager fragmentManager,
      List<SHModel> models) {
    setModels(models);
    this.activity = activity;
    this.fragmentManager = fragmentManager;
    this.initialDate = new Date();
  }

  public void setModels(List<SHModel> models) {
    this.models = models;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    View view = null;
    switch (viewType) {
      case SHModel.CUSTOM:
        view =
            LayoutInflater.from(context).inflate(R.layout.adapter_scheduler_custom, parent, false);
        return new SHCustom(view);

      default:
        view =
            LayoutInflater.from(context).inflate(R.layout.adapter_scheduler_default, parent, false);
        return new SH(view);
    }
  }

  @Override public int getItemViewType(int position) {
    return this.models.get(position).typeview;
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    int typeview = models.get(position).typeview;
    SHModel m = models.get(position);
    holder.itemView.setTag(position);
    switch (typeview) {
      case SHModel.CUSTOM:
        SHCustom shc = (SHCustom) holder;
        shc.tv_title.setText(m.title);
        shc.tv_start.setText(m.labelDateStart);
        shc.tv_end.setText(m.labelDateEnd);
        shc.indicator.setVisibility(m.isChecked ? View.VISIBLE : View.GONE);
        shc.v_divider.setVisibility(position >= getItemCount() - 1 ? View.GONE : View.VISIBLE);
        break;

      default:
        SH sh = (SH) holder;
        sh.tv_title.setText(m.title);
        sh.tv_start.setText(m.labelDateStart);
        sh.tv_end.setText(m.labelDateEnd);
        sh.indicator.setVisibility(m.isChecked ? View.VISIBLE : View.GONE);
        sh.v_divider.setVisibility(position >= getItemCount() - 1 ? View.GONE : View.VISIBLE);
        break;
    }
  }

  public void setChecked(boolean checked, int pos) {
    if (mCheckedPos != -1 && mCheckedPos != pos) {
      setChecked(false, mCheckedPos);
    }

    models.get(pos).isChecked = checked;
    mCheckedPos = pos;
    notifyDataSetChanged();
  }

  @Override public int getItemCount() {
    return models.size();
  }

  class SH extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_start) TextView tv_start;
    @Bind(R.id.tv_end) TextView tv_end;
    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.indicator) View indicator;

    public SH(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      itemView.setOnClickListener(v -> {
        int pos = (int) v.getTag();
        rxBus.send(new BudgetSchedulerEvent(models.get(pos)));
        activity.finish();
      });
    }
  }

  class SHCustom extends RecyclerView.ViewHolder {

    @Bind(R.id.tv_title) TextView tv_title;
    @Bind(R.id.tv_start) TextView tv_start;
    @Bind(R.id.tv_end) TextView tv_end;
    @Bind(R.id.v_divider) View v_divider;
    @Bind(R.id.indicator) View indicator;
    @Bind(R.id.btn_ok) Button btn_ok;

    private TextView targetText;
    private GeneralHelper helper = GeneralHelper.getInstance();

    public SHCustom(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);

      Words.setButtonToListen(btn_ok, tv_start, tv_end);
    }

    private SlideDateTimeListener listener = new SlideDateTimeListener() {
      @Override public void onDateTimeSet(Date date) {
        final int pos = (int) itemView.getTag();
        if (targetText != null) {
          targetText.setText(
              Helper.setSimpleDateFormat(date, GeneralHelper.FORMAT_DD_MM_YYYY_SLICED));
        }
        if (targetText.getId() == R.id.tv_start) {
          models.get(pos).dateStart = Helper.setSimpleDateFormat(date,GeneralHelper.FORMAT_YYYY_MM_DD);
        } else {
          models.get(pos).dateEnd = Helper.setSimpleDateFormat(date,GeneralHelper.FORMAT_YYYY_MM_DD);
        }

        initialDate = date;
      }
    };

    @OnClick({ R.id.tv_start, R.id.tv_end }) void setDate(TextView t) {
      new SlideDateTimePicker.Builder(fragmentManager).setInitialDate(initialDate)
          .setListener(listener)
          .setIsDateOnly(true)
          .setIs24HourTime(true)
          .setIndicatorColor(Helper.GREEN_DOMPET_COLOR)
          .build()
          .show();

      targetText = t;
    }

    @OnClick(R.id.btn_ok) void onDone(View v) {
      int pos = (int) itemView.getTag();
      rxBus.send(new BudgetSchedulerEvent(models.get(pos)));
      activity.finish();
    }
  }

  public static class SHModel {

    static final int DEFAULT = 0;
    static final int CUSTOM = 1;
    int typeview = DEFAULT;

    public String identify;
    public String title;
    public String dateStart;
    public String leftSwitchLabel = "";

    public String dateEnd;
    public boolean isRepeatAble = false;
    public boolean isChecked = false;

    public String labelDateStart;
    public String labelDateEnd;

    public SHModel(String title) {
      this.title = title;
    }

    public SHModel setTypeViewCustom() {
      this.typeview = CUSTOM;
      return this;
    }

    public SHModel setIdentify(String identify) {
      this.identify = identify;
      return this;
    }

    public SHModel setPeriode(String dateStart, String dateEnd) {
      this.dateStart = dateStart;
      this.dateEnd = dateEnd;
      return this;
    }

    public SHModel setLeftSwitchLabel(String leftSwitchLabel) {
      this.leftSwitchLabel = leftSwitchLabel;
      return this;
    }

    public SHModel setRepeatAble(boolean repeat) {
      this.isRepeatAble = repeat;
      return this;
    }

    public SHModel setChecked(boolean checked) {
      this.isChecked = checked;
      return this;
    }

    public SHModel setLabelDatePeriode(String labelDateStart, String labelDateEnd) {
      this.labelDateStart = labelDateStart;
      this.labelDateEnd = labelDateEnd;
      return this;
    }

    public String getLabelDateEnd() {
      return labelDateEnd;
    }

    public String getLabelDateStart() {
      return labelDateStart;
    }
  }
}
