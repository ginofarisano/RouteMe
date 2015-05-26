package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.teamrouteme.routeme.R;

import java.util.ArrayList;

/**
 * Created by nicolopergola on 25/05/15.
 */
public class CustomCardItemView extends CardItemView<CustomCard>{
    TextView mTitle;
    TextView mDescription;
    TextView mListTags;
    RatingBar mRatingBar;

    // Default constructors
    public CustomCardItemView(Context context) {
        super(context);
    }

    public CustomCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void build(CustomCard card) {
        setTitle(card.getTitle());
        setDescription(card.getDescription());
        setListTags(card.getListTags());
        setRatingBar(card.getRating());
    }

    public void setTitle(String title){
        mTitle = (TextView)findViewById(R.id.nomeItinerarioCard);
        mTitle.setText(title);
    }

    public void setDescription(String description){
        mDescription = (TextView)findViewById(R.id.descrizioneItinerarioCard);
        mDescription.setText(description);
    }

    public void setListTags(ArrayList<String> listTags){
        mListTags = (TextView)findViewById(R.id.tagsListCard);
        String tags = "Tags: ";
        for(String s : listTags)
            tags = tags+ " "+s;
        mListTags.setText(tags);
    }

    public void setRatingBar(float valutazione){
        mRatingBar = (RatingBar)findViewById(R.id.ratingBarMieiItinerariCustom);
        mRatingBar.setRating(valutazione);
    }
}
