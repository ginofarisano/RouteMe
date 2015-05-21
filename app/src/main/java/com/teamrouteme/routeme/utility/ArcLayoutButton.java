package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.widget.Button;

import com.ogaclejapan.arclayout.ArcLayout;
import com.teamrouteme.routeme.R;

/**
 * Created by massimo299 on 20/05/15.
 */
public class ArcLayoutButton extends Button{

    private final static int width = 90, height = 90, text_size = 15;

    public ArcLayoutButton(Context context) {
        super(context);
    }

    public void setButtonAttributes(String text, int color){
        ArcLayout.LayoutParams params = new ArcLayout.LayoutParams(height, width);
        setLayoutParams(params);
        setTextSize(text_size);
        setTextColor(getResources().getColor(R.color.tumblr_primary));
        setBackgroundResource(color);
        setText(text);
    }

}
