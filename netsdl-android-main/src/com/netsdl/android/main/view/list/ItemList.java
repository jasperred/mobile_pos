package com.netsdl.android.main.view.list;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.netsdl.android.common.db.DatabaseHelper;
import com.netsdl.android.common.db.SkuMaster;
import com.netsdl.android.common.view.list.Currentable;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.Main;
import com.netsdl.android.main.view.MainActivity;

public class ItemList {
	MainActivity grandpa;
	Main parent;
	GestureDetector gestureDetector;
	ItemAdapter adapter;

	public ItemList(Main parent) {
		this.parent = parent;
		grandpa = parent.parent;
	}

	public void init() {
		ListView listView = (ListView) grandpa.findViewById(R.id.listViewItem);
		adapter = new ItemAdapter(grandpa);
		listView.setAdapter(adapter);

		gestureDetector = new GestureDetector(grandpa,
				new ListViewGestureListener(adapter));
		gestureDetector.setOnDoubleTapListener(new ListViewGestureListener(
				adapter));

		listView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			public boolean onItemLongClick(AdapterView<?> adapterView,
					View view, int position, long id) {
				Integer[] skuIDs = grandpa.mapItem.keySet().toArray(
						new Integer[] {});
				ItemDialog itemDialog = new ItemDialog(grandpa, adapter
						.getView(), grandpa.mapItem.get(skuIDs[position]),
						position);
				grandpa.mapDialogable.put(itemDialog.hashCode(), itemDialog);
				grandpa.showDialog(itemDialog.hashCode());
				return false;
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
			return grandpa.mapSkuMaster.size();
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

				convertView = mInflater.inflate(R.layout.item, null);
				holder.prop1 = (TextView) convertView.findViewById(R.id.prop1);
				holder.prop2 = (TextView) convertView.findViewById(R.id.prop2);
				holder.price = (TextView) convertView.findViewById(R.id.price);
				holder.count = (TextView) convertView.findViewById(R.id.count);
				holder.lumpSum = (TextView) convertView
						.findViewById(R.id.lumpSum);
				holder.buttonDelete = (Button) convertView
						.findViewById(R.id.buttonDelete);
				//销售商品字体颜色设置
				holder.prop1.setTextColor(Color.WHITE);
				holder.prop2.setTextColor(Color.WHITE);
				holder.price.setTextColor(Color.WHITE);
				holder.count.setTextColor(Color.WHITE);
				holder.lumpSum.setTextColor(Color.WHITE);
				convertView.setTag(holder);

			} else {

				holder = (ViewHolder) convertView.getTag();
			}

			final Integer[] skuIDs = grandpa.mapItem.keySet().toArray(
					new Integer[] {});
			Object[] objs = grandpa.mapSkuMaster.get(skuIDs[position]);

			holder.prop1.setText((String) objs[DatabaseHelper.getColumnIndex(
					SkuMaster.COLUMN_SKU_CD, SkuMaster.COLUMNS)]);

			holder.prop2.setText((String) objs[DatabaseHelper.getColumnIndex(
					SkuMaster.COLUMN_ITEM_CAT_NAME, SkuMaster.COLUMNS)]);

			holder.price.setText(grandpa.mapItem.get(skuIDs[position]).price
					.toString());

			holder.count.setText(grandpa.mapItem.get(skuIDs[position]).count
					.toString());

			holder.lumpSum
					.setText(grandpa.mapItem.get(skuIDs[position]).lumpSum
							.toString());

			holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					grandpa.mapItem.remove(skuIDs[position]);
					grandpa.mapSkuMaster.remove(skuIDs[position]);
					v.setVisibility(View.GONE);
					parent.listViewNotifyDataSetChanged(R.id.listViewItem);
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
		public TextView prop1;
		public TextView prop2;
		public TextView price;
		public TextView count;
		public TextView lumpSum;
		public Button buttonDelete;

	}

}
