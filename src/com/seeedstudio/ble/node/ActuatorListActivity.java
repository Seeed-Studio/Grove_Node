package com.seeedstudio.ble.node;

import java.util.ArrayList;
import java.util.Arrays;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ActuatorListActivity extends Activity {
	public String[] actuators = new String[] {"LED", "Chainable LED", " "};
	
	private ListView actuatorListView;
	private ArrayAdapter<String> listAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actuator_list);
		
		// Find the ListView resource. 
	    actuatorListView = (ListView) findViewById( R.id.actuatorListView );

	    // Create and populate a List of planet names.
	      
	    ArrayList<String> actuatorList = new ArrayList<String>();
	    actuatorList.addAll( Arrays.asList(actuators) );
	    
	    // Create ArrayAdapter using the planet list.
	    listAdapter = new ArrayAdapter<String>(this, R.layout.device_row, actuatorList);
	    
	    actuatorListView.setAdapter(listAdapter);
	    
	    actuatorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int postion,
					long id) {
				String item = ((TextView)view).getText().toString();
				if (item.equals("LED")) {
					Intent intent = new Intent(ActuatorListActivity.this, LedActivity.class);
					startActivity(intent);
				}
			}
	    	
	    });
	}
	
	public void onItemClick(View v) {
		
		
	}



}
