package com.faraz.android.cpufreqoverlay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;


public class HomeActivity extends AppCompatActivity {
    MyReceiver myReceiver;
    private static final String[] cpu_temp_paths = new String[]{"/sys/devices/system/cpu/cpu0/cpufreq/cpu_temp", "/sys/devices/system/cpu/cpu0/cpufreq/FakeShmoo_cpu_temp", "/sys/class/thermal/thermal_zone1/temp", "/sys/class/i2c-adapter/i2c-4/4-004c/temperature", "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/temperature", "/sys/devices/platform/omap/omap_temp_sensor.0/temperature", "/sys/devices/platform/tegra_tmon/temp1_input", "/sys/kernel/debug/tegra_thermal/temp_tj", "/sys/devices/platform/s5p-tmu/temperature", "/sys/class/thermal/thermal_zone0/temp", "/sys/devices/virtual/thermal/thermal_zone0/temp", "/sys/class/hwmon/hwmon0/device/temp1_input", "/sys/devices/virtual/thermal/thermal_zone1/temp", "/sys/devices/platform/s5p-tmu/curr_temp", "/sys/htc/cpu_temp", "/sys/devices/platform/tegra-i2c.3/i2c-4/4-004c/ext_temperature", "/sys/devices/platform/tegra-tsensor/tsensor_temperature"};
    private int NUMBER_OF_CORES;
    TextView tx1,tx2,tx3,tx4;
    Button startServiceButton,graphButton;
    private Runnable mTimer2;
    private double graph1LastXValue = 0d;
    private LineGraphSeries<DataPoint> mSeries[]=new LineGraphSeries[4];
    private double graph1LastXValue2 = 0d;
    GraphView graphs[]=new GraphView[4];

    Timer timer;
    TimerTask timerTask;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
   tx1=(TextView)findViewById(R.id.textView1);
        tx2=(TextView)findViewById(R.id.textView2);
        tx3=(TextView)findViewById(R.id.textView3);
        tx4=(TextView)findViewById(R.id.textView4);
        startServiceButton=(Button)findViewById(R.id.start);
        graphButton=(Button)findViewById(R.id.graphAct);

        //graphs refrences
        graphs[0] = (GraphView) findViewById(R.id.graph1);
        graphs[1] = (GraphView) findViewById(R.id.g2);
        graphs[2] = (GraphView) findViewById(R.id.graph3);
        graphs[3] = (GraphView) findViewById(R.id.graph4);



        //hraph 1



        InputStream in;
        byte[] re;
        String maxFreq = "";
        String minFreq="";
  //getting max frequency
        try {
            in = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq"}).start().getInputStream();
            re = new byte[1024];
            while (in.read(re) != -1) {
                maxFreq= maxFreq + new String(re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //getting minimum frequency

        try {
            in = new ProcessBuilder(new String[]{"/system/bin/cat", "/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_min_freq"}).start().getInputStream();
            re = new byte[1024];
            while (in.read(re) != -1) {
                minFreq += new String(re);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        int i;
        for(i=0;i<4;++i)
        {
            graphs[i].getGridLabelRenderer().setHorizontalLabelsVisible(false);
            graphs[i].getGridLabelRenderer().setVerticalLabelsVisible(false);
            graphs[i].getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
            graphs[i].getGridLabelRenderer().setGridColor(getColor(R.color.colorPrimary));


            mSeries[i] = new LineGraphSeries<DataPoint>();
            graphs[i].getViewport().setXAxisBoundsManual(true);
            graphs[i].getViewport().setYAxisBoundsManual(true);

            graphs[i].getViewport().setMinX(0);
            graphs[i].getViewport().setMaxX(40);
            graphs[i].getViewport().setMinY(0);
            graphs[i].getViewport().setMaxY(Float.parseFloat(maxFreq) / 100);
            graphs[i].getViewport().setMinY(Float.parseFloat(minFreq) / 100);
            graphs[i].getViewport().setScalable(true);
            mSeries[i].setDrawBackground(true);
            mSeries[i].setBackgroundColor(getColor(R.color.graph));
            graphs[i].setTitle("core "+i);
            graphs[i].addSeries(mSeries[i]);
        }



        HandlerThread handlerThread=new HandlerThread("f");
        handlerThread.start();
        final Handler mHandler=new Handler(handlerThread.getLooper());
     final  Runnable r=new Runnable() {
           @Override
           public void run() {
                {
                   CPUFrequency cpuFrequency = new CPUFrequency();
                   cpuFrequency.execute();

               }
           }

       };
        timerTask = new TimerTask() {
            public void run() {
                mHandler.post(r);
            }
        };

        timer=new Timer();
        timer.schedule(timerTask, 0, 1000);





        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

           stopService(new Intent(HomeActivity.this,
                   StatusService.class));
            }
        });

      //  myReceiver = new MyReceiver();
    //    IntentFilter intentFilter = new IntentFilter();
  //      intentFilter.addAction(StatusService.MY_ACTION);
//        registerReceiver(myReceiver, intentFilter);

        //Start our own service






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

          tx1.setText("cpu0 "+arr[0]);
           tx2.setText("cpu1 "+arr[1]);
           tx3.setText("cpu2 "+arr[2]);
           tx4.setText("cpu3 " + arr[3]);

           graph1LastXValue += 3d;

           mSeries[0].appendData(new DataPoint(graph1LastXValue, Double.parseDouble(arr[0]) / 100), true, 40);

           if(arr[1].length()>0) {
               mSeries[1].appendData(new DataPoint(graph1LastXValue, Double.parseDouble(arr[1]) / 100), true, 40);
               graphs[1].getGridLabelRenderer().setHorizontalAxisTitle("");
           }
           else
               graphs[1].getGridLabelRenderer().setHorizontalAxisTitle("Offline");

           if(arr[2].length()>0) {
               mSeries[2].appendData(new DataPoint(graph1LastXValue, Double.parseDouble(arr[2]) / 100), true, 40);
           }

           if(arr[3].length()>0) {
               mSeries[3].appendData(new DataPoint(graph1LastXValue, Double.parseDouble(arr[3]) / 100), true, 40);
           }





       }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub

            String[] arr = arg1.getStringArrayExtra("DATAPASSED");
            tx1.setText("cpu0 "+arr[0]);
            tx2.setText("cpu1 "+arr[1]);
            tx3.setText("cpu2 "+arr[2]);
            tx4.setText("cpu3 "+arr[3]);


        }

    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
  //      if(myReceiver!=null)
//        unregisterReceiver(myReceiver);
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }


}