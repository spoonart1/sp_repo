package javasign.com.dompetsehat.utils;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Fernando on 8/7/2015.
 * Very flexible formatter for showing value in chart
 */
public class CustomValueFormatter implements ValueFormatter {

    public CustomValueFormatter(){

    }

    @Override
    public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
        return "";
    }
}
