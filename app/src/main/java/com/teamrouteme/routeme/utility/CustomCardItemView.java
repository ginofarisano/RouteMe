package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RatingBar;
import android.widget.TextView;

import com.dexafree.materialList.model.CardItemView;
import com.teamrouteme.routeme.R;

/**
 * Created by nicolopergola on 25/05/15.
 */
public class CustomCardItemView extends CardItemView<CustomCard>{
    TextView mTitle;
    TextView mDescription;
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
        setRatingBar(card.getRating());
    }

    public void setTitle(String title){
        mTitle = (TextView)findViewById(R.id.titleTextView);
        mTitle.setText(title);
    }

    public void setDescription(String description){
        mDescription = (TextView)findViewById(R.id.descriptionTextView);
        mDescription.setText(description);
    }

    public void setRatingBar(float valutazione){
        mRatingBar = (RatingBar)findViewById(R.id.ratingBarMieiItinerariCustom);
        mRatingBar.setRating(valutazione);
    }
}
