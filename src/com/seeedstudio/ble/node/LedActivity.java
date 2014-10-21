package com.seeedstudio.ble.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.os.Build;

public class LedActivity extends Activity {
	
	private ListView actionListView;
	private ArrayAdapter<String> listAdapter;
	private String[] actions = {"Action - ON", "Action - OFF"};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.led);

		// Find the ListView resource. 
		actionListView = (ListView) findViewById( R.id.ledActionListView );

	    // Create and populate a List of planet names.
	      
	    ArrayList<String> sensorList = new ArrayList<String>();
	    sensorList.addAll( Arrays.asList(actions) );
	    
	    // Create ArrayAdapter using the planet list.
	    listAdapter = new ArrayAdapter<String>(this, R.layout.device_row, sensorList);
	    
	    actionListView.setAdapter(listAdapter);
	    
	    actionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				String item = ((TextView)view).getText().toString();
				
			}
	    	
	    });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.led, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
