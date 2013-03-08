package com.netsdl.android.main.view.list;

import java.math.BigDecimal;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.netsdl.android.common.Structs.Item;
import com.netsdl.android.common.Util;
import com.netsdl.android.main.R;
import com.netsdl.android.main.view.MainActivity;
import com.netsdl.android.main.view.list.ItemList.ItemAdapter;
import com.netsdl.android.common.view.dialog.Dialogable;

public class ItemDialog implements Dialogable, Runnable {
	public ItemDialog itemDialog;
	public MainActivity netSDLActivity;
	public Item itemOld;
	public Item itemNew;
	public int position;

	View viewListItem;
	View viewItemDialog;

	boolean isRun = true;
	Thread thread;

	public boolean isButtonPriceIncreaseDown = false;
	public boolean isButtonPriceDecreaseDown = false;

	public boolean isButtonCountIncreaseDown = false;
	public boolean isButtonCountDecreaseDown = false;

	public boolean isButtonLumpSumIncreaseDown = false;
	public boolean isButtonLumpSumDecreaseDown = false;
	//折扣
	public boolean isButtonItemDiscountIncreaseDown = false;
	public boolean isButtonItemDiscountDecreaseDown = false;

	int intButtonPriceMargin = 1;

	public ItemDialog(MainActivity netSDLActivity, View viewListItem,
			Item itemOld, int position) {
		this.netSDLActivity = netSDLActivity;
		this.viewListItem = viewListItem;
		this.itemOld = itemOld;
		try {
			itemNew = (Item) itemOld.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		this.position = position;
		thread = new Thread(this);
		itemDialog = this;
	}

	public void onPrepareDialog(int id, Dialog dialog) {
		try {
			itemNew = (Item) itemOld.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}

	public Dialog onCreateDialog(final int id) {
		LayoutInflater inflater = LayoutInflater.from(netSDLActivity);
		viewItemDialog = inflater.inflate(R.layout.item_dialog, null);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				netSDLActivity);

		builder.setCancelable(true);
		//builder.setTitle(R.string.item_change);
		builder.setView(viewItemDialog);

		showItem(viewItemDialog);

		setOnTouchListener(R.id.buttonPriceIncrease,
				"isButtonPriceIncreaseDown");

		setOnTouchListener(R.id.buttonPriceDecrease,
				"isButtonPriceDecreaseDown");

		setOnTouchListener(R.id.buttonCountIncrease,
				"isButtonCountIncreaseDown");

		setOnTouchListener(R.id.buttonCountDecrease,
				"isButtonCountDecreaseDown");

		setOnTouchListener(R.id.buttonLumpSumIncrease,
				"isButtonLumpSumIncreaseDown");

		setOnTouchListener(R.id.buttonLumpSumDecrease,
				"isButtonLumpSumDecreaseDown");
		//合计不可调整
		((Button) (viewItemDialog.findViewById(R.id.buttonLumpSumDecrease))).setEnabled(false);
		((Button) (viewItemDialog.findViewById(R.id.buttonLumpSumIncrease))).setEnabled(false);
		//折扣
		setOnTouchListener(R.id.buttonItemDiscountDecrease,
				"isButtonItemDiscountDecreaseDown");
		
		setOnTouchListener(R.id.buttonItemDiscountIncrease,
				"isButtonItemDiscountIncreaseDown");

		//明细修改确认
		builder.setPositiveButton(R.string.Confirm, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

				Integer[] skuIDs = netSDLActivity.mapItem.keySet().toArray(
						new Integer[] {});
				netSDLActivity.mapItem.remove(skuIDs[position]);
				netSDLActivity.mapItem.put(skuIDs[position], itemNew);
				netSDLActivity.dismissDialog(id);

				netSDLActivity.main
						.listViewNotifyDataSetChanged(R.id.listViewItem);
				netSDLActivity.main.setTotal();

			}
		});

