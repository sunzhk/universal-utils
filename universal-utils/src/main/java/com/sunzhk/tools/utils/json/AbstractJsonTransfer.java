package com.sunzhk.tools.utils.json;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Iterator;

public class AbstractJsonTransfer extends JsonInterface{

	@Override
	public String toJson(FirstLetterType firstLetterType) {
		// TODO Auto-generated method stub
		
		StringBuilder bf = new StringBuilder("{");
		
		Field[] fields = getClass().getDeclaredFields();
		
		Object obj = null;
//		Class<?> type = null;
		for(Field field : fields){
			
			field.setAccessible(true);
//			type = field.getType();
			try {
				obj = field.get(this);
				
				bf.append("\"" + field.getName() + "\":\"" + obj.toString() + "\",");
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		bf.deleteCharAt(bf.length() - 1);
		
		bf.append("}");
		return bf.toString();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E toObject(JSONObject obj ,FirstLetterType firstLetterType) {
		// TODO Auto-generated method stub
		
		Class<?> clazz = getClass();
		
		Iterator<String> keys = obj.keys();
		String key = "";
		Field field = null;
//		Class<?> type = null;
		while(keys.hasNext()){
			key = keys.next();
			try {
				field = clazz.getDeclaredField(key);
				field.setAccessible(true);
				field.set(this, obj.get(key));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return (E) this;
	}
	
}
