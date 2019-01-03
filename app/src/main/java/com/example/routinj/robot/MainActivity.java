package com.example.routinj.robot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    // Déclaration des objets utiles à l'activité
    public Button mButClose, mButConnect, mButButtonsActivity, mButAccelerometerActivity, mButSettingsActivity = null;
    public TextView mTVStep1, mTVStep2, mTVStep1Desc, mTVStep2Desc;
    // Déclaration de l'objet Bluetooth
    public static BlueT mBluetooth;
    // Déclaration des variables paramètres utiles à l'application
    public static boolean bDebug = false; // Utile pour tester l'interface sans connexion
    public static boolean bLights = false;
    public static boolean bNeons = false;
    public static boolean bCamera = false;
    public static boolean bNeonsMode = false;
    public static boolean bNeonsRed = false;
    public static boolean bNeonsGreen = false;
    public static boolean bNeonsBlue = false;
    public static int iSpeed = 0;
    // Déclaration des variables utiles pour la lecture des capteurs
    public static int iDist = 0;
    public static int[] iCapt = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Déclaration des objets utiles à l'activité
        this.mBluetooth = new BlueT(this, mHandler);
        this.mButConnect = (Button) findViewById(R.id.button_BlueTConnection);
        this.mButConnect.setOnClickListener(this);
        this.mButClose = (Button) findViewById(R.id.button_Close);
        this.mButButtonsActivity = (Button) findViewById(R.id.button_ButtonsActivity);
        this.mButAccelerometerActivity = (Button) findViewById(R.id.button_AccelerometerActivity);
        this.mButSettingsActivity = (Button) findViewById(R.id.button_SettingsActivity);
        this.mTVStep1 = (TextView) findViewById(R.id.text_step1);
        this.mTVStep1Desc = (TextView) findViewById(R.id.text_Step1Desc);
        this.mTVStep2 = (TextView) findViewById(R.id.text_Step2);
        this.mTVStep2Desc = (TextView) findViewById(R.id.text_Step2Desc);
        // Déclaration de l'AsyncTask permettant de rafraîchir l'interface (50ms)
        ScheduledExecutorService scheduleTaskExecutor;
        PeriodicTask MyTask = new PeriodicTask();
        scheduleTaskExecutor= Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(MyTask, 0, 50, TimeUnit.MILLISECONDS);
    }

    // Handler permettant de lire les informations des capteurs
    static public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            String myString=(String) msg.obj;
            char[] cFrame = myString.toCharArray();
            // Capteurs de proximité
            iCapt[0] = Character.getNumericValue(cFrame[0]);
            iCapt[1] = Character.getNumericValue(cFrame[1]);
            iCapt[2] = Character.getNumericValue(cFrame[2]);
            // Capteur de distance
            iDist = Character.getNumericValue(cFrame[3])*100 + Character.getNumericValue(cFrame[4])*10 + Character.getNumericValue(cFrame[5]);
        }
    };

    // Gère les clics sur les boutons
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_BlueTConnection:
                if(!bDebug){
                    this.mBluetooth.connexion();
                }
                break;
            case R.id.button_ButtonsActivity:
                Intent ButtonActivity_intent = new Intent(MainActivity.this, ButtonsActivity.class);
                startActivityForResult(ButtonActivity_intent,0);
                break;
            case R.id.button_AccelerometerActivity:
                Intent AccelerometerActivity_intent = new Intent(MainActivity.this, AccelerometerActivity.class);
                startActivityForResult(AccelerometerActivity_intent,0);
                break;
            case R.id.button_SettingsActivity:
                Intent SettingsActivity_intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivityForResult(SettingsActivity_intent,0);
                break;
            case R.id.button_Close:
                finish();
                break;
        }
    }

    // Gère l'AsyncTask pour la mise à jour de l'interface (50ms)
    class PeriodicTask implements Runnable {
        public void run() {
            runOnUiThread(new TaskUI());
        }
    }
    class TaskUI implements Runnable{
        public void run() {
            if((MainActivity.mBluetooth.mbtConnected) || (bDebug)){
                mButConnect.setEnabled(false);
                mButButtonsActivity.setEnabled(true);
                mButAccelerometerActivity.setEnabled(true);
                mTVStep2.setEnabled(true);
                mTVStep2Desc.setEnabled(true);
                mButSettingsActivity.setEnabled(true);
            }else{
                mButConnect.setEnabled(true);
                mButButtonsActivity.setEnabled(false);
                mButAccelerometerActivity.setEnabled(false);
                mTVStep2.setEnabled(false);
                mTVStep2Desc.setEnabled(false);
                mButSettingsActivity.setEnabled(false);
            }
        }
    }

}