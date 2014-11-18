package com.seeedstudio.node.ui;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.seeedstudio.node.R;
import com.seeedstudio.node.data.DataCenter;
import com.seeedstudio.node.data.Grove;

public class GroveListActivity extends ListActivity {

	private DataCenter mDataCenter;
	private GroveArrayAdapter mListAdapter;
	private int mNext;
	private int mType;
	private ArrayList<Grove> mGroveList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getActionBar().setDisplayHomeAsUpEnabled(true);

		mNext = getIntent().getExtras().getInt(DataCenter.NEXT);
		mType = getIntent().getExtras().getInt(DataCenter.TYPE);
		mDataCenter = DataCenter.getInstance();

		if (mType == DataCenter.ACTUATOR) {
			setTitle("Select a Actuator");
			mGroveList = mDataCenter.actuatorList;
		} else {
			setTitle("Select a Sensor");
			mGroveList = mDataCenter.sensorList;
		}
		
		mListAdapter = new GroveArrayAdapter(this, mGroveList);
		setListAdapter(mListAdapter);

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		Grove grove = mListAdapter.getItem(position);
		if (mType == DataCenter.ACTUATOR) {
			mDataCenter.setActuator(grove);
		} else {
			mDataCenter.setSensor(grove);
		}

		if (mNext != DataCenter.FORWARD) {
			finish();
		} else {
			Intent intent = new Intent(this, grove.activity);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.grove_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
