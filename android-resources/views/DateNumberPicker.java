package javasign.com.dompetsehat.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.GridView;

import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.ui.globaladapters.CustomDateNumberAdapter;

/**
 * Created by Spoonart on 12/23/2015.
 */
public class DateNumberPicker extends DialogFragment {

    private final int MAX_DATE = 30;
    private int mNumber = 0;
    OnNumberPick onNumberPick;
    public String initTitle;
    public Integer initDate;

    public DateNumberPicker(){

    }

    public static DateNumberPicker getInstance(int initDate,String title){
        DateNumberPicker fragment = new DateNumberPicker();
        fragment.initDate = initDate;
        fragment.initTitle = title;
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_date_number_picker, null);
        GridView gv_number = (GridView)rootView.findViewById(R.id.gv_number);

        CustomDateNumberAdapter adapter = new CustomDateNumberAdapter(getActivity(), MAX_DATE);
        adapter.setInitDate(initDate);
        gv_number.setAdapter(adapter);

        adapter.setOnNumberSelected(new CustomDateNumberAdapter.OnNumberSelected() {
            @Override
            public void onPickNumber(int number) {
                mNumber = number;
                //if(onNumberPick != null){
                //    onNumberPick.onPick(mNumber);
                //}
            }
        });

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(mNumber == 0){
                            String title = getActivity().getResources().getString(R.string.pick_the_date_first);
                            if(title != null){
                                title = DateNumberPicker.this.initTitle;
                            }
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(title)
                                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                        }

                        if(onNumberPick != null){
                            onNumberPick.onPick(mNumber);
                        }
                        dialog.dismiss();
                    }
                })
                .setView(rootView).create();

        return dialog;
    }

    public interface OnNumberPick{
        void onPick(int number);
    }

    public void setOnNumberPick(OnNumberPick onNumberPick){
        this.onNumberPick = onNumberPick;
    }
}
