package com.sunzhk.tools.utils.json;

import org.json.JSONObject;

public abstract class JsonInterface {

	enum FirstLetterType{
		UpperCase,LowerCase;
	}
	/**
	 * 转换为Json-key小写开头
	 * @return
	 */
	public String toJson(){
		return toJson(FirstLetterType.LowerCase);
	};
	/**
	 * 将Json实例化为本地对象-本地参数名小写开头
	 * @param obj
	 * @return
	 */
	public <E> E toObject(JSONObject obj){
		return toObject(obj, FirstLetterType.LowerCase);
	};
	
	public abstract String toJson(FirstLetterType firstLetterType);
	public abstract <E> E toObject(JSONObject obj, FirstLetterType firstLetterType);
}
