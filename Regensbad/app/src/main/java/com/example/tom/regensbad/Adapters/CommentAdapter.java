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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Tom on 05.09.2015.
 */
public class CommentAdapter extends ArrayAdapter<CommentRating> {

    private static final int THUMBS_WIDTH = 36;
    private static final int THUMBS_HEIGHT = 33;
    private static final int PARSE_ZERO_UP_VOTES = 1;


    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_UP_VOTES = "upVotes";
    private static final String PARSE_USERNAME = "userName";
    private static final String PARSE_DATE = "date";
    private static final String PARSE_COMMENT = "comment";
    private static final String PARSE_RATING = "rating";
    private static final String PARSE_CREATED_AT = "createdAt";
    private static final String PARSE_CURRENT_RATING = "currentRating";

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

        final CommentRating commentRating = commentRatingArrayList.get(position);

        if (commentRating != null) {
            TextView userName = (TextView)view.findViewById(R.id.text_view_username_comment_single);
            RatingBar ratingBar = (RatingBar)view.findViewById(R.id.ratingbar_comment_rating_single);
            final TextView comment = (TextView)view.findViewById(R.id.text_view_comment_single);
            final TextView date = (TextView)view.findViewById(R.id.text_view_comment_date_single);
            final TextView upVotes = (TextView)view.findViewById(R.id.text_view_comment_upvotes);
            final ImageView thumbsUp = (ImageView)view.findViewById(R.id.image_view_thumbs_up);


            userName.setText(commentRating.getUserName());
            final String nameToUpdate = commentRating.getUserName();
            ratingBar.setRating(commentRating.getRating());
            comment.setText(commentRating.getComment());
            final String commentToUpdate = commentRating.getComment();
            date.setText(String.valueOf(commentRating.getDate()));
            final String dateToUpdate = commentRating.getDate();
            upVotes.setText(String.valueOf(commentRating.getUpVotes()));
            final int upVotesToUpdate = commentRating.getUpVotes();
            thumbsUp.getLayoutParams().height = THUMBS_HEIGHT;
            thumbsUp.getLayoutParams().width = THUMBS_WIDTH;
            thumbsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onePlusOnUpVotesOnParse(nameToUpdate, commentToUpdate, dateToUpdate);
                    upVotes.setText(String.valueOf(upVotesToUpdate + 1));
                    thumbsUp.setEnabled(false);
                }
            });

            // hier onclicklsitener on thumbsup, dann noch nen textview, der die anzahl der upvotes anzeigt und alles auf parse kommunizieren. sweeeeet.
        }
        return view;
    }

    private void onePlusOnUpVotesOnParse(String nameToUpdate, String commentToUpdate, String dateToUpdate) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_USERNAME, nameToUpdate);
        query.whereEqualTo(PARSE_COMMENT, commentToUpdate);
        query.whereEqualTo(PARSE_DATE, dateToUpdate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject comment = list.get(0);
                int upVotes = (int)comment.getNumber(PARSE_UP_VOTES) + 1;
                comment.put(PARSE_UP_VOTES, upVotes);
                comment.saveInBackground();
            }
        });
    }


}
