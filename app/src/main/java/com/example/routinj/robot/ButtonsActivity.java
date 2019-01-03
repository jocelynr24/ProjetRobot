package com.example.routinj.robot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ButtonsActivity extends AppCompatActivity {
    // Déclaration des objets utiles à l'activité
    public TextView mTextDistance;
    public ImageButton mIButUp, mIButDown, mIButRight, mIButLeft;
    public ImageView mImgSensor1, mImgSensor2, mImgSensor3;
    // Déclaration des variables utiles
    public int mBinTrame = 0;
    boolean bRun = true;
    // Déclaration des images utilisées dans l'activité
    int iActive = R.mipmap.ic_active;
    int iInactive = R.mipmap.ic_inactive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buttons);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Déclaration des objets utiles à l'activité
        this.mIButUp = (ImageButton) findViewById(R.id.button_Forward);
        this.mIButDown = (ImageButton) findViewById(R.id.button_Backward);
        this.mIButRight = (ImageButton) findViewById(R.id.button_Right);
        this.mIButLeft = (ImageButton) findViewById(R.id.button_Left);
        this.mTextDistance = (TextView) findViewById(R.id.text_DistanceValue);
        this.mImgSensor1 = (ImageView) findViewById(R.id.image_Sensor1);
        this.mImgSensor2 = (ImageView) findViewById(R.id.image_Sensor2);
        this.mImgSensor3 = (ImageView) findViewById(R.id.image_Sensor3);
        // Gestion de l'interaction avec les boutons
        mIButUp.setOnTouchListener(handleTouch);
        mIButDown.setOnTouchListener(handleTouch);
        mIButRight.setOnTouchListener(handleTouch);
        mIButLeft.setOnTouchListener(handleTouch);
        // Déclaration de deux AsyncTask permettant de rafraîchir l'interface (50ms) et d'envoyer les trames (200ms)
        ScheduledExecutorService scheduleTaskExecutor;
        PeriodicTask MyTask = new PeriodicTask();
        PeriodicTask2 MyTask2 = new PeriodicTask2();
        scheduleTaskExecutor= Executors.newScheduledThreadPool(2);
        scheduleTaskExecutor.scheduleAtFixedRate(MyTask, 0, 200, TimeUnit.MILLISECONDS);
        scheduleTaskExecutor.scheduleAtFixedRate(MyTask2, 0, 50, TimeUnit.MILLISECONDS);
    }

    // Booléen permettant de savoir si l'activité est démarrée en premier plan ou non
    @Override
    public void onStart() {
        super.onStart();
        bRun = true;
    }
    @Override
    public void onStop() {
        super.onStop();
        bRun = false;
    }

    // Gère les clics sur les boutons (sauf les quatre directions)
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_SettingsActivity:
                Intent SettingsActivity_intent = new Intent(ButtonsActivity.this, SettingsActivity.class);
                startActivityForResult(SettingsActivity_intent,0);
                break;
            case R.id.button_CloseActivity:
                finish();
                break;
        }
    }

    // Gère l'interraction avec les boutons de direction
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN: // Ce cas intervient quand on laisse pressé le bouton
                        switch (v.getId()) {
                            case R.id.button_Forward:
                                mBinTrame = 0b00010011;
                                break;
                            case R.id.button_Backward:
                                mBinTrame = 0b00010010;
                                break;
                            case R.id.button_Right:
                                mBinTrame = 0b00010001;
                                break;
                            case R.id.button_Left:
                                mBinTrame = 0b00010100;
                                break;
                        }
                    break;
                case MotionEvent.ACTION_UP: // Ce cas intervient quand on relâche le bouton
                        mBinTrame = 0b00010000;
                    break;
            }
            return false;
        }
    };

    // Gère l'AsyncTask pour l'envoi des trames (200ms)
    class PeriodicTask implements Runnable {
        public void run() {
            runOnUiThread(new TaskUI());
        }
    }
    class TaskUI implements Runnable{
        public void run() {
            if(!MainActivity.bDebug){
                if(bRun) {
                    if (MainActivity.mBluetooth.mbtConnected) {
                        MainActivity.mBluetooth.envoi(Character.toString((char) mBinTrame)+'\0');
                    } else {
                        //Toast toast = Toast.makeText(getApplicationContext(), "Erreur : vous n'êtes pas connecté !", Toast.LENGTH_SHORT);
                        //toast.show();
                        finish();
                    }
                }
            }
        }
    }

    // Gère l'AsyncTask pour la mise à jour de l'interface (50ms)
    class PeriodicTask2 implements Runnable {
        public void run() {
            runOnUiThread(new TaskUI2());
        }
    }
    class TaskUI2 implements Runnable{
        public void run() {
            if(MainActivity.iDist==0){
                mTextDistance.setText("∞");
            }else{
                mTextDistance.setText(Integer.toString(MainActivity.iDist));
            }
            if(MainActivity.iCapt[0]==0){
                mImgSensor1.setImageDrawable(getResources().getDrawable(iInactive));
            }else{
                mImgSensor1.setImageDrawable(getResources().getDrawable(iActive));
            }
            if(MainActivity.iCapt[1]==0){
                mImgSensor2.setImageDrawable(getResources().getDrawable(iInactive));
            }else{
                mImgSensor2.setImageDrawable(getResources().getDrawable(iActive));
            }
            if(MainActivity.iCapt[2]==0){
                mImgSensor3.setImageDrawable(getResources().getDrawable(iInactive));
            }else{
                mImgSensor3.setImageDrawable(getResources().getDrawable(iActive));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}