package com.together.raz.together.Painters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.together.raz.together.AsyncTasks.EditQuote;
import com.together.raz.together.AsyncTasks.GetEntity;
import com.together.raz.together.Entities.Comment;
import com.together.raz.together.Entities.Quote;
import com.together.raz.together.Entities.UserInfo;
import com.together.raz.together.Enums.Account;
import com.together.raz.together.Interfaces.AsyncResponse;
import com.together.raz.together.Interfaces.Cookied;
import com.together.raz.together.Packs.QuotePack;
import com.together.raz.together.R;
import com.together.raz.together.Tools.ImageBase64;

import java.util.List;

/**
 * Created by Raz on 3/9/2017.
 */
public class QuotesPainter {


    private final Activity activity;
    private final AsyncResponse response;
    private final Cookied cookied;
    private final LayoutInflater inflater;
    private UserInfo user;
    private String TAG="QuotesPainter";

    public QuotesPainter(Activity activity, AsyncResponse response, Cookied cookied, LayoutInflater inflater
            , UserInfo user) {
        this.activity = activity;
        this.response = response;
        this.cookied = cookied;
        this.inflater = inflater;
        this.user = user;
    }

    public void paint(View child, List<Quote> quotes) {
        LinearLayout layout = (LinearLayout) child.findViewById(R.id.quotes_container);
        layout.removeAllViews();
        for(int i = 0; i < quotes.size(); i++){
            View quoteLayout = inflater.inflate(R.layout.quote_layout,null);
            if(quoteLayout==null) continue;

            setQuoteDetails(quoteLayout, quotes.get(i));

            layout.addView(quoteLayout);
            if(i!=quotes.size()-1) layout.addView(createDivider());
        }
    }

    private View createDivider() {
        View view = inflater.inflate(R.layout.quote_divider,null);
        return view;
    }

    private void setQuoteDetails(View quoteLayout, Quote quote) {
        final TextView editAuthorBtn = (TextView) quoteLayout.findViewById(R.id.quote_author_edit_btn);
        final TextView editContentBtn = (TextView) quoteLayout.findViewById(R.id.quote_content_edit_btn);
        final TextView delContentBtn = (TextView) quoteLayout.findViewById(R.id.quote_content_delete_btn);
        final TextView content = (TextView) quoteLayout.findViewById(R.id.quote_content);
        final TextView author = (TextView) quoteLayout.findViewById(R.id.quote_author);
        final ViewSwitcher switcherContent = (ViewSwitcher) quoteLayout.findViewById(R.id.quote_content_switcher);
        final ViewSwitcher switcherAuthor = (ViewSwitcher) quoteLayout.findViewById(R.id.quote_author_switcher);
        final EditText contentEditBox = (EditText) quoteLayout.findViewById(R.id.quote_content_textbox);
        final EditText authorEditBox = (EditText) quoteLayout.findViewById(R.id.quote_author_textbox);
        final LinearLayout container = (LinearLayout) quoteLayout.findViewById(R.id.quote_item_container);

        setButtons(editAuthorBtn, editContentBtn, delContentBtn, switcherAuthor,
                switcherContent,contentEditBox,authorEditBox, quote);

        if(content!= null && contentEditBox != null && quote.getContent()!=null){
            content.setText(quote.getContent().toString());
            contentEditBox.setText(quote.getContent().toString());
        }
        if(author!= null && authorEditBox != null && quote.getCreator() !=null){
            author.setText(quote.getCreator().toString());
            authorEditBox.setText(quote.getCreator().toString());
        }
        if(user.getAccount()==Account.CHILD){
            editAuthorBtn.setVisibility(View.GONE);
            editContentBtn.setVisibility(View.GONE);
            delContentBtn.setVisibility(View.GONE);
        }
//        container.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(container.getHeight()< 400){
//                    container.setMinimumHeight(800);
//                } else {
//                    container.setMinimumHeight(300);
//                }
//            }
//        });
    }

