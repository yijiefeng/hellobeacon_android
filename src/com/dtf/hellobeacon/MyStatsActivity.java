package com.dtf.hellobeacon;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hellobeacon.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MyStatsActivity extends Activity {

	private SharedPreferences prefs;
	private String firstname;
	private String lastname;
	private TextView myname;
	private TextView mystats;
	private ProgressBar spinner;
	List<Long> visits;
	StringBuilder builder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mystats);
		
		spinner = (ProgressBar)findViewById(R.id.progressBar1);
		//spinner.setVisibility(View.GONE);
		spinner.setVisibility(View.VISIBLE);
		
		myname = (TextView) findViewById(R.id.tv_MyName);
		mystats = (TextView) findViewById(R.id.tv_mystats);
		
		//set person's name
		prefs = this.getSharedPreferences("com.dtf.hellobeacon", 0);
		firstname = prefs.getString("firstName", "No First Name");
		lastname = prefs.getString("lastName", "No Last Name");
		
		mystats.setText("");
		
		myname.setText(firstname + " " + lastname + " :: PERSONAL STATS");
		
		builder = new StringBuilder();
		final DateFormat df = new SimpleDateFormat("MM/dd/yyyy K:mm a");
		
		//get the user's visits from firebase
		Firebase visitsref = new Firebase("https://hellobeacon.firebaseio.com/Users/" + firstname + lastname + "/Visits/");
		
		visitsref.addValueEventListener(new ValueEventListener() {
			
			@Override
			public void onDataChange(DataSnapshot snapshot) {
				//GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
			    //List<String> messages = snapshot.getValue(t);
				
				String list_items = snapshot.getValue().toString();
				String[] list_values = list_items.split(",");
								
				int i = 1;
				for (String s : list_values){
					
					String e = s.substring(s.lastIndexOf("=") + 1, s.lastIndexOf("=") + 14);
					//e = e.replaceAll("[^\\d.]", "");
					//timestamps.add(Long.valueOf(e).longValue());
					
					Date date = new Date(Long.valueOf(e).longValue());
					String reportdate = df.format(date);
					
					builder.append("VISIT " + Integer.toString(i) + ": " + reportdate + "\n\n");
					i++;
				}
				
				spinner.setVisibility(View.GONE);
				
				mystats.setText(builder.toString());
				
				
			}

			@Override
			public void onCancelled(FirebaseError error) {
				System.err.println("Listener was cancelled");
			}

		});
		
		
		//display all of the user's visits on the screen
//		for(long v : visits){
//			Log.i("EEE", Long.toString(v));
//		}
//		
		
	}
		
	
	
}







