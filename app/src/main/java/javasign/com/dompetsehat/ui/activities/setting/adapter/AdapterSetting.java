package javasign.com.dompetsehat.ui.activities.setting.adapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.ui.activities.setting.Setting;
import javasign.com.dompetsehat.ui.event.SettingCheckBoxEvent;
import timber.log.Timber;

/**
 * Created by bastianbentra on 8/23/16.
 */
public class AdapterSetting extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

  private List<SettingModel> settingModels;
  private OnSettingClick onSettingClick;

  public AdapterSetting(List<SettingModel> settingModels) {
    this.settingModels = settingModels;
  }

  public void setOnSettingClick(OnSettingClick onSettingClick) {
    this.onSettingClick = onSettingClick;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = null;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_setting_header, parent, false);
        return new Header(view);

      case VIEW_TYPE_ITEM:
        view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.adapter_setting_child, parent, false);
        return new Child(view);
    }
    return null;
  }

  @Override public int getSectionCount() {
    return settingModels.size();
  }

  @Override public int getItemCount(int section) {
    return settingModels.get(section).items.size();
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
    Header h = (Header) holder;
    SettingModel model = settingModels.get(section);
    h.tv_title.setText(model.title);
    h.tv_title.setVisibility(TextUtils.isEmpty(model.title) ? View.GONE : View.VISIBLE);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition,
      int absolutePosition) {
    Child c = (Child) holder;
    SettingModel.Item item = settingModels.get(section).items.get(relativePosition);

    c.icon.setText(item.icon);
    c.tv_label.setText(item.label);
    c.tv_keterangan.setText(item.note);
    if (item.checkboxStyle != null) {
      c.checkbox.setVisibility(View.VISIBLE);
      c.checkbox.setChecked(item.checkboxStyle.isCheckboxActive);
      c.checkbox.setOnCheckedChangeListener((compoundButton, b) -> {
        Timber.e("avesina passcode event "+b);
        MyCustomApplication.getRxBus().send(new SettingCheckBoxEvent(b, item.type));
      });
    } else {
      c.checkbox.setVisibility(View.GONE);
      c.checkbox.setOnCheckedChangeListener(null);
    }
    c.itemView.setTag(item);
    c.itemView.setOnClickListener(v -> {
      if (onSettingClick == null) return;
      if(item.checkboxStyle != null){
        c.checkbox.setChecked(!c.checkbox.isChecked());
      }
      SettingModel.Item it = (SettingModel.Item) v.getTag();
      onSettingClick.onItemClick(v, it, section, relativePosition);
    });
  }

  public interface OnSettingClick {
    void onItemClick(View v, SettingModel.Item item, int section, int position);
  }

  class Header extends RecyclerView.ViewHolder {
    @Bind(R.id.tv_title) TextView tv_title;

    public Header(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  class Child extends RecyclerView.ViewHolder {

    @Bind(R.id.icon) IconicsTextView icon;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.checkbox) AppCompatCheckBox checkbox;
    @Bind(R.id.tv_keterangan) TextView tv_keterangan;
    @Bind(R.id.v_divider) View v_divider;

    public Child(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  public static class SettingModel {

    public String title;
    public List<Item> items;

    public static class Item {
      public Setting type;
      public String icon;
      public String label;
      public String note = "";
      public CheckboxStyle checkboxStyle = null;
    }

    public static class CheckboxStyle {
      public boolean isCheckboxActive = false;

      public CheckboxStyle isCheckboxActive(boolean isCheckboxActive) {
        this.isCheckboxActive = isCheckboxActive;
        return this;
      }
    }
  }
}
