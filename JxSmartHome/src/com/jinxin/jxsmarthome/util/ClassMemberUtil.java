package com.jinxin.jxsmarthome.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.jinxin.infrared.model.InfraredCodeLibraryConstant;

public class ClassMemberUtil {
	
	public static void main(String[] args) {
		getObjAttr(new InfraredCodeLibraryConstant.MatchCodeImage.Fan());
	}
	
	public static int[] getObjAttr(Object obj)  
	{  
	    // 获取对象obj的所有属性域  
	    Field[] fields = obj.getClass().getDeclaredFields();  
	    int[] id = new int[fields.length];
	    for (int i = 0; i < id.length; i++)  
	    {  
	        // 对于每个属性，获取属性名  
	        String varName = fields[i].getName();  
	        try  
	        {  
	            boolean access = fields[i].isAccessible();
	            if(!access) fields[i].setAccessible(true);
	              
	            //从obj中获取field变量  
	            Object o = fields[i].get(obj);  
	            System.out.println("变量： " + varName + " = " + o);  
	            id[i] = (int) o;
	            if(!access) fields[i].setAccessible(false);  
	        }
	        catch (Exception ex)  
	        {  
	            ex.printStackTrace();  
	        }  
	    }
		return id;
	    
	}  
	public static Map<String, Integer> getObjMap(Object obj)  
	{  
	    // 获取对象obj的所有属性域  
	    Field[] fields = obj.getClass().getDeclaredFields();  
	    Map<String, Integer> map = new HashMap<String,Integer>();
	    
	    for (int i = 0; i < fields.length; i++)  
	    {  
	        // 对于每个属性，获取属性名  
	        String varName = fields[i].getName();  
	        try  
	        {  
	            boolean access = fields[i].isAccessible();
	            if(!access) fields[i].setAccessible(true);
	              
	            //从obj中获取field变量  
	            Object o = fields[i].get(obj);  
	            System.out.println("变量： " + varName + " = " + o);  
	            map.put(varName, new Integer((int) o));
	            if(!access) fields[i].setAccessible(false);  
	        }
	        catch (Exception ex)  
	        {  
	            ex.printStackTrace();  
	        }  
	    }
		return map;
	    
	}  
}
