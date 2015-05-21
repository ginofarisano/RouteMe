package com.teamrouteme.routeme.utility;

import android.content.Context;
import android.widget.Button;

import com.ogaclejapan.arclayout.ArcLayout;
import com.teamrouteme.routeme.R;

/**
 * Created by massimo299 on 20/05/15.
 */
public class ArcLayoutButton extends Button{

    private int width, height, text_size = 12;

    public ArcLayoutButton(Context context) {
        super(context);
    }

    public void setButtonAttributes(String text, int color){
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixels = (int) (70 * scale + 0.5f);
        width = height = pixels;

        ArcLayout.LayoutParams params = new ArcLayout.LayoutParams(height, width);
        setLayoutParams(params);
        setTextSize(text_size);
        setTextColor(getResources().getColor(R.color.tumblr_primary));
        setBackgroundResource(color);
        setText(text);
    }

}
