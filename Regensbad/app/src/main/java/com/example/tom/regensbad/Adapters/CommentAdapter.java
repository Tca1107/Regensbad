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

    private static final int THUMBS_WIDTH = 36;
    private static final int THUMBS_HEIGHT = 33;
    private static final int PARSE_ONE_UP_VOTE = 1;


    private static final String PARSE_COMMENT_RATING = "CommentRating";
    private static final String PARSE_UP_VOTES = "upVotes";
    private static final String PARSE_USERNAME = "userName";
    private static final String PARSE_DATE = "date";
    private static final String PARSE_COMMENT = "comment";
    private static final String PARSE_RATING = "rating";
    private static final String PARSE_CREATED_AT = "createdAt";
    private static final String PARSE_CURRENT_RATING = "currentRating";

    private static final String NO_OWN_VOTING_ALLOWED = "Sie koennen Ihre eigenen Kommentare nicht bewerten.";
    private static final String NOT_LOGGED_IN = "Sie sind nicht eingeloggt oder haben keinen Account. Sie koennen daher keine Kommentare bewerten.";

    private Context context;
    private ArrayList<CommentRating> commentRatingArrayList;
    private int customLayoutResource;


    private String nameToUpdate;
    private String dateToUpdate;
    private String commentToUpdate;
    private String currentComment = "";
    private int upVotesToUpdate;

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
            TextView comment = (TextView)view.findViewById(R.id.text_view_comment_single);
            TextView date = (TextView)view.findViewById(R.id.text_view_comment_date_single);
            TextView upVotes = (TextView)view.findViewById(R.id.text_view_comment_upvotes);
            ImageView thumbsUp = (ImageView)view.findViewById(R.id.image_view_thumbs_up);
            if (commentRating.getIsLiked() == true) {
                thumbsUp.setImageResource(R.drawable.ic_thumb_up_green);
            }

            userName.setText(commentRating.getUserName());
            ratingBar.setRating(commentRating.getRating());
            comment.setText(commentRating.getComment());
            date.setText(String.valueOf(commentRating.getDate()));
            upVotes.setText(String.valueOf(commentRating.getUpVotes()));

            nameToUpdate = commentRating.getUserName();
            commentToUpdate = commentRating.getComment();
            dateToUpdate = commentRating.getDate();
            upVotesToUpdate = commentRating.getUpVotes();


            thumbsUp.getLayoutParams().height = THUMBS_HEIGHT;
            thumbsUp.getLayoutParams().width = THUMBS_WIDTH;
            final int pos = position;
            thumbsUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    if (currentUser != null){
                        if (!currentUser.getUsername().equals(nameToUpdate)) {
                            CommentRating commentRatingToChange = commentRatingArrayList.get(pos);
                            commentRatingToChange.setIsLiked(true);
                            setOnePlusOnUpVotesOnParse(nameToUpdate, commentToUpdate, dateToUpdate);
                            notifyDataSetChanged();
                        } else {
                            // from: http://stackoverflow.com/questions/13927601/how-to-show-toast-in-a-class-extended-by-baseadapter-get-view-method
                            // Toast.makeText(finalView.getContext(), NO_OWN_VOTING_ALLOWED, Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // from: http://stackoverflow.com/questions/13927601/how-to-show-toast-in-a-class-extended-by-baseadapter-get-view-method
                        // Toast.makeText(finalView.getContext(), NOT_LOGGED_IN, Toast.LENGTH_LONG).show();
                    }
                }
            });

            // hier onclicklistener on thumbsup, dann noch nen textview, der die anzahl der upvotes anzeigt und alles auf parse kommunizieren. sweeeeet.
        }
        return view;
    }

    private void setOnePlusOnUpVotesOnParse(String nameToUpdate, String commentToUpdate, String dateToUpdate) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(PARSE_COMMENT_RATING);
        query.whereEqualTo(PARSE_USERNAME, nameToUpdate);
        query.whereEqualTo(PARSE_COMMENT, commentToUpdate);
        query.whereEqualTo(PARSE_DATE, dateToUpdate);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                ParseObject comment = list.get(0);
                int upVotes = (int)comment.getNumber(PARSE_UP_VOTES) + PARSE_ONE_UP_VOTE;
                comment.put(PARSE_UP_VOTES, upVotes);
                comment.saveInBackground();
            }
        });
    }


}
