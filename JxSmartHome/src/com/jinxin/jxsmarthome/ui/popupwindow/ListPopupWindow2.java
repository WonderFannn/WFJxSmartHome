
package com.jinxin.jxsmarthome.ui.popupwindow;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jinxin.datan.net.command.CustomerBrandsListTask;
import com.jinxin.datan.net.command.DeleteCustomerRemoteBrandTask;
import com.jinxin.datan.toolkit.task.ITask;
import com.jinxin.datan.toolkit.task.ITaskListener;
import com.jinxin.jxsmarthome.R;
import com.jinxin.jxsmarthome.activity.CustomerRemoteControlActivity;
import com.jinxin.jxsmarthome.entity.CustomerBrands;
import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
import com.jinxin.jxsmarthome.ui.dialog.CustomerCenterDialog;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * 遥控器选择弹出窗口
 * @author JackeyZhang
 * @company 金鑫智慧
 */
public class ListPopupWindow2 extends PopupWindow {

    private View contentView = null;

    private View view = null;

    private ListView listView = null;

    private int selectIndex = -1;

    private Context context = null;

    private CustomerBrandsListTask cBrandsListTask = null;
	private List<CustomerBrands> cbList = null;
	private ListViewSimpleAdapter adapter = null;

    /**
     * @param context
     * @param str[] 显示的内容
     * @param view 显示在哪个view边上
     * @param itemWidth item 的宽度
     * @param initializeStr 初始内容信息
     */
    public ListPopupWindow2(Context context, View view, int itemWidth, int ietmHeigh) {
        super(LayoutInflater.from(context).inflate(
                R.layout.slide_popupwindow_list_layout, null), itemWidth,
                ietmHeigh, true);
        this.contentView = LayoutInflater.from(context).inflate(
                R.layout.slide_popupwindow_list_layout, null);
        contentView.setLayoutParams(new LayoutParams(itemWidth,ietmHeigh));
        this.context = context;
        this.view = view;
        initList();
        this.setContentView(contentView);
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        this.setBackgroundDrawable(new BitmapDrawable());
    }
    
