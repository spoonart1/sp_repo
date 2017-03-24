package javasign.com.dompetsehat.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ClipDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import javasign.com.dompetsehat.R;

/**
 * Created by Spoonart on 9/2/2015.
 */
public class RoundProgress extends RelativeLayout {
    private FrameLayout progressDrawableImageView;
    private FrameLayout trackDrawableImageView;
    private RelativeLayout relativePadding;
    private float max = 100;
    private int left=2,right=2,top=2,bottom=2;
    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
    public void setPadding(int left, int right, int top, int bottom){
        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
    }

    public RoundProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.round_progress, this);
        setup(context, attrs);
    }

    protected void setup(Context context, AttributeSet attrs)
    {
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.RoundProgress);

        final String xmlns= "http://schemas.android.com/apk/res-auto";
        int bgResource = attrs.getAttributeResourceValue(xmlns,
                "progressDrawable", 0);

        relativePadding = (RelativeLayout)findViewById(R.id.relative_padding);
        relativePadding.setPadding(left,top,right,bottom);

        progressDrawableImageView = (FrameLayout) findViewById(
                R.id.progress_drawable_image_view);
        progressDrawableImageView.setBackgroundResource(bgResource);

        int trackResource = attrs.getAttributeResourceValue(xmlns, "trackerDrawable", 0);
        trackDrawableImageView = (FrameLayout) findViewById(R.id.track_image_view);

        trackDrawableImageView.setBackgroundResource(trackResource);

        int progress = attrs.getAttributeIntValue(xmlns, "progress", 0);
        setProgress(progress);

        int max = attrs.getAttributeIntValue(xmlns, "max", 100);
        setMax(max);

        a.recycle();

        /*ProgressBarOutline outline = new ProgressBarOutline(context);
        addView(outline);*/
    }

    public void setProgress(float value)
    {
        ClipDrawable drawable = (ClipDrawable)
                progressDrawableImageView.getBackground();
        double percent = (double) value/ (double)max;
        int level = (int)Math.floor(percent*10000);
        drawable.setLevel(level);
    }
}
