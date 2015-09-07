package com.example.tom.regensbad.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.tom.regensbad.Domain.CommentRating;
import com.example.tom.regensbad.R;

import org.w3c.dom.Text;

import java.util.ArrayList;


/**
 * Created by Tom on 05.09.2015.
 */
public class CommentAdapter extends ArrayAdapter<CommentRating> {

    private static final int THUMBS_WIDTH = 36;
    private static final int THUMBS_HEIGHT = 33;

    private Context context;
    private ArrayList<CommentRating> commentRatingArrayList;
    private int customLayoutResource;

    public CommentAdapter (Context context, ArrayList<CommentRating> commentRatingArrayList) {
        super (context, R.layout.single_comment_rating_item, commentRatingArrayList);
        this.context = context;
        this.commentRatingArrayList = commentRatingArrayList;
        this.customLayoutResource = R.layout.single_comment_rating_item;
    }


    public View getView (int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if(view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(customLayoutResource, null);
        }

        CommentRating commentRating = commentRatingArrayList.get(position);

        if (commentRating != null) {
            TextView userName = (TextView)view.findViewById(R.id.text_view_username_comment_single);
            RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingbar_comment_rating_single);
            TextView comment = (TextView)view.findViewById(R.id.text_view_comment_single);
            TextView date = (TextView)view.findViewById(R.id.text_view_comment_date_single);
            ImageView thumbsUp = (ImageView)view.findViewById(R.id.image_view_thumbs_up);

            userName.setText(commentRating.getUserName());
            ratingBar.setRating(commentRating.getRating());
            comment.setText(commentRating.getComment());
            date.setText(String.valueOf(commentRating.getDate()));
            thumbsUp.getLayoutParams().height = THUMBS_HEIGHT;
            thumbsUp.getLayoutParams().width = THUMBS_WIDTH;

            // hier onclicklsitener on thumbsup, dann noch nen textview, der die anzahl der upvotes anzeigt und alles auf parse kommunizieren. sweeeeet.
        }
        return view;
    }




}
