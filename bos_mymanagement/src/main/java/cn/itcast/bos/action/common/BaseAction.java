package cn.itcast.bos.action.common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.data.domain.Page;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class BaseAction<T> extends ActionSupport implements ModelDriven<T> {

	//泛型无法创建对象，所以需要利用子类上的泛型参数
	protected T model;
	@Override
	public T getModel() {
		return model;
	}

	//创建子类对象时会创建父类对象，只不过this均指代子类对象，this贯穿
	public BaseAction(){
		Type genericSuperclass = this.getClass().getGenericSuperclass();
		ParameterizedType parameterizedType = (ParameterizedType) genericSuperclass;
		Class<T> modelClass = (Class<T>) parameterizedType.getActualTypeArguments()[0];
		try {
			model = modelClass.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("模型构造失败");
		}
	}
	public void pushPageToValueStack(Page<T> page){
		//封装成json
		Map<String,Object> map = new HashMap<>();
		map.put("total", page.getTotalElements());
		map.put("rows", page.getContent());
		ActionContext.getContext().getValueStack().push(map);
	}
}
