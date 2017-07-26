package com.jinxin.jxsmarthome.fragment;

import java.util.ArrayList;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.ui.adapter.SpeakerChooseDialogAdapter;

/**
 * 单选列表DialogFragment
 * 
 * @author TangLong
 * @company 金鑫智慧
 */
public class CheckboxListDialogFragment extends DialogFragment {
	private ListView mListView;
	private Button mPositive;

	private OnClickListener positiveButtonListener;

	public static CheckboxListDialogFragment newInstance(int title, ArrayList<String> listData,
			OnClickListener positiveButtonListener) {
		CheckboxListDialogFragment frag = new CheckboxListDialogFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putStringArrayList("listData", listData);
		frag.setArguments(args);
		frag.setPositiveButtonListener(positiveButtonListener);
		return frag;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View view = inflater.inflate(R.layout.fragment_checkbox_list, new LinearLayout(getActivity()), false);
		initView(view);

		String title = getActivity().getResources().getString(
				getArguments().getInt("title"));

		title = title == null ? "" : title;
		
		final Dialog dialog = new Dialog(getActivity(), R.style.DialogStyle);
		dialog.setContentView(view);
		return dialog;
	}
	
	private void initView(View view) {
		mListView = (ListView) view.findViewById(R.id.speaker_choose_list);
		mPositive = (Button) view
				.findViewById(R.id.speaker_choose_positiveButton);
		TextView titleView = (TextView) view.findViewById(R.id.dialog_title);
		
		String title = getActivity().getResources().getString(
				getArguments().getInt("title"));
		title = title == null ? "" : title;
		titleView.setText(title);

		ArrayList<String> listData = getArguments().getStringArrayList("listData");

		final SpeakerChooseDialogAdapter adapter = new SpeakerChooseDialogAdapter(getActivity(), listData);
		mListView.setAdapter(adapter);

		mPositive.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View v) {
				SparseBooleanArray checckedItems = adapter.getCheckboxItems();
				if(positiveButtonListener != null) {
					positiveButtonListener.onClick(v, checckedItems);
				}
			}
		});
	}
	
	private void setPositiveButtonListener(OnClickListener positiveButtonListener) {
		this.positiveButtonListener = positiveButtonListener;
	}
	
	public interface OnClickListener {
		public void onClick(View view, SparseBooleanArray checckedItems);
	}

}
