package com.example.tom.regensbad.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tom.regensbad.Domain.CommentRating;
import com.example.tom.regensbad.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tom on 05.09.2015.
 */
public class CommentAdapter extends ArrayAdapter<CommentRating> {


    private Context context;
    private ArrayList<CommentRating> commentRatingArrayList;
    private int customLayoutResource;


    private String nameToUpdate;
    private String dateToUpdate;
    private String commentToUpdate;


    public CommentAdapter (Context context, ArrayList<CommentRating> commentRatingArrayList) {
        super (context, R.layout.single_comment_rating_item, commentRatingArrayList);
        this.context = context;
        this.commentRatingArrayList = commentRatingArrayList;
        this.customLayoutResource = R.layout.single_comment_rating_item;
    }



    public View getView (int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(customLayoutResource, null);
        }

        CommentRating commentRating = commentRatingArrayList.get(position);

        if (commentRating != null) {
            TextView userName = (TextView) view.findViewById(R.id.text_view_username_comment_single);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingbar_comment_rating_single);
            TextView comment = (TextView) view.findViewById(R.id.text_view_comment_single);
            TextView date = (TextView) view.findViewById(R.id.text_view_comment_date_single);

            userName.setText(commentRating.getUserName());
            ratingBar.setRating(commentRating.getRating());
            comment.setText(commentRating.getComment());
            date.setText(String.valueOf(commentRating.getDate()));
        }
        return view;
    }




}
