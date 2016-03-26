package com.ensoft.mob.waterbiller.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.ensoft.mob.waterbiller.BillingActivity;
import com.ensoft.mob.waterbiller.BuildingMapLocationActivity;
import com.ensoft.mob.waterbiller.Models.Building;
import com.ensoft.mob.waterbiller.Models.Street;
import com.ensoft.mob.waterbiller.MyApp;
import com.ensoft.mob.waterbiller.R;
import com.ensoft.mob.waterbiller.helpers.GenericHelper;
import com.ensoft.mob.waterbiller.helpers.Touch;
import com.loopj.android.http.Base64;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

/**
 * Created by Ebuka on 03/09/2015.
 */
public class BaseBuildingAdapter extends BaseAdapter {
    // declare the color generator and drawable builder
    private ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
    private TextDrawable.IBuilder mDrawableBuilder;
    ArrayList<Building> myList = new ArrayList<Building>();
    LayoutInflater inflater;
    Context context;
    MyApp app;
    String lat,lon;


    public BaseBuildingAdapter(Context context, ArrayList<Building> myList) {
        this.myList = myList;
        this.context = context;

        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Building getItem(int position) {
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
            convertView = inflater.inflate(R.layout.building_list_item, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        Building currentListData = getItem(position);
        String commnt;
        mViewHolder.tvHouseNo.setText(currentListData.getBuilding_no());
        mViewHolder.tvAccountNo.setText(currentListData.getAccount_no() != null ? currentListData.getAccount_no() : "None");
        mViewHolder.tvMeterNo.setText(currentListData.getMeter_no() != null ? currentListData.getMeter_no():"None");
        lat = currentListData.getLatitude() != null && !currentListData.getLatitude().isEmpty() ? currentListData.getLatitude() : "1.234556";
        lon = currentListData.getLongitude() != null && !currentListData.getLongitude().isEmpty() ? currentListData.getLongitude() : "7.234556";

        if (currentListData.getBuilding_image() != null && !currentListData.getBuilding_image().isEmpty()) {
            System.out.println("Yes Building Picture from db : " + currentListData.getBuilding_image());
            //byte[] imageData = currentListData.getBuilding_image().getBytes();
            //ByteArrayInputStream imageStream = new ByteArrayInputStream(imageData);

            byte[] decoded64String = Base64.decode(currentListData.getBuilding_image(), Base64.NO_WRAP);//currentListData.getBuilding_image()
            Bitmap bmp = BitmapFactory.decodeByteArray(decoded64String, 0, decoded64String.length);

            //Bitmap theImage= BitmapFactory.decodeStream(imageStream);
            mViewHolder.ivBuildingIcon.setImageBitmap(bmp);

            /*mViewHolder.ivBuildingIcon.setImageBitmap(
                    BitmapFactory.decodeByteArray(imageData, 0, imageData.length)
            );*/
            //using hex
            //byte[] bImage = GenericHelper.hexStringToByteArray(hexMan);
            /*if(currentListData.getSampleByteMan().length > 0 && currentListData.getSampleByteMan() != null) {
                Bitmap bmp = BitmapFactory.decodeByteArray(currentListData.getSampleByteMan(), 0, currentListData.getSampleByteMan().length);
                mViewHolder.ivBuildingIcon.setImageBitmap(bmp);
            }*/

            //using base64
            /*byte[] decodedString = Base64.decode(base64Man, Base64.URL_SAFE);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            mViewHolder.ivBuildingIcon.setImageBitmap(decodedByte);*/

        }
        else{
            int tColor = mColorGenerator.getColor("NO");
            TextDrawable drawable = TextDrawable.builder()
                    .buildRect("B", tColor);
            mViewHolder.ivBuildingIcon.setImageDrawable(drawable);

        }

        mViewHolder.ivBuildingIcon.setTag(new Integer(position));
        mViewHolder.ivBuildingIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Image clicked for the row = "+view.getTag().toString(), Toast.LENGTH_SHORT).show();

            }
        });
        mViewHolder.ivBuildingIcon.setOnTouchListener(new Touch());

        //byte[] imageData = app.getCameraByteForMeterReading();

        //Bitmap bmp = BitmapFactory.decodeByteArray(currentListData.getBuilding_image(), 0, currentListData.getBuilding_image().length);

        //mViewHolder.ivBuildingIcon.setImageBitmap(bmp);

        mViewHolder.ivBuildingMapIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                //to get a specific item
                Toast.makeText(context, "You clicked to use GIS; " + lat + " "+lon, Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(context, BuildingMapLocationActivity.class);
                    intent.putExtra("mlatitude",lat);
                    intent.putExtra("mlongitude",lon);
                    context.startActivity(intent);


            }
        });

        return convertView;
    }


    private class MyViewHolder {

        TextView tvHouseNo, tvAccountNo, tvMeterNo;
        ImageView ivBuildingIcon,ivBuildingMapIcon;

        public MyViewHolder(View item) {
            tvHouseNo = (TextView)item.findViewById(R.id.textViewHouseNumber);
            tvAccountNo = (TextView)item.findViewById(R.id.textViewAccountNumber);
            tvMeterNo = (TextView)item.findViewById(R.id.textViewMeterNo);
            ivBuildingIcon = (ImageView) item.findViewById(R.id.ivBuildingIcon);
            ivBuildingMapIcon = (ImageView) item.findViewById(R.id.ivBuildingMapIcon);
        }
    }
}
