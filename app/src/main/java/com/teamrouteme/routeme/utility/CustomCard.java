package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.widget.RatingBar;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.events.BusProvider;
import com.teamrouteme.routeme.R;

import java.util.ArrayList;

/**
 * Created by nicolopergola on 25/05/15.
 */
public class CustomCard extends SimpleCard {

    float mRatingBar;
    String mNumFeedback;
    ArrayList<String> mlistTagString;
    public CustomCard(Context context) {
        super(context);
    }

    @Override
    public int getLayout() {
        return R.layout.card_layout;
    }

    public float getRating(){
        return mRatingBar;
    }

    public String getNumFeedback() {return mNumFeedback;}

    public void setNumFeedback(int numFeedback){this.mNumFeedback = ""+numFeedback;}

    public void setRatingBar(float ratingBar) {
        this.mRatingBar = ratingBar;
    }

    public void setListTags(ArrayList<String> ar) {
        this.mlistTagString = ar;
        //BusProvider.dataSetChanged();
    }

    public ArrayList<String> getListTags(){
        return this.mlistTagString;
    }
}
