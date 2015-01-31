package com.faceme.manage;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import com.faceme.R;

public class NotiFicationListAdapter extends ArrayAdapter<DataItem> {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	protected Context context;
	protected int layoutResourceId;

	protected LinkedList<DataItem> data;
	protected LayoutInflater mInflater;

	public NotiFicationListAdapter(Context context, int layoutResourceId, LinkedList<DataItem> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.data = items;
		this.mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.layoutResourceId = layoutResourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		DataItemHolder holder = null;

		// if the object doesn't exist yet, we inflate a new layout item
		if (convertView == null) {
			row = mInflater.inflate(R.layout.list_miniface, null);
		}

		holder = new DataItemHolder();
		holder.item = data.get(position);
		holder.deleteButton = (Button) row.findViewById(R.id.miniFaceDelete);
		holder.deleteButton.setTag(holder.item);

		holder.selectButton = (Button) row.findViewById(R.id.miniFaceSelect);
		holder.selectButton.setEnabled(holder.item.selectable);
		holder.selectButton.setTag(holder.item);

		holder.uploadButton = (Button) row.findViewById(R.id.miniFaceUpload);
		holder.uploadButton.setTag(holder.item);

		// Log.d(TAG, "Boolean de l'item :" +
		// data.get(position).isSelectable());
		// Log.d(TAG, "Boolean de l'objet :" + holder.item.selectable);
		// Log.d(TAG, "Boolean du button :" + holder.selectButton.isEnabled());
		// Log.d(TAG, "index depuis l'adapter" + position);

		holder.imgViewMouth = (ImageView) row.findViewById(R.id.miniFaceImgMouth);
		holder.imgViewMouth.setBackgroundDrawable(holder.item.getBpDrawable());

		holder.imgViewLEye = (ImageView) row.findViewById(R.id.miniFaceImgLEye);
		holder.imgViewLEye.setImageDrawable(holder.item.getLdLeft());

		holder.imgViewREye = (ImageView) row.findViewById(R.id.miniFaceImgREye);
		holder.imgViewREye.setImageDrawable(holder.item.getLdRigth());

		row.setTag(holder);

		return row;
	}

	public static class DataItemHolder {
		DataItem item;
		ImageView imgViewMouth;
		ImageView imgViewLEye;
		ImageView imgViewREye;
		Button deleteButton;
		Button selectButton;
		Button uploadButton;
	}

}
