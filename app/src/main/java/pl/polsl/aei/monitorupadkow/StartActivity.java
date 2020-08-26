package pl.polsl.aei.monitorupadkow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputEditText;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class StartActivity extends AppCompatActivity {

    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> bluetoothDevices;
    private BluetoothDevice miband;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattCharacteristic gattCharacteristicSensContr;
    private BluetoothGattCharacteristic gattCharacteristicSensMeasure;
    private BluetoothGattCharacteristic gattCharacteristicHRC;
    private BluetoothGattCharacteristic gattCharacteristicHRM;
    private BluetoothGattCharacteristic characteristicAuthorization;
    private BluetoothGattCharacteristic characteristicAuthorizationB;
    private BluetoothGattCharacteristic gattCharacteristicNotifications;
    private BluetoothGattDescriptor gattDescriptor0051;
    private BluetoothGattDescriptor gattDescriptor005b;
    private BluetoothGattDescriptor gattDescriptorHRM;
    private BluetoothGattDescriptor gattDescriptorSensorContr;
    private BluetoothGattDescriptor gattDescriptorSensorMeasure;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int NOTIFICATIONS_REQUESTED_b = 32;
    private static final int NOTIFICATIONS_REQUESTED_0 = 31;
    private static final int CHARACTERISTIC_READ = 30;
    private static final int KEY_SENT = 4;
    private static final int KEY_REQUESTED = 5;
    private static final int KEY_ENCRYPTED = 6;
    private static final int SENSOR_DATA_REQUESTED = 71;
    private static final int SENSOR_CONTROL_REQUESTED = 72;
    private static final int SENSOR_CONTROL_REQUESTED2 = 81;
    private static final int HRD_REQUESTED = 91;
    private static final int HRC_REQUESTED = 92;

    private static final String CHARACTERISTIC_HEART_RATE_MEASURE = "00002a37-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_HEART_RATE_CONTROL = "00002a39-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_SENSOR_CONTROL = "00000001-0000-3512-2118-0009af100700";
    private static final String CHARACTERISTIC_SENSOR_MEASURE = "00000002-0000-3512-2118-0009af100700";
    private static final String CHARACTERISTIC_NOTIFICATION = "00002a46-0000-1000-8000-00805f9b34fb";
    private static final String CHARACTERISTIC_AUTHORIZATION_B = "00000009-0000-3512-2118-0009af100700";
    private static final String CHARACTERISTIC_AUTHORIZATION = "00000020-0000-3512-2118-0009af100700";
    private static final String NOTIFICATION_DESCRIPTOR_2902 = "00002902-0000-1000-8000-00805f9b34fb";


    private static final int queueCapacity = 300; //over 6 seconds of mi band use
    // all times are in miliseconds
    private static final int frameWidth = 6000;
    private static final int shadeLatency = frameWidth/2;
    private static final int sensorRefreshTime = 35000;

    public final static String ACTION_GATT_CONNECTED =
            "pl.polsl.aei.monitorupadkow.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "pl.polsl.aei.monitorupadkow.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "pl.polsl.aei.monitorupadkow.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "pl.polsl.aei.monitorupadkow.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "pl.polsl.aei.monitorupadkow.EXTRA_DATA";


    byte[] encryptionKey = "Kj6dUM1y3kBAPBey".getBytes();//new byte[]{(byte)0xf0, (byte)0xac, (byte)0xa3, (byte)0xb6, (byte)0xcd, 0x0e, (byte)0xc5, (byte)0x85, 0x37, 0x12, (byte)0x8f, 0x48, 0x4f, 0x68, 0x7b, (byte)0xb5};
    String key = "Kj6dUM1y3kBAPBey"; //8VoK4rpNZjZ04oh4

    private LineChart chartView;
    private Storage storage;
    private ChooseActivity.MeasurementMode measurementMode;

    private boolean sensorsOn = false;
    private boolean experiment = false;     //if it is experiment getting data to teach neuron network

    private TextInputEditText filenameTextInput;
    private String filename;

    private SensorManager sensorManager;
    private Sensor sensor;

    private static NotificationManager notificationManager;
    private static boolean fallDetected = false;
    private int fallNumber = 0;

    final Handler h = new Handler();
    SmsManager smsmanager;
    LocationManager locationManager;
    private FusedLocationProviderClient fusedLocationClient;

    SensorEventListener accelerometerSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            // More code goes here
            storage.writePhoneAccelerometer(new double[]{sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]});
            if (measurementMode == ChooseActivity.MeasurementMode.PHONE && sensorsOn){
                //draw mi band chart
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addEntry(sensorEvent.values[0], 1);
                        addEntry(sensorEvent.values[1], 2);
                        addEntry(sensorEvent.values[2], 3);
                    }
                });
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {
            System.out.println("onAccChanged: " + sensor.getName() + ", i: " + i);
        }
    };



    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    String intentAction;
                    bluetoothGatt = gatt;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        gatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                    }
                    gatt.discoverServices();
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    List<BluetoothGattService> services = gatt.getServices();
                    for (BluetoothGattService service : services) {
                        for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_AUTHORIZATION)){ //authorization
                                characteristicAuthorization = characteristic;
                                bluetoothGatt.setCharacteristicNotification(characteristicAuthorization, true);
                                gattDescriptor0051 = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902)); //0x2902
                                bluetoothGatt.readCharacteristic(characteristicAuthorization);
                                connectionState = CHARACTERISTIC_READ;
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_AUTHORIZATION_B)){ //authorization
                                characteristicAuthorizationB = characteristic;
                                bluetoothGatt.setCharacteristicNotification(characteristicAuthorizationB, true);
                                gattDescriptor005b = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902)); //0x2902
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_NOTIFICATION)){
                                gattCharacteristicNotifications = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicNotifications, true);
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL)){
                                gattCharacteristicHRC = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicHRC, true);
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_MEASURE)){
                                gattCharacteristicHRM = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicHRM, true);
                                gattDescriptorHRM = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902));
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_CONTROL)){
                                gattCharacteristicSensContr = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicSensContr, true);
                                gattDescriptorSensorContr = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_MEASURE)){
                                gattCharacteristicSensMeasure = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicSensMeasure, true);
                                gattDescriptorSensorMeasure = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                            }
                        }
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION) && connectionState == CHARACTERISTIC_READ){
                        gattDescriptor0051.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptor0051))
                            connectionState = NOTIFICATIONS_REQUESTED_0;
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                {
                    if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[]{0x01, 0x03, 0x19})) {
                        gattDescriptorSensorContr.setValue(new byte[]{0x00, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorSensorContr))
                            connectionState = SENSOR_CONTROL_REQUESTED2;
                    } else if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[]{0x15, 0x01, 0x01})){
                        gattCharacteristicSensContr.setValue(new byte[]{0x02});//0x00, 0x02});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicSensContr);
                    }

                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt,
                                              BluetoothGattDescriptor descriptor,
                                              int status){
                    if (descriptor.getUuid().toString().contentEquals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == NOTIFICATIONS_REQUESTED_0) {
                        gattDescriptor005b.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptor005b))
                            connectionState = NOTIFICATIONS_REQUESTED_b;

                    } else if (descriptor.getUuid().toString().contentEquals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == NOTIFICATIONS_REQUESTED_b) {
                        try {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x01, 0x00});
                            output.write(encryptionKey);

                            byte[] out = output.toByteArray();
                            characteristicAuthorizationB.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                                connectionState = KEY_SENT;
                        } catch(IOException e){
                            System.out.println("Nie udało się stworzyć tablicy bajtów");
                        }
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_CONTROL_REQUESTED2){
                        gattDescriptorHRM.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorHRM))
                            connectionState = HRD_REQUESTED;
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_DATA_REQUESTED){
                        gattDescriptorSensorContr.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorSensorContr))
                            connectionState = SENSOR_CONTROL_REQUESTED;
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_CONTROL_REQUESTED){
                        gattCharacteristicSensContr.setValue(new byte[]{0x01, 0x03, 0x19});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicSensContr);
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == HRD_REQUESTED){
                        gattCharacteristicHRC.setValue(new byte[]{0x15, 0x01, 0x01});
                        if (bluetoothGatt.writeCharacteristic(gattCharacteristicHRC))
                            connectionState = HRC_REQUESTED;
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_SENT){
                        characteristicAuthorizationB.setValue(new byte[] {0x02, 0x00});
                        if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                            connectionState = KEY_REQUESTED;
                    } else if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_REQUESTED){
                        byte[] response = Arrays.copyOfRange(characteristic.getValue(), 3, 19);

                        try {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x03, 0x00});
                            output.write(encrypt(response));

                            byte[] out = output.toByteArray();
                            characteristicAuthorizationB.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                                connectionState = KEY_ENCRYPTED;
                        } catch (IOException e){
                            System.out.println(e.toString());
                        }
                    } else if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_ENCRYPTED) {
                        if (Arrays.equals(characteristic.getValue(), new byte[]{0x10, 0x03, 0x01}))
                            System.out.println("ZAUTORYZOWANO");
                    } else {
                        if (characteristic.getValue()[0] == 1 && sensorsOn && measurementMode != ChooseActivity.MeasurementMode.PHONE) {
                            parseAccelerationData(characteristic.getValue());
                        }
                    }
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt,
                                             BluetoothGattDescriptor descriptor,
                                             int status){
                }
            };


    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                //String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null && device.getName().contentEquals("Mi Band 3")) {
                    miband = device;
                    bluetoothGatt = miband.connectGatt(context, true, gattCallback);
                    Toast.makeText(context, "ŁĄCZENIE", Toast.LENGTH_SHORT).show();
                    bluetoothGatt.requestMtu(33);
                }
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createNotificationChannel();
        smsmanager = SmsManager.getDefault();

        Intent intent = getIntent();
        measurementMode = ChooseActivity.MeasurementMode.values()[intent.getIntExtra("MeasurementMode", 0)];

        storage = new Storage(getApplicationContext(), queueCapacity);

        setContentView(R.layout.activity_start);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this, "NIE MA BLUETOOTH", Toast.LENGTH_SHORT).show();
        } else if (btAdapter.isEnabled()){
            Toast.makeText(this, "BLUETOOTH JEST WŁĄCZONE", Toast.LENGTH_SHORT).show();
        } else if (!btAdapter.isEnabled()) {
            Toast.makeText(this, "BLUETOOTH JEST WYŁĄCZONE", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        chartView = findViewById(R.id.chartView);
        setupChart();
        setupAxes();
        setupData();
        setupData();
        setupData();
        setLegend();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(receiver, filter);

        if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("This app needs location access");
            builder.setMessage("Please grant location access so this app can detect peripherals.");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                }
            });
            builder.show();
        }

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "FALL_CHANNEL";
            String description = "CANAL_USED_AFTER_FALL";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("FALL_CHANNEL_ID", name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.enableLights(true);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setupChart() {
        // disable description text
        chartView.getDescription().setEnabled(false);
        // enable touch gestures
        chartView.setTouchEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        chartView.setPinchZoom(true);
        // enable scaling
        chartView.setScaleEnabled(true);
        chartView.setDrawGridBackground(false);
    }

    private void setupAxes() {
        XAxis xl = chartView.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = chartView.getAxisLeft();
        leftAxis.setTextColor(Color.BLUE);
        if (measurementMode != ChooseActivity.MeasurementMode.PHONE) {
            leftAxis.setAxisMaximum(1100);
            leftAxis.setAxisMinimum(-1100);
        } else {
            leftAxis.setAxisMaximum(50);
            leftAxis.setAxisMinimum(-50);
        }
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = chartView.getAxisRight();
        rightAxis.setEnabled(false);
    }

    private void setupData() {
        LineData data = new LineData();
        data.setValueTextColor(Color.BLACK);

        // add empty data
        chartView.setData(data);
    }

    private void setLegend() {
        // get the legend (only possible after setting data)
        Legend l = chartView.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setTextColor(Color.BLACK);
    }

    private LineDataSet createSet(int axis) {
        LineDataSet set;
        if (axis == 1) {
            set = new LineDataSet(null, "X axis");
            set.setColors(Color.GREEN);
        } else if (axis == 2){
            set = new LineDataSet(null, "Y axis");
            set.setColors(Color.RED);
        } else {
            set = new LineDataSet(null, "Z axis");
            set.setColors(Color.BLUE);
        }
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setCircleColor(Color.BLACK);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setValueTextColor(Color.BLACK);
        set.setValueTextSize(10f);
        // To show values of each point
        set.setDrawValues(true);

        return set;
    }

    private void addEntry(float value, int axis) {
        LineData data = chartView.getData();

        if (data != null) {
            ILineDataSet set;
            if(axis == 1)
                set = data.getDataSetByIndex(0);
            else if (axis == 2)
                set = data.getDataSetByIndex(1);
            else //(axis == 3)
                set = data.getDataSetByIndex(2);

            if (set == null) {
                set = createSet(axis);
                data.addDataSet(set);
            }

            if (axis == 1)
                data.addEntry(new Entry(set.getEntryCount(), value), 0);
            else if (axis == 2)
                data.addEntry(new Entry(set.getEntryCount(), value), 1);
            else
                data.addEntry(new Entry(set.getEntryCount(), value), 2);

            // let the chart know it's data has changed
            data.notifyDataChanged();
            chartView.notifyDataSetChanged();
            chartView.invalidate();

            // limit the number of visible entries
            chartView.setVisibleXRangeMaximum(20);

            // move to the latest entry
            chartView.moveViewToX(data.getEntryCount());
        }
    }

    public void connect(View view) {
        if (bluetoothGatt == null) {
            if (btAdapter == null) {
                Toast.makeText(this, "NIE MA BLUETOOTH", Toast.LENGTH_SHORT).show();
            } else if (btAdapter.isEnabled()) {
                Toast.makeText(this, "LISTOWANIE URZĄDZEŃ", Toast.LENGTH_SHORT).show();
                bluetoothDevices = btAdapter.getBondedDevices();

                if (btAdapter.startDiscovery()) {
                    Toast.makeText(this, "ROZPOCZETO SKANOWANIE", Toast.LENGTH_SHORT).show();
                }
            } else if (!btAdapter.isEnabled()) {
                Toast.makeText(this, "BLUETOOTH JEST WYŁĄCZONE", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            bluetoothGatt = null;
            connectionState = STATE_DISCONNECTED;
        }
    }

    public void startAccelerometer(View view){
        requestAccelerometer();
    }

    public void notify(String x){
        if (!fallDetected) {
            fallDetected = true;
            Intent notificationIntent = new Intent(this, AlarmRejected.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            notificationIntent.putExtra("REJECT_ALARM", 1);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "FALL_CHANNEL_ID");
            builder.setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("WYKRYTO UPADEK")
                    .setContentText("DOTKNIJ, ABY ANULOWAĆ ALARM")
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH); // or NotificationCompat.PRIORITY_MAX
            Intent dismissIntent = new Intent(this, AlarmRejected.class);
            dismissIntent.setAction("ANULUJ");
            dismissIntent.putExtra("REJECT_ALARM", 2);
            PendingIntent pendingDismissIntent = PendingIntent.getActivity(this, 0, dismissIntent, 0);
            builder.addAction(R.drawable.ic_action_search, "ANULUJ", pendingDismissIntent);
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            Notification notification = builder.build();
            notificationManager.notify(1002, notification);

            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (fallDetected) {
                        //send sms
                        LocationRequest locationRequest = new LocationRequest();
                        locationRequest.setInterval(10000);
                        locationRequest.setFastestInterval(3000);
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                        LocationServices.getFusedLocationProviderClient(StartActivity.this)
                                .requestLocationUpdates(locationRequest, new LocationCallback(){
                                    @Override
                                    public void onLocationResult(LocationResult locationResult){
                                        super.onLocationResult(locationResult);
                                        LocationServices.getFusedLocationProviderClient(StartActivity.this)
                                                .removeLocationUpdates(this); //stop getting updates
                                        String smsText = "Pomocy, doszło do upadku. Moje współrzędne:\n";
                                        if (locationResult != null && locationResult.getLocations().size() > 0){
                                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                                            smsText += "http://www.google.com/maps/place/" + latitude + "," + longitude;
                                            smsmanager.sendTextMessage(SettingsActivity.getPhoneNumber(getApplicationContext()), null, smsText, null, null);
                                        }
                                    }
                                }, Looper.getMainLooper());
                        fallDetected = false;
                    }
                }
            }, 15000);
        }
    }

    public static void resetFallDetected(){
        notificationManager.cancel(1002);
        fallDetected = false;
    }

    private byte[] encrypt(byte[] data){
        try {
            byte[] bkey = key.getBytes("UTF-8");

            SecretKeySpec skeySpec = new SecretKeySpec(bkey, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            return cipher.doFinal(data);
        } catch (UnsupportedEncodingException e){
            System.out.println(e.toString());
            return data;
        } catch(java.security.NoSuchAlgorithmException e){
            System.out.println(e.toString());
            return data;
        } catch(javax.crypto.NoSuchPaddingException e){
            System.out.println(e.toString());
            return data;
        } catch(javax.crypto.BadPaddingException e){
            System.out.println(e.toString());
            return data;
        } catch(javax.crypto.IllegalBlockSizeException e){
            System.out.println(e.toString());
            return data;
        } catch(java.security.InvalidKeyException e){
            System.out.println(e.toString());
            return data;
        }
    }

    private void parseAccelerationData(byte[] data){
        for(int i = 0; i < (data.length -2)/6;i++){
            final int x = data[3+i*6] < 0 ? (data[2+i*6] & 0xff) - 256 - (~data[3+i*6] << 8) : (data[2+i*6] & 0xFF) + (data[3+i*6] << 8);
            final int y = data[5+i*6] < 0 ? (data[4+i*6] & 0xff) - 256 - (~data[5+i*6] << 8) : (data[4+i*6] & 0xFF) + (data[5+i*6] << 8);
            final int z = data[7+i*6] < 0 ? (data[6+i*6] & 0xff) - 256 - (~data[7+i*6] << 8) : (data[6+i*6] & 0xFF) + (data[7+i*6] << 8);

            storage.writeWearableSensor(new Integer[]{x, y, z});

            //draw mi band chart
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    addEntry(x, 1);
                    addEntry(y, 2);
                    addEntry(z, 3);
                }
            });
        }
    }

    public void startSensor(View view){
        if (measurementMode != ChooseActivity.MeasurementMode.PHONE && miband != null) {//only in this mode wearable sensor isn't requested
            // request wearable's sensor
            requestSensor();
        } else if (measurementMode != ChooseActivity.MeasurementMode.PHONE) {
            Toast.makeText(this, "NAJPIERW POŁĄCZ SIĘ Z OPASKĄ", Toast.LENGTH_LONG).show();
            return;
        }
        // request phone's accelerometer
        if (measurementMode != ChooseActivity.MeasurementMode.WEARABLE)
            requestAccelerometer();
        storage.clear();
        StartActivity delegate = this;
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                //start only a few seconds after button clicked
                storage.unblock(Storage.Type.PRIMARY);
                sensorsOn = true;

                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //shade starts a few seconds later; frameWidth / 2
                        storage.unblock(Storage.Type.SHADE);
                    }
                }, shadeLatency);
                //put 2 workers to obtain and send data and refresh connection
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        storage.sendToQualify(delegate, measurementMode);
                        h.postDelayed(this, frameWidth); //6 seconds of data obtanining
                    }
                }, frameWidth); //a few seconds later
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        storage.sendShadeToQualify(delegate,measurementMode);
                        h.postDelayed(this, frameWidth); //6 seconds of data obtanining
                    }
                }, frameWidth + shadeLatency); //a few seconds later, frame + latency seconds
                if (measurementMode != ChooseActivity.MeasurementMode.PHONE && miband != null)
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestSensor();
                            h.postDelayed(this, sensorRefreshTime); //request sensor every 35 seconds
                        }
                    }, sensorRefreshTime);
            }
        }, 3000); //3 seconds later to obtain first data
    }

    private void requestSensor(){
        gattDescriptorSensorMeasure.setValue(new byte[]{0x01, 0x00});
        if (bluetoothGatt.writeDescriptor(gattDescriptorSensorMeasure))
            connectionState = SENSOR_DATA_REQUESTED;
    }

    private void requestAccelerometer(){
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER_UNCALIBRATED); //Sensor.TYPE_GYROSCOPE

        sensorManager.registerListener(accelerometerSensorListener,
                sensor, SensorManager.SENSOR_DELAY_UI);
    }

    public void back(View view){
        h.removeCallbacksAndMessages(null);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }
}
