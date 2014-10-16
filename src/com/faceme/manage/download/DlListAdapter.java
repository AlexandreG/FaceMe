package com.faceme.manage.download;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.faceme.R;

public class DlListAdapter extends ArrayAdapter<DownloadItem> {
	protected final static boolean D = true;
	protected final static String TAG = "Log";

	protected Context context;
	protected int layoutResourceId;
	
	protected LinkedList<DownloadItem> data;
	protected LayoutInflater mInflater;


	public DlListAdapter(Context context, int layoutResourceId, LinkedList<DownloadItem> items){
		super(context, layoutResourceId, items);
		this.context = context;
		this.data = items;        
		this.mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		this.layoutResourceId = layoutResourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent){
		View row=convertView;
		DataItemHolder holder = null;
		
		//if the object doesn't exist yet, we inflate a new layout item
		if(convertView==null){
			row = mInflater.inflate(R.layout.download_miniface, null);
		}

		holder = new DataItemHolder();
		holder.item = data.get(position);
		holder.dlButton = (Button) row.findViewById(R.id.miniFaceDownloadBt);
		holder.dlButton.setTag(holder.item);

//		Log.d(TAG, "Boolean de l'item :" + data.get(position).isSelectable());
//		Log.d(TAG, "Boolean de l'objet :" + holder.item.selectable);
//		Log.d(TAG, "Boolean du button :" + holder.selectButton.isEnabled());
//	    Log.d(TAG, "index depuis l'adapter" + position);
		
		holder.imgViewMouth = (ImageView) row.findViewById(R.id.dlMiniFaceImgMouth);
		holder.imgViewMouth.setBackgroundDrawable(holder.item.getBpDrawable());

		holder.imgViewLEye = (ImageView) row.findViewById(R.id.dlMiniFaceImgLEye);
		holder.imgViewLEye.setImageDrawable(holder.item.getLdLeft());

		holder.imgViewREye = (ImageView) row.findViewById(R.id.dlMiniFaceImgREye);
		holder.imgViewREye.setImageDrawable(holder.item.getLdRigth());
		
		holder.infoTextView = (TextView) row.findViewById(R.id.faceInfo);
		holder.infoTextView.setText(holder.item.getFaceInfo());
		
		row.setTag(holder);

		return row;
	}
	
    public static class DataItemHolder {
        DownloadItem item;
        ImageView imgViewMouth;
        ImageView imgViewLEye;
        ImageView imgViewREye;
        Button dlButton;
        TextView infoTextView;
    }

}
