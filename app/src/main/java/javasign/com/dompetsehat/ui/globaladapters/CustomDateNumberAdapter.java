package javasign.com.dompetsehat.ui.globaladapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.utils.IconCategoryRounded;

/**
 * Created by Spoonart on 12/23/2015.
 */
public class CustomDateNumberAdapter extends BaseAdapter {

    List<Integer> dates = new ArrayList<>();
    LayoutInflater inflater;
    Context context;
    IconCategoryRounded selected;
    OnNumberSelected onNumberSelected;
    Integer initDate;

    public CustomDateNumberAdapter(Context context, int maxDateCount){

        this.context = context;
        for(int i=0; i<maxDateCount;i++){
            int date = i+1;
            dates.add(date);
        }
        inflater = LayoutInflater.from(context);
    }

    public CustomDateNumberAdapter setInitDate(Integer initDate) {
        this.initDate = initDate;
        return this;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        HolderNumber hn = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.adapter_number_of_list_date, null);
            hn = new HolderNumber();
            convertView.setTag(hn);
        }
        else {
            if(convertView != null){
                hn = (HolderNumber)convertView.getTag();
            }
        }

        hn.icr_number = (IconCategoryRounded)convertView.findViewById(R.id.icr_number);
        hn.icr_number.setIconCode(String.valueOf(dates.get(position)));

        if(initDate != null){
            if(dates.get(position)==initDate){
                hn.icr_number.setBackgroundColorIcon(ContextCompat.getColor(context,R.color.green_tosca_darker_opacity_400));
                selected = hn.icr_number;
            }
        }

        hn.icr_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selected != null){
                    selected.setBackgroundColorIcon(Color.WHITE);
                    selected = null;
                }

                IconCategoryRounded clicked = (IconCategoryRounded)v;
                clicked.setBackgroundColorIcon(ContextCompat.getColor(context,R.color.green_tosca_darker_opacity_400));

                if(onNumberSelected != null)
                    onNumberSelected.onPickNumber(dates.get(position));

                selected = (IconCategoryRounded) v;
            }
        });


        return convertView;
    }

    public interface OnNumberSelected{
        void onPickNumber(int number);
    }

    public void setOnNumberSelected(OnNumberSelected onNumberSelected) {
        this.onNumberSelected = onNumberSelected;
    }

    class HolderNumber{
        IconCategoryRounded icr_number;
    }
}
