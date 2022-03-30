package com.toptop.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.ClipData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.toptop.R;
import com.toptop.models.Notification;
import com.toptop.models.Video;

import java.util.ArrayList;
import java.util.List;


public class NotificationAdapter extends ArrayAdapter<Notification> {
    Fragment context;
    int resource;

    public NotificationAdapter(Fragment context , int resource) {
        super(context.getContext(), resource);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        LayoutInflater inflater = context.getLayoutInflater();
        View v = inflater.inflate(resource, null);

        Notification notification = getItem(position);

        CircularImageView avatar = v.findViewById(R.id.img_avatarNotification);
        TextView username = v.findViewById(R.id.txt_usernameNotification);
        TextView content = v.findViewById(R.id.txt_contentNotification);
        TextView time = v.findViewById(R.id.txt_timeNotification);
        ImageView img_notification = v.findViewById(R.id.img_Notification);

        avatar.setImageResource(notification.getAvatar());
        username.setText(notification.getUsername());
        content.setText(notification.getContent());
        time.setText(notification.getTime_notification());
        img_notification.setImageResource(notification.getImage_notification());
        return v;

    }
}
