package cn.itcast.bos.action;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.FixedAreaService;

public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixAreaService;
	
	@Action(value="fixedArea_add",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String fixedAreaAdd(){
		fixAreaService.add(model);
		return SUCCESS;
	}
}
