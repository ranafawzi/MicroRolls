package com.example.rana.microrolls;

import android.content.Context;
import android.graphics.Matrix;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by rana on 6/23/2016.
 */
public class TouchImageView extends ImageView {


    ArrayList<float[]> list = new ArrayList<>();
    float X,Y,X1,Y1,delta;
    ImageView imageView;
    TouchImageView ViewImg;
    RelativeLayout.LayoutParams params;
    Matrix matrix = new Matrix();
    Matrix savedMatrix = new Matrix();

    private static final String DEBUG_TAG ="MyActivity" ;

    float maxScale = 3f;

    private View v;
    private MotionEvent event;

    public TouchImageView(Context context) {
        super(context);
       //sharedConstructing(context);
    }

    public TouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
       // sharedConstructing(context);
    }
    public void setMaxZoom(float x) {

        maxScale = x;

    }
   /** @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        Log.d(DEBUG_TAG, String.valueOf(action));

        return super.onTouchEvent(event);
    }**/

}
