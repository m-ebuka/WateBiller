package com.ensoft.mob.waterbiller;

import android.app.Activity;
import android.app.Application;

import com.ensoft.mob.waterbiller.Models.Street;

/**
 * Created by Ebuka on 16/08/2015.
 */
public class MyApp extends Application {
    private String accountNoSearchCriteria;
    private String meterNoSearchCrieria;
    private String userid;
    private String serviceid;
    private String authorizationid;
    private String email;
    private String firstname;
    private String middlename;
    private String lastname;
    private String userGroup_id;
    private String phone;
    private byte[] cameraByte;
    private byte[] sampleByteImage;
    private byte[] cameraByteForMeterReading;
    private byte[] profileImage;
    private byte[] situationImage;
    private String activeBuilding;
    private String activeBuildingName;
    private String activeBuildingNo;
    private String activeBuildingCategoryId;
    private String activeBuildingCategoryName;
    private String activeLatitude;
    private String activeLongitude;
    private float currentBillValue;
    private Activity activity;

    private int activeStreet;
    private String activeStreetName;

    private int activeAreaCode;
    private String activeAreaName;

    private String activeMeterNo;

    private String lastName;
    private String firstName;
    private String middleNmae;
    private String address;
    private String phoneNumber;
    private String eEmail;
    private String profileTypeId;

    private String ireport_zone;
    private String ireport_area;
    private String ireport_street;
    private String ireport_building;
    private String ireport_description;

    public void clear()
    {
        this.cameraByte = null;
        this.cameraByteForMeterReading = null;
        this.userid = null;

    }

    public String getUserid()
    {
        return userid;
    }
    public void setUserid(String _userid)
    {
        this.userid = _userid;
    }

    public String getEmail()
    {
        return email;
    }
    public void setEmail(String _email)
    {
        this.email = _email;
    }

    public String getServiceid()
    {
        return serviceid;
    }
    public  void setServiceid(String _serviceid)
    {
        this.serviceid = _serviceid;
    }

    public String getAuthorizationid()
    {
        return authorizationid;
    }

