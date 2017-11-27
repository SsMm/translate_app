package com.zgy.translate.activitys;

import android.app.Activity;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zgy.translate.R;
import com.zgy.translate.utils.BluetoothRecorder;

public class BluetoothActivity extends Activity {

    private Button startRecordBut, stopRecordBut, startPlayBut, stopPlayBut;
    private BluetoothRecorder bluetoothRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        startRecordBut = (Button) findViewById(R.id.but_startRecord);
        stopRecordBut = (Button) findViewById(R.id.but_stopRecord);
        startPlayBut = (Button) findViewById(R.id.but_startPlay);
        stopPlayBut = (Button) findViewById(R.id.but_stopPlay);

        bluetoothRecorder = new BluetoothRecorder();

        final AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        startRecordBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothRecorder.startRecording(BluetoothActivity.this, audioManager);
            }
        });

        stopRecordBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothRecorder.stopRecording();
            }
        });


        startPlayBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothRecorder.startPlaying();
            }
        });

        stopPlayBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bluetoothRecorder.stopPlaying();
            }
        });

        /*startRecordBut.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        bluetoothRecorder.startRecording(BluetoothActivity.this, audioManager);
                        break;
                    case MotionEvent.ACTION_UP:
                        bluetoothRecorder.stopRecording();
                        break;
                }
                return true;
            }
        });*/

    }

}
