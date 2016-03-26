package com.ensoft.mob.waterbiller.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ensoft.mob.waterbiller.Models.Street;
import com.ensoft.mob.waterbiller.R;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

import java.util.ArrayList;

/**
 * Created by Ebuka on 01/09/2015.
 */
public class BaseStreetAdapter extends BaseAdapter {
    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ArrayList<Street> myList = new ArrayList<Street>();
    LayoutInflater inflater;
    Context context;


    public BaseStreetAdapter(Context context, ArrayList<Street> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Street getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MyViewHolder mViewHolder;
        View view;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.street_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Street currentListData = getItem(position);
        String commnt,buildngs;
        mViewHolder.tvStreetID.setText(currentListData.getStreetid());
        mViewHolder.tvStreetName.setText(currentListData.getStreetname());
        if(Integer.parseInt(currentListData.getTotal_account()) <=1)
        {
            commnt = "account";
        }
        else {
            commnt = "accounts";
        }
        mViewHolder.tvAcctNo.setText(currentListData.getTotal_account() + " "+commnt);
        if(Integer.parseInt(currentListData.getTotal_building()) <= 1)
        {
            buildngs = "building";
        }
        else
        {
            buildngs = "buildings";
        }
        mViewHolder.tvBuildingNo.setText(currentListData.getTotal_building() + " " + buildngs);
        char myChar = currentListData.getStreetname().charAt(0);
        String str = Character.toString(myChar).toUpperCase();
        int tColor = mColorGenerator.getColor(str);
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(str, tColor);
        mViewHolder.ivIcon.setImageDrawable(drawable);
        //mViewHolder.ivIcon.setImageDrawable(mDrawableBuilder.build("A", 0xff616161));

        return convertView;
    }

    private class MyViewHolder {
        /*TextView tvStreetID = (TextView) convertView.findViewById(R.id.textViewStreetid);
        tvStreetID.setText(street.getStreetid());
        TextView tvStreetName = (TextView) convertView.findViewById(R.id.textViewStreetid);
        tvStreetName.setText(street.getStreetname());
        //TextView tvAreaCodeID = (TextView) convertView.findViewById(R.id.textViewStreetid);
        TextView tvAcctNo = (TextView) convertView.findViewById(R.id.textViewTotalAccount);
        tvAcctNo.setText(street.getTotal_account());*/

        TextView tvStreetID, tvStreetName, tvAcctNo, tvBuildingNo;
        ImageView ivIcon;

        public MyViewHolder(View item) {
            tvStreetID = (TextView)item.findViewById(R.id.textViewStreetid);
            tvStreetName = (TextView)item.findViewById(R.id.textViewStreetName);
            tvAcctNo = (TextView)item.findViewById(R.id.textViewTotalAccount);
            tvBuildingNo = (TextView)item.findViewById(R.id.textViewTotalBuilding);
            ivIcon = (ImageView) item.findViewById(R.id.ivIcon);
        }
    }
}
