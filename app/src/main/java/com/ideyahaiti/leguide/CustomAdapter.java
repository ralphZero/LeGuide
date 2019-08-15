package com.ideyahaiti.leguide;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomAdapter extends ArrayAdapter<BusinessInfo> implements Filterable {

    Context context;
    List<BusinessInfo> arrayList;
    ArrayList<BusinessInfo> arrayPam;

    public CustomAdapter(MainActivity context, List<BusinessInfo> list) {
        super(context, R.layout.custom_listview, list);
        this.context = context;
        this.arrayList = list;
        this.arrayPam = new ArrayList<>();
        this.arrayPam.addAll(list);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.custom_listview,parent,false);

        TextView textView = (TextView) convertView.findViewById(R.id.tv_name);
        textView.setText(arrayList.get(position).getName());

        TextView textView1 = (TextView) convertView.findViewById(R.id.tv_address);
        textView1.setText(arrayList.get(position).getAddress());

        return convertView;
    }


    public void filter(String chartext)
    {
        chartext = chartext.toLowerCase(Locale.getDefault());
        arrayList.clear();
        if(chartext.length()==0)
        {
            arrayList.addAll(arrayPam);
        }
        else
        {
            for (BusinessInfo customModelList : arrayPam)
            {
                if (customModelList.getName().toLowerCase(Locale.getDefault()).contains(chartext))
                {
                    arrayList.add(customModelList);
                }
            }
        }
        notifyDataSetChanged();
    }
}
