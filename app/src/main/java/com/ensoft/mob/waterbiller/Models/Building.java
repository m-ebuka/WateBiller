package com.ensoft.mob.waterbiller.Models;

/**
 * Created by Ebuka on 16/08/2015.
 */
public class Building {
    String building_id;
    int meter_id;
    String meter_no;
    String serial_no;
    String building_no;
    String building_name;
    String building_category;
    String building_image;
    byte[] sampleByteMan;
    String account_no;
    String latitude;
    String longitude;


    public void setBuilding_id(String _buildingid)
    {
        this.building_id = _buildingid;
    }
    public String getBuilding_id()
    {
        return building_id;
    }

    public void setMeter_id(int _meterid)
    {
        this.meter_id = _meterid;
    }
    public int getMeter_id()
    {
        return meter_id;
    }

    public void setMeter_no(String _meterno)
    {
        this.meter_no = _meterno;
    }
    public String getMeter_no()
    {
        return meter_no;
    }

    public void setSerial_no(String _serialno)
    {
        this.serial_no = _serialno;
    }
    public String getSerial_no()
    {
        return serial_no;
    }

    public void setBuilding_name(String _buildingname)
    {
        this.building_name = _buildingname;
    }
    public String getBuilding_name()
    {
        return building_name;
    }

    public void setBuilding_image(String _buildingimage)
    {
        this.building_image = _buildingimage;
    }
    public String getBuilding_image()
    {
        return building_image;
    }

    public void setBuilding_category(String _buildingcategory)
    {
        this.building_category = _buildingcategory;
    }
    public String getBuilding_category()
    {
        return building_category;
    }

    public void setAccount_no(String _accountno)
    {
        this.account_no = _accountno;
    }
    public String getAccount_no()
    {
        return account_no;
    }

    public void setBuilding_no(String _buildingno)
    {
        this.building_no = _buildingno;
    }
    public String getBuilding_no()
    {
        return building_no;
    }

    public void setLatitude(String _latitude)
    {
        this.latitude = _latitude;
    }
    public String getLatitude()
    {
        return latitude;
    }
    public void setLongitude(String _longitude)
    {
        this.longitude = _longitude;
    }
    public String getLongitude()
    {
        return longitude;
    }

    public byte[] getSampleByteMan() {
        return sampleByteMan;
    }

    public void setSampleByteMan(byte[] sampleByteMan) {
        this.sampleByteMan = sampleByteMan;
    }
}
