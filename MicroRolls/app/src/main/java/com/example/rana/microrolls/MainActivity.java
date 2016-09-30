package com.example.rana.microrolls;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.content.Context;
import android.widget.Toast;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Arrays;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    ArrayList<float[]> list = new ArrayList<>();
    float X, Y, X1, Y1, delta, LastX, LastY;
    ImageView imageView;
    TouchImageView ViewImg;
    RelativeLayout.LayoutParams params;
    int windowwidth;
    int windowheight;
    static final int NONE = 0;
    static final int Right = 1;
    static final int Left = 2;
    static final int Up = 3;
    static final int Down = 4;
    static final int Track = 5;
    int mode = NONE;
    float scale, dX = 0f, dY = 0f, rawx, rawy;
    Paint pt;

    static final float pi = 3.1415926535f;
    static final float rtd = 57.29577951f;
    static final float sectionNum = 8;
    static final float roundMinAngle = 2*pi * 11/12;

    ArrayList<Vertex> arVertex1; //사용자가 터치한직선
    ArrayList<Vertex> arVertex2; //1차 보간된 선분
    ArrayList<Vertex> arVertex3; //

    Paint mPaint;
    TextView tv;
    static Context c;

    private static final String DEBUG_TAG = "MyActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TouchImageView ViewImg = new TouchImageView(this);
        ViewImg = (TouchImageView) findViewById(R.id.touchimageView);
        assert ViewImg != null;
        ViewImg.setImageResource(R.drawable.img);
        //  ViewImg.setMaxZoom(4f);
        init_variable();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int windowwidth = size.x;
        int windowheight = size.y;


        // setContentView(ViewImg);

        // imageView = (ImageView) findViewById(R.id.imageView);
        // imageView.setImageResource(R.drawable.img);

    }

    public void init_variable(){
       // setBackgroundColor(Color.WHITE);
        arVertex1 = new ArrayList<Vertex>();
        arVertex2 = new ArrayList<Vertex>();
        arVertex3 = new ArrayList<Vertex>();

        mPaint = new Paint();

        pt = new Paint();
        mPaint = new Paint();

       // tv = new TextView(this.getContext());

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //int i;
        int action = MotionEventCompat.getActionMasked(event);
        ViewImg.setScaleType(ImageView.ScaleType.MATRIX);


        switch (action) {
            case (MotionEvent.ACTION_DOWN):

                arVertex1.removeAll(arVertex1);
                arVertex2.removeAll(arVertex2);
                arVertex3.removeAll(arVertex3);
                arVertex1.add(new Vertex(event.getX(), event.getY()));
                return true;
            case (MotionEvent.ACTION_MOVE):
                arVertex1.add(new Vertex(event.getX(), event.getY()));

                return true;


            case (MotionEvent.ACTION_CANCEL):
                Log.d(DEBUG_TAG, "Action was CANCEL");
                return true;

            case (MotionEvent.ACTION_OUTSIDE):
                Log.d(DEBUG_TAG, "Movement occurred outside bounds " +
                        "of current screen element");
                return true;

            case (MotionEvent.ACTION_UP):
                float section = 2*pi / sectionNum;
                float allAngle = 0, allAngle1 = 0, allLength = 0;
                boolean allAngleReset = true;
                arVertex2.add(arVertex1.get(0));
                for(int i=1; i<arVertex1.size(); i+=1)
                {
                    float x2, y2;
                    x2 = arVertex1.get(i).x - arVertex1.get(i-1).x;
                    y2 = arVertex1.get(i-1).y - arVertex1.get(i).y;

                    //각도 구하기
                    float radian = getAngle(x2, y2);
                    //거리 구하기
                    float length = (float)Math.sqrt(Math.pow(x2, 2.f)+Math.pow(y2, 2.f));
                    //각도로 구역구하기
                    float tempang = (radian + (section/2))% (2 * pi);
                    int sec = (int)(tempang / section);

                    arVertex1.get(i).radian = radian;
                    arVertex1.get(i).length = length;
                    arVertex1.get(i).section = sec;

                    //이전 직선과의 각도차
                    if( !allAngleReset )
                    {
                        float AngGap = arVertex1.get(i-1).radian - arVertex1.get(i).radian;
                        if(AngGap > pi)
                        {
                            AngGap -= 2*pi;
                        }
                        else if(AngGap < -pi)
                        {
                            AngGap += 2*pi;
                        }
                        allAngle += AngGap;
                        allAngle1 += AngGap;
                    }
                    else
                    {
                        allAngleReset = false;
                    }

                    allLength += length;
                    Log.v("test", i +"번라인  구간  : "+sec+ "  각도 : " + (int)(radian*rtd) +
                            " 길이합 : " + allLength+ " 각도차합 : " + (int)(allAngle*rtd) + "    " + (int)(allAngle1*rtd));

                    if(allAngle > section*3/2 || allAngle < -section*3/2 )
                    {
                        Log.v("test", i + "번째" +
                                " 변곡점 각도 : "+(int)(allAngle*rtd)+
                                " 총 길이는 " + allLength);

                        allAngleReset = false;
                        allAngle = 0;
                        arVertex2.add(arVertex1.get(i));
                    }
                }
                arVertex2.add(arVertex1.get(arVertex1.size()-1));

                Log.v("test","=========> 총각도 : "+ (int)(allAngle*rtd));

                if(allAngle1 > roundMinAngle)
                {
                    int round = (int) (allAngle1 / (2*pi));
                    if(allAngle1 % (2*pi) > roundMinAngle)
                    {
                        round++;
                    }
                   // Toast.makeText(this.getContext(), "원(반시계방향) "+ round + "바퀴" , Toast.LENGTH_SHORT).show();
                    //counterclockwise
                    ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ViewImg, "scaleX", 0.5f);
                    ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ViewImg, "scaleY", 0.5f);
                    scaleDownX.setDuration(1000);
                    scaleDownY.setDuration(1000);
                    AnimatorSet scaleDown = new AnimatorSet();
                    scaleDown.play(scaleDownX).with(scaleDownY);
                    scaleDown.start();

                }
                else if(-allAngle1 > roundMinAngle)
                {
                    int round = (int)(-allAngle1 / (2*pi));
                    if(-allAngle1 % (2*pi) > roundMinAngle)
                    {
                        round++;
                    }
                    //Toast.makeText(this.getContext(), "원(시계방향) "+ round + "바퀴 " , Toast.LENGTH_SHORT).show();

                    //return false;

                    //clockwise
                    ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ViewImg, "scaleX", 1.5f);
                    ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ViewImg, "scaleY", 1.5f);
                    scaleUpX.setDuration(1000);
                    scaleUpY.setDuration(1000);
                    AnimatorSet scaleUp = new AnimatorSet();
                    scaleUp.play(scaleUpX).with(scaleUpY);
                    scaleUp.start();
                }
                float AllmoveAngle = 0;
                for(int i=1; i<arVertex2.size(); i+=1)
                {
                    float x2, y2;
                    x2 = arVertex2.get(i).x - arVertex2.get(i-1).x;
                    y2 = arVertex2.get(i-1).y - arVertex2.get(i).y;


                    float length = (float)Math.sqrt(Math.pow(x2, 2.f)+Math.pow(y2, 2.f));
                    if( length < (allLength/(arVertex2.size())/2) )
                    {
                        Log.v("test","2단계   ==> 전체 길이 : "+ allLength/(arVertex2.size()) + " 이부분길이 : "+length);
                        continue;
                    }
                    //각도 구하기
                    float radian = getAngle(x2, y2);

                    //첫번째 직선으로 보정
                    radian += AllmoveAngle;
                    //매칭되는 가장 가까운 직선각 구하기 22.5 도 회전
                    float tempang = (radian + (section/2))% (2 * pi);

                    float moveAngle = tempang % section;
                    moveAngle = (moveAngle < (section/2) ? (section/2) - moveAngle : (section/2) - moveAngle);

                    if(i == 1)//첫번째 직선에대해 보정.
                    {
                        AllmoveAngle = moveAngle;
                    }
                    //각도로 구역구하기
                    int sec = (int)(tempang / section);

                    Log.v("test","2단계   ==> "+ i +"번라인   구간  : "+sec+ "  각도 : " + (int)((radian+moveAngle)*rtd)+ "hi");
                    //ours/////
                    if(sec==0){
                        ObjectAnimator animObj=ObjectAnimator.ofFloat(ViewImg,"rotationY",0f,180f);
                        animObj.setDuration(3000);
                        animObj.start();
                    }else if(sec==4){

                        ObjectAnimator animObj=ObjectAnimator.ofFloat(ViewImg,"rotationY",0f,-180f);
                        animObj.setDuration(3000);
                        animObj.start();
                    }else if(sec==2){
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(ViewImg, "scaleX", 1.5f);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(ViewImg, "scaleY", 1.5f);
                        scaleUpX.setDuration(1000);
                        scaleUpY.setDuration(1000);
                        AnimatorSet scaleUp = new AnimatorSet();
                        scaleUp.play(scaleUpX).with(scaleUpY);
                        scaleUp.start();
                    }
                    else if(sec==6){
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(ViewImg, "scaleX", 0.5f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(ViewImg, "scaleY", 0.5f);
                        scaleDownX.setDuration(1000);
                        scaleDownY.setDuration(1000);
                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);
                        scaleDown.start();
                    }


                    //////////////////////////////////777777

                    if(arVertex3.size() > 0)
                    {
                        if( arVertex3.get(arVertex3.size()-1).section == sec )
                        {
                            arVertex3.get(arVertex3.size()-1).length += length;
                            continue;
                        }
                    }
                    Vertex vertex = new Vertex(arVertex2.get(i-1).x, arVertex2.get(i-1).y);
                    vertex.radian = (radian+moveAngle);
                    vertex.length = length;
                    vertex.section = sec;
                    arVertex3.add(vertex);
                }
                //invalidate();
                //텍스트 출력
                String str = "결과 : ";
                for(int i=0; i<arVertex3.size(); i++)
                {
                    str = str + arVertex3.get(i).section;
                    if( i < arVertex3.size()-1 )
                        str = str + " -> ";
                }
              //  Toast.makeText(this.getContext(), str, Toast.LENGTH_SHORT).show();


                Log.v("test", "=================끝===============");
                return true;

               default:
                 return super.onTouchEvent(event);
        }
    }




    //x = 0 직선에 대한 점의 각도를 계산한다
    float getAngle(float x2, float y2) {
        //기준선문
        float x1 = 1.f, y1 = 0.f;
        //0으로 나누는거 방지
        if (x2 == x1) {
            x2 *= 2;
            y2 *= 2;
        }
        float radian = -(float) Math.atan((y2 - y1) / (x2 - x1));

        //180도
        if (x2 < 0 && y2 == 0) {
            radian -= pi;
        }

        //사분면별 각도 조정
        if (y2 < y1 && x2 > x1) {
        } else if ((y2 < y1 && x2 < x1) ||
                (y2 > y1 && x2 < x1)) {
            radian += pi;
        } else {
            radian += 2 * pi;
        }
        return radian;
    }




    class Vertex {
    Vertex(float ax, float ay){
        x = ax;
        y = ay;
    }

    float x;
    float y;
    float radian;
    float length;
    int section;
}
}

