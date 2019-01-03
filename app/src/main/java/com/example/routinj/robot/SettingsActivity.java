package com.example.routinj.robot;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SettingsActivity extends AppCompatActivity {
    // Déclaration des objets utiles à l'activité
    public RadioButton mRdLightOn, mRdLightOff, mRdNeonOn, mRdNeonOff, mRdCameraOn, mRdCameraOff, mRdNeonManual, mRdNeonAuto;
    public CheckBox mCbRed, mCbGreen, mCbBlue;
    public SeekBar mSbSpeed;
    // Déclaration des variables utiles
    public int mBinTrame = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Déclaration des objets utiles à l'activité
        this.mRdLightOn = (RadioButton) findViewById(R.id.radio_LightOn);
        this.mRdLightOff = (RadioButton) findViewById(R.id.radio_LightOff);
        this.mRdNeonOn = (RadioButton) findViewById(R.id.radio_NeonOn);
        this.mRdNeonOff = (RadioButton) findViewById(R.id.radio_NeonOff);
        this.mRdCameraOn = (RadioButton) findViewById(R.id.radio_CameraOn);
        this.mRdCameraOff = (RadioButton) findViewById(R.id.radio_CameraOff);
        this.mRdNeonManual = (RadioButton) findViewById(R.id.radio_NeonManual);
        this.mRdNeonAuto = (RadioButton) findViewById(R.id.radio_NeonAuto);
        this.mCbRed = (CheckBox) findViewById(R.id.check_Red);
        this.mCbGreen = (CheckBox) findViewById(R.id.check_Green);
        this.mCbBlue = (CheckBox) findViewById(R.id.check_Blue);
        this.mSbSpeed = (SeekBar) findViewById(R.id.seek_Speed);
        // Gestion de l'interaction avec la barre coulissante
        mSbSpeed.setOnTouchListener(handleTouch);
        // Déclaration de l'AsyncTask permettant de rafraîchir l'interface (50ms)
        ScheduledExecutorService scheduleTaskExecutor;
        SettingsActivity.PeriodicTask MyTask = new SettingsActivity.PeriodicTask();
        scheduleTaskExecutor= Executors.newScheduledThreadPool(1);
        scheduleTaskExecutor.scheduleAtFixedRate(MyTask, 0, 50, TimeUnit.MILLISECONDS);
    }

    // Au démarrage ou redémarrage de l'application, on coche ou on décoche les paramètres en fonction de la valeur dont il dispose dans les variables du MainActivity
    public void onStart() {
        super.onStart();
        if(MainActivity.bLights){
            mRdLightOn.setChecked(true);
            mRdLightOff.setChecked(false);
        }else{
            mRdLightOn.setChecked(false);
            mRdLightOff.setChecked(true);
        }
        if(MainActivity.bNeons){
            mRdNeonOn.setChecked(true);
            mRdNeonOff.setChecked(false);
        }else{
            mRdNeonOn.setChecked(false);
            mRdNeonOff.setChecked(true);
        }
        if(MainActivity.bCamera){
            mRdCameraOn.setChecked(true);
            mRdCameraOff.setChecked(false);
        }else{
            mRdCameraOn.setChecked(false);
            mRdCameraOff.setChecked(true);
        }
        if(MainActivity.bNeonsMode){
            mRdNeonManual.setChecked(false);
            mRdNeonAuto.setChecked(true);
        }else{
            mRdNeonManual.setChecked(true);
            mRdNeonAuto.setChecked(false);
        }
        if(MainActivity.bNeonsRed){
            mCbRed.setChecked(true);
        }else{
            mCbRed.setChecked(false);
        }
        if(MainActivity.bNeonsGreen){
            mCbGreen.setChecked(true);
        }else{
            mCbGreen.setChecked(false);
        }
        if(MainActivity.bNeonsBlue){
            mCbBlue.setChecked(true);
        }else{
            mCbBlue.setChecked(false);
        }
        mSbSpeed.setProgress(MainActivity.iSpeed);
    }

    // Gère les clics sur les boutons
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.radio_LightOn:
                MainActivity.bLights = true;
                mBinTrame = 0b00100001;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_LightOff:
                MainActivity.bLights = false;
                mBinTrame = 0b00100000;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_NeonOn:
                MainActivity.bNeons = true;
                mBinTrame = 0b00110001;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_NeonOff:
                MainActivity.bNeons = false;
                mBinTrame = 0b00110000;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_CameraOn:
                MainActivity.bCamera = true;
                mBinTrame = 0b01000001;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_CameraOff:
                MainActivity.bCamera = false;
                mBinTrame = 0b01000000;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_NeonManual:
                MainActivity.bNeonsMode = false;
                mBinTrame = 0b00110011;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.radio_NeonAuto:
                MainActivity.bNeonsMode = true;
                mBinTrame = 0b00110010;
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.check_Red:
                if(mCbRed.isChecked()){
                    MainActivity.bNeonsRed = true;
                    mBinTrame = 0b00111100;
                }else{
                    MainActivity.bNeonsRed = false;
                    mBinTrame = 0b00110100;
                }
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.check_Green:
                if(mCbGreen.isChecked()){
                    MainActivity.bNeonsGreen = true;
                    mBinTrame = 0b00111101;
                }else{
                    MainActivity.bNeonsGreen = false;
                    mBinTrame = 0b00110101;
                }
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.check_Blue:
                if(mCbBlue.isChecked()){
                    MainActivity.bNeonsBlue = true;
                    mBinTrame = 0b00111110;
                }else{
                    MainActivity.bNeonsBlue = false;
                    mBinTrame = 0b00110110;
                }
                if(!MainActivity.bDebug) {
                    MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');
                }
                break;
            case R.id.button_Close:
                finish();
                break;
        }
    }

    // Gère l'interraction avec la barre coulissante
    private View.OnTouchListener handleTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            if(v.getId() == R.id.seek_Speed){
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_UP: // Ce cas intervient quand on relâche le bouton
                        MainActivity.iSpeed = mSbSpeed.getProgress();
                        switch(MainActivity.iSpeed){
                            case 0:
                                mBinTrame = 0b01010000;
                                break;
                            case 1:
                                mBinTrame = 0b01010001;
                                break;
                            case 2:
                                mBinTrame = 0b01010010;
                                break;
                            case 3:
                                mBinTrame = 0b01010011;
                                break;
                            case 4:
                                mBinTrame = 0b01010100;
                                break;
                            case 5:
                                mBinTrame = 0b01010101;
                                break;
                            case 6:
                                mBinTrame = 0b01010110;
                                break;
                            case 7:
                                mBinTrame = 0b01010111;
                                break;
                            case 8:
                                mBinTrame = 0b01011000;
                                break;
                            case 9:
                                mBinTrame = 0b01011001;
                                break;
                            case 10:
                                mBinTrame = 0b01011010;
                                break;
                            case 11:
                                mBinTrame = 0b01011011;
                                break;
                            case 12:
                                mBinTrame = 0b01011100;
                                break;
                            case 13:
                                mBinTrame = 0b01011101;
                                break;
                            case 14:
                                mBinTrame = 0b01011110;
                                break;
                            case 15:
                                mBinTrame = 0b01011111;
                                break;
                        }
                        if(!MainActivity.bDebug) {
                            MainActivity.mBluetooth.envoi(Character.toString((char)mBinTrame)+'\0');;
                        }
                        break;
                }
            }
            return false;
        }
    };

    // Gère l'AsyncTask pour la mise à jour de l'interface (50ms)
    class PeriodicTask implements Runnable {
        public void run() {
            runOnUiThread(new SettingsActivity.TaskUI());
        }
    }
    class TaskUI implements Runnable{
        public void run() {
            if((MainActivity.mBluetooth.mbtConnected) || (MainActivity.bDebug)){
                if(mRdNeonOn.isChecked()){
                    mRdNeonManual.setEnabled(true);
                    mRdNeonAuto.setEnabled(true);
                }else{
                    mRdNeonManual.setEnabled(false);
                    mRdNeonAuto.setEnabled(false);
                }
                if((mRdNeonManual.isChecked()) && (mRdNeonOn.isChecked())){
                    mCbRed.setEnabled(true);
                    mCbGreen.setEnabled(true);
                    mCbBlue.setEnabled(true);
                }else{
                    mCbRed.setEnabled(false);
                    mCbGreen.setEnabled(false);
                    mCbBlue.setEnabled(false);
                }
            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "Erreur : vous n'êtes pas connecté !", Toast.LENGTH_SHORT);
                toast.show();
                finish();
            }
        }
    }

}