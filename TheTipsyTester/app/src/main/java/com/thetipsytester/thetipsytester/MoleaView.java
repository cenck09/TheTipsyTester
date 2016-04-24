package com.thetipsytester.thetipsytester;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.RelativeLayout;

/*
 * Created by chrisenck on 4/24/16.
 */

public class MoleaView extends View {

    public static final int MOLEA_SIZE = 100;        // The size of the moleas
    public static final int MOLEA_MARGIN = 5;        // The space between Moleas

    public MoleaView(Context context) {
        super(context);

        GradientDrawable shape =  new GradientDrawable();
        shape.setCornerRadius((MOLEA_SIZE / 2));
        shape.setColor(Color.BLACK);
        this.setBackground(shape);
    }

    public RelativeLayout.LayoutParams getMoleaPlacement(int top, int left){

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MOLEA_SIZE, MOLEA_SIZE);
        params.leftMargin = (top * ((MOLEA_SIZE / 4) + MOLEA_MARGIN));
        params.topMargin = (left * ((MOLEA_SIZE / 4) + MOLEA_MARGIN));
        return params;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
          setMeasuredDimension(MOLEA_SIZE, MOLEA_SIZE);
    }
}
