package com.together.raz.together.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.together.raz.together.Entities.Message;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.R;
import com.together.raz.together.Tools.ImageBase64;
import com.together.raz.together.Tools.Time;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Raz on 3/17/2017.
 */
public class MessageAdapter extends BaseAdapter{
    private Activity activity;
    private LayoutInflater inflator;
    private List<Message> msgs = new ArrayList<>();
    private UserInfo user;
    private AsyncResponse response;
    private Cookied cookied;

    public MessageAdapter(Activity activity, List<Message> msgs, UserInfo user, AsyncResponse response, Cookied cookied) {
        this.activity = activity;
        this.inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.msgs = msgs;
        this.user = user;
        this.response = response;
        this.cookied = cookied;
    }

    @Override
    public int getCount() {
        return msgs.size();
    }

    @Override
    public Object getItem(int position) {
        return msgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message msg = msgs.get(position);
        if(convertView==null)
            convertView = this.inflator.inflate(R.layout.message_layout, null);
        LinearLayout messageMainLayout = (LinearLayout) convertView.findViewById(R.id.chat_message_orientation);
        LinearLayout messageLayout = (LinearLayout) convertView.findViewById(R.id.chat_message_layout);
        LinearLayout textOrientationLayout = (LinearLayout) convertView.findViewById(R.id.text_orienatation);
        TextView chatMessageContent = (TextView) convertView.findViewById(R.id.chat_message_content);
        TextView chatMessageTime = (TextView) convertView.findViewById(R.id.chat_message_time);
        TextView chatMessageAuthor = (TextView) convertView.findViewById(R.id.chat_message_author);
        LinearLayout messageLinkLayout = (LinearLayout) convertView.findViewById(R.id.message_link_layout);
        TextView chatMessageLink = (TextView) convertView.findViewById(R.id.chat_message_link);
        ImageView chatMessageImage = (ImageView) convertView.findViewById(R.id.chat_message_image);

        if(msg.getMessage()!=null && chatMessageContent!=null) chatMessageContent.setText(msg.getMessage().toString());
        if(msg.getAuthorName()!=null && chatMessageAuthor!=null) chatMessageAuthor.setText(msg.getAuthorName());
        if(msg.getTime()!=null && chatMessageTime!=null)
            Time.setTime(activity, chatMessageTime, Calendar.getInstance().getTimeInMillis(),
                    msg.getTime(), activity.getResources().getString(R.string.date_ago), "");
        Log.d("IMAGE",position + "  " +msg.getNum() + "  " +msg.getImage());
        if(msg.getImage()!=null && !msg.getImage().equals("") && chatMessageImage!=null && msg.getImage().length()>30){
            setImage(chatMessageImage, msg);
//            LayoutWrapContentUpdater.wrapContentAgain(messageMainLayout);
//            LayoutWrapContentUpdater.wrapContentAgain(messageLayout);
        } else if (chatMessageImage!= null){
            chatMessageImage.setVisibility(View.GONE);
        }
        if(msg.getLink()!=null && !msg.getLink().equals("") && chatMessageLink!=null){
            setLink(chatMessageLink, msg.getLink());
            messageLinkLayout.setVisibility(View.VISIBLE);
        } else {
            messageLinkLayout.setVisibility(View.GONE);
        }
        setCorrectOrientation(messageMainLayout,messageLayout,textOrientationLayout,
                (LinearLayout) convertView.findViewById(R.id.chat_link_container),user.getId(), msg.getAuthorID());
        return convertView;
    }

    private void setCorrectOrientation(LinearLayout messageMainLayout,LinearLayout messageLayout,
                                       LinearLayout textOriantation,
                                       LinearLayout messageLinkLayout, String id, String authorID) {
        if(id.equals(authorID)){
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            messageMainLayout.setLayoutParams(params);
            messageLayout.setGravity(Gravity.RIGHT);
            messageLinkLayout.setGravity(Gravity.RIGHT);
            textOriantation.setGravity(Gravity.RIGHT);

            messageLayout.setBackground(activity.getResources().getDrawable(R.drawable.message_background1));
        } else {
            RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            messageMainLayout.setLayoutParams(params);
            messageLayout.setGravity(Gravity.LEFT);
            messageLayout.setBackground(activity.getResources().getDrawable(R.drawable.message_background));
        }
    }


    private void setLink(TextView textView, String link) {
        final String url = link;
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (url != "" && url.startsWith("http")) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    activity.startActivity(browserIntent);
                } else if (url != "") {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://" + url));
                    activity.startActivity(browserIntent);
                }
            }
        });
    }

    private void setImage(ImageView image,Message msg) {
        image.getLayoutParams().width = Integer.valueOf(msg.getImageX());
        image.getLayoutParams().height = Integer.valueOf(msg.getImageY());
        image.requestLayout();
        Bitmap bitmap = ImageBase64.decodeBase64(msg.getImage(), activity);
        Drawable dr = new BitmapDrawable(bitmap);
        image.setBackground(dr);
    }

    public void setList(List<Message> list) {
        this.msgs = list;
    }
}
