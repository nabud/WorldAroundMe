package android.app1.findfriends;

import java.util.HashMap;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
//import android.os.UserManager.*;

public class GetPhonesContacts {

	// get the phones contacts list
	public static HashMap<String,String> getContacts(ContentResolver cr, Cursor cur){
		HashMap<String,String> phoneContacts = new HashMap<String,String>(); // <phone number, name>
		if (cur.getCount() > 0) {
			while (cur.moveToNext()) {
				String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID)); // Get contact ID
				String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)); // Get contact name
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					// Get phone numbers for the given contact ID 
					Cursor phoneCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", new String[]{id}, null);
					while (phoneCur.moveToNext()) {
						// insert the phone number as a key and the name as a value. 
						phoneContacts.put(phoneCur.getString(phoneCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), name);
					} 
					phoneCur.close(); 	 
				}
			}			
		}
		cur.close();		
		return phoneContacts;
	}
}

