package cn.itcast.bos.action;

import java.io.File;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Area;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class AreaAction extends ActionSupport implements ModelDriven<Area>{

	//接收area对象参数
	private Area area = new Area();
	@Override
	public Area getModel() {
		return area;
	}

	private File file;
	public void setFile(File file) {
		this.file = file;
	}
	
	@Action(value="area_upload",results={@Result(name="success",type="json")})
	public String area_upload(){
		System.out.println(file);
		return SUCCESS;
	}
}
