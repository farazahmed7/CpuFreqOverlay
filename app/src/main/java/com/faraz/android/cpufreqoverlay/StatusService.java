package com.faraz.android.cpufreqoverlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by abc on 7/3/2016.
 */
public class StatusService extends Service {

    private TextView statusText;
    private TextView statusText2;
    private TextView statusText3;
    private TextView statusText4;
    WindowManager windowManager;
    private RelativeLayout parentLayout;
    Handler handler;
    Runnable r;
    final static String status="flag";
    boolean flag;
    Context a;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        flag=intent.getBooleanExtra(status, true);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        statusText=new TextView(this);
        statusText2=new TextView(this);
        statusText3=new TextView(this);
        statusText4=new TextView(this);
        parentLayout = new RelativeLayout(this);

        statusText.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        statusText.setTextColor(getResources().getColor(R.color.black));
        statusText.setId(R.id.cpu1);
        statusText2.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        statusText2.setTextColor(getResources().getColor(R.color.black));
        statusText2.setId(R.id.cpu2);
        statusText3.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        statusText3.setTextColor(getResources().getColor(R.color.black));
        statusText3.setId(R.id.cpu3);
        statusText4.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText4.setTextColor(getResources().getColor(R.color.black));
        statusText4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        parentLayout.setLayoutParams(rlp);


        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        final RelativeLayout.LayoutParams params_statusText = new RelativeLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        params_statusText.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        final RelativeLayout.LayoutParams params_statusText2 = new RelativeLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);

        params_statusText2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params_statusText2.addRule(RelativeLayout.BELOW,statusText.getId());

        final RelativeLayout.LayoutParams params_statusText3=new RelativeLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        params_statusText3.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params_statusText3.addRule(RelativeLayout.BELOW,statusText2.getId());

        final RelativeLayout.LayoutParams params_statusText4=new RelativeLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        params_statusText4.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params_statusText4.addRule(RelativeLayout.BELOW,statusText3.getId());




        params.width = 400;



        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(parentLayout, params);

        parentLayout.addView(statusText, params_statusText);
        parentLayout.addView(statusText2, params_statusText2);
        parentLayout.addView(statusText3, params_statusText3);
        parentLayout.addView(statusText4, params_statusText4);
        windowManager.updateViewLayout(parentLayout, params);

        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();
      handler=new Handler(thread.getLooper());



        r = new Runnable() {
            public void run() {

                    String cpuCore0 = "";
                String cpuCore1 = "";
                String cpuCore2 = "";
                String cpuCore3 = "";


                    try {
                        RandomAccessFile reader = new RandomAccessFile("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq", "r");
                        cpuCore0 = reader.readLine();
                         RandomAccessFile reader2= new RandomAccessFile("/sys/devices/system/cpu/cpu1/cpufreq/scaling_cur_freq", "r");
                         cpuCore1=reader2.readLine();
                         RandomAccessFile reader3=new RandomAccessFile("/sys/devices/system/cpu/cpu2/cpufreq/scaling_cur_freq", "r");
                          cpuCore2=reader3.readLine();
                        RandomAccessFile reader4=new RandomAccessFile("/sys/devices/system/cpu/cpu3/cpufreq/scaling_cur_freq", "r");
                         cpuCore3=reader4.readLine();

                        reader.close();
                         reader2.close();
                         reader3.close();
                         reader4.close();
                    } catch (IOException e) {

                    }
                Handler h=new Handler(Looper.getMainLooper());
                final String finalCpuCore0 = cpuCore0;
                final String finalCpuCore1 = cpuCore1;
                final String finalCpuCore2 = cpuCore2;
                final String finalCpuCore3 = cpuCore3;
                Runnable r0=new Runnable() {
                    @Override
                    public void run() {
                        statusText.setText("cpu0: "+ finalCpuCore0);
                        statusText2.setText("cpu1: "+ finalCpuCore1);
                        statusText3.setText("cpu2: "+finalCpuCore2);
                        statusText4.setText("cpu3: "+finalCpuCore3);



                    }
                };
                h.post(r0);


                handler.postDelayed(this, 1000);


            }
        };

      handler.postDelayed(r, 1000);




        statusText.setOnTouchListener(new View.OnTouchListener() {

            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(parentLayout, params);
                        return true;
                }
                return false;
            }
        });




        return super.onStartCommand(intent, flags, startId);
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public void onCreate() {
        super.onCreate();



    }

    @Override
   public void onDestroy() {
     super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        windowManager.removeView(parentLayout);


    }
}

