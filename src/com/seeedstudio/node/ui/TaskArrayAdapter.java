package com.seeedstudio.node.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seeedstudio.grove_node.R;
import com.seeedstudio.node.data.Task;



public class TaskArrayAdapter extends ArrayAdapter<Task> {
	  private final Context context;
	  private final ArrayList<Task> list;

	  public TaskArrayAdapter(Context context, ArrayList<Task> list) {
	    super(context, R.layout.sensor_data, list);
	    this.context = context;
	    this.list = list;
	  }

	  @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
	    LayoutInflater inflater = (LayoutInflater) context
	        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    View rowView = inflater.inflate(R.layout.task, parent, false);
	    
	    TextView nameView = (TextView) rowView.findViewById(R.id.name_text_view);
	    TextView textView = (TextView) rowView.findViewById(R.id.description_text_view);
	    
	    Task task = list.get(position);
	    nameView.setText(task.name);
	    textView.setText(task.requirements.toString());

	    return rowView;
	  }
}
