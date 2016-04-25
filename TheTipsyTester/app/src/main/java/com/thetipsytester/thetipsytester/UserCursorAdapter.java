package com.thetipsytester.thetipsytester;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Nick on 4/17/2016.
 */
public class UserCursorAdapter extends CursorAdapter {
    public UserCursorAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, 0);
    }

    // The newView method is used to inflate a new view and return it,
    // you don't bind any data to the view at this point.
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false);
    }

    // The bindView method is used to bind all data to a given view
    // such as setting the text on a TextView.
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find fields to populate in inflated template
        TextView tvName = (TextView) view.findViewById(R.id.txtTitle);
        TextView tvGender = (TextView) view.findViewById(R.id.txtGender);
        TextView tvWeight = (TextView) view.findViewById(R.id.txtWeight);
        // Extract properties from cursor
        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        String gender = cursor.getString(cursor.getColumnIndexOrThrow("gender"));
        int weight = cursor.getInt(cursor.getColumnIndexOrThrow("weight"));
        // Populate fields with extracted properties
        tvName.setText(name);
        tvGender.setText(gender);
        tvWeight.setText(String.valueOf(weight));
    }
}