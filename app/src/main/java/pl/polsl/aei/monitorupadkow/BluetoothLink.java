package pl.polsl.aei.monitorupadkow;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

public class BluetoothLink {
    String name;
    UUID serviceUUID;

    BluetoothDevice bluetoothDevice;
    BluetoothSocket bluetoothSocket;

    public BluetoothLink(BluetoothDevice bluetoothDevice){
        this.bluetoothDevice = bluetoothDevice;
    }

    public boolean connect(UUID serviceUUID){
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(serviceUUID);
            bluetoothSocket.connect();
            return true;
        } catch (IOException e){
            return false;
        }
    }

    public void close(){

    }

    public boolean send(byte[] bytes){
        return true;
    }

    public byte[] receive(){
        return new byte[2];
    }
}
