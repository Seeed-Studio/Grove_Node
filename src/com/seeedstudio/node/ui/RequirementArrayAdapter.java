package com.seeedstudio.node.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.seeedstudio.grove_node.R;
import com.seeedstudio.node.data.Requirement;

public class RequirementArrayAdapter extends ArrayAdapter<Requirement> {
	private final Context context;
	private final ArrayList<Requirement> list;

	public RequirementArrayAdapter(Context context, ArrayList<Requirement> list) {
		super(context, R.layout.sensor_data, list);
		this.context = context;
		this.list = list;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.requirement, parent, false);
		TextView textView = (TextView) rowView.findViewById(R.id.text_view);

		Requirement requirement = list.get(position);

		textView.setText(requirement.toString());

		return rowView;
	}
}
