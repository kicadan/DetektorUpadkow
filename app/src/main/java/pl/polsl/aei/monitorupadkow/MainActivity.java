package pl.polsl.aei.monitorupadkow;

import android.Manifest;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ListView devices;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> bluetoothDevices;
    private BluetoothDevice miband;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService gattServiceSensor;
    private BluetoothGattService gattServiceHeartMonitor;
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


    ArrayList<String> list;
    ArrayAdapter adapter;

    byte[] encryptionKey = "Kj6dUM1y3kBAPBey".getBytes();//new byte[]{(byte)0xf0, (byte)0xac, (byte)0xa3, (byte)0xb6, (byte)0xcd, 0x0e, (byte)0xc5, (byte)0x85, 0x37, 0x12, (byte)0x8f, 0x48, 0x4f, 0x68, 0x7b, (byte)0xb5};
    String key = "Kj6dUM1y3kBAPBey"; //8VoK4rpNZjZ04oh4

    List<byte[]> queue;

    private int queueCounter = 0;

    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    //System.out.println("onConnectionStateChange");
                    String intentAction;
                    bluetoothGatt = gatt;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        //System.out.println("Connection state change - connected: " + newState + " " + status);
                        gatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        //System.out.println("Connection state change - disconnected: "  + newState + " " + status);
                    }
                    gatt.discoverServices();
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    //System.out.println("onServiceDiscovered");
                    /*if (status == BluetoothGatt.GATT_SUCCESS) {
                        System.out.println("Service with success discovered: " + status);
                    } else {
                        System.out.println("Service discovered but else: " + status);
                    }*/
                    List<BluetoothGattService> services = gatt.getServices();
                    for (BluetoothGattService service : services) {
                        //System.out.println("Service: " + service.getUuid().toString());
                        for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_AUTHORIZATION)){ //authorization
                                //System.out.println("Auth 0x0050 i 1 znaleziona");
                                characteristicAuthorization = characteristic;
                                bluetoothGatt.setCharacteristicNotification(characteristicAuthorization, true);
                                gattDescriptor0051 = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902)); //0x2902
                                bluetoothGatt.readCharacteristic(characteristicAuthorization); System.out.println("success");
                                connectionState = CHARACTERISTIC_READ;
                                //System.out.println("characteristic read req");
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_AUTHORIZATION_B)){ //authorization
                                //System.out.println("Auth 0x005b i a znaleziona");
                                characteristicAuthorizationB = characteristic;
                                bluetoothGatt.setCharacteristicNotification(characteristicAuthorizationB, true);
                                gattDescriptor005b = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902)); //0x2902
                                /*for(BluetoothGattDescriptor desc : characteristic.getDescriptors())
                                    System.out.println(desc.getUuid());*/
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_NOTIFICATION)){
                                //System.out.println("Jest notification service");
                                gattCharacteristicNotifications = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicNotifications, true);
                                /*for(BluetoothGattDescriptor desc: gattCharacteristicNotifications.getDescriptors()){
                                    System.out.println(desc.getUuid());
                                }*/
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL)){
                                //System.out.println("Jest HRC");
                                gattCharacteristicHRC = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicHRC, true);
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_MEASURE)){
                                //System.out.println("Jest HRM");
                                gattCharacteristicHRM = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicHRM, true);
                                gattDescriptorHRM = characteristic.getDescriptor(UUID.fromString(NOTIFICATION_DESCRIPTOR_2902));
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_CONTROL)){
                                //System.out.println("Jest Sensor Control");
                                gattCharacteristicSensContr = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicSensContr, true);
                                gattDescriptorSensorContr = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                /*for(BluetoothGattDescriptor desc: gattCharacteristicSensContr.getDescriptors()){
                                    System.out.println(desc.getUuid());
                                }*/
                            }
                            if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_MEASURE)){
                                //System.out.println("Jest Sensor Measure");
                                gattCharacteristicSensMeasure = characteristic;
                                bluetoothGatt.setCharacteristicNotification(gattCharacteristicSensMeasure, true);
                                gattDescriptorSensorMeasure = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                /*for(BluetoothGattDescriptor desc: gattCharacteristicSensContr.getDescriptors()){
                                    System.out.println(desc.getUuid());
                                }*/
                            }
                            //System.out.println("Characteristic: " + characteristic.getUuid().toString());
                        }
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    //System.out.println("onCharacteristicRead");
                    if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION) && connectionState == CHARACTERISTIC_READ){
                        //System.out.println("On characteristic read: " + Arrays.toString(characteristic.getValue()));
                        gattDescriptor0051.setValue(new byte[]{0x01, 0x00});
                        //System.out.println("notifications_requested req: " + Arrays.toString(new byte[]{0x01, 0x00}));
                        if (bluetoothGatt.writeDescriptor(gattDescriptor0051))
                            connectionState = NOTIFICATIONS_REQUESTED_0;
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                {
                    //System.out.println("onCharacteristicWrite " + characteristic.getUuid() + ", val: " + Arrays.toString(characteristic.getValue()));
                    /*if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[] {0x15, 0x02, 0x00})){
                        System.out.println("GIT");
                        gattCharacteristicHRC.setValue(new byte[]{0x15, 0x01, 0x00});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicHRC);
                    } else if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[] {0x15, 0x01, 0x00})){
                        System.out.println("GIT2");
                        gattCharacteristicSensContr.setValue(new byte[]{0x01, 0x03, 0x19});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicSensContr);
                    } else */if (characteristic.getUuid().toString().equals(CHARACTERISTIC_SENSOR_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[]{0x01, 0x03, 0x19})) {
                        //System.out.println("GIT3");
                        gattDescriptorSensorContr.setValue(new byte[]{0x00, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorSensorContr))
                            connectionState = SENSOR_CONTROL_REQUESTED2;
                    } else if (characteristic.getUuid().toString().equals(CHARACTERISTIC_HEART_RATE_CONTROL) && Arrays.equals(characteristic.getValue(), new byte[]{0x15, 0x01, 0x01})){
                        //System.out.println("GIT5");
                        gattCharacteristicSensContr.setValue(new byte[]{0x02});//0x00, 0x02});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicSensContr);
                    }

                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt,
                                              BluetoothGattDescriptor descriptor,
                                              int status){
                    //System.out.println("onDescriptorWrite: " + descriptor.getUuid() + ", val: " + Arrays.toString(descriptor.getValue()));
                    if (descriptor.getUuid().toString().contentEquals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == NOTIFICATIONS_REQUESTED_0) {
                        //System.out.println("On notifications descriptor_0 write: " + Arrays.toString(descriptor.getValue()));
                        gattDescriptor005b.setValue(new byte[]{0x01, 0x00});
                        //System.out.println("notifications_requested_b req: " + Arrays.toString(new byte[]{0x01, 0x00}));
                        if (bluetoothGatt.writeDescriptor(gattDescriptor005b))
                            connectionState = NOTIFICATIONS_REQUESTED_b;

                    } else if (descriptor.getUuid().toString().contentEquals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == NOTIFICATIONS_REQUESTED_b) { //System.out.println("descriptor b notifications: " + Arrays.toString(descriptor.getValue()));
                        try { //System.out.println("udane notifications_requested");
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x01, 0x00});
                            output.write(encryptionKey); //System.out.println("key_sent req: " + Arrays.toString(output.toByteArray()));

                            byte[] out = output.toByteArray();
                            characteristicAuthorizationB.setValue(out);//characteristicAuth.setValue(new byte[]{0x01, 0x00});
                            if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                                connectionState = KEY_SENT;
                        } catch(IOException e){
                            System.out.println("Nie udało się stworzyć tablicy bajtów");
                        }
                    }/* else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && Arrays.equals(descriptor.getValue(), new byte[] {0x01, 0x00})){
                        System.out.println("GIT42");
                        gattCharacteristicHRC.setValue(new byte[]{0x15, 0x01, 0x01});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicHRC);
                    }*/ else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_CONTROL_REQUESTED2){
                        //System.out.println("GIT41");
                        gattDescriptorHRM.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorHRM))
                            connectionState = HRD_REQUESTED;
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_DATA_REQUESTED){
                        //System.out.println("*GIT11");
                        gattDescriptorSensorContr.setValue(new byte[]{0x01, 0x00});
                        if (bluetoothGatt.writeDescriptor(gattDescriptorSensorContr))
                            connectionState = SENSOR_CONTROL_REQUESTED;
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == SENSOR_CONTROL_REQUESTED){
                        //System.out.println("*GIT12");
                        gattCharacteristicSensContr.setValue(new byte[]{0x01, 0x03, 0x19});
                        bluetoothGatt.writeCharacteristic(gattCharacteristicSensContr);
                    } else if (descriptor.getUuid().toString().equals(NOTIFICATION_DESCRIPTOR_2902) && connectionState == HRD_REQUESTED){
                        //System.out.println("HRD REQ RESP");
                        gattCharacteristicHRC.setValue(new byte[]{0x15, 0x01, 0x01});
                        if (bluetoothGatt.writeCharacteristic(gattCharacteristicHRC))
                           connectionState = HRC_REQUESTED;
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_SENT){ //System.out.println(" key_sent resp: " + Arrays.toString(characteristic.getValue()));
                        characteristicAuthorizationB.setValue(new byte[] {0x02, 0x00}); //System.out.println("key_requested req: " + Arrays.toString(new byte[] {0x02, 0x00}));
                        if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                            connectionState = KEY_REQUESTED;
                    } else if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_REQUESTED){
                        byte[] response = Arrays.copyOfRange(characteristic.getValue(), 3, 19);
                        //System.out.println("Random key: " + Arrays.toString(response));
                        //System.out.println("Pełna odpowiedź: " + Arrays.toString(characteristic.getValue()));

                        try {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x03, 0x00});
                            output.write(encrypt(response));
                            //System.out.println("key_encrypt req: " + Arrays.toString(output.toByteArray()));

                            byte[] out = output.toByteArray();
                            //System.out.println(out.length);
                            characteristicAuthorizationB.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuthorizationB))
                                connectionState = KEY_ENCRYPTED;
                        } catch (IOException e){
                            //System.out.println(e.toString());
                        }
                    } else if (characteristic.getUuid().toString().contentEquals(CHARACTERISTIC_AUTHORIZATION_B) && connectionState == KEY_ENCRYPTED) {
                        if (Arrays.equals(characteristic.getValue(), new byte[]{0x10, 0x03, 0x01}))
                            System.out.println("ZAUTORYZOWANO");

                        //pobieranie danych sensora
                        //new byte[]{0x01, 0x03, 0x19}
                    } else {
                        //System.out.println("onCharacteristicChanged, value: " + Arrays.toString(characteristic.getValue()) + ", length: " + characteristic.getValue().length + " uns. bajt[1] " + (characteristic.getValue()[1] & 0xFF));
                        //System.out.println(Arrays.toString(characteristic.getValue()));
                        //if(characteristic.getValue().length == 20)
                            //parseAccelerationData(characteristic.getValue());
                        if (characteristic.getValue()[0] == 1) {
                            parseAccelerationData(characteristic.getValue());
                        }
                        //if(characteristic.getValue()[0] == 1) {
                            //parseAccelerationData(characteristic.getValue());
                            //System.out.println(characteristic.getValue()[1] & 0xFF);
                        //}
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
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);System.out.println("x");
                String deviceName = device.getName();System.out.println("y");
                //String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null && device.getName().contentEquals("Mi Band 3")) {
                    miband = device;
                    bluetoothGatt = miband.connectGatt(context, true, gattCallback);
                    Toast.makeText(context, "ŁĄCZENIE", Toast.LENGTH_SHORT).show();
                    bluetoothGatt.requestMtu(33);
                    //Toast.makeText(context, "JEST MI BAND", Toast.LENGTH_SHORT).show();
                }
                //list.add(deviceName);
                //adapter.notifyDataSetChanged();
            }
        }


    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queue = new ArrayList<>();

        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.btIcon);
        devices = findViewById(R.id.btDevicesList);

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

        list = new ArrayList<>();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        devices.setAdapter(adapter);

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


    public void mainButtonOnClick(View view) {
        if (bluetoothGatt == null) {
            if (btAdapter == null) {
                Toast.makeText(this, "NIE MA BLUETOOTH", Toast.LENGTH_SHORT).show();
            } else if (btAdapter.isEnabled()) {
                Toast.makeText(this, "LISTOWANIE URZĄDZEŃ", Toast.LENGTH_SHORT).show();
                bluetoothDevices = btAdapter.getBondedDevices();

                if (btAdapter.startDiscovery()) {
                    Toast.makeText(this, "ROZPOCZETO SKANOWANIE", Toast.LENGTH_SHORT).show();
                }
                for (BluetoothDevice btDevice : bluetoothDevices) {
                    list.add(btDevice.getName());
                }
            } else if (!btAdapter.isEnabled()) {
                Toast.makeText(this, "BLUETOOTH JEST WYŁĄCZONE", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            bluetoothGatt = null;
            connectionState = STATE_DISCONNECTED;
            //Toast.makeText(this, "MI BAND POŁĄCZONY: " + bluetoothGatt.getDevice().getName() + connectionState, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, bluetoothGatt.getDevice().getUuids().toString(), Toast.LENGTH_SHORT).show();
        }
    }
/*
    public void connectButtonClick(View view){
        if (miband != null){
            bluetoothGatt = miband.connectGatt(this, true, gattCallback);
            Toast.makeText(this, "ŁĄCZENIE", Toast.LENGTH_SHORT).show();
            bluetoothGatt.requestMtu(33);
        } else {
            Toast.makeText(this, "NIE ZNALEZIONO MI BAND", Toast.LENGTH_SHORT).show();
        }
    }*/

    public void authButtonClick(View view){
        if (miband != null){
            /*System.out.println("authButtonClick");
            BluetoothGattCharacteristic readGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString(CHARACTERISTIC_AUTHORIZATION), BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
            if (bluetoothGatt.readCharacteristic(readGattCharacteristic))
                System.out.println("TRUE");
            readGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            readGattCharacteristic.setValue(new byte[]{0x01, 0x00});
            bluetoothGatt.writeCharacteristic(readGattCharacteristic);*/
            //pisanie notyfikacji characteristic
            gattCharacteristicNotifications.setValue(new byte[] {0x03, 0x01, 0x50, 0x4f, 0x4c, 0x53, 0x4c, 0x2d, 0x32, 0x30, 0x32, 0x30}); //80, 79, 76, 83, 76, 45, 50, 48, 50, 48
            bluetoothGatt.writeCharacteristic(gattCharacteristicNotifications);
        }
    }

    private byte[] encrypt(byte[] data){
        try {
            byte[] bkey = key.getBytes("UTF-8");

            SecretKeySpec skeySpec = new SecretKeySpec(bkey, "AES");//"AES/ECB/NoPadding");
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
        int x, y, z;
        int potx, poty, potz;
        byte coefX, coefY, coefZ;
        for(int i = 0; i < (data.length -2)/6;i++){
            /*x = data[3+i*6] > -1 ? data[2+i*6] & 0xFF : data[2+i*6];
            potx = data[3+i*6];
            y = data[5+i*6] > -1 ? data[4+i*6] & 0xFF : data[4+i*6];
            poty = data[5+i*6];
            z = data[7+i*6] > -1 ? data[6+i*6] & 0xFF : data[6+i*6];
            potz = data[7+i*6];*/
            x = data[3+i*6] < 0 ? (data[2+i*6] & 0xff) - 256 - (~data[3+i*6] << 8) : (data[2+i*6] & 0xFF) + (data[3+i*6] << 8);
            y = data[5+i*6] < 0 ? (data[4+i*6] & 0xff) - 256 - (~data[5+i*6] << 8) : (data[4+i*6] & 0xFF) + (data[5+i*6] << 8);
            z = data[7+i*6] < 0 ? (data[6+i*6] & 0xff) - 256 - (~data[7+i*6] << 8) : (data[6+i*6] & 0xFF) + (data[7+i*6] << 8);
            queue.add(Arrays.copyOfRange(data,  2 + i * 6, 8 + i * 6));
            System.out.println("[" + x + ", " + data[3+i*6] + ", " + y + ", " + data[5+i*6] + ", " + z + ", " + data[7+i*6] + "]");
            //System.out.println(Arrays.toString(Arrays.copyOfRange(data, 2 + i * 6, 8 + i * 6)));
            queueCounter++;
        }
    }

    public void startSensor(View view){
        if (miband != null){
            /*System.out.println("startSensor");
            gattCharacteristicHRC.setValue(new byte[]{0x15, 0x02, 0x00});
            if (bluetoothGatt.writeCharacteristic(gattCharacteristicHRC))
                connectionState = SENSOR_REQUESTED;*/
            gattDescriptorSensorMeasure.setValue(new byte[]{0x01, 0x00});
            if (bluetoothGatt.writeDescriptor(gattDescriptorSensorMeasure))
                connectionState = SENSOR_DATA_REQUESTED;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


}
