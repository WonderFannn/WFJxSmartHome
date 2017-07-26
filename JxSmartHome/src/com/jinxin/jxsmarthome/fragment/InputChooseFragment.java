package com.jinxin.jxsmarthome.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jinxin.jxsmarthome.R;

/**
 * 单选列表DialogFragment
 * @author TangLong
 * @company 金鑫智慧
 */
public class InputChooseFragment extends DialogFragment {
	private OnItemClickListener listener;
	
	public static InputChooseFragment newInstance(int title, int array, OnItemClickListener listener) {
		InputChooseFragment frag = new InputChooseFragment();
		Bundle args = new Bundle();
		args.putInt("title", title);
		args.putInt("array", array);
		frag.setArguments(args);
		frag.addItemSelectedListener(listener);
		return frag;
	} 
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_dialog_radio, null);
		
		ListView listView = (ListView) view.findViewById(R.id.dialog_list);
		
		int array = getArguments().getInt("array");
		String[] inputs = getActivity().getResources().getStringArray(array);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_input, R.id.item_name, inputs);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(listener);
		
		return view;
	}
	
	private void addItemSelectedListener(OnItemClickListener listener) {
		this.listener = listener;
	}
}
