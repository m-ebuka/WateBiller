package com.ensoft.mob.waterbiller.Models;

/**
 * Created by Ebuka on 08/10/2015.
 */
public class Bill {
    String meter_no;
    double amountdue;
    double previous_reading;
    double current_reading;
    double rate;
    int totalunit;
    String building_id;
    double previous_charge;
    String areacodeid;
    String reading_timestamp;
    String billing_no;
    String billing_period;
    String bill_duedate;
    String date_previous_reading;
    String date_current_reading;
    String invoice_date;

    String account_no;
    String customer_name;
    String building_no;



    public String getBilling_no() {
        return billing_no;
    }

    public void setBilling_no(String billing_no) {
        this.billing_no = billing_no;
    }

    public String getBilling_period() {
        return billing_period;
    }

    public void setBilling_period(String billing_period) {
        this.billing_period = billing_period;
    }

    public String getDate_previous_reading() {
        return date_previous_reading;
    }

    public void setDate_previous_reading(String date_previous_reading) {
        this.date_previous_reading = date_previous_reading;
    }

    public String getDate_current_reading() {
        return date_current_reading;
    }

    public void setDate_current_reading(String date_current_reading) {
        this.date_current_reading = date_current_reading;
    }

    public String getBill_duedate() {
        return bill_duedate;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public void setBill_duedate(String bill_duedate) {
        this.bill_duedate = bill_duedate;
    }

    public String getAccount_no() {
        return account_no;
    }

    public void setAccount_no(String account_no) {
        this.account_no = account_no;
    }

    public String getMeter_no() {
        return meter_no;
    }

    public void setMeter_no(String meter_no) {
        this.meter_no = meter_no;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getAreacodeid() {
        return areacodeid;
    }

    public void setAreacodeid(String areacodeid) {
        this.areacodeid = areacodeid;
    }

    public String getBuilding_no() {
        return building_no;
    }

    public void setBuilding_no(String building_no) {
        this.building_no = building_no;
    }

    public String getBuilding_id() {
        return building_id;
    }

    public void setBuilding_id(String building_id) {
        this.building_id = building_id;
    }

    public int getTotalunit() {
        return totalunit;
    }

    public void setTotalunit(int totalunit) {
        this.totalunit = totalunit;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getPrevious_reading() {
        return previous_reading;
    }

    public void setPrevious_reading(double previous_reading) {
        this.previous_reading = previous_reading;
    }

    public double getCurrent_reading() {
        return current_reading;
    }

    public void setCurrent_reading(double current_reading) {
        this.current_reading = current_reading;
    }

    public double getAmountdue() {
        return amountdue;
    }

    public void setAmountdue(double amountdue) {
        this.amountdue = amountdue;
    }

    public double getPrevious_charge() {
        return previous_charge;
    }

    public void setPrevious_charge(double previous_charge) {
        this.previous_charge = previous_charge;
    }

    public String getReading_timestamp() {
        return reading_timestamp;
    }

    public void setReading_timestamp(String reading_timestamp) {
        this.reading_timestamp = reading_timestamp;
    }
}
