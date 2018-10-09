package com.example.androidthings.assistant;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * Created by brady on 3/29/2018.
 */

public class NavigationPath extends View {
    //Line and text colors
    private int LineCol;
    private int RoomNumber;
    private int EndCol;
    //paint for drawing custom view
    private Paint LinePaint;
    private Paint CirclePaint;
    private Paint EndCirclePaint;
    float[] Line = new float[]{0,0,0,0, 0,0,0,0};

    private int SignStartx1 = 515;
    private int SignStarty1 = 574;
    private int SignStartx2 = 661;
    private int SignStarty2 = 628;
    private int SignStartx3 = 549;
    private int SignStarty3 = 430;

    private int Endx1;
    private int Endy1;

    private boolean leave = false;





    public NavigationPath(Context context, AttributeSet attrs){
        super(context, attrs);
        LinePaint = new Paint();
        CirclePaint = new Paint();
        EndCirclePaint = new Paint();

        /*mButtonWidgetNav.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
               leave = true;
            }
        });*/
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs,R.styleable.NavigationPath,0,0);

        try {
            LineCol = a.getInteger(R.styleable.NavigationPath_PathColor, 0);//0 is default
            RoomNumber = a.getInteger(R.styleable.NavigationPath_RoomNumber, 0);
            EndCol = a.getInteger(R.styleable.NavigationPath_EndColor, 0);
        } finally {
            a.recycle();
        }
    }
    protected void onDraw(Canvas canvas) {
        LinePaint.setStyle(Paint.Style.FILL);
        LinePaint.setAntiAlias(true);
        LinePaint.setColor(LineCol);
        LinePaint.setStrokeWidth(3);

        CirclePaint.setStyle(Paint.Style.FILL);
        CirclePaint.setAntiAlias(true);
        CirclePaint.setColor(LineCol);

        EndCirclePaint.setStyle(Paint.Style.FILL);
        EndCirclePaint.setAntiAlias(true);
        EndCirclePaint.setColor(EndCol);

        DrawCommand(RoomNumber);
        canvas.drawLines(Line, LinePaint);
        canvas.drawCircle(SignStartx1,SignStarty1,5,CirclePaint);
        canvas.drawCircle(Endx1,Endy1,5,EndCirclePaint);
    }
    public void SetRoomNumber(int x) {
        RoomNumber = x;
        invalidate();
        requestLayout();
        Log.i("Nav","Leaving");
    }

    //Ridiculously inefficient since this would need to be redone for any movement of the sign, and any new buildings.
    private void DrawCommand(int RoomNumber){
        switch(RoomNumber) {
            //Floor 1
            case 100:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,640, 580,640,650,640};
                Endx1 = 650;
                Endy1 = 640;
                //"Commons Area";
                break;
            case 105:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,515, 580,515,550,515};
                Endx1 = 550;
                Endy1 = 515;
                //"Men's Bathroom";
                break;
            case 106:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,490, 580,490,550,490};
                Endx1 = 550;
                Endy1 = 490;
                //"Woman's Bathroom";
                break;
            case 107:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,430, 580,430,530,430};
                Endx1 = 530;
                Endy1 = 430;
                break;
            //"Classroom";
            case 116:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,275, 580,275,610,275};
                Endx1 = 610;
                Endy1 = 275;
                //"Automotive shop";
                break;
            case 117:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,319, 580,319,622,319};
                Endx1 = 622;
                Endy1 = 319;
                //"General Engineering Room";
                break;
            case 119:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,440, 580,440,622,440};
                Endx1 = 622;
                Endy1 = 440;
                //"Civil Lab";
                break;
            case 118:
                Line = new float[]{SignStartx1,SignStarty1,580,574, 580,574,580,469, 580,469,622,469};
                Endx1 = 622;
                Endy1 = 469;
                //"Civil Lab";
                break;
            case 122:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,458,567, 458,567,467,550};
                Endx1 = 467;
                Endy1 = 550;
                //"Collab Room";
                break;
            case 123:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,440,558, 440,558,446,539};
                Endx1 = 446;
                Endy1 = 539;
                //"Collab Room";
                break;
            case 124:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,416,545, 416,545,427,525};
                Endx1 = 427;
                Endy1 = 525;
                //"Collab Room";
                break;
            case 125:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,393,533, 393,533,403,513};
                Endx1 = 403;
                Endy1 = 513;
                //"Collab Room";
                break;
            case 126:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,372,521, 372,521,381,502};
                Endx1 = 381;
                Endy1 = 502;
                //"Collab Room";
                break;
            case 127:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,351,510, 351,510,359,492};
                Endx1 = 359;
                Endy1 = 492;
                //"Collab Room";
                break;
            case 128:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,328,498, 328,498,337,482};
                Endx1 = 337;
                Endy1 = 482;
                //"Collab Room";
                break;
            case 144:
                Line = new float[]{SignStartx1,SignStarty1,439,595, 439,595,426,588};
                Endx1 = 426;
                Endy1 = 588;
                //"Collab Room";
                break;
            case 145:
                Line = new float[]{SignStartx1,SignStarty1,436,610, 436,610,416,601};
                Endx1 = 416;
                Endy1 = 601;
                //"Collab Room";
                break;
            case 146:
                Line = new float[]{SignStartx1,SignStarty1,436,610, 436,610,425,627};
                Endx1 = 425;
                Endy1 = 627;
                //"Collab Room";
                break;
            case 147:
                Line = new float[]{SignStartx1,SignStarty1,461,617, 462,617,450,636};
                Endx1 = 550;
                Endy1 = 636;
                //"Collab Room";
                break;
            case 148:
                Line = new float[]{SignStartx1,SignStarty1,483,627, 483,627,479,646}; //check
                Endx1 = 479;
                Endy1 = 646;
                //"Collab Room";
                break;
            case 129:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,307,487, 308,487,315,470};
                Endx1 = 315;
                Endy1 = 470;
                //"Clinic Clerical Waiting";
                break;
            case 130:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,260,462, 260,462,271,445};
                Endx1 = 271;
                Endy1 = 445;
                //"Clinics Faculty Office";
                break;
            case 131:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,240,451, 240,451,245,433};
                Endx1 = 245;
                Endy1 = 433;
                //"Clinics Faculty Office";
                break;
            case 132:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,217,439, 217,439,226,420};
                Endx1 = 226;
                Endy1 = 420;
                //"Clinics Faculty Office";
                break;
            case 133:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,195,427, 195,427,202,409};
                Endx1 = 202;
                Endy1 = 409;
                //"Clinics Faculty Office";
                break;
            case 134:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,172,415, 172,415,175,395};
                Endx1 = 175;
                Endy1 = 395;
                //"Clinics Chair Office";
                break;
            case 140:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,240,451, 240,451,221,490};
                Endx1 = 221;
                Endy1 = 490;
                //"Project Lab";
                break;
            case 141:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,279,472, 279,472,260,506};
                Endx1 = 260;
                Endy1 = 506;
                //"Project Lab";
                break;
            case 143:
                Line = new float[]{SignStartx1,SignStarty1,471,574, 471,574,393,533, 393,533,375,571};
                Endx1 = 375;
                Endy1 = 571;
                //"Clinics Faculty Office";
                break;

            //Floor 2
            case 205:
                //"Men's Bathroom";
            case 206:
                //"Woman's Bathroom";
            case 207:
                Line = new float[]{SignStartx1,SignStarty1,604,628,604,628,744,628,744,628,744,426,744,426,700,426};
                Endx1 = 700;
                Endy1 = 426;
                //"BME Research Lab";
                break;
            case 208:
                //"BME Research Lab";
            case 209:
                //"Tissue Culture Suite";
            case 210:
                //"Shared Instrument Lab";
            case 215:
                //"Imaging";
            case 217:
                //"Prep Room";
            case 218:
                //"BME Tech";
            case 219:
                //"BME Teaching Lab";
            case 220:
                //"BME Teaching Lab";
            case 221:
                //"BME Teaching Lab";
            case 223:
                //"Collab room";
            case 224:
                //"Collab room";
                //Faculty Offices: Make easy way to implement names and navigate here by name?
            case 225:
                //"Faculty Office";
            case 226:
                //"Faculty Office";
            case 227:
                //"Faculty Office";
            case 228:
                //"Faculty Office";
            case 229:
                //"Faculty Office";
            case 230:
                //"Faculty Office";
            case 231:
                //"Faculty Office";
            case 232:
                //"Faculty Office";
            case 233:
                //"Faculty Office";
            case 234:
                //"Faculty Office";
            case 235:
                //"Faculty Office";
            case 236:
                //"Faculty Office";
            case 237:
                //"Faculty Office";
                //End of Faculty Offices
            case 238:
                //"Clinics Conference Room";
            case 240:
                //"Project Lab";
            case 241:
                //"Project Lab";
            case 243:
                //"Project Lab";

                //Floor 3
            case 305:
                //"Men's Bathroom";
            case 306:
                //"Woman's Bathroom";
            case 307:
                //"ECE Research Lab";
            case 308:
                //"ECE Research Lab";
            case 309:
                //"ECE Research Lab";
            case 312:
                //"Pick-and-Place Room";
            case 317:
                //"Tech Office";
            case 319:
                //"Classroom";
            case 320:
                //"Classroom";
            case 321:
                //"Classroom";
                //Faculty ECE
            case 323:
                //"Faculty Office";
            case 324:
                //"Faculty Office";
            case 325:
                //"Faculty Office";
            case 326:
                //"Faculty Office";
            case 327:
                //"Faculty Office";
            case 328:
                //"Faculty Office";
            case 329:
                //"Faculty Office";
            case 330:
                //"Faculty Office";
            case 331:
                //"Faculty Office";
            case 332:
                //"Faculty Office";
            case 333:
                //"Faculty Office";
            case 334:
                //"Faculty Office";
            case 335:
                //"Faculty Office";
            case 336:
                //"Faculty Office";
            case 337:
                //"Faculty Office";
                //Faculty Offices end
            case 338:
                //"ECE Teaching";
            case 339:
                //"ECE Teaching";
            case 340:
                //"ECE Teaching";
            case 341:
                //"ECE Teaching";
            case 346:
                //"ECE Office";
            case 350:
                //"COE Dean's Office Waiting room";
            case 352:
                //"COE Dean's Office";

        }
    }
}