		builder.setNeutralButton("Delete", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Button buttonDelete = (Button) viewListItem
						.findViewById(R.id.buttonDelete);
				buttonDelete.setVisibility(View.VISIBLE);
				netSDLActivity.dismissDialog(id);
			}
		});

		builder.setNegativeButton(R.string.Cancel, new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				netSDLActivity.dismissDialog(id);
			}
		});

		AlertDialog alertDialog = builder.create();
		alertDialog.setOnDismissListener(new OnDismissListener() {
			public void onDismiss(DialogInterface dialog) {
				isRun = false;
			}
		});

		thread.start();

		return alertDialog;
	}

	private void setOnTouchListener(int id, final String strArg) {
		(viewItemDialog.findViewById(id))
				.setOnTouchListener(new OnTouchListener() {
					public boolean onTouch(View v, MotionEvent event) {
						if (event.getAction() == MotionEvent.ACTION_DOWN) {
							try {
								ItemDialog.class.getField(strArg).setBoolean(
										itemDialog, true);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (NoSuchFieldException e) {
								e.printStackTrace();
							}

						} else if (event.getAction() == MotionEvent.ACTION_UP) {
							try {
								ItemDialog.class.getField(strArg).setBoolean(
										itemDialog, false);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (SecurityException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (NoSuchFieldException e) {
								e.printStackTrace();
							}
						}

						return false;
					}
				});
	}

	@Override
	protected void finalize() throws Throwable {
		isRun = false;
		super.finalize();
	}

	private void showItem(View view) {
		((TextView) (view.findViewById(R.id.oldPriceViewTextView))).setText(itemNew.oldPrice
				.toString());
		((TextView) (view.findViewById(R.id.price))).setText(itemNew.price
				.toString());
		((TextView) (view.findViewById(R.id.count))).setText(itemNew.count
				.toString());
		((TextView) (view.findViewById(R.id.lumpSum))).setText(itemNew.lumpSum
				.toString());
		//折扣
		((TextView) (view.findViewById(R.id.ItemDiscount))).setText(itemNew.itemDiscount
				.toString());
	}

	public void run() {
		int intFlg = 1;
		while (isRun) {
			try {
				Thread.sleep(150);

				if (isButtonPriceIncreaseDown) {
					//限制有折扣时不能调整价格
					if(itemNew.itemDiscount!=100)
						continue;
					itemNew.price = itemNew.price.add(new BigDecimal(
							intButtonPriceMargin));
					countSum();

				} else if (isButtonPriceDecreaseDown) {
					//限制有折扣时不能调整价格
					if(itemNew.itemDiscount!=100)
						continue;
					itemNew.price = itemNew.price.subtract(new BigDecimal(
							intButtonPriceMargin));
					if(itemNew.price.doubleValue()<0)
						itemNew.price = new BigDecimal(0);
					countSum();

				} else if (isButtonCountIncreaseDown) {
					itemNew.count += intButtonPriceMargin;
					countSum();

				} else if (isButtonCountDecreaseDown) {
					itemNew.count -= intButtonPriceMargin;
					if(itemNew.count<0)
						itemNew.count = new Integer(0);
					countSum();

				} else if (isButtonLumpSumIncreaseDown) {
					itemNew.lumpSum = itemNew.lumpSum.add(new BigDecimal(
							intButtonPriceMargin));

				} else if (isButtonLumpSumDecreaseDown) {
					itemNew.lumpSum = itemNew.lumpSum.subtract(new BigDecimal(
							intButtonPriceMargin));

				}
				//折扣
				else if (isButtonItemDiscountIncreaseDown) {
					itemNew.itemDiscount +=intButtonPriceMargin;
					if(itemNew.itemDiscount>100)
						itemNew.itemDiscount = 100;					
					//计算折后单价
					itemNew.price = Util.round(itemNew.oldPrice.multiply(new BigDecimal(
								itemNew.itemDiscount).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP)));
					countSum();

				} else if (isButtonItemDiscountDecreaseDown) {
					itemNew.itemDiscount -=intButtonPriceMargin;
					if(itemNew.itemDiscount<0)
						itemNew.itemDiscount = 0;					
					//计算折后单价
					itemNew.price = Util.round(itemNew.oldPrice.multiply(new BigDecimal(
								itemNew.itemDiscount).divide(new BigDecimal(100),2, BigDecimal.ROUND_HALF_UP)));
					countSum();

				} else {
					intFlg = 1;
					intButtonPriceMargin = 1;
				}

				if (isButtonPriceIncreaseDown || isButtonPriceDecreaseDown
						|| isButtonCountIncreaseDown
						|| isButtonCountDecreaseDown
						|| isButtonLumpSumIncreaseDown
						|| isButtonLumpSumDecreaseDown
						|| isButtonItemDiscountIncreaseDown
						|| isButtonItemDiscountDecreaseDown) {
					intFlg++;
					if (intFlg > 10) {
						intFlg = 1;
						if (intButtonPriceMargin < 100)
							intButtonPriceMargin = intButtonPriceMargin * 10;

					}
					viewItemDialog.post(new Runnable() {
						public void run() {
							showItem(viewItemDialog);
						}
					});
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

	}

	private void countSum() {
		
		
		itemNew.lumpSum = Util.round(itemNew.price.multiply(new BigDecimal(
						itemNew.count)));
	}
}
