package pl.polsl.aei.monitorupadkow;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.companion.BluetoothLeDeviceFilter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    ListView devices;
    private BluetoothAdapter btAdapter;
    private Set<BluetoothDevice> bluetoothDevices;
    private BluetoothDevice miband;
    private BluetoothGatt bluetoothGatt;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private int connectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

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
                if (device.getName().contentEquals("Mi Band 3")) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver);
    }


}
