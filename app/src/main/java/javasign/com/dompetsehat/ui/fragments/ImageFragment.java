package javasign.com.dompetsehat.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import javasign.com.dompetsehat.R;


/**
 * Created by Spoonart on 12/31/2015.
 */
public class ImageFragment extends Fragment {

  private int imageId;
  private int identifier;
  private String text;
  private int color;

  public ImageFragment() {

  }

  public static ImageFragment newInstance(int imageId) {
    ImageFragment imageFragment = new ImageFragment();
    imageFragment.setImageId(imageId);
    return imageFragment;
  }

  public static ImageFragment newInstance(int identifier,int imageId, String text, int color) {
    ImageFragment imageFragment = new ImageFragment();
    imageFragment.setImageId(imageId);
    imageFragment.color = color;
    imageFragment.setIdentifier(identifier);
    imageFragment.setText(text);
    return imageFragment;
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootV = View.inflate(getActivity(), R.layout.layout_content_ads, null);
    ImageView img = (ImageView) rootV.findViewById(R.id.img);

    TextView tv_text = (TextView) rootV.findViewById(R.id.tv_text);
    tv_text.setText(getText());

    Bitmap background = BitmapFactory.decodeResource(getResources(),getImageId());
    img.setImageBitmap(background);
    img.setColorFilter(color);
    return rootV;
  }

  public int getImageId() {
    return imageId;
  }

  public void setImageId(int imageId) {
    this.imageId = imageId;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public int getIdentifier() {
    return identifier;
  }

  public void setIdentifier(int identifier) {
    this.identifier = identifier;
  }
}
