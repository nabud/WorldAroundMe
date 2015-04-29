package android.app1.findfriends;

import java.util.List;

import android.app.Activity;
import android.app1.findfriends.models.Contact;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdaptor extends ArrayAdapter<Contact> {

	Context context;
	int resourceId;
	public CustomArrayAdaptor(Context context, int resourceId, List<Contact> items) {
		super(context, resourceId, items);
		this.context = context;
		this.resourceId = resourceId;
	}

	
	private class ViewHolder {
		ImageView image;
		TextView contactId;
		TextView contactNumber;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		Contact contact = getItem(position);

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {

			convertView = mInflater.inflate(resourceId, null);
			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.img1);
			holder.contactId = (TextView) convertView.findViewById(R.id.contactId);
			holder.contactNumber = (TextView) convertView.findViewById(R.id.numbers);
			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if(resourceId == R.layout.contactlayout){
			if(contact.getImage() != null){
				Bitmap bm = BitmapFactory.decodeByteArray(contact.getImage(), 0 ,contact.getImage().length);
				holder.image.setImageBitmap(bm);
			}
			holder.contactId.setText(contact.getFirstName() + " " + contact.getLastName() + "\n" + contact.getPhoneNumber());
			//holder.contactNumber.setText(contact.getPhoneNumber());
		}
		else{
			
			holder.contactId.setText(contact.getFirstName() + " " + contact.getLastName());
			holder.contactNumber.setText(contact.getPhoneNumber());
		}
		return convertView;
	}
}