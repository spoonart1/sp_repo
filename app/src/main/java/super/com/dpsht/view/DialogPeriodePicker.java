package javasign.com.dompetsehat.view;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import javasign.com.dompetsehat.R;

/**
 * Created by Spoonart on 12/17/2015.
 */
public class DialogPeriodePicker extends DialogFragment {

    OnPeriodePicked onPeriodePicked;
    String value;
    String[] periodes;

    public DialogPeriodePicker(){

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_periode_picker, null);
        periodes = getActivity().getResources().getStringArray(R.array.periode_times);
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(rootView).create();

        NumberPicker periode_picker = (NumberPicker)rootView.findViewById(R.id.periode_picker);
        Button btn_done = (Button)rootView.findViewById(R.id.btn_done);

        periode_picker.setMinValue(0);
        periode_picker.setMaxValue(periodes.length-1);
        periode_picker.setDisplayedValues(periodes);

        periode_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                value = periodes[newVal];
                if(onPeriodePicked != null)
                    onPeriodePicked.onPick("");
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(onPeriodePicked != null)
                    onPeriodePicked.onPick(value);

                getDialog().dismiss();
            }
        });


        return dialog;
    }

    public interface OnPeriodePicked{
        void onPick(String periode);
    }

    public void setOnPeriodePickedListener(OnPeriodePicked onPeriodePicked){
        this.onPeriodePicked = onPeriodePicked;
    }
}