    private Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				adapter = new ListViewSimpleAdapter(cbList);
		        listView.setAdapter(adapter);
				break;
			case 1:
				showSaveDialog(msg.arg1);
			}
		}
    	
    };

    private void initList() {
        this.listView = (ListView) contentView.findViewById(R.id.lv_brands);
        adapter = new ListViewSimpleAdapter(cbList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new ListViewOnItemClickListener());
        listView.setOnItemLongClickListener(new ListVoewOnItemLongClickListener());
        getBrandsList();
    }
    
    private void getBrandsList() {
		cBrandsListTask = new CustomerBrandsListTask(context,null);
		cBrandsListTask.addListener(new ITaskListener<ITask>() {

			@Override
			public void onStarted(ITask task, Object arg) {
			}

			@Override
			public void onCanceled(ITask task, Object arg) {
			}

			@Override
			public void onFail(ITask task, Object[] arg) {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(ITask task, Object[] arg) {
				Logger.debug(null, "onSuccess");
				if(arg != null && arg.length > 0){
					cbList = (List<CustomerBrands>) arg[0];
					handler.sendEmptyMessage(0);
				}
			}

			@Override
			public void onProcess(ITask task, Object[] arg) {
			}
		});
		cBrandsListTask.start();
	}

    public void dismiss() {
        super.dismiss();
        view.setSelected(false);
    }

    /**
     * 显示PopupWindow
     */
    public void show() {
        /*
         * popupWindow.showAsDropDown（View view）弹出对话框，位置在紧挨着view组件
         * showAsDropDown(View anchor, int xoff, int yoff)弹出对话框，位置在紧挨着view组件，x y
         * 代表着偏移量 showAtLocation(View parent, int gravity, int x, int y)弹出对话框
         * parent 父布局 gravity 依靠父布局的位置如Gravity.CENTER x y 坐标值
         */
//        this.showAsDropDown(view);
        this.showAtLocation(view, Gravity.RIGHT, 0, 0);
        view.setSelected(true);
    }

    /**
     * 显示PopupWindow
     * 
     * @param xoff
     * @param yoff
     */
    public void show(int xoff, int yoff) {
        this.setContentView(contentView);
//        this.showAsDropDown(view, xoff, yoff);
        this.showAtLocation(view, Gravity.RIGHT, xoff, yoff);
        view.setSelected(true);
    }

    class ListViewSimpleAdapter extends BaseAdapter {

        private List<CustomerBrands> cbList = null;

        private ListViewSimpleAdapter(List<CustomerBrands> cbList) {
        	this.cbList = cbList;
        }

        @Override
        public int getCount() {
            return cbList == null ?  0 : cbList.size();
        }

        @Override
        public Object getItem(int position) {
            return cbList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	ViewHolder holder = null;
        	if (convertView == null) {
        		convertView = CommDefines.getSkinManager().view(
    					R.layout.popupwindow_remote_item);
    			holder = new ViewHolder(convertView);
    			convertView.setTag(holder);
			}else {
				holder = (ViewHolder) convertView.getTag();
			}
        	CustomerBrands brands = this.cbList.get(position);
        	if (!TextUtils.isEmpty(brands.getNickName())) {
        		if (TextUtils.isEmpty(brands.getBrandName())) {
        			holder.textView.setText(brands.getNickName()+"(自定义)");
        		}else{
        			holder.textView.setText(brands.getNickName()+"("+brands.getBrandName()+")");
        		}
			}else{
				holder.textView.setText("未命名遥控");
			}
//            convertView.setBackgroundResource(R.drawable.option_top_state);
//            if (listStr.size() - 1 == position) {
//                convertView.setBackgroundResource(R.drawable.option_bottom_state);
//            }
            return convertView;
        }
        class ViewHolder{
        	TextView textView;
        	public ViewHolder(View view) {
        		textView = (TextView) view.findViewById(R.id.tv_cusomer_name);
			}
        }
    }

    class ListViewOnItemClickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            selectIndex = arg2;
            Intent intent = new Intent(context, CustomerRemoteControlActivity.class);
            intent.putExtra("customerBrand", cbList.get(arg2));
            ListPopupWindow2.this.dismiss();
            context.startActivity(intent);
        }
    }
    
    class ListVoewOnItemLongClickListener implements OnItemLongClickListener{

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			deleteRemote(position);
			return false;
		}
    	
    }
    
    private void deleteRemote(final int position){
    	CustomerBrands brand = cbList.get(position);
    	if (brand != null) {
    		DeleteCustomerRemoteBrandTask task = new DeleteCustomerRemoteBrandTask(context, brand.getId(),
    				brand.getmCode(), brand.getWhId(), brand.getDeviceId(),  brand.getBrandsId());
    		task.addListener(new ITaskListener<ITask>() {

				@Override
				public void onStarted(ITask task, Object arg) {
					
				}

				@Override
				public void onCanceled(ITask task, Object arg) {
					
				}

				@Override
				public void onFail(ITask task, Object[] arg) {
					
				}

				@Override
				public void onSuccess(ITask task, Object[] arg) {
					
//					handler.sendEmptyMessage(1);
					Message msg = new Message();
					msg.arg1 = position;
					msg.what = 1;
					handler.sendMessage(msg);
				}

				@Override
				public void onProcess(ITask task, Object[] arg) {
					
				}
			});
    		task.start();
		}
    }
    
    /**
	 * 保存遥控器
	 */
	private void showSaveDialog(final int position){
		LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = li.inflate(R.layout.delete_remote_confirm_dialog, null);
		final CustomerCenterDialog dialog = new CustomerCenterDialog(context, R.style.dialog, v);

		Button btnSure = (Button) v.findViewById(R.id.button_ok);
		Button btnCancle = (Button) v.findViewById(R.id.button_cancel);
		btnSure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				cbList.remove(position);
				handler.sendEmptyMessage(0);
				
			}
		});
		
		btnCancle.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	public int getSelectIndex() {
		return selectIndex;
	}
}
