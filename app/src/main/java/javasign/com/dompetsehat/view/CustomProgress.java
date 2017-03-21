package javasign.com.dompetsehat.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import java.util.Arrays;

import javasign.com.dompetsehat.R;

/*
 * Custom class for shwing progress bar of budget
 */

public class CustomProgress extends TextView {

    private final static int SHAPE_RECTANGLE = 0;
    private final static int SHAPE_ROUNDED_RECTANGLE = 1;
    private final static int DEFAULT_TEXT_MARGIN = 10;

    private ShapeDrawable progressDrawable;
    private TextView textView;
    private int boundWidth = 0;
    private double valueProgress = 0;
    private Double maxWidth = 0.0d;
    private int maxHeight = 0;
    private int progressColor;
    private int progressBackgroundColor;
    private int progressShape = SHAPE_RECTANGLE;
    private Double maxValue = 0.0d;
    private float cornerRadius = 25.0f;
    private boolean showingPercentage = false;
    private int textSize = 14;

    public int textColor;

    private int healthyColor;
    private int warningColor;
    private int redAlerColor;
    private Canvas canvas;

    public String from = "";

    //Constructor

    public CustomProgress(Context context) {
        super(context);
        setDefaultValue();
    }

    public void setColors(int minimColor, int mediumColor, int maxColor){
        healthyColor = minimColor;
        warningColor =  mediumColor;
        redAlerColor = maxColor;
    }

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultValue();
    }

    public CustomProgress(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDefaultValue();
    }

    private void setDefaultValue() {
        // default color

        healthyColor = getResources().getColor(R.color.green_health);
        warningColor = getResources().getColor(R.color.yellow_warning);
        redAlerColor = getResources().getColor(R.color.red_alert);

        //progressColor = healthyColor;
        progressBackgroundColor = getResources().getColor(R.color.grey_400);
    }

    private int getHsvColor(int color){
        float factor = 0.8f;
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    //View Lifecycle

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed) {
            initView();
        }
    }

    public int speed = 5;
    private int maxPercent = 100;



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        progressDrawable.setBounds(0, 0, boundWidth, this.getHeight());
        progressDrawable.draw(canvas);

        if(isShowingPercentage()) {
            this.setText(getCurrentPercentage() + "%");
        }

        if(getCurrentPercentage() <= maxPercent * 0.5) {
            setProgressColor(healthyColor);
        }
        else if(getCurrentPercentage() > maxPercent * 0.5 && getCurrentPercentage() <= maxPercent * 0.7) {
            setProgressColor(warningColor);
        }
        else if(getCurrentPercentage() > maxPercent * 0.7){
            setProgressColor(redAlerColor);
        }

        if(boundWidth < maxWidth) {
            boundWidth+=speed;
            invalidate();
        }


        progressDrawable.getPaint().setColor(getProgressColor());
    }

    public void invalidateDraw(){
        this.boundWidth = 0;
        this.maxWidth = (getCurrentPercentage()/100) * this.getWidth();
        invalidate();
    }

    /**
     * Initialize the view before it will be drawn
     */
    private void initView() {
        Shape progressShapeDrawable = null;
        Shape backgroundProgressShapeDrawable = null;
        switch (progressShape) {
            case SHAPE_RECTANGLE:
                progressShapeDrawable = new RectShape();
                backgroundProgressShapeDrawable = new RectShape();
                break;
            case SHAPE_ROUNDED_RECTANGLE:
                float[] outerRadius = new float[8];
                Arrays.fill(outerRadius, cornerRadius);
                progressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                backgroundProgressShapeDrawable = new RoundRectShape(outerRadius, null, null);
                break;
        }

        //Progress
        progressDrawable = new ShapeDrawable(progressShapeDrawable);
        progressDrawable.getPaint().setColor(getProgressColor());
        progressBackgroundColor = getProgressBackgroundColor();

        if((this.getText().length() > 0) || isShowingPercentage()) {
            progressDrawable.setAlpha(100);
        }

        //Background
        ShapeDrawable backgroundDrawable = new ShapeDrawable(backgroundProgressShapeDrawable);
        backgroundDrawable.getPaint().setColor(progressBackgroundColor);
        backgroundDrawable.setBounds(0, 0, this.getWidth(), this.getHeight());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.setBackground(backgroundDrawable);
        } else {
            this.setBackgroundDrawable(backgroundDrawable);
        }

        this.maxWidth = (getCurrentPercentage()/100) * this.getWidth();

        //Percentage
        if(isShowingPercentage()) {
            this.setTextSize(TypedValue.COMPLEX_UNIT_SP, getBarTextSize());
            this.setTextColor(textColor != 0 ? textColor : Color.WHITE);
            this.setGravity(Gravity.CENTER);
        }
    }

    public void setBarTextSize(int size){
        this.textSize = size;
    }
    private int getBarTextSize(){
        return  this.textSize;
    }

    //Helper

    /**
     * Set the progress color
     * @param color
     */
    public void setProgressColor(int color) {
        this.progressColor = color;
    }

    public int getProgressColor(){
        return progressColor;
    }

    /**
     * Set the background color
     * @param color
     */
    public void setProgressBackgroundColor(int color) {
        this.progressBackgroundColor = color;
    }

    /**
     * Reset the progress to 0
     */
    public void resetWidth() {
        valueProgress = 0;
    }

    public void setProgressValue(double currentAmount){
        this.valueProgress = currentAmount;
    }

    /**
     * Set the maximum percentage for the progress
     * @param amount
     */
    public void setMaxValue(double amount) {
        this.maxValue = amount;
    }

    public double getMaxValue(){
        return maxValue;
    }

    public double getValueProgress(){
        return valueProgress;
    }

    /**
     * Get current percentage based on current valueProgress
     * @return
     */
    public Double getCurrentPercentage() {
        return Math.ceil((valueProgress /(maxValue*1.0f))*100);
    }

    /**
     * Set the shape of custom progress to rectangle
     */
    public void useRectangleShape() {
        this.progressShape = SHAPE_RECTANGLE;
    }

    /**
     * Set the shape of custom progress to rounded rectangle
     * @param cornerRadius radius of the corner
     */
    public void useRoundedRectangleShape(float cornerRadius) {
        this.progressShape = SHAPE_ROUNDED_RECTANGLE;
        this.cornerRadius = cornerRadius;
    }

    /**
     * If this returns true the custom progress
     * will show progress based on getCurrentPercentage()
     * @return true for showing percentage false for not showing anything
     */
    public boolean isShowingPercentage() {
        return showingPercentage;
    }

    /**
     * Set if the custom progress will show percentage or not
     * @param showingPercentage true for showing percentage false for not showing anything
     */
    public void setShowingPercentage(boolean showingPercentage) {
        this.showingPercentage = showingPercentage;
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getProgressBackgroundColor() {
        return progressBackgroundColor;
    }
}