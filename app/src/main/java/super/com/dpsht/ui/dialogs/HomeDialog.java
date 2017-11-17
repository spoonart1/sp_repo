package javasign.com.dompetsehat.ui.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.IntegerRes;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import javasign.com.dompetsehat.R;

/**
 * Created by lafran on 9/9/16.
 */

public class HomeDialog extends DialogFragment {

  public interface HomeDialogDelegate{
    void onNext(int identifier);
  }

  @Bind(R.id.iv_image) ImageView iv_image;
  @Bind(R.id.tv_desc) TextView tv_desc;
  @Bind(R.id.title) TextView tv_title;
  @Bind(R.id.cancel) TextView cancel;
  @Bind(R.id.ok) TextView ok;

  private int imageRes;
  private String title;
  private String description;
  private Class<?> classTarget;
  private int identifier;
  private String tagFragmentTarget;
  private HomeDialogDelegate delegate;

  public HomeDialog(){

  }

  public void setDelegate(HomeDialogDelegate delegate){
    this.delegate = delegate;
  }

  public static HomeDialog newInstance(int imageRes, String title, String description, Class<?> classTarget){
    HomeDialog h = new HomeDialog();
    h.init(imageRes, title, description, classTarget);
    return h;
  }

  protected void init(@IntegerRes int imageRes, String title, String description, Class<?> classTarget){
    this.imageRes = imageRes;
    this.title = title;
    this.description = description;
    this.classTarget = classTarget;
  }

  public void setIdentifier(int identifier){
    this.identifier = identifier;
  }

  public void setTagFragmentTarget(String tagFragmentTarget) {
    this.tagFragmentTarget = tagFragmentTarget;
  }

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    View view = View.inflate(getContext(), R.layout.dialog_home, null);
    ButterKnife.bind(this, view);

    AlertDialog d = new AlertDialog.Builder(getContext())
        .setView(view)
        .create();

    d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    lp.copyFrom(d.getWindow().getAttributes());
    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
    lp.height = WindowManager.LayoutParams.MATCH_PARENT;

    iv_image.setImageResource(imageRes);
    tv_title.setText(title);
    tv_desc.setText(description);

    d.getWindow().setAttributes(lp);

    return d;
  }

  @OnClick(R.id.cancel) void cancel(){
    dismiss();
  }

  @OnClick(R.id.ok) void onNext(){
    if(delegate == null) return;

    delegate.onNext(identifier);
    navigateTo(tagFragmentTarget);
  }

  protected void navigateTo(String tagFragmentTarget){
    Intent i = new Intent(getContext(), classTarget);
    i.putExtra("dummy",tagFragmentTarget);
    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(i);
    getActivity().finish();
  }
}
