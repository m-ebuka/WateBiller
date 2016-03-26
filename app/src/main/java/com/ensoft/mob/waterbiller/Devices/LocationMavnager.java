package com.ensoft.mob.waterbiller.Devices;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Ebuka on 07/09/2015.
 */
public class LocationMavnager {
    int myLatitude, myLongitude;
    String locCid; String locLac; String gsmCellLocation; String geo;
    public String getMyLoc(Context context)
    {
        //retrieve a reference to an instance of TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();

        int cid = cellLocation.getCid();
        int lac = cellLocation.getLac();
        //textGsmCellLocation.setText(cellLocation.toString());
        gsmCellLocation = cellLocation.toString();
        locCid = "gsm cell id: " + String.valueOf(cid);
        locLac = "gsm location area code: " + String.valueOf(lac);
        //textCID.setText("gsm cell id: " + String.valueOf(cid));
        //textLAC.setText("gsm location area code: " + String.valueOf(lac));

        if(RqsLocation(cid, lac)){
            /*textGeo.setText(
                    String.valueOf((float)myLatitude/1000000)
                            + " : "
                            + String.valueOf((float)myLongitude/1000000));*/
            geo = String.valueOf((float)myLatitude/1000000)
                    + " : "
                    + String.valueOf((float)myLongitude/1000000);
        }else{
            //textGeo.setText("Can't find Location!");
            geo = "err"; //:Can't find Location!
        }
        return geo;
    }

    private boolean RqsLocation(int cid, int lac) {
        Boolean result = false;

        String urlmmap = "http://www.google.com/glm/mmap";

        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {
                myLatitude = dataInputStream.readInt();
                myLongitude = dataInputStream.readInt();

                result = true;

            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;
    }

    private void WriteData(OutputStream outputStream, int cid, int lac) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }
}
