package com.purdue.elab;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.hardware.Camera;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

public class Settings {

    protected MainActivity context;
    int sceneSelected = -1;
    ArrayList<String> categoryList;
    ArrayList<String> models;
    int modelSelected = -1;

    /**
     *
     * @param context
     */
    public Settings(MainActivity context) {
        this.context = context;
    }

    /**
     *
     */
    public void changeScene() {
        final Camera.Parameters params = context.myCamera.getParameters();
        final ArrayList<String> sceneModeList = (ArrayList) params.getSupportedSceneModes();
        if (sceneModeList == null) {
            // no scene mode available!
            Toast.makeText(context, "No Scene Mode Available!", Toast.LENGTH_LONG).show();
            return;
        }
        final CharSequence[] sceneModeText = sceneModeList.toArray(
                new CharSequence[sceneModeList.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select Scene Mode");

        Adapter1 adapter = new Adapter1(context, android.R.layout.simple_list_item_single_choice, sceneModeList);

        builder.setSingleChoiceItems(adapter, sceneSelected, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                sceneSelected = which;
                params.setSceneMode(sceneModeList.get(which));
                context.myCamera.setParameters(params);
                dialog.dismiss();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
        alert.getWindow().setLayout(context.screenwidth - 120, context.screenheight - 480);
    }

    /**
     *
     */
    public void viewCategories(){
        categoryList = new ArrayList<String>();
        for (int key = 0; key < Categories.get(context).getSize(); key++) {
            categoryList.add(Categories.get(context).getCategory(key));
        }

        //CREATE LIST OF CATEGORIES
         final ArrayList<String> templist = categoryList;
        if (categoryList == null) {
            // no scene mode available!
            Toast.makeText(context, "No Categories Available!", Toast.LENGTH_LONG).show();
            return;
        }
        //System.out.println("STRING: "+stringArray[1]);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = context.getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("Categories");
        ListView lv = (ListView) convertView.findViewById(R.id.listView1);
        Adapter1 adapter = new Adapter1(context, android.R.layout.simple_list_item_1, templist);
        lv.setAdapter(adapter);
        final AlertDialog alert = alertDialog.create();
        alert.show();
        alert.getWindow().setLayout(context.screenwidth - 120, context.screenheight - 480);
    }

    /**
     *  Make changes to font and color of the text in AlertDialogs in this class
     */
    public class Adapter1 extends ArrayAdapter<String> {

        public Adapter1(Context context, int resID, ArrayList<String> items) {
            super(context, resID, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);

            ((TextView) v).setTextColor(Color.BLACK);
            ((TextView) v).setTypeface(context.openSans);
            return v;
        }
    }

}
