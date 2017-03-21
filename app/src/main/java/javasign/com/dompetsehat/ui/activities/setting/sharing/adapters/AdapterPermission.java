package javasign.com.dompetsehat.ui.activities.setting.sharing.adapters;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.activities.setting.sharing.permission.model.PermissionModel;

/**
 * Created by lafran on 1/5/17.
 */

public class AdapterPermission extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public interface OnStateChange{
    void onChange(PermissionModel model,boolean status);
  }

  private ArrayList<PermissionModel> models;
  private OnStateChange onStateChange;

  public AdapterPermission(ArrayList<PermissionModel> models){
    this.models = models;
  }

  public void setOnStateChange(OnStateChange onStateChange) {
    this.onStateChange = onStateChange;
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_permission, parent, false);
    return new PermissionHolder(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PermissionModel model = models.get(position);
    PermissionHolder ph = (PermissionHolder) holder;
    ph.tv_label.setText(model.name);
    ph.bind(model);
  }

  @Override public int getItemCount() {
    return models.size();
  }

  public ArrayList<PermissionModel> getAllPermissions(){
    return models;
  }

  class PermissionHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.checkbox_visible) AppCompatCheckBox ch_visible;

    private PermissionModel model;

    public PermissionHolder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void bind(PermissionModel model){
      this.model = model;
      ch_visible.setChecked(model.isVisible);
      ch_visible.setOnCheckedChangeListener(this);
    }

    @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      model.isVisible = ch_visible.isChecked();
      if(onStateChange != null){
        onStateChange.onChange(this.model,isChecked);
      }
    }

    private void showAlert(CompoundButton buttonView, boolean isChecked){
      Context context = buttonView.getContext();
      AlertDialog dialog = new AlertDialog.Builder(buttonView.getContext())
          .setMessage("")
          .create();
    }
  }
}
