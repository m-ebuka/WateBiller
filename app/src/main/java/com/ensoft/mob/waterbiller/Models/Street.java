package com.ensoft.mob.waterbiller.Models;

/**
 * Created by Ebuka on 08/08/2015.
 */
public class Street {
    String streetid;
    String streetname;
    String total_account;
    String total_building;
    String id_area_code;
    int img;

    public String getStreetid()
    {
        return streetid;
    }
    public void setStreetid(String street_id)
    {
        this.streetid = street_id;
    }
    public String getStreetname()
    {
        return streetname;
    }
    public void setStreetname(String street_name)
    {
        this.streetname = street_name;
    }
    public String getTotal_account()
    {
        return total_account;
    }
    public void setTotal_account(String _total_acct)
    {
        this.total_account = _total_acct;
    }
    public String getId_area_code()
    {
        return id_area_code;
    }
    public void setId_area_code(String area_code)
    {
        this.id_area_code = area_code;
    }
    public int getImg() {return img;}
    public void setImg(int _img) { this.img = _img; }

    public String getTotal_building() {
        return total_building;
    }

    public void setTotal_building(String total_building) {
        this.total_building = total_building;
    }
}
