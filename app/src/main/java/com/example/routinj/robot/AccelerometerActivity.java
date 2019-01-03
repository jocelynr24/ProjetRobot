package com.example.routinj.robot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import junit.runner.BaseTestRunner;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AccelerometerActivity extends AppCompatActivity {
    // Déclaration des objets utiles à l'activité
    public TextView mTextDistance;
    public ImageView mImgSensor1, mImgSensor2, mImgSensor3;
    public ImageView mImgDir;
    // Déclaration des variables utiles
    public int mBinTrame = 0;
    float[] accelerometerVector=new float[2];
    boolean bRun = true;
    private int iImg = 0;
    // Déclaration des images utilisées dans l'activité
    int iActive = R.mipmap.ic_active;
    int iInactive = R.mipmap.ic_inactive;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Déclaration des objets utiles à l'activité
        this.mTextDistance = (TextView) findViewById(R.id.text_DistanceValue);
        this.mImgSensor1 = (ImageView) findViewById(R.id.image_Sensor1);
        this.mImgSensor2 = (ImageView) findViewById(R.id.image_Sensor2);
        this.mImgSensor3 = (ImageView) findViewById(R.id.image_Sensor3);
        this.mImgDir = (ImageView) findViewById(R.id.image_Dir);
        // Déclaration du capteur accéléromètre
        SensorManager mSensorManager;
        Sensor mAccelerometer;
        mSensorManager = (SensorManager)getSystemService(MainActivity.SENSOR_SERVICE);
        mAccelerometer=mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(mSensorListener, mAccelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        accelerometerVector[0]=0;
        accelerometerVector[1]=0;
        // Déclaration de deux AsyncTask permettant de rafraîchir l'interface (50ms) et d'envoyer les trames (200ms)
        ScheduledExecutorService scheduleTaskExecutor;
        PeriodicTask MyTask = new PeriodicTask();
        PeriodicTask2 MyTask2 = new PeriodicTask2();
        scheduleTaskExecutor = Executors.newScheduledThreadPool(2);
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

    // Gère les clics sur les boutons
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_SettingsActivity:
                Intent SettingsActivity_intent = new Intent(AccelerometerActivity.this, SettingsActivity.class);
                startActivityForResult(SettingsActivity_intent,0);
                break;
            case R.id.button_Close:
                finish();
                break;
        }
    }

    // Détermine les valeurs de l'accéléromètre lorsque celui-ci est utilisé
    private final SensorEventListener mSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent se) {
            if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                accelerometerVector=se.values;
            }
        }
        public void onAccuracyChanged(Sensor sensor, int accuracy) {} // not used
    };

    // Gère l'AsyncTask pour l'envoi des trames (200ms)
    class PeriodicTask implements Runnable {
        public void run() {
            if (bRun) {
                if (accelerometerVector[0] > 3) {
                    mBinTrame = 0b00010010; //reculer
                    iImg = R.drawable.ic_down;
                } else if (accelerometerVector[0] < -3) {
                    mBinTrame = 0b00010011; //avancer
                    iImg = R.drawable.ic_up;
                } else if (accelerometerVector[1] > 3) {
                    mBinTrame = 0b00010001; //droite
                    iImg = R.drawable.ic_right;
                } else if (accelerometerVector[1] < -3) {
                    mBinTrame = 0b00010100; //gauche
                    iImg = R.drawable.ic_left;
                } else {
                    mBinTrame = 0b00010000; //stop
                    iImg = R.drawable.ic_stop;
                }
                if (MainActivity.mBluetooth.mbtConnected) {
                    if (!MainActivity.bDebug) {
                        MainActivity.mBluetooth.envoi(Character.toString((char) mBinTrame)+'\0');
                    }
                } else {
                    //Toast toast = Toast.makeText(getApplicationContext(), "Erreur : vous n'êtes pas connecté !", Toast.LENGTH_SHORT);
                    //toast.show();
                    finish();
                }
                runOnUiThread(new TaskUI());
            }
        }
    }
    class TaskUI implements Runnable{
        public void run() {
            mImgDir.setImageDrawable(getResources().getDrawable(iImg));
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