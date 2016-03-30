package com.thetipsytester.thetipsytester;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;


public class newUserActivity extends AppCompatActivity {

    Long rowid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        if (getIntent().hasExtra("rowid")) {
            rowid = getIntent().getLongExtra("rowid", 0);
            ContentResolver cr = getContentResolver();

            Cursor c = cr.query(UserContentProvider.CONTENT_URI.buildUpon().appendPath(Long.toString(rowid)).build(),
                    new String[] {"name","gender","weight"},null, null, null);

            if (!c.moveToFirst()) {
                this.setTitle("Add new user");
                Toast.makeText(this, "Error retrieving user.  Adding new user, instead.", Toast.LENGTH_LONG).show();
                rowid = null;
            }
            else {
                this.setTitle("Edit user");
                ((EditText) findViewById(R.id.NameEditText)).setText(c.getString(0));

                RadioButton male = (RadioButton) findViewById(R.id.maleRadioButton);
                RadioButton female = (RadioButton) findViewById(R.id.femaleRadioButton);
                if(c.getString(1).equals("male")){
                    male.setChecked(true);
                }else{
                    female.setChecked(true);
                }


                ((EditText) findViewById(R.id.WeightEditText)).setText(c.getString(2));
            }
            c.close();
        }
    }

    public void btnAddClick(View view) {
        ContentResolver cr = getContentResolver();

        RadioButton male = (RadioButton)findViewById(R.id.maleRadioButton);

        ContentValues values = new ContentValues();
        values.put("name", ((EditText) findViewById(R.id.NameEditText)).getText().toString());
        if(male.isChecked()){
            values.put("gender", "male");
        }else{
            values.put("gender", "female");
        }
        values.put("weight", ((EditText) findViewById(R.id.WeightEditText)).getText().toString());

        try {
            if (rowid == null) {
                cr.insert(UserContentProvider.CONTENT_URI, values);
            } else {
                cr.update(UserContentProvider.CONTENT_URI.buildUpon().appendPath(Long.toString(rowid)).build(), values, null, null);
            }
            finish();
        } catch (SQLException e) {
            Toast.makeText(this, "Error updating database.", Toast.LENGTH_LONG).show();
        }
    }

    public void btnDeleteClick(View view) {
        if (rowid != null) {
            ConfirmDeleteDialog confirmDialog = new ConfirmDeleteDialog();
            Bundle args = new Bundle();
            args.putLong("rowid", rowid);
            confirmDialog.setArguments(args);
            confirmDialog.show(getFragmentManager(), "deletionConfirmation");
        }
        else {
            finish();
        }
    }

    public static class ConfirmDeleteDialog extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Delete this user?")
                    .setMessage("You will not be able to undo the deletion!")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ContentResolver cr = getActivity().getContentResolver();

                            try {
                                long rowid = getArguments().getLong("rowid");
                                cr.delete(UserContentProvider.CONTENT_URI, "_id = " + rowid, null);
                                getActivity().finish();
                            } catch (SQLException e) {
                                Toast.makeText(getActivity(), "Error deleting record.", Toast.LENGTH_LONG).show();
                            }
                        }
                    })
                    .setNegativeButton("Return to user list", new
                            DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    getActivity().finish();
                                }
                            });
            return builder.create();
        }
    }
}
