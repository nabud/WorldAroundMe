package android.app1.findfriends.events;

import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app1.findfriends.R;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context _context;
	private List<String> _listDataHeader; 
	private HashMap<String, String[]> _listDataChild;
	private Bundle bundle;


	public ExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, String[]> listChildData, Bundle bundle) {
		this._context = context;
		this._listDataHeader = listDataHeader;
		this._listDataChild = listChildData;
		this.bundle = bundle;
	}

	@Override
	public Object getChild(int groupPosition, int childPosititon) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition));
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View view, ViewGroup parent) {

		final String[] eventDetail = (String[]) getChild(groupPosition, childPosition);

		if (view == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = infalInflater.inflate(R.layout.expandablelistview_child_showevents, null);			
		}

		TextView location = (TextView) view.findViewById(R.id.location);
		TextView address = (TextView) view.findViewById(R.id.address);
		TextView date = (TextView) view.findViewById(R.id.date);
		TextView time = (TextView) view.findViewById(R.id.time);
		TextView attending = (TextView) view.findViewById(R.id.attending);
		TextView createdBy = (TextView) view.findViewById(R.id.createdBy);

		String[] details = eventDetail[0].split("\\?");		

		buttons(view, details);		
		location.setText(details[0]);
		address.setText(details[1]);
		date.setText(details[2]);
		time.setText(details[3]);
		attending.setText((details[4].equals("A")) ? "Yes" : ((details[4].equals("NA")) ? "NO" : "Please Respond"));
		createdBy.setText(details[5]);		
		return view;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this._listDataChild.get(this._listDataHeader.get(groupPosition)).length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return this._listDataHeader.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return this._listDataHeader.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {

		String headerTitle = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.expandable_listview_group, null);
		}

		TextView lblListHeader = (TextView) convertView.findViewById(R.id.ListHeaderGroup);
		lblListHeader.setTypeface(null, Typeface.BOLD);
		lblListHeader.setText(headerTitle);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	// determine which text view buttons to show based on what events are being shown
	private void buttons(View convertView, final String[] data){		
		if(data[9].equals("MYEVENTS")){
			this.whoIsAttending(convertView, data);
			this.edit(convertView, data);
			this.inviteContact(convertView, data);
			this.delete(convertView, data);
		}
		else{
			this.whoIsAttending(convertView, data);
			this.notAttending(convertView, data);
		}
	}

	/**
	 * show who is attending the events	
	 */
	private void whoIsAttending(final View convertView, final String[] data){
		final TextView attendingList = (TextView) convertView.findViewById(R.id.who_is_attending);
		attendingList.setVisibility(View.VISIBLE);
		attendingList.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				attendingList.setPaintFlags(attendingList.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);			
				new EventAsyncTasks(_context, data).whoIsAttending(data);				
			}
		});
	}

	/**
	 * show who is attending the events	
	 */
	private void inviteContact(final View convertView, final String[] data){
		final TextView invite = (TextView) convertView.findViewById(R.id.invite);
		invite.setVisibility(View.VISIBLE);
		invite.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				invite.setPaintFlags(invite.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);			
				new EventAsyncTasks(_context, data).attending();				
			}
		});
	}

	/**
	 * tell the event owner that your not attending	
	 */
	private void notAttending(View convertView, final String[] data){
		final TextView notAttending = (TextView) convertView.findViewById(R.id.not_attending);
		final TextView attending = (TextView) convertView.findViewById(R.id.attending);
		notAttending.setVisibility(View.VISIBLE);
		if (data[4].equals("NR")) {
			attending.setText("Please Respond");
			notAttending.setText("Send RSVP");

		}
		notAttending.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) { 	
				if (!data[4].equals("NR")) {
					new EventAsyncTasks(_context, data).notAttending(bundle, data); // do not attend action
					
				} else{
					//new EventAsyncTasks(_context, data).Attending(attending, notAttending);
					chooseAttending(bundle, data, attending, notAttending);
					
				}
				notAttending.setPaintFlags(notAttending.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);				
			}
		});
	}
	
	private void chooseAttending(final Bundle bundle, final String[] data, final TextView attending, final TextView notAttending){
		AlertDialog dialog =new AlertDialog.Builder(_context)
	    .setTitle("R.V.S.P.")
	    .setMessage("Do you wish to attend?")
	    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	new EventAsyncTasks(_context, data).Attending(attending, notAttending);
	        }
	     })
	    .setNegativeButton("No", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	new EventAsyncTasks(_context, data).notAttending(bundle, data); // do not attend action
	        }
	     })
	     .show();
	}

	/**
	 * Edit an event	
	 */
	private void edit(View convertView, final String[] data){
		final TextView editEvent = (TextView) convertView.findViewById(R.id.edit);
		editEvent.setVisibility(View.VISIBLE);
		editEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(_context, EditEvent.class);
				intent.putExtra("profileData", bundle);
				intent.putExtra("eventData", data);
				editEvent.setPaintFlags(editEvent.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
				_context.startActivity(intent);							
			}
		});
	}

	/**
	 * Delete an event	
	 */
	private void delete(View convertView, final String[] data){
		final TextView deleteEvent = (TextView) convertView.findViewById(R.id.delete);
		deleteEvent.setVisibility(View.VISIBLE);
		deleteEvent.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {				
				new EventAsyncTasks(_context, data).delete(bundle);				
				deleteEvent.setPaintFlags(deleteEvent.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);				
			}
		});
	}


}
