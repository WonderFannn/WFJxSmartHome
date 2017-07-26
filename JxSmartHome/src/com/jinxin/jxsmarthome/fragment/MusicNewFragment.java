//package com.jinxin.jxsmarthome.fragment;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Stack;
//
//import android.annotation.SuppressLint;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import com.jinxin.jxsmarthome.util.Logger;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.jinxin.datan.net.command.CommonDeviceControlByServerTask;
//import com.jinxin.datan.net.command.MusicListControlByServerTask;
//import com.jinxin.datan.toolkit.internet.InternetTask;
//import com.jinxin.datan.toolkit.task.ITask;
//import com.jinxin.datan.toolkit.task.ITaskListener;
//import com.jinxin.jxsmarthome.R;
//import com.jinxin.jxsmarthome.constant.CommonMethodForMusic;
//import com.jinxin.jxsmarthome.constant.GenPowerAmplifier;
//import com.jinxin.jxsmarthome.constant.ProductConstants;
//import com.jinxin.jxsmarthome.entity.FunDetail;
//import com.jinxin.jxsmarthome.entity.Music;
//import com.jinxin.jxsmarthome.entity.ProductFun;
//import com.jinxin.jxsmarthome.main.JxshApp;
//import com.jinxin.jxsmarthome.main.JxshApp.CommDefines;
//import com.jinxin.jxsmarthome.ui.widget.RadialMenuWidget;
//import com.jinxin.jxsmarthome.ui.widget.RadialMenuWidget.RadialMenuEntry;
//
///**
// * 音乐播放详细控制界面
// * @author  TangLong
// * @company 金鑫智慧
// */
//@SuppressLint("ValidFragment")
//public class MusicNewFragment extends Fragment implements View.OnClickListener {
//	public static final String ARGUMENT_PRODUCTFUN = "ProductFun";
//	public static final String ARGUMENT_FUNDETAIL = "FunDetail";
//	
//	private LinearLayout mMainControl;	// 主控制面板区域
//	private TextView mSoundOff;			// 静音
//	private TextView mSoundOn;			// 取消静音
//	private TextView mSongList;			// 歌曲列表
//	
//	private RadialMenuWidget pieMenu;	// 主控制面板
//	private ProductFun productFun;		// 产品对象
//	private FunDetail funDetail;		// 设备对象
//	private ArrayList<Music> songs;		// 歌曲列表
//	private Play play;
//	private Pause pause;
//	private Handler mDownLoadHandler;
//	public MusicNewFragment(){
//		
//	}
//	public MusicNewFragment(Handler handler){
//		mDownLoadHandler = handler;
//	}
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		productFun = (ProductFun) getArguments().get(ARGUMENT_PRODUCTFUN);
//		funDetail = (FunDetail) getArguments().get(ARGUMENT_FUNDETAIL);
//		funDetail.setShortcutClose("111111");
//		funDetail.setShortcutOpen("111111");
//		funDetail.setShortCutOperation("11111");
//		songs = new ArrayList<Music>();
//		play = new Play();
//		pause = new Pause();
//		Logger.info("tangl", productFun.toString());
//		Logger.info("tangl", funDetail.toString());
//	}
//	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.music_new, null);
//		
//		mMainControl = (LinearLayout)view.findViewById(R.id.music_new_main_control_panel);
//		mSoundOff = (TextView)view.findViewById(R.id.music_new_tv_soundoff);
//		mSoundOn = (TextView)view.findViewById(R.id.music_new_tv_soundon);
//		mSongList = (TextView)view.findViewById(R.id.music_new_song_list);
//		
//		mSoundOff.setOnClickListener(this);
//		mSoundOn.setOnClickListener(this);
//		mSongList.setOnClickListener(this);
//		
//		pieMenu = new RadialMenuWidget(getActivity());
//
//		pieMenu.setAnimationSpeed(0L);
//		pieMenu.setSourceLocation(300,300);
//		//pieMenu.setCenterLocation(240,400);
//		//pieMenu.setInnerRingRadius(50, 120);
//		//pieMenu.setInnerRingColor(Color.LTGRAY, 255);
//		//pieMenu.setHeader("Menu Header", 20);
//
//		//int xLayoutSize = mMainControl.getWidth();
//		//int yLayoutSize = mMainControl.getHeight();	
//		
//		Logger.debug("tangl", mMainControl.getWidth() + "");
//		Logger.debug("tangl", mMainControl.getHeight() + "");
//		
//		//pieMenu.setSourceLocation(xLayoutSize, yLayoutSize);
//		pieMenu.setIconSize(15, 30);
//		pieMenu.setTextSize(13);				
//		
//		pieMenu.setCenterCircle(play);
//		pieMenu.addMenuEntry(new SoundUp());
//		pieMenu.addMenuEntry(new Next());
//		pieMenu.addMenuEntry(new SoundDown());
//		pieMenu.addMenuEntry(new Previous());
//		
//		mMainControl.addView(pieMenu);
//		
//		return view;
//	}
//	
//	@Override
//	public void onPause() {
//		super.onPause();
//	}
//	
//	@Override
//	public void onClick(View v) {
//		switch(v.getId()) {
//		default :
//		case R.id.music_new_tv_soundoff :
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_MUTE);		// 操作指令为“静音”		
//			break;
//		case R.id.music_new_tv_soundon :
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_UNMUTE);		// 操作指令为“取消静音”
//			break;
//		case R.id.music_new_song_list :
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_LIST);
//			break;
//		}
//		
//		sendCmd();
//	}
//	
//	// 发送操作命令
//	private void sendCmd() {
//		if(productFun == null || funDetail == null) {
//			return;
//		}
//		
//		List<byte[]> cmds = CommonMethodForMusic.productFunToCMD(getActivity(), productFun, funDetail, 0);
//		if(cmds == null || cmds.size() < 1){
//			return;
//		}
//		byte[] cmd = cmds.get(0);
//		InternetTask cdcbsTask = null;
//		if(ProductConstants.POWER_AMPLIFIER_SOUND_LIST.equals(productFun.getFunType())){
//			cdcbsTask = new MusicListControlByServerTask(getActivity(), cmd, false);
//		} else {
//			cdcbsTask = new CommonDeviceControlByServerTask(getActivity(), cmd, false);
//		}
//		cdcbsTask.addListener(new ITaskListener<ITask>() {
//			@Override
//			public void onStarted(ITask task, Object arg) {
//				Logger.debug("tangl", "onstart...");
//				JxshApp.showLoading(getActivity(), CommDefines.getSkinManager().string(R.string.qing_qiu_chu_li_zhong));
//			}
//
//			@Override
//			public void onCanceled(ITask task, Object arg) {
//				Logger.debug("tangl", "oncancel...");
//				JxshApp.closeLoading();
//			}
//
//			@Override
//			public void onFail(ITask task, Object[] arg) {
//				Logger.debug("tangl", "onfail...");
//				JxshApp.closeLoading();
//			}
//
//			@Override
//			public void onSuccess(ITask task, Object[] arg) {
//				Logger.debug("tangl", "onsuccess...");
//				JxshApp.closeLoading();
//				Logger.debug("tangl", arg.length + "");
//				if(ProductConstants.POWER_AMPLIFIER_SOUND_LIST.equals(productFun.getFunType())) {
//					String result = (String)arg[0];
//					Logger.debug("tangl.success", result);
//					// 成功filesumusb0012138066
//					if ( result.indexOf("filesumusb") != -1 ) {
//						String musicCount = result.replace( "filesumusb", "" ).substring( 0 , 4 );
//						Integer size = Integer.parseInt( musicCount );
//						Logger.debug("tangl", size + "");
//						
//						if(size < 1) {
//							return;
//						}
//						
//						// 递归获取歌曲列表
//						Logger.debug("tangl", "开始递归获取歌曲...");
//						productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SONG);
//						Stack<Integer> ppos = new Stack<Integer>();
//						for(int i = size - 1; i > 0; i--) {
//							ppos.push(i);
//						}
//						patternOperationSend(ppos);
//					}
//					
//				}
//				productFun.setOpen(!productFun.isOpen());
//			}
//
//			@Override
//			public void onProcess(ITask task, Object[] arg) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//		cdcbsTask.start();
//	}
//	
//	// 发送播放指令
//	private void playCmdSend() {
//		Logger.debug("tangl.play", "发送播放指令...");
//		List<byte[]> cmds = CommonMethodForMusic.productFunToCMD(getActivity(), productFun, funDetail, 0);
//		Logger.debug("tangl.play", "发送播放指令数量" + cmds.size());
//		if(cmds == null || cmds.size() < 1) {
//			return;
//		}
//		Stack<byte[]> ppos = new Stack<byte[]>();
//		for(int i = 0; i < cmds.size(); i++) {
//			ppos.push(cmds.get(i));
//		}
//		
//		patternOperationSendForPlay(ppos);
//	}
//	
//	// 递归发送播放指令
//	private void patternOperationSendForPlay(final Stack<byte[]> _ppos) {
//		if(_ppos == null || _ppos.empty()) {
//			return;
//		}
//		
//		byte[] _ppo = _ppos.pop();
//		
//		if(_ppo != null){
//			byte[] cmd = _ppo;
//			final MusicListControlByServerTask cdcbsTask = new MusicListControlByServerTask(getActivity(), cmd, true);
//			
//			if(_ppos == null || _ppos.empty()){
//				//关闭socket长连接
//				cdcbsTask.closeSocketLong();
//			}
//			
//			cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//				public void onStarted(ITask task, Object arg) {
//					Logger.debug("tangl.play", "onstart...");
//				}
//
//				@Override
//				public void onCanceled(ITask task, Object arg) {
//					Logger.debug("tangl.play", "oncancel...");
//				}
//
//				@Override
//				public void onFail(ITask task, Object[] arg) {
//					Logger.debug("tangl.play", "onfail...");
//					String result = (String)arg[0];
//					Logger.debug("tangl.play.success", result);
//				}
//
//				@Override
//				public void onSuccess(ITask task, Object[] arg) {
//					String result = (String)arg[0];
//					Logger.debug("tangl.play.success", result);
//					patternOperationSendForPlay(_ppos);
//					
//				}
//
//				@Override
//				public void onProcess(ITask task, Object[] arg) {
//					// TODO Auto-generated method stub
//					
//				}
//			});
//			cdcbsTask.start();
//		}
//	}
//	
//	 // 递归获取歌曲列表
//	 private void patternOperationSend(final Stack<Integer> _ppos){
//		 if(_ppos == null || _ppos.empty()) {
//			Fragment fragment = new MusicListFragment();
//			FragmentManager fm = this.getActivity().getSupportFragmentManager();
//			Bundle data = new Bundle();
////			data.putSerializable(MusicListFragment.ARGUMENT_LIST, this.songs);
//			fragment.setArguments(data);
//			fm.beginTransaction().add(R.id.device_detail_control, fragment).addToBackStack(null)
//								 .commit();
//			 return;
//		 }
//		 Integer _ppo = _ppos.pop();
//		 if(_ppo != null){
//			 Logger.debug("tangl", "获取歌曲:" + _ppo);
//				byte[] cmd = CommonMethodForMusic.productFunToCMD(getActivity(), productFun, funDetail, _ppo).get(0);
//				final MusicListControlByServerTask cdcbsTask = new MusicListControlByServerTask(getActivity(), cmd, true);
//				
//				if(_ppos == null || _ppos.empty()){
//					Logger.debug("tangl.song", "获取歌曲名完成");
//					//关闭socket长连接
//					cdcbsTask.closeSocketLong();
//				}
//				
//				cdcbsTask.addListener(new ITaskListener<ITask>() {
//
//					public void onStarted(ITask task, Object arg) {
//						Logger.debug("tangl.song", "onstart...");
//					}
//
//					@Override
//					public void onCanceled(ITask task, Object arg) {
//						Logger.debug("tangl.song", "oncancel...");
//					}
//
//					@Override
//					public void onFail(ITask task, Object[] arg) {
//						Logger.debug("tangl.song", "onfail...");
//					}
//
//					@Override
//					public void onSuccess(ITask task, Object[] arg) {
//						patternOperationSend(_ppos);
//						String result = (String)arg[0];
//						Logger.debug("tangl.song.success", result);
//						if ( result.indexOf("filenameusb") != -1 && result.length() > 38) {
//							String temp = result.replace( "filenameusb", "" ).substring(12);
//							temp = temp.substring( 0 , temp.length() - 6 );
//							String str = GenPowerAmplifier.getLongMusicName( temp );
//							Logger.debug("tangl.song.name", str + "");
//							Music m = new Music();
//							m.setTitle(str);
//							songs.add(m);
//						}
//						
//					}
//
//					@Override
//					public void onProcess(ITask task, Object[] arg) {
//						// TODO Auto-generated method stub
//						
//					}
//				});
////				cdcbsTask.start();
//				mDownLoadHandler.postDelayed(cdcbsTask, 10);
//			}
//	 }
//	
//	// 播放
//	public class Play implements RadialMenuEntry {
//		public String getName() { return "Play"; } 
//		public String getLabel() { return "Play"; } 
//		public int getIcon() { return R.drawable.play_ctrl_play_prs; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PLAY);	// 操作指令为“上一曲”
//			playCmdSend();
//			pieMenu.setCenterCircle(pause);
//		}
//	}
//	// 暂停
//	public class Pause implements RadialMenuEntry {
//		public String getName() { return "Pause"; } 
//		public String getLabel() { return "Pause"; } 
//		public int getIcon() { return R.drawable.play_ctrl_pause_prs; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_PAUSE);	// 操作指令为“上一曲”
//			sendCmd();
//			pieMenu.setCenterCircle(play);
//		}
//	}
//	// 上一曲
//	public class Previous implements RadialMenuEntry {
//		public String getName() { return "previous"; } 
//		public String getLabel() { return "上一曲"; } 
//		public int getIcon() { return R.drawable.play_btn_prev; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_PROVIOUS);	// 操作指令为“上一曲”
//			sendCmd();
//		}
//	}
//	// 下一曲
//	public class Next implements RadialMenuEntry {
//		public String getName() { return "next"; } 
//		public String getLabel() { return "下一曲"; } 
//		public int getIcon() { return R.drawable.play_btn_next; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_NEXT);	// 操作指令为“下一曲”
//			sendCmd();
//		}
//	}
//	// 音量+
//	public class SoundUp implements RadialMenuEntry {
//		public String getName() { return "up"; } 
//		public String getLabel() { return "音量 +"; } 
//		public int getIcon() { return android.R.drawable.ic_menu_close_clear_cancel; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_ADD);	// 操作指令为“下一曲”
//			sendCmd();
//		}
//	}
//	// 音量 -
//	public class SoundDown implements RadialMenuEntry {
//		public String getName() { return "down"; } 
//		public String getLabel() { return "音量 -"; } 
//		public int getIcon() { return android.R.drawable.ic_menu_close_clear_cancel; }
//		public List<RadialMenuEntry> getChildren() { return null; }
//		public void menuActiviated() {
//			productFun.setFunType(ProductConstants.POWER_AMPLIFIER_SOUND_SUB);	// 操作指令为“下一曲”
//			sendCmd();
//		}
//	}
//	
//	
//	public class Close implements RadialMenuEntry
//	   {
//
//		  public String getName() { return "Close"; } 
//		  public String getLabel() { return null; } 
//	      public int getIcon() { return android.R.drawable.ic_menu_close_clear_cancel; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	  
//	    	  System.out.println( "Close Menu Activated");
//	    	  //Need to figure out how to to the layout.removeView(pieMenu)
//	    	  //ll.removeView(pieMenu);
//	    	  ((LinearLayout)pieMenu.getParent()).removeView(pieMenu); 
//
//	    	  
//	      }
//	   }	
//	
//	   
//	   public static class Menu1 implements RadialMenuEntry
//	   {
//	      public String getName() { return "Menu1 - No Children"; } 
//		  public String getLabel() { return "Menu1\nTest"; } 
//		  public int getIcon() { return 0; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	    	  System.out.println( "Menu #1 Activated - No Children");
//	      }
//	   }	
//	   
//
//	   public static class Menu2 implements RadialMenuEntry
//	   {
//	      public String getName() { return "Menu2 - Children"; }
//		  public String getLabel() { return "Menu2"; }
//	      public int getIcon() { return R.drawable.ic_launcher; }
//	      private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>( Arrays.asList( new StringOnly(), new IconOnly(), new StringAndIcon() ) );
//	      public List<RadialMenuEntry> getChildren() { return children; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Menu #2 Activated - Children");
//	      }
//	   }	
//	   
//	   
//	   
//	   public static class Menu3 implements RadialMenuEntry
//	   {
//	      public String getName() { return "Menu3 - No Children"; }
//		  public String getLabel() { return null; } 
//	      public int getIcon() { return R.drawable.ic_launcher; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Menu #3 Activated - No Children");
//	      }
//	   }
//	   
//	   public static class IconOnly implements RadialMenuEntry
//	   {
//	      public String getName() { return "IconOnly"; }
//		  public String getLabel() { return null; } 
//	      public int getIcon() { return R.drawable.icon_info; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "IconOnly Menu Activated");
//	      }
//	   }
//	   
//	   
//	   public static class StringAndIcon implements RadialMenuEntry
//	   {
//	      public String getName() { return "StringAndIcon"; }
//		  public String getLabel() { return "String"; } 
//	      public int getIcon() { return R.drawable.icon_info; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "StringAndIcon Menu Activated");
//	      }
//	   }
//	   
//	   public static class StringOnly implements RadialMenuEntry
//	   {
//	      public String getName() { return "StringOnly"; } 
//		  public String getLabel() { return "String\nOnly"; }
//	      public int getIcon() { return 0; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "StringOnly Menu Activated");
//	      }
//	   }
//	   
//	   public static class NewTestMenu implements RadialMenuEntry
//	   {
//	      public String getName() { return "NewTestMenu"; } 
//		  public String getLabel() { return "New\nTest\nMenu"; }
//	      public int getIcon() { return 0; }
//	      private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>( Arrays.asList( new StringOnly(), new IconOnly() ) );
//	      public List<RadialMenuEntry> getChildren() { return children; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "New Test Menu Activated");
//	      }
//	   }
//
//
//	   
//	   
//	   
//	   public static class CircleOptions implements RadialMenuEntry
//	   {
//	      public String getName() { return "CircleOptions"; } 
//		  public String getLabel() { return "Circle\nSymbols"; }
//	      public int getIcon() { return 0; }
//	      private List<RadialMenuEntry> children = new ArrayList<RadialMenuEntry>( Arrays.asList( new RedCircle(), new YellowCircle(), new GreenCircle(), new BlueCircle() ) );
//	      public List<RadialMenuEntry> getChildren() { return children; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Circle Options Activated");
//	      }
//	   }	
//	   public static class RedCircle implements RadialMenuEntry
//	   {
//	      public String getName() { return "RedCircle"; } 
//		  public String getLabel() { return "Red"; }
//	      public int getIcon() { return R.drawable.red_circle; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Red Circle Activated");
//	      }
//	   }	   
//	   public static class YellowCircle implements RadialMenuEntry
//	   {
//	      public String getName() { return "YellowCircle"; } 
//		  public String getLabel() { return "Yellow"; }
//	      public int getIcon() { return R.drawable.yellow_circle; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Yellow Circle Activated");
//	      }
//	   }
//	   public static class GreenCircle implements RadialMenuEntry
//	   {
//	      public String getName() { return "GreenCircle"; } 
//		  public String getLabel() { return "Green"; }
//	      public int getIcon() { return R.drawable.green_circle; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Green Circle Activated");
//	      }
//	   }
//	   public static class BlueCircle implements RadialMenuEntry
//	   {
//	      public String getName() { return "BlueCircle"; } 
//		  public String getLabel() { return "Blue"; }
//	      public int getIcon() { return R.drawable.blue_circle; }
//	      public List<RadialMenuEntry> getChildren() { return null; }
//	      public void menuActiviated()
//	      {
//	         System.out.println( "Blue Circle Activated");
//	      }
//	   }
//	
//}
