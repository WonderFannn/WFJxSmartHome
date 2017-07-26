package com.jinxin.jxsmarthome.dlna.controller;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Action;
import org.teleal.cling.model.meta.Device;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.ServiceId;
import org.teleal.cling.model.types.UDAServiceId;
import org.teleal.cling.support.avtransport.callback.GetMediaInfo;
import org.teleal.cling.support.avtransport.callback.GetPositionInfo;
import org.teleal.cling.support.avtransport.callback.GetTransportInfo;
import org.teleal.cling.support.avtransport.callback.Pause;
import org.teleal.cling.support.avtransport.callback.Play;
import org.teleal.cling.support.avtransport.callback.Seek;
import org.teleal.cling.support.avtransport.callback.SetAVTransportURI;
import org.teleal.cling.support.avtransport.callback.Stop;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PositionInfo;
import org.teleal.cling.support.model.TransportInfo;
import org.teleal.cling.support.renderingcontrol.callback.GetVolume;
import org.teleal.cling.support.renderingcontrol.callback.SetMute;
import org.teleal.cling.support.renderingcontrol.callback.SetVolume;

import com.jinxin.jxsmarthome.main.JxshApp;

import android.content.Context;
import android.text.TextUtils;
import com.jinxin.jxsmarthome.util.Logger;

/**
 * DLNA设备操作、查询状态
 * @author YangJiJun
 * @company 金鑫智慧
 */
@SuppressWarnings("rawtypes")
public class UpnpController implements IController{
	
	private static final String AVTransport1 = "AVTransport";
	
    private ControlPoint mControlPoint;
    
    private static final String SetAVTransportURI = "SetAVTransportURI";
    
    private static final String RenderingControl = "RenderingControl";
    
    private static final String Play = "Play";
    
    String duration = "";

    String positonInfo = "";
    
    String tandsportInfo = "";
    
    private Context context;
    
    public UpnpController(ControlPoint conPoint)
    {
        super();
        this.mControlPoint = conPoint;
    }
    public UpnpController(Context context,ControlPoint conPoint)
    {
    	super();
    	this.mControlPoint = conPoint;
    	this.context = context;
    }
    
    @Override
    public int getMaxVolumeValue( Device device)
    {
        String maxValue = getVolumeDbRange(device, "MaxValue");
        if (TextUtils.isEmpty(maxValue))
        {
            return 100;
        }
        return Integer.parseInt(maxValue);
    }
    
    @Override
    public String getMediaDuration(Device device)
    {
        Service service = device.findService(new UDAServiceId(AVTransport1));
        if (null == service)
        {
            return null;
        }
        ActionCallback callback = new GetMediaInfo(service)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
                Logger.debug("Yang", "getMediaDuration失败");
            }
            
            @Override
            public void received(ActionInvocation arg0, MediaInfo arg1)
            {
                arg0.getAction();
                duration = arg1.getMediaDuration();
                Logger.debug("Yang","歌曲时长："+duration);
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
//            	Logger.debug("Yang", "getMediaDuration成功");
            	
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(callback);
        return duration;
    }
    
    @Override
    public int getMinVolumeValue(Device device)
    {
        String minValue = getVolumeDbRange(device, "MinValue");
        if (TextUtils.isEmpty(minValue))
        {
            return 0;
        }
        return Integer.parseInt(minValue);
    }
    
    @Override
    public String getMute(Device device)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (null == service)
        {
            return null;
        }
        /* ActionCallback callback=new GetMute(service)
         {
             
             @Override
             public void failure(ActionInvocation paramActionInvocation, UpnpResponse paramUpnpResponse, String paramString)
             {
                 paramString.getBytes();
             }
             
             @Override
             public void received(ActionInvocation arg0, boolean arg1)
             {
                 arg0.getAction();
                 
                 
             }
             @Override
             public void success(ActionInvocation invocation)
             {
                 super.success(invocation);
                 invocation.getAction();
             }
         };*/
        final Action action = service.getAction("GetMute");
        if (action == null)
        {
            return null;
        }
        ActionInvocation<Service> ai = new ActionInvocation<Service>(action);
        
