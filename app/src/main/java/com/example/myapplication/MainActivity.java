package com.example.myapplication;



//
//
// Created by  Michele Dussin 10/09/2019
//
//


import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import io.github.controlwear.virtual.joystick.android.JoystickView;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.channels.SeekableByteChannel;
import java.util.Set;
import java.util.UUID;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener{

    private TextView powerTextView;
    private TextView angleTextView;
    private TextView mBluetoothStatus;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;
    private Button Changeact, Changeact1,spara;
    private Handler mHandler;
    private ConnectedThread mConnectedThread;
    private BluetoothSocket mBTSocket = null;
    private Button share,suono2;
    private TextView currentX, currentY;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float deltaX, deltaY;
    private ImageView carro,giu,su,destra,sinistra;
    private int verifica=0,versu=0,vergiu=0,verdes=0,versin=0,soundver=8,soundver2=8;
    private float a;
    private  int ang=0,str=0;
    private SeekBar barra,barra2;



    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    private final static int RECIEVE_MESSAGE = 1;
    private final static int REQUEST_ENABLE_BT = 1;
    private final static int MESSAGE_READ = 2;
    private final static int CONNECTING_STATUS = 3;

    private void hideNavigationBar(){
        this.getWindow().getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                );
    }

    protected void pulisci(){
        hideNavigationBar();
        Changeact1.setVisibility(View.GONE);
        share.setVisibility(View.GONE);
        mBluetoothStatus.setVisibility(View.GONE);
        mScanBtn.setVisibility(View.GONE);
        mOffBtn.setVisibility(View.GONE);
        mDiscoverBtn.setVisibility(View.GONE);
        mListPairedDevicesBtn.setVisibility(View.GONE);
        Changeact.setVisibility(View.GONE);
        mDevicesListView.setVisibility(View.GONE);
        currentX.setVisibility(View.VISIBLE);
        currentY.setVisibility(View.VISIBLE);
        carro.setVisibility(View.VISIBLE);
        suono2.setVisibility(View.VISIBLE);
        barra2.setVisibility(View.VISIBLE);

    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        hideNavigationBar();
        cazzofunzionatiprego();
        carro = (ImageView)findViewById(R.id.carro);
        barra2=(SeekBar)findViewById(R.id.barra2);
        suono2=(Button)findViewById(R.id.sound2);
        giu = (ImageView)findViewById(R.id.giu);
        su = (ImageView)findViewById(R.id.su);
        destra = (ImageView)findViewById(R.id.destra);
        sinistra = (ImageView)findViewById(R.id.sinistra);
        mBluetoothStatus = (TextView)findViewById(R.id.bluetoothStatus);
        mScanBtn = (Button)findViewById(R.id.scan);
        share = (Button)findViewById(R.id.share);
        mOffBtn = (Button)findViewById(R.id.off);
        Changeact1 = (Button) findViewById(R.id.changea);
        barra = (SeekBar)findViewById(R.id.barra);
        mDiscoverBtn = (Button)findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button)findViewById(R.id.PairedBtn);
        Changeact = (Button) findViewById(R.id.newac);
        mBTArrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        mDevicesListView = (ListView)findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter);
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);





        Changeact1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pulisci();
                verifica=1;
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://esalab.wordpress.com/"));
                startActivity(browserIntent);
            }
        });




        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Fail");
                }
            }
        };

        if (mBTArrayAdapter == null) {

            mBluetoothStatus.setText("Status: Bluetooth not found");
            Toast.makeText(getApplicationContext(),"Bluetooth non trovato",Toast.LENGTH_SHORT).show();
        }
        else {


            Changeact.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    setContentView(R.layout.activity_main);
                    hideNavigationBar();
                    angleTextView = (TextView) findViewById(R.id.sas);
                    powerTextView = (TextView) findViewById(R.id.sas1);
                    barra=(SeekBar)findViewById(R.id.barra);
                    soundver=8;
                    Button suono= findViewById(R.id.sound);

                    suono.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            soundver = barra.getProgress();
                            if(mConnectedThread != null) {
                               mConnectedThread.write("E" + (str+200)+"%"+ (ang+200)+"C"+soundver+((char)10));//manda angolo e velocità
                            }
                        }
                    });

                    JoystickView joystick = (JoystickView) findViewById(R.id.joystickView);

                    joystick.setOnMoveListener(new JoystickView.OnMoveListener() {
                        @Override
                        public void onMove(int angle, int strength) {
                            angleTextView.setText(" " + (angle) + "");//angolo del joystick
                            powerTextView.setText(" " + (strength) + "");//velocita per Robot
                            if(mConnectedThread != null) {
                                mConnectedThread.write("E" + (angle + 200) + "%" + (strength + 200) + "C" +"8"+((char) 10));//manda angolo e velocità
                            }

                        }
                    });




                }
            });//funzione che invia stringhe di velocità e direzione

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });
            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);//richiama la funzione di spegnimento
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);//richiama la funzione dei dispostivi
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);//cerca nuovi dispositivi
                }
            });
        }



    }

    private void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth acceso",Toast.LENGTH_SHORT).show();
            hideNavigationBar();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth è acceso", Toast.LENGTH_SHORT).show();
            hideNavigationBar();
        }
    }//fuonzione per i toast message


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent Data) {

        super.onActivityResult(requestCode, resultCode, Data);
        if (requestCode == REQUEST_ENABLE_BT) {

            if (resultCode == RESULT_OK) {

                mBluetoothStatus.setText("Enabled");
                hideNavigationBar();
            } else
                mBluetoothStatus.setText("Disabled");
                hideNavigationBar();
        }
    }

    private void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Not Connected");
        Toast.makeText(getApplicationContext(),"Bluetooth spento", Toast.LENGTH_SHORT).show();
    }

    private void discover(View view){

        if(mBTAdapter.isDiscovering()){//non funziona non capisco ho sonno
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Fermo",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear();
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Inizia", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth spento", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    private void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {

            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Dispositivi", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth spento", Toast.LENGTH_SHORT).show();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth spento", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Errore", Toast.LENGTH_SHORT).show();
                    }
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            Toast.makeText(getBaseContext(), "Connesione fallita", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                .sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100);
                        bytes = mmInStream.available();
                        bytes = mmInStream.read(buffer, 0, bytes);
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget();
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }


        public void write(String input) {
            byte[] bytes = input.getBytes();
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }


        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
    public void initializeViews() {
        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);

    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();

        // get the change of the x,y,z values of the accelerometer
        deltaX = event.values[0];
        deltaY = event.values[1];


    }

    public void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
    }
    //funzuione per l'accelerometro
    public void displayCurrentValues() {
        a=200;
        if (verifica==1) {
            suono2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    soundver2=barra2.getProgress();
                }
            });
            if (deltaX > -1.3){
                if(deltaX<1.3){
                    if(deltaY>4){
                        a=250;
                    }
                    if(deltaY<-4){
                        a=250;
                    }
                }
            }
            if (deltaX > 1.3){
                if(deltaX<3){
                    a=240;
                }
            }
            if (deltaX > 3){
                if(deltaX<6){
                    a=260;
                }
            }
            if (deltaX > 6){
                if(deltaX<8){
                    a=280;
                }
            }
            if (deltaX > 8){

                    a=300;

            }

            if (deltaX < -1.3){
                if(deltaX>-3){
                    a=240;
                }
            }
            if (deltaX < -3){
                if(deltaX >-6){
                    a=260;
                }
            }
            if (deltaX < -6){
                if(deltaX > -8){
                    a=280;
                }
            }
            if (deltaX < -8){

                a=300;

            }
            if (deltaX < -1.3) {
                giu.setVisibility(View.GONE);
                su.setVisibility(View.VISIBLE);
                versu=1;

            }
            if (deltaX > 1.3) {
                giu.setVisibility(View.VISIBLE);
                su.setVisibility(View.GONE);
                vergiu=1;
            }
            if (deltaY > 1.3) {
                sinistra.setVisibility(View.GONE);
                destra.setVisibility(View.VISIBLE);
                verdes=1;
            }
            if (deltaY < -1.3) {
                sinistra.setVisibility(View.VISIBLE);
                destra.setVisibility(View.GONE);
                versin=1;
            }
            if (deltaX < 1.3) {
                if (deltaX > -1.3) {
                    giu.setVisibility(View.GONE);
                    su.setVisibility(View.GONE);
                    vergiu=0;
                    versu=0;
                }
            }
            if (deltaY < 1.3) {
                if (deltaY > -1.3) {
                    destra.setVisibility(View.GONE);
                    sinistra.setVisibility(View.GONE);
                    verdes=0;
                    versin=0;
                }
            }
            if (versu==1&&verdes==0&&versin==0){
                deltaX=290;
            }
            if (versu==1&&verdes==1&&versin==0){
                deltaX=245;
            }
            if (versu==1&&verdes==0&&versin==1){
                deltaX=335;
            }


            if (vergiu==1&&verdes==0&&versin==0){
                deltaX=470;
            }
            if (vergiu==1&&verdes==1&&versin==0){
                deltaX=520;
            }
            if (vergiu==1&&verdes==0&&versin==1){
                deltaX=420;
            }


            if (versu==0&&verdes==1&&versin==0&&vergiu==0){
                deltaX=200;
            }
            if (versu==0&&verdes==0&&versin==1&&vergiu==0){
                deltaX=380;
            }

            if (versu==0&&verdes==0&&versin==0&&vergiu==0){
                deltaX=290;
            }


            currentX.setText(""+(int)deltaX);
            currentY.setText(""+(int)a);
            if(mConnectedThread != null) {
                mConnectedThread.write("E" + (int) deltaX + "%" + (int) a + "C" + soundver2 + ((char) 10));//manda
                soundver=8;

            }
        }


    }
    public void cazzofunzionatiprego(){
        initializeViews();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fai! we dont have an accelerometer!
        }
    }


}