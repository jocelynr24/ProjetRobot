package com.example.routinj.robot;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/*
    to use of BT needs to add :
    <uses-permission android:name="android.permission.BLUETOOTH"/>
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
    in AndroidManifest.xml
    The phone must :
    -> enable bluetooth
    -> pair the phone and the robot with the phone : code 1234
 */

public class BlueT {
    public static final int DEMANDE_AUTH_ACT_BT = 1;
    public static final int N_DEMANDE_AUTH_ACT_BT = 0;
    private static final String TAG = "BTT";

    BluetoothAdapter mbtAdapt; //BT adapter of the phone
    Activity mActivity; //main activity who instantiate blueT -> association
    boolean mbtActif = false;	//state of the association

    private Set<BluetoothDevice> mDevices; //liste of mDevices
    private BluetoothDevice[]mPairedDevices;// table of known devices

    int mDeviceSelected = -1; //the device choosen by the phone
    String[] mstrDeviceName;
    int miBlc = 0;				//used by connection
    boolean mbtConnected = false;

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");  // dummy UUID
    private BluetoothSocket mSocket;
    private OutputStream mOutStream;	//mSocket for communication
    private InputStream mInStream;		//mSocket for communication

    public Handler mHandler;
    private Thread mThreadReception =null;	//thread that receives data from device
    private String mstrData = "";
    private String mstrDataTemp = "";
    private String mstrDataFinal = "";

    public String mstrRecu = " ";

    byte mbBuffer[] = new byte[200]; // large buffer !
    int iPos=0;
    int iAntiSlash;

    public BlueT(Activity Activity)
    {
        this.mActivity = Activity;
        this.Verif();
    }
    public BlueT(Activity Activity, Handler Handler)
    {
        this.mActivity = Activity;
        this.mHandler = Handler;
        this.Verif();
        mThreadReception = new Thread(new Runnable() { //create Thread for reception
            @Override
            public void run() {

                while(true)
                {
                    //Log.i(TAG, "etat="+mbtActif);
                    if(mbtAdapt != null)
                    {   //Log.i(TAG, "etat="+mbtAdapt);
                        if(mbtAdapt.isEnabled())
                        {
                            mbtActif = true;
                            //Log.i(TAG, "etat="+mbtActif);
                        }
                        else
                        {
                            mbtActif = false;
                            //Log.i(TAG, "etat="+mbtActif);
                        }
                    }

                    if(mbtConnected == true) // reception of data when connected
                    {

                        mstrRecu = reception();
                        if (!mstrRecu.equals("")) { // if there is something -> send message to the handler of the activity
                            Message msg = mHandler.obtainMessage();
                            msg.obj = mstrRecu;
                            mHandler.sendMessage(msg);
                            //Log.i("Recu", mstrRecu);
                        }
                        //else
                        //Log.i("mstrRecu", "vide");
                    }
                    try {
                        Thread.sleep(20, 0);
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                        //Log.i("IT", "mstrRecu");
                    }
                }
            }
        });
        mThreadReception.start(); //start thread
    }

    public void Verif() // Verification of BT adapter
    {
        mbtAdapt = BluetoothAdapter.getDefaultAdapter(); // recover BT informations on adapter
        if(mbtAdapt == null) {
            Log.i(TAG, "Not presentt");
        }
        else {
            Log.i(TAG, "Present");
        }
    }

    public void connexion() // connection to device
    {
        this.Device_Connu(); //recover informations for each connected devices
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(mActivity);//pop up off knoxn devices
        //        adBuilder.setTitle("device");
        //miDeviceDelected = mDeviceSelected;
        adBuilder.setSingleChoiceItems(mstrDeviceName, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mDeviceSelected = which;
                dialog.dismiss();
                tryconnect(); //connection to the chosen device
            }
        });

        AlertDialog adb = adBuilder.create();
        adb.show();
    }

    public void Device_Connu() // recover all known devices
    {
        this.mDevices = mbtAdapt.getBondedDevices(); //recover the devices in a tab
        this.miBlc = mDevices.size(); // number of known devices
        this.mstrDeviceName = new String[this.miBlc]; //table will be given to pop up menu
        this.miBlc = 0;
        for(BluetoothDevice dev : this.mDevices) {
            this.mstrDeviceName[this.miBlc] = dev.getName();
            this.miBlc = this.miBlc + 1;
        }
        this.mPairedDevices = (BluetoothDevice[]) this.mDevices.toArray(new BluetoothDevice[this.mDevices.size()]); //cast of set in array.
    }

    public void tryconnect()
    {
        try {
            this.mSocket =this.mPairedDevices[this.mDeviceSelected].createRfcommSocketToServiceRecord(MY_UUID); //connection to vhchoosen device via Socket, mUUID: id of BT on device of the target
            this.mSocket.connect();
            Toast.makeText(this.mActivity, "Connected", Toast.LENGTH_SHORT).show();
            this.mbtConnected = true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.mActivity, "Try again", Toast.LENGTH_SHORT).show();
            try {
                mSocket.close();
            }
            catch(Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public Boolean envoi(String strOrdre) // false -> error; true -> ok
    {
        try	{
            this.mOutStream = this.mSocket.getOutputStream(); //open output stream

            byte[] trame = strOrdre.getBytes();

            this.mOutStream.write(trame); //send frame via output stream
            this.mOutStream.flush();
            Log.i(TAG, "Send");
        }
        catch(Exception e2) {
            Log.i(TAG, "Error");
            tryconnect();
            try {
                this.mSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.mbtConnected = false;
        }
        return this.mbtConnected;
    }

    private String reception()
    {
        int iNbLu;
        mstrData = new String("");
        try {
            this.mInStream = this.mSocket.getInputStream();// input stream

            if(this.mInStream.available() > 0 ) {
                // inBLu = number of characters
                // the following part has to be improved
                iNbLu=mInStream.read(mbBuffer,iPos,20); // be aware -> a complete frame is not received
                this.mstrData = new String(mbBuffer,0,iNbLu); //create a string using byte received
                mstrDataTemp = mstrDataTemp + mstrData;
                iAntiSlash = mstrDataTemp.indexOf('\0');
                /*if(iAntiSlash == -1){
                    mstrDataFinal ="test";
                }
                */
                while (iAntiSlash != -1){
                    mstrDataFinal = mstrDataTemp.substring(0,iAntiSlash+1);
                    mstrDataTemp = mstrDataTemp.substring(iAntiSlash + 1);
                    iAntiSlash = mstrDataTemp.indexOf('\0');
                }
            }
        }
        catch (Exception e) {
            Log.i(TAG, "Error");
            try {
                mSocket.close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            this.mbtConnected = false;
        }
        return mstrDataFinal;
    }
}