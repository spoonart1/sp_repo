package javasign.com.dompetsehat.ui.activities.account.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.mikepenz.iconics.view.IconicsTextView;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.models.Product;
import javasign.com.dompetsehat.ui.activities.account.AddAccountBankActivity;
import javasign.com.dompetsehat.ui.activities.account.pojo.CellAccount;
import javasign.com.dompetsehat.ui.activities.institusi.loader.WebviewInstutisiActivity;
import javasign.com.dompetsehat.ui.activities.institusi.vendor.manulife.LoginKlikMamiActivity;
import javasign.com.dompetsehat.utils.CircleShapeView;
import javasign.com.dompetsehat.utils.Words;
import javasign.com.dompetsehat.view.AccountView;

/**
 * Created by bastianbentra on 8/25/16.
 */
public class AdapterListSimpleAccount extends RecyclerView.Adapter<ViewHolder> {

  public final static String TYPE_PRODUCT = "type_product";
  public final static String TYPE_VENDOR = "type_vendor";
  private Context context;
  private String type;
  private ArrayList<CellAccount> products;
  String[] color_array;

  OnSelectedProduct onSelectedProduct;

  public interface OnSelectedProduct{
    void OnSelectedProduct(View v,Product product);
  }

  public AdapterListSimpleAccount(Context context,String type,ArrayList<CellAccount> products){
    this.context =  context;
    this.type = type;
    this.products = products;
    color_array = context.getResources().getStringArray(R.array.color_array);
  }

  public void setOnSelectedProduct(OnSelectedProduct onSelectedProduct) {
    this.onSelectedProduct = onSelectedProduct;
  }

  public AdapterListSimpleAccount(Context context,String type){
    this.context =  context;
    this.type = type;
  }

  public Object getData(){
    switch (type){
      case TYPE_PRODUCT:
        return products;
      case TYPE_VENDOR:
        return AccountView.vendors;
    }
    return null;
  }

  public int getCount(){
    switch (type){
      case TYPE_PRODUCT:
        return products.size();
      case TYPE_VENDOR:
        return AccountView.vendors.size();
    }
    return 0;
  }


  @Override public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    context = parent.getContext();
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_list_simple_account, parent, false);
    return new Holder(view);
  }

  @Override public void onBindViewHolder(ViewHolder holder, int position) {
    Holder h = (Holder) holder;
    switch (type){
      case TYPE_PRODUCT:
        CellAccount product = products.get(position);
        h.cv_background.setCircleColor(Color.parseColor(color_array[product.getColor()]));
        h.tv_account_name.setText(product.getName());
        h.itemView.setOnClickListener(v -> {
          onSelectedProduct.OnSelectedProduct(h.itemView,product.getProduct());
        });
        break;
      case TYPE_VENDOR:
        h.cv_background.setCircleColor(AccountView.accountColor.get(position + 1));
        if(AccountView.vendors.get(position+1).equalsIgnoreCase("Dompet")){
          h.tv_account_name.setText("Manual");
        }else {
          h.tv_account_name.setText(AccountView.vendors.get(position + 1));
        }
        final int idVendor = position + 1;
        h.itemView.setOnClickListener(v -> {
          if(idVendor == AccountView.MNL){
            context.startActivity(new Intent(context, WebviewInstutisiActivity.class).putExtra(WebviewInstutisiActivity.URL_KEY, WebviewInstutisiActivity.URL_MAMI_HOME));
            //context.startActivity(new Intent(context,LoginKlikMamiActivity.class));
          }
          else {
            context.startActivity(new Intent(context, AddAccountBankActivity.class)
                .putExtra(Words.ID, idVendor)
            );
          }
        });
        break;
    }
  }

  @Override public int getItemCount() {
    return getCount();
  }

  class Holder extends RecyclerView.ViewHolder {
    public int identity;
    @Bind(R.id.cv_background) CircleShapeView cv_background;
    @Bind(R.id.tv_account_name) TextView tv_account_name;
    @Bind(R.id.icon) IconicsTextView icon;

    public Holder(View itemView) {
      super(itemView);
      ButterKnife.bind(this, itemView);
    }
  }
}
