package com.seeedstudio.node.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.SensorData;

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
	    TextView unitView = (TextView) rowView.findViewById(R.id.unit_text_view);
	    ImageView imageView = (ImageView) rowView.findViewById(R.id.type_image_view);
	    
	    float data = values[position].data;
	    if (Float.isNaN(data)) {
	    	dataView.setText("N/A");
	    } else {
	    	dataView.setText(DataCenter.floatToString(data));
	    }
	    
	    unitView.setText(values[position].unit);
	    imageView.setImageResource(values[position].image);

	    return rowView;
	  }
}
