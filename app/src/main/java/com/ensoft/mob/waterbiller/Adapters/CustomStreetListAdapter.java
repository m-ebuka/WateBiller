package com.ensoft.mob.waterbiller.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ensoft.mob.waterbiller.Models.Street;
import com.ensoft.mob.waterbiller.R;

import java.util.ArrayList;

/**
 * Created by Ebuka on 31/08/2015.
 */
public class CustomStreetListAdapter extends BaseAdapter {
    Context context;
    ArrayList<Street> streetList;

    public CustomStreetListAdapter(Context context,ArrayList<Street> list) {
        this.streetList = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        //return 0;
        return streetList.size();
    }

    @Override
    public Object getItem(int position) {
        //return null;
        return streetList.get(position);
    }

    @Override
    public long getItemId(int position) {
        //return 0;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return null;
        Street street = streetList.get(position);
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater)context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.street_list_item,null);
        }

        TextView tvStreetID = (TextView) convertView.findViewById(R.id.textViewStreetid);
        tvStreetID.setText(street.getStreetid());
        TextView tvStreetName = (TextView) convertView.findViewById(R.id.textViewStreetName);
        tvStreetName.setText(street.getStreetname());
        //TextView tvAreaCodeID = (TextView) convertView.findViewById(R.id.textViewStreetid);
        TextView tvAcctNo = (TextView) convertView.findViewById(R.id.textViewTotalAccount);
        tvAcctNo.setText(street.getTotal_account());
        return  convertView;
    }
}
