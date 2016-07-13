package com.faraz.android.cpufreqoverlay;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Random;

public class graph extends AppCompatActivity {

    private final Handler mHandler = new Handler();
    private Runnable mTimer2;
    private LineGraphSeries<DataPoint> mSeries2;
    private double graph2LastXValue = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);



        GraphView graph2 = (GraphView) findViewById(R.id.graph1);
        graph2.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        graph2.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph2.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph2.getGridLabelRenderer().setGridColor(getColor(R.color.colorPrimary));



        mSeries2 = new LineGraphSeries<DataPoint>();
        graph2.getViewport().setXAxisBoundsManual(true);
        graph2.getViewport().setYAxisBoundsManual(true);

        graph2.getViewport().setMinX(0);
        graph2.getViewport().setMaxX(40);
        graph2.getViewport().setMinY(0);
        graph2.getViewport().setMaxY(4);
        graph2.getViewport().setScalable(true);
        graph2.addSeries(mSeries2);




    }


    double mLastRandom = 2;
    Random mRand = new Random();
    private double getRandom() {
         mLastRandom += mRand.nextDouble()*0.5 - 0.25;
        if (mLastRandom<0)
            mLastRandom= mLastRandom*-1;
        return mLastRandom;
    }

    @Override
    public void onResume() {
        super.onResume();


        mTimer2 = new Runnable() {
            @Override
            public void run() {
                graph2LastXValue += 1d;
                mSeries2.appendData(new DataPoint(graph2LastXValue, getRandom()), true, 40);
                mHandler.postDelayed(this, 1000);
            }
        };
        mHandler.postDelayed(mTimer2, 1000);
    }

}

