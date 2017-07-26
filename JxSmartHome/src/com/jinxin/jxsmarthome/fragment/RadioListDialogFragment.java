package com.jinxin.jxsmarthome.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.constant.MusicSetting;
import com.jinxin.jxsmarthome.ui.adapter.RadioListAdapter;
import com.jinxin.jxsmarthome.util.CommUtil;
import com.jinxin.record.SharedDB;

/**
 * 单选列表DialogFragment
 * @author TangLong
 * @company 金鑫智慧
 */
public class RadioListDialogFragment extends DialogFragment {
	private OnItemClickListener listener;
	private String tag;
	private String[] items;
	private int selectedPosition;
	
	public static RadioListDialogFragment newInstance(int title, int array, OnItemClickListener listener, String tag) {
		RadioListDialogFragment frag = new RadioListDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("array", array);
		frag.setArguments(args);
		frag.addOnItemClickListener(listener);
		frag.addTag(tag);
		return frag;
	} 
	
	public static RadioListDialogFragment newInstance(int title, String[] items, OnItemClickListener listener, String tag) {
		RadioListDialogFragment frag = new RadioListDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		frag.setArguments(args);
		frag.addOnItemClickListener(listener);
		frag.addTag(tag);
		frag.setItems(items);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.fragment_dialog_radio, 
				new LinearLayout(getActivity()), false);
		initView(view);

		final Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
		dialog.setContentView(view);
		return dialog;
	}
	
	private void initView(View view) {
		ListView listView = (ListView) view.findViewById(R.id.dialog_list);
		TextView titleView = (TextView) view.findViewById(R.id.dialog_title);
		
		int array = getArguments().getInt("array");
		if(items == null) {
			items = getActivity().getResources().getStringArray(array);
		}
		
		String title = getActivity().getResources().getString(
				getArguments().getInt("title"));
		title = title == null ? "" : title;
		titleView.setText(title);
		
		final RadioListAdapter adapter = new RadioListAdapter(getActivity(), CommUtil.convertArrayToList(items));
		adapter.setSelectedPosition(selectedPosition);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(listener != null) {
					listener.onItemClick(position, items[position], tag);
				}
			}
		});
	}
	
	private void addOnItemClickListener(OnItemClickListener listener) {
		this.listener = listener;
	}
	
	private void addTag(String tag) {
		this.tag = tag;
	}
	
	private void setItems(String[] items) {
		this.items = items;
	}
	
	public interface OnItemClickListener {
		public void onItemClick(int position, String itemName, String tag);
	}
	
	public void setSelectedPosition(int position) {
		this.selectedPosition = position;
	}
}