    private void setButtons(final TextView editAuthorBtn,final TextView editContentBtn,final TextView delContentBtn,
                            final ViewSwitcher switcherAuthor, final ViewSwitcher switcherContent,
                            final EditText contentEditBox, final EditText authorEditBox,final Quote quote) {
        editAuthorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAuthorBtn.getText().toString().equals(activity.getResources().getString(R.string.edit_text))){
                    switcherAuthor.showNext();
                    editAuthorBtn.setText(activity.getResources().getString(R.string.done_text));
                    contentEditBox.setVisibility(View.VISIBLE);
                    authorEditBox.setVisibility(View.VISIBLE);
                } else {
                    EditQuote(quote.getNum(), contentEditBox.getText().toString(), authorEditBox.getText().toString());
                    switcherAuthor.showNext();
                    editAuthorBtn.setText(activity.getResources().getString(R.string.edit_text));
                    if(editContentBtn.getText().toString().equals(activity.getResources().getString(R.string.edit_text))){
                        switcherContent.showNext();
                        editContentBtn.setText(activity.getResources().getString(R.string.edit_text));
                    }
                    contentEditBox.setText(activity.getResources().getString(R.string.empty));
                    authorEditBox.setText(activity.getResources().getString(R.string.empty));
                    contentEditBox.setVisibility(View.GONE);
                    authorEditBox.setVisibility(View.GONE);
                }
            }
        });
        editContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editContentBtn.getText().toString().equals(activity.getResources().getString(R.string.edit_text))){
                    switcherContent.showNext();
                    editContentBtn.setText(activity.getResources().getString(R.string.done_text));
                    contentEditBox.setVisibility(View.VISIBLE);
                    authorEditBox.setVisibility(View.VISIBLE);
                } else {
                    EditQuote(quote.getNum(), contentEditBox.getText().toString(),authorEditBox.getText().toString());
                    switcherContent.showNext();
                    editContentBtn.setText(activity.getResources().getString(R.string.edit_text));
                    if(editAuthorBtn.getText().toString().equals(activity.getResources().getString(R.string.edit_text))){
                        switcherAuthor.showNext();
                        editAuthorBtn.setText(activity.getResources().getString(R.string.edit_text));
                    }
                    contentEditBox.setText(activity.getResources().getString(R.string.empty));
                    authorEditBox.setText(activity.getResources().getString(R.string.empty));
                    contentEditBox.setVisibility(View.GONE);
                    authorEditBox.setVisibility(View.GONE);
                }
            }
        });
        delContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteQuote(quote.getNum());
            }
        });
    }

    private void EditQuote(String num, String content, String author) {
        EditQuote editQuote = new EditQuote(response, activity.getResources().getString(R.string.key_edit_quote),
                cookied, activity.getResources().getString(R.string.getcookie),
                activity.getResources().getString(R.string.setCookie),activity);
        String[] keys = {
                activity.getResources().getString(R.string.key_quote_num),
                activity.getResources().getString(R.string.key_quote_content),
                activity.getResources().getString(R.string.key_quote_creator),
        };
        String editQuoteUrl = activity.getResources().getString(R.string.request_edit_quote_url);
        editQuote.execute(new QuotePack[]{new QuotePack(editQuoteUrl, new Quote(num, content, author), keys)});
    }

    private void DeleteQuote(String num) {
        String key = activity.getResources().getString(R.string.key_del_quote);
        GetEntity getEntity = new GetEntity(response,key,cookied,
                activity.getResources().getString(R.string.getcookie),
                activity.getResources().getString(R.string.setCookie));
        String delQuoteUrl = activity.getResources().getString(R.string.request_del_quote_url)
                + num;
        getEntity.execute(new String[]{delQuoteUrl});
    }

    private void setImageAndLink(View child, Comment comment) {
        final View _view = child;
        TextView link = (TextView) child.findViewById(R.id.comment_link);
        ImageView image = (ImageView) child.findViewById(R.id.comment_image);
        if(link !=null && !comment.getLink().equals("")) link.setText(activity.getResources().getString(R.string.link_text));
        if(image !=null && !comment.getImage().equals("") && comment.getImage().length() > 30) {
            Log.d("IMAGE",comment.getImage());
            image.getLayoutParams().width = Integer.valueOf(comment.getImageX());
            image.getLayoutParams().height = Integer.valueOf(comment.getImageY());
            image.requestLayout();
            Bitmap bitmap = ImageBase64.decodeBase64(comment.getImage(), activity);
            Drawable dr = new BitmapDrawable(bitmap);
            image.setBackground(dr);
        }
        if(comment.getLink().equals("")) _view.findViewById(R.id.link_layout_in_comment).setVisibility(View.INVISIBLE);
        final String url = comment.getLink();
        link.setOnClickListener(new View.OnClickListener() {
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
        if(comment.getLink().equals("")) child.findViewById(R.id.link_layout_in_comment).setVisibility(View.GONE);
    }
}
