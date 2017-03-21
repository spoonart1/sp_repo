package javasign.com.dompetsehat.ui.activities.plan.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.afollestad.sectionedrecyclerview.SectionedRecyclerViewAdapter;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Account;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.utils.Helper;
import javasign.com.dompetsehat.utils.IconCategoryRounded;
import javasign.com.dompetsehat.utils.MCryptNew;
import javasign.com.dompetsehat.utils.RupiahCurrencyFormat;
import javasign.com.dompetsehat.view.AccountView;
import timber.log.Timber;

/**
 * Created by lafran on 10/12/16.
 */
public class AdapterNonInstitusi extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

  private List<ANIModel> models = new ArrayList<>();
  private Account account;
  private MCryptNew mCryptNew = new MCryptNew();
  private HashMap<String, Integer> mCheckedPos;
  public String single = "single";
  List<Product> products;
  List<Integer> choosed_products;
  Context context;

  private boolean is_multi_selected = false;
  private View dependentView;
  private int normalPaintFlag = -1;

  public AdapterNonInstitusi(Account account, List<Product> products) {
    this.account = account;
    this.products = products;
    initialize(products);
    mCheckedPos = new HashMap<>();
  }

  private void initialize(List<Product> products) {
    models.clear();
    for (Product p:products) {
      ANIModel model = new ANIModel();
      model.position = products.indexOf(p);
      model.product = p;
      models.add(model);
    }
  }

  public AdapterNonInstitusi setDependentView(View dependentView){
    this.dependentView = dependentView;
    return this;
  }

  public AdapterNonInstitusi setMultiSelected(boolean is_multi_selected) {
    this.is_multi_selected = is_multi_selected;
    return this;
  }

  public AdapterNonInstitusi setChoosed_products(List<Integer> choosed_products) {
    this.choosed_products = choosed_products;
    return this;
  }

  @Override public int getSectionCount() {
    return 1;
  }

  @Override public int getItemCount(int section) {
    return models.size();
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    this.context = parent.getContext();
    LayoutInflater inflater = LayoutInflater.from(parent.getContext());
    View view = null;
    switch (viewType) {
      case VIEW_TYPE_HEADER:
        view = inflater.inflate(R.layout.adapter_product_non_institusi_header, parent, false);
        return new ANIHead(view);
      case VIEW_TYPE_ITEM:
        view = inflater.inflate(R.layout.adapter_product_non_institusi_child, parent, false);
        return new ANIChild(view);
    }
    return null;
  }

  @Override public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
    ANIHead head = (ANIHead) holder;
    head.icr_category.setIconCode(AccountView.iconVendor.get(account.getIdvendor()));
    head.icr_category.setBackgroundColorIcon(AccountView.accountColor.get(account.getIdvendor()));
    head.tv_label.setText(mCryptNew.decrypt(account.getName()).toUpperCase());
    head.tv_total.setText("(" + getItemCount(0) + ")");
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, int relativePosition,
      int absolutePosition) {
    ANIChild child = (ANIChild) holder;
    ANIModel aniModel = models.get(relativePosition);
    child.itemView.setTag(aniModel);

    if(normalPaintFlag == -1) normalPaintFlag = child.tv_label.getPaintFlags();

    child.tv_label.setText(mCryptNew.decrypt(aniModel.product.getName()));
    child.tv_total.setText(RupiahCurrencyFormat.getInstance().toRupiahFormatSimple(aniModel.product.getBalance()));
    child.v_divider.setVisibility(
        relativePosition == getItemCount(0) - 1 ? View.GONE : View.VISIBLE);
    child.setCheckbox();

    child.itemView.setOnClickListener(v -> {
      ANIModel a = (ANIModel) v.getTag();
      setCheckedByPosition(a.position, !a.isChecked);
    });

    if(choosed_products != null){
      if(choosed_products.contains(aniModel.product.getId_product())){
        //child.itemView.setEnabled(false);
        //child.itemView.setClickable(false);
        child.itemView.setOnClickListener(view -> {
          Helper.showCustomSnackBar(child.itemView,LayoutInflater.from(child.itemView.getContext()),"Maaf rekening anda sudah tersambung dengan rencana lain",true,ContextCompat.getColor(child.itemView.getContext(),R.color.red_400));
        });
        child.tv_label.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        child.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.grey_400));
      }
      else{
        child.tv_label.setPaintFlags(normalPaintFlag);
      }
    }
  }

  public void setCheckedByPosition(int pos, boolean check) {
    if (models != null) {
      if (!is_multi_selected) {
        mCheckedPos.put(single, pos);
        for (ANIModel model : models) {
          model.isChecked = false;
        }
      } else {
        mCheckedPos.put(String.valueOf(pos), pos);
      }
      models.get(pos).isChecked = check;
    }

    if(dependentView != null) {
      dependentView.setEnabled(checkIfOneIsSelected());
    }

    notifyDataSetChanged();
  }

  private boolean checkIfOneIsSelected(){
    boolean enable = false;
    for(ANIModel a : models){
      if(a.isChecked) {
        enable = true;
        break;
      }
      enable = false;
    }
    return enable;
  }

  public Product getProductChecked() {
    Timber.e("getProductChecked data "+mCheckedPos);
    if (!is_multi_selected) {
      if(mCheckedPos.containsKey(single)) {
        return products.get(mCheckedPos.get(single));
      }
    }
    return null;
  }

  public List<Product> getProductsChecked() {
    if (is_multi_selected) {
      if(mCheckedPos.size()>0) {
        ArrayList<Product> datas = new ArrayList<>();
        for(Map.Entry<String,Integer> data:mCheckedPos.entrySet()){
          datas.add(products.get(data.getValue()));
        }
        return datas;
      }
    }
    return null;
  }

  class ANIHead extends RecyclerView.ViewHolder {

    @Bind(R.id.icr_category) IconCategoryRounded icr_category;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_total) TextView tv_total;

    final String code_uncheck =
        CommunityMaterial.Icon.cmd_checkbox_blank_circle_outline.getFormattedName();
    final String code_check =
        CommunityMaterial.Icon.cmd_checkbox_marked_circle_outline.getFormattedName();

    public ANIHead(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }

  class ANIChild extends RecyclerView.ViewHolder {

    @Bind(R.id.checkbox) IconicsTextView checkbox;
    @Bind(R.id.tv_label) TextView tv_label;
    @Bind(R.id.tv_total) TextView tv_total;
    @Bind(R.id.v_divider) View v_divider;

    final String code_uncheck =
        CommunityMaterial.Icon.cmd_checkbox_blank_circle_outline.getFormattedName();
    final String code_check =
        CommunityMaterial.Icon.cmd_checkbox_marked_circle_outline.getFormattedName();

    public ANIChild(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }

    public void setCheckbox() {
      ANIModel aniModel = (ANIModel) itemView.getTag();
      boolean check = aniModel.isChecked;
      if (check) {
        checkbox.setText(code_check);
        checkbox.setTextColor(Helper.GREEN_DOMPET_COLOR);
      } else {
        checkbox.setText(code_uncheck);
        checkbox.setTextColor(Color.BLACK);
      }

      checkbox.setTag(check);
    }
  }

  class ANIModel {
    int position;
    boolean isChecked = false;
    Product product;
  }
}
