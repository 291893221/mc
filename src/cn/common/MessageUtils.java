package cn.common;

import java.util.Map;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MessageUtils {

	public static void addMessage(Map map){
		if(map.get("resultMap") != null){
			map.put("Message", Constans.SUCCESS);
		}else map.put("Message", Constans.FAILURE);
	}
	
}
