package android.app1.findfriends;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

public class CommonUtility {

	// Show progress bar
	public static void showProgress(boolean show, View view, int id) {

		ProgressBar loginProgressBarView = (ProgressBar) view.findViewById(id);
        
		if (show) {
			loginProgressBarView.setVisibility(View.VISIBLE);
			showText(view, View.VISIBLE);
		} else {
			loginProgressBarView.setVisibility(View.INVISIBLE);
			showText(view, View.GONE);
		}
	}
	
	private static void showText(View view, int visibility){
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.progressLayout);
		if(layout != null){
			layout.setVisibility(visibility);
		}
	}

}
