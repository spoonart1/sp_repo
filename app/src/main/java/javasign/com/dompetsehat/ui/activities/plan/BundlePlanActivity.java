package javasign.com.dompetsehat.ui.activities.plan;

import android.os.Bundle;
import android.support.annotation.Nullable;
import javasign.com.dompetsehat.base.BaseActivity;
import javasign.com.dompetsehat.base.MyCustomApplication;
import javasign.com.dompetsehat.presenter.plan.DanaInterface;
import javasign.com.dompetsehat.ui.event.AddPlanEvent;
import javasign.com.dompetsehat.utils.RxBus;
import rx.Observer;

/**
 * Created by lafran on 9/22/16.
 */

public class BundlePlanActivity extends BaseActivity implements DanaInterface {

  private RxBus rxBus = MyCustomApplication.getRxBus();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    rxBus.toObserverable().ofType(AddPlanEvent.class).subscribe(new Observer<AddPlanEvent>() {
      @Override public void onCompleted() {

      }

      @Override public void onError(Throwable e) {

      }

      @Override public void onNext(AddPlanEvent addPlanEvent) {
        if(addPlanEvent.eventCode == AddPlanEvent.ADD_PLANT_EVENT_SUCCESS){
          finish();
        }
      }
    });
  }

  @Override public void setUmur(int umur) {

  }

  @Override public void setDanaDisiapkan(double dana) {

  }

  @Override public void setProsentase(double prosentase) {

  }

  @Override public void setCicilanPerbulan(double cicilanPerbulan) {

  }

  @Override public void setPendapatanBulanan(double pendapatanBulanan) {

  }

  @Override public void setDibayarLunas(double dibayarLunas) {

  }

  @Override public void setCicilanPertahun(double cicilanPertahun) {

  }

  @Override public void onLoad(int RequestID) {

  }

  @Override public void onComplete(int RequestID) {

  }

  @Override public void onError(int RequestID) {

  }

  @Override public void onNext(int RequestID) {

  }

  @Override protected void onDestroy() {
    super.onDestroy();
  }
}
