package android.app1.findfriends;

import java.util.List;

import android.app.Activity;
import android.app1.findfriends.models.Event;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomEventArrayAdaptor extends ArrayAdapter<Event> {

	Context context;
	int resourceId;
	public CustomEventArrayAdaptor(Context context, int resourceId, List<Event> items) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
	}

	
	private class ViewHolder {
		//ImageView image;
		TextView contactId;
		//TextView contactNumber;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		Event event = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {

			convertView = mInflater.inflate(resourceId, null);
			holder = new ViewHolder();
			//holder.image = (ImageView) convertView.findViewById(R.id.img1);
			holder.contactId = (TextView) convertView.findViewById(R.id.contactId);
			//holder.contactNumber = (TextView) convertView.findViewById(R.id.numbers);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
			
			holder.contactId.setText(event.getEventName());
			//holder.contactNumber.setText(contact.getPhoneNumber());
		
		return convertView;
	}
}