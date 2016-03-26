package com.ensoft.mob.waterbiller.Models;

/**
 * Created by Ebuka on 08/09/2015.
 */
public class CoordinateModel {
    String latitude;
    String longitude;

    public void setLatitude(String _latitude)
    {
        this.latitude = _latitude;
    }
    public void setLongitude(String _longitude)
    {
        this.longitude = _longitude;
    }

    public String getLatitude()
    {
        return latitude;
    }
    public String getLongitude()
    {
        return longitude;
    }
}
