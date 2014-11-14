package com.seeedstudio.node.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.data.Grove;

public class GroveArrayAdapter extends ArrayAdapter<Grove> {
	  private final Context context;
	  private final ArrayList<Grove> groves;

	  public GroveArrayAdapter(Context context, ArrayList<Grove> groves) {
	    super(context, R.layout.grove, groves);
	    this.context = context;
	    this.groves = groves;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.grove, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    
	    Grove grove = groves.get(position);
	    textView.setText(grove.name);
	    imageView.setImageResource(grove.image);

	    return rowView;
	  }
}
