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
import java.util.ArrayList;
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
    private BluetoothGattCharacteristic gattCharacteristicSensor;
    private BluetoothGattCharacteristic characteristicNotifications;
    private BluetoothGattCharacteristic characteristic0x0051;
    private BluetoothGattCharacteristic characteristicAuth;
    private BluetoothGattCharacteristic characteristic0x005b;
    private BluetoothGattDescriptor bluetoothGattDescriptor;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    private static final int NOTIFICATIONS_REQUESTED = 3;
    private static final int KEY_SENT = 4;
    private static final int KEY_REQUESTED = 5;
    private static final int KEY_ENCRYPTED = 6;

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

    byte[] encryptionKey = new byte[]{(byte)0xf0, (byte)0xac, (byte)0xa3, (byte)0xb6, (byte)0xcd, 0x0e, (byte)0xc5, (byte)0x85, 0x37, 0x12, (byte)0x8f, 0x48, 0x4f, 0x68, 0x7b, (byte)0xb5};

    private final BluetoothGattCallback gattCallback =
            new BluetoothGattCallback() {
                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                                    int newState) {
                    System.out.println("onConnectionStateChange");
                    String intentAction;
                    bluetoothGatt = gatt;
                    if (newState == BluetoothProfile.STATE_CONNECTED) {
                        intentAction = ACTION_GATT_CONNECTED;
                        connectionState = STATE_CONNECTED;
                        System.out.println("Connection state change - connected: " + newState + " " + status);
                        gatt.discoverServices();

                    } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                        intentAction = ACTION_GATT_DISCONNECTED;
                        connectionState = STATE_DISCONNECTED;
                        System.out.println("Connection state change - disconnected: "  + newState + " " + status);
                    }
                    gatt.discoverServices();
                }

                @Override
                // New services discovered
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    System.out.println("onServiceDiscovered");
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        System.out.println("Service with success discovered: " + status);
                    } else {
                        System.out.println("Service discovered but else: " + status);
                    }
                    List<BluetoothGattService> services = gatt.getServices();
                    for (BluetoothGattService service : services) {
                        System.out.println("Service: " + service.getUuid().toString());
                        for (final BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                            if (characteristic.getUuid().toString().equals("00000001-0000-3512-2118-0009af100700")) {
                                //boolean setValue = characteristic.setValue(new byte[]{/*..BYTES.*/});
                                //boolean writeCharacteristic = gatt.writeCharacteristic(characteristic);
                                gattServiceSensor = service;
                                System.out.println("Znalezionio sensor 00000001");
                                gattCharacteristicSensor = characteristic;
                            }
                            if (characteristic.getUuid().toString().equals("00002a39-0000-1000-8000-00805f9b34fb")) {
                                //boolean setValue = characteristic.setValue(new byte[]{/*..BYTES.*/});
                                //boolean writeCharacteristic = gatt.writeCharacteristic(characteristic);
                                gattServiceSensor = service;
                                System.out.println("Znalezionio heart monitor");
                            }
                            if (characteristic.getUuid().toString().equals("00000020-0000-3512-2118-0009af100700")){
                                System.out.println("Auth 0x0050 i 1 znaleziona");
                                characteristicNotifications = characteristic;
                                bluetoothGattDescriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                if (bluetoothGattDescriptor != null)
                                    System.out.println("jest 0x2902");
                                bluetoothGattDescriptor.setValue(new byte[]{0x01, 0x00});
                                if (bluetoothGatt.writeDescriptor(bluetoothGattDescriptor))
                                    connectionState = NOTIFICATIONS_REQUESTED;
                            }
                            if (characteristic.getUuid().toString().equals("00000009-0000-3512-2118-0009af100700")){
                                System.out.println("Auth 0x005b i a znaleziona");
                                characteristicAuth = characteristic;
                            }
                            System.out.println("Characteristic: " + characteristic.getUuid().toString());
                        }
                    }
                }

                @Override
                // Result of a characteristic read operation
                public void onCharacteristicRead(BluetoothGatt gatt,
                                                 BluetoothGattCharacteristic characteristic,
                                                 int status) {
                    System.out.println("onCharacteristicRead");
                    if (status == BluetoothGatt.GATT_SUCCESS) {
                        System.out.println("Characteristic read: " + characteristic.getUuid().toString());
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                {
                    System.out.println("onCharacteristicWrite " + status);
                    if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_SENT){ System.out.println(characteristic.getValue());
                        characteristicAuth.setValue(new byte[] {0x02, 0x00}); System.out.println("udane key_sent");
                        if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                            connectionState = KEY_REQUESTED;
                    } else if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_REQUESTED){
                        byte[] response = characteristic.getValue();
                        byte[] encrypted = null;
                        System.out.println(String.valueOf(response));
                        SecretKeySpec skeySpec = new SecretKeySpec(encryptionKey, "AES/ECB/PKCS5Padding"); if (skeySpec == null) System.out.println("nie ma encryptora");

                        try {
                            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
                            encrypted = cipher.doFinal(response);

                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x03, 0x00});
                            output.write(encrypted);

                            byte[] out = output.toByteArray();
                            characteristicAuth.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                                connectionState = KEY_ENCRYPTED;
                        } catch(IOException e){
                            System.out.println("Nie udało się stworzyć tablicy bajtów");
                        } catch(java.security.NoSuchAlgorithmException e){
                            System.out.println(e.toString());
                        } catch(javax.crypto.NoSuchPaddingException e){
                            System.out.println(e.toString());
                        } catch(javax.crypto.BadPaddingException e){
                            System.out.println(e.toString());
                        } catch(javax.crypto.IllegalBlockSizeException e){
                            System.out.println(e.toString());
                        } catch(java.security.InvalidKeyException e){
                            System.out.println(e.toString());
                        }
                    } else if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_REQUESTED){
                        System.out.println(characteristic.getValue());
                    }
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt,
                                              BluetoothGattDescriptor descriptor,
                                              int status){
                    System.out.println("onDescriptorWrite: " + descriptor.getUuid().toString());
                    if (descriptor.getUuid().toString().contentEquals("00002902-0000-1000-8000-00805f9b34fb") && connectionState == NOTIFICATIONS_REQUESTED) {
                        try { System.out.println("udane notifications_requested"); System.out.println(descriptor.getValue());
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x01, 0x00});
                            output.write(encryptionKey);

                            byte[] out = output.toByteArray();
                            characteristicAuth.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                                connectionState = KEY_SENT;
                        } catch(IOException e){
                            System.out.println("Nie udało się stworzyć tablicy bajtów");
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
                String deviceHardwareAddress = device.getAddress(); // MAC address
                if (deviceName != null && device.getName().contentEquals("Mi Band 3")) {
                    miband = device;
                    Toast.makeText(context, "JEST MI BAND", Toast.LENGTH_SHORT).show();
                }
                list.add(deviceName);
                adapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)){
                Toast.makeText(context, "Contents: " + intent.describeContents(), Toast.LENGTH_SHORT).show();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.btIcon);
        devices = findViewById(R.id.btDevicesList);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null) {
            Toast.makeText(this, "NIE MA BLUETOOTH", Toast.LENGTH_SHORT).show();
        } else if (btAdapter.isEnabled()){
            Toast.makeText(this, "JEST WŁĄCZONE", Toast.LENGTH_SHORT).show();
        } else if (!btAdapter.isEnabled()) {
            Toast.makeText(this, "JEST WYŁĄCZONE", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "JEST WYŁĄCZONE", Toast.LENGTH_SHORT).show();
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        } else {
            Toast.makeText(this, "JEST GATT: " + bluetoothGatt.getDevice().getName() + connectionState, Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, bluetoothGatt.getDevice().getUuids().toString(), Toast.LENGTH_SHORT).show();
        }
    }

    public void connectButtonClick(View view){
        if (miband != null){
            bluetoothGatt = miband.connectGatt(this, true, gattCallback);
            Toast.makeText(this, "ŁĄCZENIE", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "NIE MA MI BANDA", Toast.LENGTH_SHORT).show();
        }
    }

    public void authButtonClick(View view){
        if (miband != null){
            System.out.println("authButtonClick");
            BluetoothGattCharacteristic readGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("00000020-0000-3512-2118-0009af100700"), BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
            if (bluetoothGatt.readCharacteristic(readGattCharacteristic))
                System.out.println("TRUE");
            readGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            readGattCharacteristic.setValue(new byte[]{0x01, 0x00});
            bluetoothGatt.writeCharacteristic(readGattCharacteristic);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


}
