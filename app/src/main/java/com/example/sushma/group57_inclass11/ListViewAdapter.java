package com.example.sushma.group57_inclass11;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.List;
import java.util.Locale;

/**
 * Created by Vinayak on 11/14/2016.
 */
public class ListViewAdapter extends ArrayAdapter<Message> {

    List<Message> mData;
    Context mContext;
    int mResource;

    public ListViewAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mData = objects;
        this.mResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource,parent,false);
        }

        Message message = mData.get(position);

        TextView tv_msg = (TextView) convertView.findViewById(R.id.tv_chat_msg);
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name_chat);
        TextView tv_date = (TextView) convertView.findViewById(R.id.tv_chat_date);
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageViewChat);

        tv_msg.setText(message.getMessageText());
        tv_name.setText(message.getFname() + " " + message.getLname());

        PrettyTime prettyTime = new PrettyTime(new Locale("DEFAULT"));
        tv_date.setText(prettyTime.format(message.getChatDate()));

        Picasso.with(mContext).load(message.getThumbNail()).into(iv);
        return convertView;
    }
}
