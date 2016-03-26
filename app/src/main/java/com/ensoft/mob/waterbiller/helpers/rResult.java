package com.ensoft.mob.waterbiller.helpers;

import android.util.Log;

/**
 * Created by Ebuka on 07/08/2015.
 */
public class rResult {
    public static String _result;
    public rResult()
    {

    }
    public static void setResult(String r)
    {
        Log.i("ResultTAGR",r);
        _result = r;
    }
    public static String getResult()
    {
        Log.i("ResultTAGR","Result inside getResult;" + _result);
        return _result;
    }
}
