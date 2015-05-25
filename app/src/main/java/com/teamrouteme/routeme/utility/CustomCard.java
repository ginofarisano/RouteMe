package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.widget.RatingBar;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.events.BusProvider;
import com.teamrouteme.routeme.R;

/**
 * Created by nicolopergola on 25/05/15.
 */
public class CustomCard extends SimpleCard {

    float mRatingBar;
    public CustomCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.card_layout;
    }

    public float getRating(){
        return 2;
    }

    public void setRatingBar(float ratingBar) {
        this.mRatingBar = ratingBar;
    }
}
