package javasign.com.dompetsehat.view;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import butterknife.ButterKnife;
import java.util.ArrayList;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 2/3/17.
 */

public class CustomSpinnerAdapter extends ArrayAdapter {

  private String[] labels;
  private int selectedPosition = -1;
  private Context context;
  private boolean showIndicator = true;
  ArrayList<?> datas;

  public CustomSpinnerAdapter(Context context, int resource, String[] labels) {
    super(context, resource, labels);
    this.context = context;
    this.labels = labels;
  }

  public CustomSpinnerAdapter(Context context, int resource, String[] labels,ArrayList<?> datas) {
    super(context, resource, labels);
    this.context = context;
    this.labels = labels;
    this.datas = datas;
  }

  public ArrayList<?> getDatas() {
    return datas;
  }

  public CustomSpinnerAdapter showIndicator(boolean show){
    this.showIndicator = show;
    return this;
  }

  public void setInitialPositionWhenOpenned(int selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  public TextView getView(int position, View convertView, ViewGroup parent) {
    TextView textView =
        (TextView) LayoutInflater.from(context).inflate(R.layout.spinner_item_shown, parent,
            false);
    if(!showIndicator){
      textView.setCompoundDrawables(null, null, null, null);
    }
    textView.setText(labels[position]);
    return textView;
  }

  @Override public int getPosition(Object item) {
    return super.getPosition(item);
  }

  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    View v = LayoutInflater.from(context).inflate(R.layout.adapter_spinner_item_dropdown, parent,
        false);
    TextView textView = ButterKnife.findById(v, android.R.id.text1);
    textView.setText(labels[position]);
    textView.setTextColor(ContextCompat.getColor(context, R.color.grey_800));
    if(selectedPosition == position){
      textView.setTextColor(ContextCompat.getColor(context, R.color.orange_500));
    }
    return v;
  }
}
