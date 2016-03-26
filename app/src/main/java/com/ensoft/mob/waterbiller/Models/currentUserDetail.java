package com.ensoft.mob.waterbiller.Models;

/**
 * Created by Web on 09/08/2015.
 */
public class currentUserDetail {
    private String userid;
    private String email;
    public String firstname;
    public String lastname;
    public String middlename;
    private String phonenumber;
    private String serviceid;
    private String authorizationid;

    public void setUserid(String _userid)
    {
        this.userid = _userid;
    }
    public void setEmail(String _email)
    {
        this.email = _email;
    }
    public void setPhonenumber(String _phone)
    {
        this.phonenumber = _phone;
    }
    public void setFirstname(String _firstname)
    {
        this.firstname = _firstname;
    }
    public void setLastname(String _lastname)
    {
        this.lastname = _lastname;
    }
    public void setMiddlename(String _middlename)
    {
        this.middlename = _middlename;
    }
    public void setServiceid(String _serviceid)
    {
        this.serviceid = _serviceid;
    }
    public void setAuthorizationid(String _authorizationid)
    {
        this.authorizationid = _authorizationid;
    }
    public String getUserid()
    {
        return this.userid;
    }
    public String getEmail()
    {
        return this.email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }
    public String getFirstname()
    {
        return firstname;
    }
    public String getLastname()
    {
        return lastname;
    }
    public String getMiddlename()
    {
        return middlename;
    }
    public String getServiceid()
    {
        return serviceid;
    }
    public String getAuthorizationid()
    {
        return this.authorizationid;
    }
}
