package javasign.com.dompetsehat.utils;

import android.content.Context;
import android.widget.ImageView;
import com.squareup.picasso.Picasso;
import javasign.com.dompetsehat.R;
import javasign.com.dompetsehat.base.MyCustomApplication;
import timber.log.Timber;

/**
 * Created by avesina on 3/1/17.
 */

public class AdsUtils {
  public static void adsKoinWorks(Context context,ImageView imageView,String res){
    String url = "https://s3-ap-southeast-1.amazonaws.com/dompetsehat/ads/profile-bottom";
    String[] imgs = new String[]{"1.gif","2.gif","3.gif","4.gif","5.gif"};
    String _url = url+"/"+res.toLowerCase()+"/"+imgs[Helper.randInt(0,4)];
    Picasso.with(context).load(_url).fit().centerInside().placeholder(R.drawable.banner_koinworks).into(imageView);
  }
}
