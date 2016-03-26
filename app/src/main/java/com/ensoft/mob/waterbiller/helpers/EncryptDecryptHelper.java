package com.ensoft.mob.waterbiller.helpers;

import android.bluetooth.BluetoothClass;
import android.content.Context;
import android.util.Base64;

import com.ensoft.mob.waterbiller.Devices.Devices;

//import se.simbio.encryption.Encryption;

/**
 * Created by Ebuka on 04/09/2015.
 */
public class EncryptDecryptHelper {
    private static final String TAG = "Encryption";
    Context context;
    public EncryptDecryptHelper(Context _context)
    {
        context = _context;
    }


    //Context context;
    //String KEY = Devices.getDeviceIMEI(context);
    //String SALT = Devices.getDeviceUUID(context);
    //Encryption encryption = Encryption.getDefault(Devices.getDeviceIMEI(context), Devices.getDeviceUUID(context), new byte[16]);

    //Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
    //String encrypted = encryption.encryptOrNull("top secret string");
    //String decrypted = encryption.decryptOrNull(encrypted);

    public String encryptStringRequest(String parameter)
    {
        Encryption encryption = Encryption.getDefault(Devices.getDeviceIMEI(context), Devices.getDeviceUUID(context), new byte[16]);
        String encrypted = encryption.encryptOrNull(parameter);
        return encrypted;
    }
    public String dencryptStringRequest(String parameter)
{
    Encryption encryption = Encryption.getDefault(Devices.getDeviceIMEI(context), Devices.getDeviceUUID(context), new byte[16]);
    String dencrypted = encryption.decryptOrNull(parameter);
    return dencrypted;
}
}
