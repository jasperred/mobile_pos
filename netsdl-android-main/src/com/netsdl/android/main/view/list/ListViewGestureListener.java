package com.netsdl.android.main.view.list;

import com.netsdl.android.common.view.list.Currentable;
import com.netsdl.android.main.R;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class ListViewGestureListener extends SimpleOnGestureListener {
	Currentable current;

	public ListViewGestureListener(Currentable current) {
		this.current = current;
	}

	public boolean onDown(MotionEvent e) {
		if (current.getPosition() >= 0 && current.getView() != null) {
			Button buttonDelete = (Button) current.getView().findViewById(
					R.id.buttonDelete);
			buttonDelete.setVisibility(View.GONE);
		}
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		final int X = 10;
		final int Y = 10;
		// if (X + distanceX < 0 && Math.abs(distanceY) < Y)
		if (X + distanceX < 0)
			if (current.getPosition() >= 0 && current.getView() != null) {
				Button buttonDelete = (Button) current.getView().findViewById(
						R.id.buttonDelete);
				buttonDelete.setVisibility(View.VISIBLE);
			}
		return false;
	}
	
	public boolean onDoubleTap(MotionEvent e){
		if (current.getPosition() >= 0 && current.getView() != null) {
			Button buttonDelete = (Button) current.getView().findViewById(
					R.id.buttonDelete);
			buttonDelete.setVisibility(View.VISIBLE);
		}
		return false;
	}

//	public void onLongPress(MotionEvent e) {
//		Toast.makeText(current.getView().getContext(), "e:" + e,
//				Toast.LENGTH_SHORT).show();
//		ListView listView = (ListView) current.getView().findViewById(R.id.listViewItem);
//		listView.setOnItemLongClickListener(null);
//	}

}