package com.seeedstudio.ble.node;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class SensorEventArrayAdapter extends ArrayAdapter<SensorEvent> {
	  private final Context context;
	  private final ArrayList<SensorEvent> list;

	  public SensorEventArrayAdapter(Context context, ArrayList<SensorEvent> list) {
	    super(context, R.layout.sensor_data, list);
	    this.context = context;
	    this.list = list;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.sensor_event, parent, false);
	    
	    TextView dataView = (TextView) rowView.findViewById(R.id.data_text_view);
	    TextView operatorView = (TextView) rowView.findViewById(R.id.operator_text_view);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.type_image_view);
	    
	    SensorEvent event = list.get(position);
	    SensorData sensorData = event.data;
	    dataView.setText(DataCenter.floatToString(sensorData.data));

	    operatorView.setText(event.operator);
	    imageView.setImageResource(sensorData.image);

	    return rowView;
	  }
}
