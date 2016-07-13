package com.faraz.android.cpufreqoverlay;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.AsyncTask;
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
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by abc on 7/3/1816.
 */
public class StatusService extends Service {
    final static String MY_ACTION = "MY_ACTION";
    private TextView statusText;
    private TextView statusText2;
    private TextView statusText3;
    private TextView statusText4;
    private TextView cpuTemp;
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

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        statusText=new TextView(this);
        statusText2=new TextView(this);
        statusText3=new TextView(this);
        statusText4=new TextView(this);
        cpuTemp=new TextView(this);
        parentLayout = new RelativeLayout(this);

        statusText.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        statusText.setTextColor(getResources().getColor(R.color.black));
        statusText.setId(R.id.cpu1);
        statusText2.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        statusText2.setTextColor(getResources().getColor(R.color.black));
        statusText2.setId(R.id.cpu2);
        statusText3.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        statusText3.setTextColor(getResources().getColor(R.color.black));
        statusText3.setId(R.id.cpu3);
        statusText4.setTypeface(null, Typeface.BOLD_ITALIC);
        statusText4.setTextColor(getResources().getColor(R.color.black));
        statusText4.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        cpuTemp.setTypeface(null, Typeface.BOLD_ITALIC);
        cpuTemp.setTextColor(getResources().getColor(R.color.black));
        cpuTemp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        RelativeLayout.LayoutParams rlp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );


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

        final RelativeLayout.LayoutParams params_cpuTemp=new RelativeLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        params_cpuTemp.addRule(RelativeLayout.ABOVE,statusText.getId());




        params.width = 400;



        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;

        windowManager.addView(parentLayout, params);

        parentLayout.addView(statusText, params_statusText);
        parentLayout.addView(statusText2, params_statusText2);
        parentLayout.addView(statusText3, params_statusText3);
        parentLayout.addView(statusText4, params_statusText4);
        parentLayout.addView(cpuTemp,params_cpuTemp);

        windowManager.updateViewLayout(parentLayout, params);

      handler=new Handler();



        r = new Runnable() {
            public void run() {

                CPUFrequency frequency = new CPUFrequency();
                frequency.execute();
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
                        params.x = initialX - (int) (event.getRawX() - initialTouchX);
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

    class CPUFrequency extends AsyncTask<Void, Void, Void> {
        private String[] args;
        private ProcessBuilder cmd;
        private InputStream in;
        private Process process;
        private byte[] re;
        private String result="";
        String arr[]=new String[6];



        @Override
        protected Void doInBackground(Void... params) {

            for(int i=0;i<4;++i) {
                this.args = new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_cur_freq"};
                result="";
                try {
                    cmd = new ProcessBuilder(this.args);
                    process = this.cmd.start();
                    in = this.process.getInputStream();
                    re = new byte[1024];
                    while (in.read(re) != -1) {
                        this.result += new String(this.re);
                    }
                    arr[i]=result;


                } catch (IOException e) {


                }
            }

            return null;

        }
        @Override
        protected void onPostExecute(Void result) {

            statusText.setText("cpu0 "+arr[0]);
            statusText2.setText("cpu1 "+arr[1]);
            statusText3.setText("cpu2 "+arr[2]);
            statusText4.setText("cpu3 "+arr[3]);

            Intent intent = new Intent();
            intent.setAction(MY_ACTION);

            intent.putExtra("DATAPASSED", arr);

            sendBroadcast(intent);


        }




    }
}

