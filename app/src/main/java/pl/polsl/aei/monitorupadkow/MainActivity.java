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
    private BluetoothGattCharacteristic gattCharacteristicSensor;
    private BluetoothGattCharacteristic characteristicNotifications;
    private BluetoothGattCharacteristic characteristic0x0051;
    private BluetoothGattCharacteristic characteristicAuth;
    private BluetoothGattCharacteristic characteristic0x005b;
    private BluetoothGattCharacteristic notifications;
    private BluetoothGattDescriptor gattDescriptor0051;
    private BluetoothGattDescriptor gattDescriptor005b;
    private BluetoothGattDescriptor notificationsDesc;
    private BluetoothGattCharacteristic immediateAlertCh;
    private BluetoothGattDescriptor immediateAlertDesc;
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
    String key = "Kj6dUM1y3kBAPBey";

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
                                bluetoothGatt.setCharacteristicNotification(characteristicNotifications, true);
                                gattDescriptor0051 = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                if (gattDescriptor0051 != null)
                                    System.out.println("jest 0x2902");
                                bluetoothGatt.readCharacteristic(characteristicNotifications); System.out.println("success");
                                connectionState = CHARACTERISTIC_READ;
                                System.out.println("characteristic read req");
                            }
                            if (characteristic.getUuid().toString().equals("00000009-0000-3512-2118-0009af100700")){
                                System.out.println("Auth 0x005b i a znaleziona");
                                characteristicAuth = characteristic;
                                bluetoothGatt.setCharacteristicNotification(characteristicAuth, true);
                                gattDescriptor005b = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                                for(BluetoothGattDescriptor desc : characteristic.getDescriptors())
                                    System.out.println(desc.getUuid());
                            }
                            if (characteristic.getUuid().toString().equals("00002a46-0000-1000-8000-00805f9b34fb")){
                                System.out.println("Jest notification service");
                                notifications = characteristic;
                                bluetoothGatt.setCharacteristicNotification(notifications, false);
                                notificationsDesc = characteristic.getDescriptor(UUID.fromString("00002901-0000-1000-8000-00805f9b34fb"));
                                for(BluetoothGattDescriptor desc: notifications.getDescriptors()){
                                    System.out.println(desc.getUuid());
                                }
                            }
                            if (characteristic.getUuid().toString().equals("00001802-0000-1000-8000-00805f9b34fb")){
                                System.out.println("Jest immediate alert");
                                immediateAlertCh = characteristic;
                                //immediateAlertDesc = characteristic.getDescriptor(UUID.fromString("00002901-0000-1000-8000-00805f9b34fb"));
                                for(BluetoothGattDescriptor desc: notifications.getDescriptors()){
                                    System.out.println(desc.getUuid());
                                }
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
                    if (characteristic.getUuid().toString().contentEquals("00000020-0000-3512-2118-0009af100700") && connectionState == CHARACTERISTIC_READ){
                        System.out.println("On characteristic read: " + Arrays.toString(characteristic.getValue()));
                        gattDescriptor0051.setValue(new byte[]{0x01, 0x00});
                        System.out.println("notifications_requested req: " + Arrays.toString(new byte[]{0x01, 0x00}));
                        if (bluetoothGatt.writeDescriptor(gattDescriptor0051))
                            connectionState = NOTIFICATIONS_REQUESTED_0;
                    }
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status)
                {
                    System.out.println("onCharacteristicWrite " + characteristic.getUuid() + ", val: " + Arrays.toString(characteristic.getValue()));

                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt,
                                              BluetoothGattDescriptor descriptor,
                                              int status){
                    System.out.println("onDescriptorWrite: " + descriptor.getUuid() + ", val: " + Arrays.toString(descriptor.getValue()));
                    if (descriptor.getUuid().toString().contentEquals("00002902-0000-1000-8000-00805f9b34fb") && connectionState == NOTIFICATIONS_REQUESTED_0) {
                        System.out.println("On notifications descriptor_0 write: " + Arrays.toString(descriptor.getValue()));
                        gattDescriptor005b.setValue(new byte[]{0x01, 0x00});
                        System.out.println("notifications_requested_b req: " + Arrays.toString(new byte[]{0x01, 0x00}));
                        if (bluetoothGatt.writeDescriptor(gattDescriptor005b))
                            connectionState = NOTIFICATIONS_REQUESTED_b;

                    } else if (descriptor.getUuid().toString().contentEquals("00002902-0000-1000-8000-00805f9b34fb") && connectionState == NOTIFICATIONS_REQUESTED_b) { System.out.println("descriptor b notifications: " + Arrays.toString(descriptor.getValue()));
                        try { System.out.println("udane notifications_requested");
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x01, 0x00});
                            output.write(encryptionKey); System.out.println("key_sent req: " + Arrays.toString(output.toByteArray()));

                            byte[] out = output.toByteArray();
                            characteristicAuth.setValue(out);//characteristicAuth.setValue(new byte[]{0x01, 0x00});
                            if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                                connectionState = KEY_SENT;
                        } catch(IOException e){
                            System.out.println("Nie udało się stworzyć tablicy bajtów");
                        }
                    }
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_SENT){ System.out.println(" key_sent resp: " + Arrays.toString(characteristic.getValue()));
                        characteristicAuth.setValue(new byte[] {0x02, 0x00}); System.out.println("key_requested req: " + Arrays.toString(new byte[] {0x02, 0x00}));
                        if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                            connectionState = KEY_REQUESTED;
                    } else if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_REQUESTED){
                        byte[] response = Arrays.copyOfRange(characteristic.getValue(), 3, 19);
                        System.out.println("Random key: " + Arrays.toString(response));
                        System.out.println("Pełna odpowiedź: " + Arrays.toString(characteristic.getValue()));

                        try {
                            ByteArrayOutputStream output = new ByteArrayOutputStream();
                            output.write(new byte[]{0x03, 0x00});
                            output.write(encrypt(response));
                            System.out.println("key_encrypt req: " + Arrays.toString(output.toByteArray()));

                            byte[] out = output.toByteArray();
                            System.out.println(out.length);
                            characteristicAuth.setValue(out);
                            if (bluetoothGatt.writeCharacteristic(characteristicAuth))
                                connectionState = KEY_ENCRYPTED;
                        } catch (IOException e){
                            System.out.println(e.toString());
                        }
                    } else if (characteristic.getUuid().toString().contentEquals("00000009-0000-3512-2118-0009af100700") && connectionState == KEY_ENCRYPTED){
                        System.out.println("key_encrypted res: " + Arrays.toString(characteristic.getValue()));

                        //pobieranie danych sensora
                        //new byte[]{0x01, 0x03, 0x19}
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
            /*BluetoothGattCharacteristic readGattCharacteristic = new BluetoothGattCharacteristic(UUID.fromString("00000020-0000-3512-2118-0009af100700"), BluetoothGattCharacteristic.PROPERTY_READ, BluetoothGattCharacteristic.PERMISSION_READ);
            if (bluetoothGatt.readCharacteristic(readGattCharacteristic))
                System.out.println("TRUE");
            readGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            readGattCharacteristic.setValue(new byte[]{0x01, 0x00});
            bluetoothGatt.writeCharacteristic(readGattCharacteristic);*/
            //pisanie notyfikacji characteristic
            notifications.setValue(new byte[] {0x03, 0x01, 0x48, 0x69});
            bluetoothGatt.writeCharacteristic(notifications);
            //pisanie notyfikacji descriptor
            //notificationsDesc.setValue(new byte[] {0x03, 0x01, 0x48, 0x69});
            //bluetoothGatt.writeDescriptor(notificationsDesc);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


}
