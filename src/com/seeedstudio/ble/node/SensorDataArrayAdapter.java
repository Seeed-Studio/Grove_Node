package com.seeedstudio.ble.node;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SensorDataArrayAdapter extends ArrayAdapter<SensorData> {
	  private final Context context;
	  private final SensorData[] values;

	  public SensorDataArrayAdapter(Context context, SensorData[] values) {
	    super(context, R.layout.sensor_data, values);
	    this.context = context;
	    this.values = values;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.sensor_data, parent, false);
	    
	    TextView dataView = (TextView) rowView.findViewById(R.id.data_text_view);
	    TextView unitView = (TextView) rowView.findViewById(R.id.data_unit_text_view);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.data_type_image_view);
	    
	    float data = values[position].data;
	    if (data != Float.NaN) {
	    	dataView.setText(Float.toString(data));
	    } else {
	    	dataView.setText("N/A");
	    }
	    unitView.setText(values[position].unit);
	    imageView.setImageResource(values[position].image);

	    return rowView;
	  }
}