        ai.setInput("InstanceID", "0");
        ai.setInput("Channel", "Master");
        ActionCallback callback = new ActionCallback(ai)
        {
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void success(ActionInvocation paramActionInvocation)
            {
                action.getOutputArgument("CurrentMute");
            }
        };
        mControlPoint.execute(callback);
        return null;
    }
    
    @Override
    public String getPositionInfo(Device device)
    {
        Service localService =
            device.findService(new UDAServiceId("AVTransport"));
        
        if (localService == null)
        {
            return null;
        }
        final Action localAction = localService.getAction("GetPositionInfo");
        if (localAction == null)
        {
            return null;
        }
//        ActionInvocation<Service> aci =
//            new ActionInvocation<Service>(localAction);
//        aci.setInput("InstanceID", "0");
//        ActionCallback callback = new ActionCallback(aci)
//        {
//            
//            @Override
//            public void failure(ActionInvocation paramActionInvocation,
//                UpnpResponse paramUpnpResponse, String paramString)
//            {
//                
//            }
//            
//            @Override
//            public void success(ActionInvocation paramActionInvocation)
//            {
//                localAction.getInputArgument("AbsTime").getName();
//            }
//        };
        
        ActionCallback getPositonInfo = new GetPositionInfo(localService) {
			
			@Override
			public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
				
			}
			
			@Override
			public void received(ActionInvocation arg0, PositionInfo arg1) {
				positonInfo = arg1.getAbsTime();
				Logger.debug("Yang", "Time->"+positonInfo);
			}
		};
        mControlPoint.execute(getPositonInfo);
        return positonInfo;
    }
    
    @Override
    public String getTransportState(Device device)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (null == service)
        {
            return null;
        }
        ActionCallback callback = new GetTransportInfo(service)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void received(ActionInvocation arg0, TransportInfo arg1)
            {
//            	Logger.debug("Yang",arg1.getCurrentTransportState().toString());  
            	tandsportInfo = arg1.getCurrentTransportState().toString(); 
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction()
                    .getOutputArgument("CurrentTransportState");
            }
        };
        mControlPoint.execute(callback);
        return tandsportInfo;
    }
    
    @Override
    public int getVoice(Device device)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (null == service)
        {
            return -1;
        }
        ActionCallback callback = new GetVolume(service)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void received(ActionInvocation arg0, int arg1)
            {
                
            }
            
            @Override
            public void success(ActionInvocation arg0)
            {
                super.success(arg0);
            }
        };
        mControlPoint.execute(callback);
        return 0;
    }
    
    public String getVolumeDbRange(Device device, final String argument)
    {
        Service localService =
            device.findService(ServiceId.valueOf(RenderingControl));
        if (localService == null)
        {
            return null;
        }
        
        final Action action = localService.getAction("GetVolumeDBRange");
        if (action == null)
        {
            return null;
        }
        ActionInvocation<Service> localAction =
            new ActionInvocation<Service>(action);
        localAction.setInput("InstanceID", "0");
        localAction.setInput("Channel", "Master");
        
        ActionCallback callback = new ActionCallback(localAction)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void success(ActionInvocation paramActionInvocation)
            {
                action.getOutputArgument(argument);
            }
        };
        mControlPoint.execute(callback);
        
        return null;
    }
    
    @Override
    public boolean goon(Device device, String pausePosition)
    {
        Service localService =
            device.findService(new UDAServiceId("AVTransport"));
        if (localService == null)
        {
            return false;
        }
        
        final Action localAction = localService.getAction("Seek");
        if (localAction == null)
        {
            return false;
        }
        ActionInvocation<Service> ain =
            new ActionInvocation<Service>(localAction);
        ain.setInput("InstanceID", "0");
        // if (mUseRelTime) {
        // } else {
        // localAction.setArgumentValue("Unit", "ABS_TIME");
        // }
        // LogUtil.e(tag, "继续相对时间："+mUseRelTime);
        // 测试解决播放暂停后时间不准确
        ain.setInput("Unit", "ABS_TIME");
        ain.setInput("Target", pausePosition);
        ActionCallback callback = new ActionCallback(ain)
        {
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void success(ActionInvocation paramActionInvocation)
            {
                
            }
        };
        mControlPoint.execute(callback);
        ActionCallback playCallback = new Play(localService, "1")
        {
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(playCallback);
        return false;
    }
    
    @Override
    public boolean pause(Device device)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (service == null)
        {
            return false;
        }
        ActionCallback pauseCallback = new Pause(service)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                Logger.debug("Yang","暂停播放");
            }
        };
        mControlPoint.execute(pauseCallback);
        return false;
    }
    
    @Override
    public boolean play(Device device, String path)
    {
        //播放之前先stop 才可以播放新的歌曲
//        stop(device);
//        Service[] services = device.getServices();
        final Service service =
        		device.findService(new UDAServiceId("AVTransport"));
        if (service == null)
        {
            return false;
        }
        ActionCallback acb = new SetAVTransportURI(service, path)
        {
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
                Logger.debug("Yang", "解析地址失败");
                JxshApp.showToast(context, "音乐地址解析失败");
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                Logger.debug("Yang", "解析地址成功");
                ActionCallback abc = new Play(service, "1")
                {
                    @Override
                    public void failure(ActionInvocation paramActionInvocation,
                        UpnpResponse paramUpnpResponse, String paramString)
                    {
                        paramString.getBytes();
                        Logger.debug("Yang","播放失败");
                    }
                    
                    @Override
                    public void success(ActionInvocation invocation)
                    {
                        super.success(invocation);
                        invocation.getAction();
                        Logger.debug("Yang","开始播放");
                    }
                };
                mControlPoint.execute(abc);//执行播放
            }
        };
        mControlPoint.execute(acb);//URL
        return true;
    }
    
    @Override
    public boolean seek(Device device, String targetPosition)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (null == service)
        {
            return false;
        }
        ActionCallback callback = new Seek(service, targetPosition)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(callback);
        
        return true;
    }
    
    @Override
    public boolean setMute(Device device, String targetValue)
    {
        Service service = device.findService(new UDAServiceId("AVTransport"));
        if (null == service)
        {
            return false;
        }
        ActionCallback callback = new SetMute(service, true)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
                
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(callback);
        
        return true;
    }
    
    @Override
    public boolean setVoice(Device device, int value)
    {
        Service service =
            device.findService(ServiceId.valueOf(RenderingControl));
        if (null == service)
        {
            return false;
        }
        ActionCallback callback = new SetVolume(service, value)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(callback);
        return true;
    }
    
    @Override
    public boolean stop(Device device)
    {
        Service service = device.findService(new UDAServiceId(AVTransport1));
        if (null == service)
        {
            return false;
        }
        ActionCallback callback = new Stop(service)
        {
            
            @Override
            public void failure(ActionInvocation paramActionInvocation,
                UpnpResponse paramUpnpResponse, String paramString)
            {
                paramString.getBytes();
            }
            
            @Override
            public void success(ActionInvocation invocation)
            {
                super.success(invocation);
                invocation.getAction();
            }
        };
        mControlPoint.execute(callback);
        return true;
    }
}
