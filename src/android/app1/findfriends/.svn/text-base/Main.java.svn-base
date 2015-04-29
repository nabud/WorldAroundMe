package android.app1.findfriends;

import android.app.Activity;
import android.app1.findfriends.events.EventsActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Main extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		final String[] options = getResources().getStringArray(R.array.main_array);
		ListView listView = (ListView) findViewById(R.id.menuList);        
		listView.setAdapter(new CustomArrayAdaptor(this, R.layout.main_page_layout, options));         
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> listView, View view, int selection, long id) {
				Intent intent;
				switch (selection) {

				// user maps
				case 0: case 1: case 2:
					intent = new Intent(Main.this, MapActivity.class);
					intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
					intent.putExtra("Map", selection);
					startActivity(intent);
					break;

                // event map 
				case 3:
					intent = new Intent(Main.this, EventMapActivity.class);
					intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
					intent.putExtra("Map", selection);
					startActivity(intent);
					break;
					
				// contacts	
				case 4:
					startActivity(new Intent(Main.this, Contacts.class).putExtra("tab", 0));
					break;
   
				// Event Activity	
				case 5:
					intent = new Intent(Main.this, EventsActivity.class);
					intent.putExtra("profileData", getIntent().getBundleExtra("profileData"));
					intent.putExtra("tab", 1);
					startActivity(intent);
					break;

				// SMS Activity	
				case 6:
					startActivity(new Intent(Main.this, SMS.class));
					break;

				// Settings	
				case 8:
					
					startActivity(new Intent(Main.this, Settings.class).putExtra("profileData", getIntent().getBundleExtra("profileData")));
					break;
				}
				
			}
		});
	}	

	

	
	class CustomArrayAdaptor extends ArrayAdapter<String> {

		Context context;
        String[] items;
        
		public CustomArrayAdaptor(Context context, int resourceId, String[] items) {
			super(context, resourceId, items);
			this.context = context;
			this.items = items;
		}
        
		private class ViewHolder {
			TextView id;
			ImageView image;
		}

		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder = null;
			//Contact contact = getItem(position);
			int[] imageRes = {R.drawable.whosaround,R.drawable.findfriends,R.drawable.trackfriends,R.drawable.event_map,R.drawable.profile,R.drawable.events,R.drawable.text,R.drawable.chat,R.drawable.settings_page};
			LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				
				convertView = mInflater.inflate(R.layout.main_page_layout, null);
				holder = new ViewHolder();
				holder.id = (TextView) convertView.findViewById(R.id.main);
				holder.image = (ImageView) convertView.findViewById(R.id.img1);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.id.setText(items[position]);
			holder.image.setImageResource(imageRes[position]);
			return convertView;
		}
	}

	
	
}