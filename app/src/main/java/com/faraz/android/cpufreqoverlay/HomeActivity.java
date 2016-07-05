package com.faraz.android.cpufreqoverlay;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.RandomAccessFile;


public class HomeActivity extends AppCompatActivity {
    private Button mButton;
    private TextView mTextView;
    int toggle=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        mButton=(Button)findViewById(R.id.stop);
        mTextView=(TextView)findViewById(R.id.freq);
        startService(new Intent(this, StatusService.class));
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toggle == 0) {
                    stopService(new Intent(HomeActivity.this, StatusService.class));
                    toggle = 1;
                } else if (toggle == 1) {
                    startService(new Intent(HomeActivity.this, StatusService.class));
                    toggle = 0;

                }

            }
        });






    }


}
