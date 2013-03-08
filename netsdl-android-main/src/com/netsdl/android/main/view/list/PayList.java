package com.netsdl.android.main.view.list;

import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.PaymentMaster;
import com.netsdl.android.common.view.list.Currentable;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.Main;
import com.netsdl.android.main.view.MainActivity;

import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class PayList {
	MainActivity grandpa;
	Main parent;

	GestureDetector gestureDetector;
	ItemAdapter adapter;

	public PayList(Main parent) {
		this.parent = parent;
		grandpa = parent.parent;
	}

	public void init() {
		ListView listView = (ListView) grandpa.findViewById(R.id.listViewPay);

		adapter = new ItemAdapter(grandpa);
		listView.setAdapter(adapter);

		gestureDetector = new GestureDetector(new ListViewGestureListener(
				adapter));

		listView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	public class ItemAdapter extends BaseAdapter implements Currentable {
		private LayoutInflater mInflater;
		public int positionCurrent;
		public View viewCurrent;

		public ItemAdapter(Context context) {
			this.mInflater = LayoutInflater.from(context);
		}

		public int getCount() {
			return grandpa.mapPay.size();
		}

		public Object getItem(int position) {
			return null;
		}

		public long getItemId(int position) {
			return 0;
		}

		public View getView(final int position, View convertView,
				ViewGroup viewGroup) {
			ViewHolder holder = null;

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.pay, null);
				holder.payMethodName = (TextView) convertView
						.findViewById(R.id.payMethodName);
				holder.fee = (TextView) convertView.findViewById(R.id.fee);
				holder.buttonDelete = (Button) convertView
						.findViewById(R.id.buttonDelete);
				holder.fee.setTextColor(holder.payMethodName.getTextColors().getDefaultColor());
				holder.fee.setGravity(Gravity.CENTER);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final Integer[] paymentIDs = grandpa.mapPay.keySet().toArray(
					new Integer[] {});
			Object[] objs = grandpa.mapPaymentMaster.get(paymentIDs[position]);

			holder.payMethodName.setText((String) objs[DatabaseHelper
					.getColumnIndex(PaymentMaster.COLUMN_NAME,
							PaymentMaster.COLUMNS)]);
			holder.fee.setText(grandpa.mapPay.get(paymentIDs[position])
					.toString());

			holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					grandpa.mapPay.remove(paymentIDs[position]);
					v.setVisibility(View.GONE);
					parent.listViewNotifyDataSetChanged(R.id.listViewPay);
					parent.setTotal();
					parent.setButtonPay();
				}
			});

			convertView.setOnTouchListener(new OnTouchListener() {

				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						positionCurrent = -1;
						viewCurrent = null;
						break;
					case MotionEvent.ACTION_DOWN:
						positionCurrent = position;
						viewCurrent = v;
						break;
					}

					return false;
				}
			});

			return convertView;
		}

		public int getPosition() {
			return positionCurrent;
		}

		public View getView() {
			return viewCurrent;
		}
	}

	public final class ViewHolder {
		public TextView payMethodName;
		public TextView fee;
		public Button buttonDelete;
	}

}
