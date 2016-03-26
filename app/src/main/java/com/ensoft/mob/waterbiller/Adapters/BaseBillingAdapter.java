package com.ensoft.mob.waterbiller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ensoft.mob.waterbiller.Models.Bill;
import com.ensoft.mob.waterbiller.Models.Building;
import com.ensoft.mob.waterbiller.MyApp;
import com.ensoft.mob.waterbiller.R;

import java.util.ArrayList;

/**
 * Created by Ebuka on 06/10/2015.
 */
public class BaseBillingAdapter extends BaseAdapter {
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ArrayList<Bill> myList = new ArrayList<Bill>();
    LayoutInflater inflater;
    Context context;
    MyApp app;

    public BaseBillingAdapter(Context context, ArrayList<Bill> myList) {
        this.myList = myList;
        this.context = context;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        //return 0;
        return myList.size();
    }

    @Override
    public Bill getItem(int position) {
        //return null;
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        View view;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.billing_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        Bill currentListData = getItem(position);
        mViewHolder.tvMeterNo.setText(currentListData.getMeter_no() != null ? currentListData.getMeter_no() : "None");
        mViewHolder.tvAccountNo.setText(currentListData.getAccount_no() != null ? currentListData.getAccount_no() : "None");
        mViewHolder.tvAmount.setText(String.valueOf(currentListData.getAmountdue()) != null ? "N" + String.valueOf(currentListData.getAmountdue()) : "0.00");
        //mViewHolder.tvName.setText(currentListData.getCustomer_name());
        //mViewHolder.tvBillDate.setText(currentListData.getInvoice_date());
        //for the icon drawable
        char myChar = currentListData.getAccount_no().charAt(0);
        String str = Character.toString(myChar).toUpperCase();
        int tColor = mColorGenerator.getColor(str);
        TextDrawable drawable = TextDrawable.builder().buildRect(str, tColor);

        mViewHolder.ivAccountIcon.setImageDrawable(drawable);

        return convertView;
    }

    private class MyViewHolder {

        TextView tvAmount, tvAccountNo, tvMeterNo,tvName, tvBillDate;
        ImageView ivAccountIcon;

        public MyViewHolder(View item) {
            tvAccountNo = (TextView)item.findViewById(R.id.textViewAccountNo);
            tvAmount = (TextView)item.findViewById(R.id.textViewAmount);
            tvMeterNo = (TextView)item.findViewById(R.id.textViewMeterNo);
            ivAccountIcon = (ImageView) item.findViewById(R.id.ivAccountIcon);
            //tvName = (TextView) item.findViewById(R.id.textViewName);
            //tvBillDate = (TextView)item.findViewById(R.id.textViewBillDate);
        }
    }
}
