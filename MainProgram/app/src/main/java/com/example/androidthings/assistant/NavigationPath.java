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

    private int SignStartx1 = 559;
    private int SignStarty1 = 598;
    private int SignStartx2 = 716;
    private int SignStarty2 = 656;
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
        //TODO: prevent map from drawing if requested room number does not exist

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

        //draw circles at start and end points
        DrawCommand(RoomNumber);
        canvas.drawLines(Line, LinePaint);
        if (RoomNumber < 200) { //1st floor
            if(RoomNumber > 99) canvas.drawCircle(SignStartx1, SignStarty1, 5, CirclePaint);    //ensure room number is also > 99 before drawing circle
        } else if (RoomNumber < 300){   //2nd floor
            canvas.drawCircle(SignStartx2, SignStarty2, 5, CirclePaint);
        }else if (RoomNumber < 400){    //3rd floor
            canvas.drawCircle(SignStartx3, SignStarty3, 5, CirclePaint);
        }
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
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,640, 580,640,650,640};
                Endx1 = 650;
                Endy1 = 640;
                //"Commons Area";
                break;
            case 105:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,515, 580,515,550,515};
                Endx1 = 550;
                Endy1 = 515;
                //"Men's Bathroom";
                break;
            case 106:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,490, 580,490,550,490};
                Endx1 = 550;
                Endy1 = 490;
                //"Woman's Bathroom";
                break;
            case 107:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,430, 580,430,530,430};
                Endx1 = 530;
                Endy1 = 430;
                break;
            //"Classroom";
            case 116:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,275, 580,275,610,275};
                Endx1 = 610;
                Endy1 = 275;
                //"Automotive shop";
                break;
            case 117:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,319, 580,319,622,319};
                Endx1 = 622;
                Endy1 = 319;
                //"General Engineering Room";
                break;
            case 119:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,440, 580,440,622,440};
                Endx1 = 622;
                Endy1 = 440;
                //"Civil Lab";
                break;
            case 118:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,580,574, 580,574,580,469, 580,469,622,469};
                Endx1 = 622;
                Endy1 = 469;
                //"Civil Lab";
                break;
            case 122:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,458,567, 458,567,467,550};
                Endx1 = 467;
                Endy1 = 550;
                //"Collab Room";
                break;
            case 123:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,440,558, 440,558,446,539};
                Endx1 = 446;
                Endy1 = 539;
                //"Collab Room";
                break;
            case 124:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,416,545, 416,545,427,525};
                Endx1 = 427;
                Endy1 = 525;
                //"Collab Room";
                break;
            case 125:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,393,533, 393,533,403,513};
                Endx1 = 403;
                Endy1 = 513;
                //"Collab Room";
                break;
            case 126:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,372,521, 372,521,381,502};
                Endx1 = 381;
                Endy1 = 502;
                //"Collab Room";
                break;
            case 127:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,351,510, 351,510,359,492};
                Endx1 = 359;
                Endy1 = 492;
                //"Collab Room";
                break;
            case 128:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,328,498, 328,498,337,482};
                Endx1 = 337;
                Endy1 = 482;
                //"Collab Room";
                break;
            case 144:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,439,595, 439,595,426,588};
                Endx1 = 426;
                Endy1 = 588;
                //"Collab Room";
                break;
            case 145:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,436,610, 436,610,416,601};
                Endx1 = 416;
                Endy1 = 601;
                //"Collab Room";
                break;
            case 146:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,436,610, 436,610,425,627};
                Endx1 = 425;
                Endy1 = 627;
                //"Collab Room";
                break;
            case 147:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,461,617, 462,617,450,636};
                Endx1 = 550;
                Endy1 = 636;
                //"Collab Room";
                break;
            case 148:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,483,627, 483,627,479,646}; //check
                Endx1 = 479;
                Endy1 = 646;
                //"Collab Room";
                break;
            case 129:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,307,487, 308,487,315,470};
                Endx1 = 315;
                Endy1 = 470;
                //"Clinic Clerical Waiting";
                break;
            case 130:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,260,462, 260,462,271,445};
                Endx1 = 271;
                Endy1 = 445;
                //"Clinics Faculty Office";
                break;
            case 131:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574,471,574,240,451, 240,451,245,433};
                Endx1 = 245;
                Endy1 = 433;
                //"Clinics Faculty Office";
                break;
            case 132:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,217,439, 217,439,226,420};
                Endx1 = 226;
                Endy1 = 420;
                //"Clinics Faculty Office";
                break;
            case 133:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,195,427, 195,427,202,409};
                Endx1 = 202;
                Endy1 = 409;
                //"Clinics Faculty Office";
                break;
            case 134:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,172,415, 172,415,175,395};
                Endx1 = 175;
                Endy1 = 395;
                //"Clinics Chair Office";
                break;
            case 140:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,240,451, 240,451,221,490};
                Endx1 = 221;
                Endy1 = 490;
                //"Project Lab";
                break;
            case 141:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,279,472, 279,472,260,506};
                Endx1 = 260;
                Endy1 = 506;
                //"Project Lab";
                break;
            case 143:
                Line = new float[]{SignStartx1,SignStarty1,559,574,559,574,471,574, 471,574,393,533, 393,533,375,571};
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
                Line = new float[]{SignStartx2,SignStarty2,716,632,716,632,744,632,744,632,744,426,744,426,700,426};
                Endx1 = 700;
                Endy1 = 426;
                //"BME Research Lab";
                break;
            case 208:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,329,745,329,712,329,712,329};
                Endx1 = 712;
                Endy1 = 329;
                break;
                //"BME Research Lab";
            case 209:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,237,745,237,712,237};
                Endx1 = 712;
                Endy1 = 237;
                break;
                //"Tissue Culture Suite";
            case 210:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,196,745,196,755,196};
                Endx1 = 755;
                Endy1 = 196;
                //"Shared Instrument Lab";
                break;
            case 211:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,263,745,263,755,263};
                Endx1 = 755;
                Endy1 = 263;
                break;
            case 212:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,238,745,238,814,238};
                Endx1 = 814;
                Endy1 = 238;
                //"?";
                break;
            case 213:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,174,745,174,755,174};
                Endx1 = 755;
                Endy1 = 174;
                //"Imaging";
                break;
            case 215:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,198,745,198,755,198};
                Endx1 = 755;
                Endy1 = 198;
                //"Imaging";
                break;
            case 217:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,331,745,331,755,331};
                Endx1 = 755;
                Endy1 = 331;
                //"Prep Room";
                break;
            case 218:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,295,745,295,755,295};
                Endx1 = 755;
                Endy1 = 295;
                //"BME Tech";
                break;
            case 219:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,405,745,405,755,405};
                Endx1 = 755;
                Endy1 = 405;
                //"BME Teaching Lab";
                break;
            case 220:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,486,745,486,755,486};
                Endx1 = 755;
                Endy1 = 486;
                //"BME Teaching Lab";
                break;
            case 221:
                Line = new float[]{SignStartx2,SignStarty2,716,630,716,630,745,630,745,630,745,604,745,604,755,604};
                Endx1 = 755;
                Endy1 = 604;
                //"BME Teaching Lab";
                break;
            case 223:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,585,617,585,617,595,603};
                Endx1 = 595;
                Endy1 = 603;
                //"Collab room";
                break;
            case 224:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,555,604,555,604,566,585};
                Endx1 = 566;
                Endy1 = 585;
                //"Collab room";
                //Faculty Offices: Make easy way to implement names and navigate here by name?
                break;
            case 225:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,526,590,526,590,540,573};
                Endx1 = 540;
                Endy1 = 573;
                //"Faculty Office";
                break;
            case 226:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,503,574,503,574,513,555};
                Endx1 = 513;
                Endy1 = 555;
                //"Faculty Office";
                break;
            case 227:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,471,559,471,559,484,540};
                Endx1 = 484;
                Endy1 = 540;
                //"Faculty Office";
                break;
            case 228:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,446,542,446,542,456,524};
                Endx1 = 456;
                Endy1 = 524;
                //"Faculty Office";
                break;
            case 229:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,416,528,416,528,427,512};
                Endx1 = 427;
                Endy1 = 512;
                //"Faculty Office";
                break;
            case 230:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,387,512,387,512,395,497};
                Endx1 = 395;
                Endy1 = 497;
                //"Faculty Office";
                break;
            case 231:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,360,500,360,500,371,428};
                Endx1 = 371;
                Endy1 = 428;
                //"Faculty Office";
                break;
            case 232:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,333,482,333,482,343,467};
                Endx1 = 343;
                Endy1 = 467;
                //"Faculty Office";
                break;
            case 233:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,303,468,303,468,312,452};
                Endx1 = 312;
                Endy1 = 452;
                //"Faculty Office";
                break;
            case 234:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,277,452,277,452,285,440};
                Endx1 = 285;
                Endy1 = 440;                //"Faculty Office";
                break;
            case 235:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,249,436,249,436,259,422};
                Endx1 = 259;
                Endy1 = 422;
                //"Faculty Office";
                break;
            case 236:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,222,422,222,422,230,409};
                Endx1 = 230;
                Endy1 = 409;
                //"Faculty Office";
                break;
            case 237:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,193,408,193,408,202,392};
                Endx1 = 202;
                Endy1 = 392;
                //"Faculty Office";
                //End of Faculty Offices
                break;
            case 238:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,220,423,220,423,210,440};
                Endx1 = 210;
                Endy1 = 440;
                //"Clinics Conference Room";
                break;
            case 240:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,290,462,290,462,271,492};
                Endx1 = 271;
                Endy1 = 492;
                //"Project Lab";
                break;
            case 241:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,374,511,374,511,357,537};
                Endx1 = 357;
                Endy1 = 537;
                //"Project Lab";
                break;
            case 243:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,611,629,611,629,476,568,476,568,462,590};
                Endx1 = 462;
                Endy1 = 590;
                //"Project Lab";
                break;
            case 248:
                Line = new float[]{SignStartx2,SignStarty2,716,629,716,629,642,629,642,629,642,721,642,721,612,721};
                Endx1 = 612;
                Endy1 = 721;
                break;
                //Offices?

                //Floor 3
            case 305:
                //"Men's Bathroom";
            case 306:
                //"Woman's Bathroom";
            case 307:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,307,608,307,587,307};
                Endx1 = 587;
                Endy1 = 307;
                //"ECE Research Lab";
                break;
            case 308:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,217,608,217,582,217};
                Endx1 = 582;
                Endy1 = 217;
                //"ECE Research Lab";
                break;
            case 309:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,128,608,128,583,128};
                Endx1 = 583;
                Endy1 = 128;
                //"ECE Research Lab";
                break;
            case 310:
                Line = new float[]{SignStartx3,SignStarty3,606,430,606,430,606,121,606,121,625,121};
                Endx1 = 625;
                Endy1 = 121;
                break;
            case 312:

                //"Pick-and-Place Room";
                break;
            case 313:
                Line = new float[]{SignStartx3,SignStarty3,606,430,606,430,606,162,606,162,621,162};
                Endx1 = 621;
                Endy1 = 162;
                break;
            case 314:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,185,608,185,663,185};
                Endx1 = 663;
                Endy1 = 185;
                break;
            case 315:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,221,608,221,662,221};
                Endx1 = 662;
                Endy1 = 221;
                break;
            case 316:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,207,608,207,624,207};
                Endx1 = 624;
                Endy1 = 207;
                break;
            case 317:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,234,608,234,627,234};
                Endx1 = 627;
                Endy1 = 234;
                //"Tech Office";
                break;
            case 319:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,305,608,305,636,305};
                Endx1 = 636;
                Endy1 = 305;
                //"Classroom";
                break;
            case 320:
                Line = new float[]{SignStartx3,SignStarty3,608,430,608,430,608,366,608,366,633,366};
                Endx1 = 633;
                Endy1 = 366;
                //"Classroom";
                break;
            case 321:
                Line = new float[]{SignStartx3,SignStarty3,635,430};
                Endx1 = 635;
                Endy1 = 430;
                //"Classroom";
                //Faculty ECE
                break;
            case 323:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,499,424,499,424,503,411};
                Endx1 = 503;
                Endy1 = 411;
                //"Faculty Office";
                break;
            case 324:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,478,413,478,413,485,402};
                Endx1 = 485;
                Endy1 = 402;
                //"Faculty Office";
                break;
            case 325:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,460,403,460,403,466,329};
                Endx1 = 466;
                Endy1 = 329;
                //"Faculty Office";\
                break;
            case 326:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,440,392,440,392,447,382};
                Endx1 = 447;
                Endy1 = 382;
                //"Faculty Office";
                break;
            case 327:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,404,373,404,373,410,363};
                Endx1 = 410;
                Endy1 = 363;
                //"Faculty Office";
                break;
            case 328:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,402,373,402,373,408,362};
                Endx1 = 408;
                Endy1 = 362;
                //"Faculty Office";
                break;
            case 329:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,384,634,384,634,391,353};
                Endx1 = 391;
                Endy1 = 353;
                //"Faculty Office";
                break;
            case 330:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,363,352,363,352,373,342};
                Endx1 = 373;
                Endy1 = 342;
                //"Faculty Office";
                break;
            case 331:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,344,345,344,345,351,332};
                Endx1 = 351;
                Endy1 = 332;
                //"Faculty Office";
                break;
            case 332:
                //"Faculty Office";
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,325,336,325,336,335,322};
                Endx1 = 335;
                Endy1 = 322;
                break;
            case 333:
                //"Faculty Office";
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,305,325,305,325,313,311};
                Endx1 = 313;
                Endy1 = 311;
                break;
            case 334:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,286,314,286,314,394,301};
                Endx1 = 394;
                Endy1 = 301;
                //"Faculty Office";
                break;
            case 335:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,368,303,368,303,376,292};
                Endx1 = 376;
                Endy1 = 292;
                //"Faculty Office";
                break;
            case 336:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,248,294,248,294,256,282};
                Endx1 = 256;
                Endy1 = 282;
                //"Faculty Office";
                break;
            case 337:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,230,281,230,281,234,270};
                Endx1 = 234;
                Endy1 = 270;
                //"Faculty Office";
                //Faculty Offices end
                break;
            case 338:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,262,307,262,307,254,321};
                Endx1 = 254;
                Endy1 = 321;
                //"ECE Teaching";
                break;
            case 339:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,324,335,324,335,314,357};
                Endx1 = 314;
                Endy1 = 357;
                //"ECE Teaching";
                break;
            case 340:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,383,366,383,366,371,388};
                Endx1 = 371;
                Endy1 = 388;
                //"ECE Teaching";
                break;
            case 341:
                Line = new float[]{SignStartx3,SignStarty3,512,430,512,430,438,393,438,393,425,418};
                Endx1 = 425;
                Endy1 = 418;
                //"ECE Teaching";
                break;
            case 343:
                Line = new float[]{SignStartx3,SignStarty3,521,430,521,430,511,447};
                Endx1 = 511;
                Endy1 = 447;
                //"ECE Office";
                break;
            case 346:
                Line = new float[]{SignStartx3,SignStarty3,533,430,533,430,533,494,533,494,521,494};
                Endx1 = 521;
                Endy1 = 494;
                //"ECE Conference Room";
                break;
            case 348:
                Line = new float[]{SignStartx3,SignStarty3,533,430,533,430,533,553,533,553,550,553};
                Endx1 = 550;
                Endy1 = 553;
                break;
            case 350:
                Line = new float[]{SignStartx3,SignStarty3,533,430,533,430,533,623,533,623,549,623};
                Endx1 = 549;
                Endy1 = 623;
                //"COE Dean's Office Waiting room";
                break;
            case 351:
                Line = new float[]{SignStartx3,SignStarty3,533,430,533,430,533,665,533,665,556,665};
                Endx1 = 556;
                Endy1 = 665;
                break;
            case 352:
                Line = new float[]{SignStartx3,SignStarty3,533,430,533,430,533,700,533,700,550,700};
                Endx1 = 550;
                Endy1 = 700;
                //"COE Dean's Office";
                break;

        }
    }
}