    public void setAuthorizationid(String _authorizationid)
    {
        this.authorizationid = _authorizationid;
    }

    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String _phone)
    {
        this.phone = _phone;
    }

    public String getFirstname()
    {
        return firstname;
    }
    public void setFirstname(String _firstname)
    {
        this.firstname = _firstname;
    }

    public String getMiddlename()
    {
        return middlename;
    }
    public void setMiddlename(String _middlename)
    {
        this.middlename = _middlename;
    }

    public String getLastname()
    {
        return lastname;
    }
    public void setLastname(String _lastname)
    {
        this.lastname = _lastname;
    }

    public String getUserGroup_id() {
        return userGroup_id;
    }

    public void setUserGroup_id(String userGroup_id) {
        this.userGroup_id = userGroup_id;
    }

    public int getActiveAreaCode()
    {
        return activeAreaCode;
    }
    public void setActiveAreaCode(int _areacode)
    {
        this.activeAreaCode = _areacode;
    }

    public String getActiveAreaName()
    {
        return activeAreaName;
    }
    public void setActiveAreaName(String _areaName)
    {
        this.activeAreaName = _areaName;
    }

    public int getActiveStreet()
    {
        return activeStreet;
    }
    public void setActiveStreet(int _activestreet)
    {
        this.activeStreet = _activestreet;
    }

    public void setActiveStreetName(String _streetName)
    {
        this.activeStreetName = _streetName;
    }
    public String getActiveStreetName()
    {
        return activeStreetName;
    }

    public String getActiveBuilding()
    {
        return activeBuilding;
    }
    public void setActiveBuilding(String _activeBuilding)
    {
        this.activeBuilding = _activeBuilding;
    }

    public String getActiveBuildingName()
    {
        return activeBuildingName;
    }
    public void setActiveBuildingName(String _buildingName)
    {
        this.activeBuildingName = _buildingName;
    }

    public String getActiveBuildingNo() {
        return activeBuildingNo;
    }

    public void setActiveBuildingNo(String _activeBuildingNo) {
        this.activeBuildingNo = _activeBuildingNo;
    }

    public void setCameraByte(byte[] data)
    {
        this.cameraByte = null;
        this.cameraByte = data;
    }
    public byte[] getCameraByte()
    {
        return cameraByte;
    }



    public void setCameraByteForMeterReading(byte[] data)
    {
        this.cameraByteForMeterReading = data;
    }
    public byte[] getCameraByteForMeterReading()
    {
        return cameraByteForMeterReading;
    }

    public void setProfileImage(byte[] data)
    {
        this.profileImage = data;
    }
    public byte[] getProfileImage()
    {
        return profileImage;
    }

    public byte[] getSituationImage() {
        return situationImage;
    }

    public void setSituationImage(byte[] situationImage) {
        this.situationImage = situationImage;
    }

    public void setIndividualProfileData(String lName, String mName, String fName, String mAddress, String mPhone, String eMail){
        this.lastname = lName;
        this.middlename = mName;
        this.firstname = fName;
        this.address = mAddress;
        this.phoneNumber = mPhone;
        this.eEmail = eMail;
            }
    public String getProfileLastName(){
     return lastname;
    }
    public String getProfileMiddleName(){
        return middlename;
    }
    public String getProfileFirstName(){
        return firstname;
    }
    public String getProfileAddress(){
        return address;
    }
    public String getProfilePhone(){
        return lastname;
    }
    public String getProfileEmail(){
        return eEmail;
    }
    public void setProfileTypeId(String pTypeId){
        this.profileTypeId = pTypeId;
    }
    public String getProfileTypeId(){
        return profileTypeId;
    }

    public void setIreportData(String zone, String area, String street, String building, String description)
    {
        this.ireport_zone = zone;
        this.ireport_area = area;
        this.ireport_street = street;
        this.ireport_building = building;
        this.ireport_description = description;
    }

    public String getIreport_description() {
        return ireport_description;
    }

    public String getIreport_building() {
        return ireport_building;
    }

    public String getIreport_street() {
        return ireport_street;
    }

    public String getIreport_area() {
        return ireport_area;
    }

    public String getIreport_zone() {
        return ireport_zone;
    }

    public void setCurrentBillValue(Float _CurrentBillValue)
    //public void setCurrentBillValue(int _CurrentBillValue)
    {
        this.currentBillValue = _CurrentBillValue;
    }
    /*public int getCurrentBillValue()
    {
        return currentBillValue;
    }*/
    public Float getCurrentBillValue()
    {
        return currentBillValue;
    }



    public void setActiveBuildingCategoryId(String _buildingCategoryid)
    {
        this.activeBuildingCategoryId = _buildingCategoryid;
    }
    public String getActiveBuildingCategoryId()
    {
        return activeBuildingCategoryId;
    }

    public void setActiveBuildingCategoryName(String _buildingCategoryName)
    {
        this.activeBuildingCategoryName = _buildingCategoryName;
    }
    public String getActiveBuildingCategoryName()
    {
        return activeBuildingCategoryName;
    }

    public String getActiveMeterNo() {
        return activeMeterNo;
    }

    public void setActiveMeterNo(String _activeMeterNo) {
        this.activeMeterNo = _activeMeterNo;
    }

    public void setActivity(Activity activity)
    {
        this.activity = null;
        this.activity = activity;
    }
    public Activity getActivity()
    {
        return activity;
    }

    public String getActiveLatitude() {
        return activeLatitude;
    }

    public void setActiveLatitude(String activeLatitude) {
        this.activeLatitude = activeLatitude;
    }

    public String getActiveLongitude() {
        return activeLongitude;
    }

    public void setActiveLongitude(String activeLongitude) {
        this.activeLongitude = activeLongitude;
    }

    public byte[] getSampleByteImage() {
        return sampleByteImage;
    }

    public void setSampleByteImage(byte[] sampleByteImage) {
        this.sampleByteImage = sampleByteImage;
    }

    public String getAccountNoSearchCriteria() {
        return accountNoSearchCriteria;
    }

    public void setAccountNoSearchCriteria(String accountNoSearchCriteria) {
        this.accountNoSearchCriteria = accountNoSearchCriteria;
    }

    public String getMeterNoSearchCrieria() {
        return meterNoSearchCrieria;
    }

    public void setMeterNoSearchCrieria(String meterNoSearchCrieria) {
        this.meterNoSearchCrieria = meterNoSearchCrieria;
    }
}
