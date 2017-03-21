package javasign.com.dompetsehat.ui.activities.plan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.List;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Invest;
import javasign.com.dompetsehat.utils.Helper;
import timber.log.Timber;

/**
 * Created by lafran on 10/11/16.
 */
public class AdapterProductInstitusi extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

  private List<Invest> invests;
  private List<PIModel> models;
  private Context context;
  private int mCheckedPos = -1 ;
  private View dependentView;

  public AdapterProductInstitusi(List<Invest> invests){
    this.invests = invests;
    initiliaze(invests);
  }

  protected void initiliaze(List<Invest> invests){
    models = new ArrayList<>();
    for(int i=0; i<invests.size();i++){
      PIModel pim = new PIModel();
      pim.position = i;
      pim.invest = invests.get(i);
      models.add(pim);
    }
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_product_institusi, parent, false);
    return new PI(view);
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PI pi = (PI) holder;
    PIModel piModel = models.get(position);

    pi.itemView.setTag(piModel);
    pi.v_divider.setVisibility(position == getItemCount() - 1 ? View.GONE : View.VISIBLE);
    pi.tv_label.setText(invests.get(position).getInvest_name());
    pi.setScore(invests.get(position).getInvest_rate());
    pi.setCheckbox();

    pi.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        PIModel p = (PIModel) v.getTag();
        setCheckedByPosition(p.position, !p.isChecked);
      }
    });
  }

  public AdapterProductInstitusi setDependentView(View dependentView){
    this.dependentView = dependentView;
    return this;
  }

  public void setCheckedByPosition(int pos, boolean check){
    if(models != null){
      this.mCheckedPos = pos;
      models.get(pos).isChecked = check;
    }
    notifyDataSetChanged();
  }

  //TODO : get all selected invest
  public List<Invest> getAllSelectedInvests(){
    List<Invest> invests = new ArrayList<>();
    for(PIModel p : models){
      if(p.isChecked){
        invests.add(p.invest);
      }
    }
    return invests;
  }

  private boolean checkIfOneIsSelected(){
    boolean enable = false;
    for(PIModel a : models){
      if(a.isChecked) {
        enable = true;
        break;
      }
      enable = false;
    }
    return enable;
  }

  @Override public int getItemCount() {
    return this.models.size();
  }

  class PI extends RecyclerView.ViewHolder {

    @Bind(R.id.checkbox) IconicsTextView checkbox;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_right) TextView tv_right;
    @Bind(R.id.v_divider) View v_divider;

    final String code_uncheck =
        CommunityMaterial.Icon.cmd_checkbox_blank_circle_outline.getFormattedName();
    final String code_check =
        CommunityMaterial.Icon.cmd_checkbox_marked_circle_outline.getFormattedName();

    public PI(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setCheckbox(){
      PIModel piModel = (PIModel) itemView.getTag();
      boolean check = piModel.isChecked;
      if(check){
        checkbox.setText(code_check);
        checkbox.setTextColor(Helper.GREEN_DOMPET_COLOR);
      }
      else {
        checkbox.setText(code_uncheck);
        checkbox.setTextColor(Color.BLACK);
      }

      if(dependentView != null) {
        dependentView.setEnabled(checkIfOneIsSelected());
      }

      checkbox.setTag(check);
    }

    private void setScore(int amount){
      String s = "";
      int color = Color.BLACK;
      for(int i=0;i<amount;i++){
        s += CommunityMaterial.Icon.cmd_star.getFormattedName();
      }

      if(amount > 0) color = context.getResources().getColor(R.color.yellow_600);
      else s = context.getString(R.string.no_score);

      tv_right.setText(s);
      tv_right.setTextColor(color);
    }

  }

  class PIModel{

    int position;
    boolean isChecked;
    Invest invest;

    public PIModel(){

    }
  }
}
