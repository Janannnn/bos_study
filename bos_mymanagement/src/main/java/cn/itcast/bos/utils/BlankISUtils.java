package cn.itcast.bos.utils;

public class BlankISUtils {

	/**
	 * 判断对象是否为空
	 * @param obj
	 * @return
	 */
	public static boolean isBlank(Object obj){
		if("".equals(obj)||obj==null){
			return true;
		}else{
			return false;
		}
	}
	public static boolean isEqual(Object obj01,Object obj02){
		if(isBlank(obj01)||isBlank(obj02)){
			return false;
		}
		if(!obj01.equals(obj02)){
			return false;
		}
		return true;
		
		
	}
}
