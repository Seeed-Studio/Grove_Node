package com.seeedstudio.ble.node;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GroveArrayAdapter extends ArrayAdapter<Grove> {
	  private final Context context;
	  private final Grove[] values;

	  public GroveArrayAdapter(Context context, Grove[] values) {
	    super(context, R.layout.grove, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.grove, parent, false);
	    TextView textView = (TextView) rowView.findViewById(R.id.label);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
	    
	    textView.setText(values[position].name);
	    imageView.setImageResource(values[position].image);

	    return rowView;
	  }
}
