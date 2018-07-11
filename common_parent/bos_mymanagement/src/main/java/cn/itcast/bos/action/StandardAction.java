package cn.itcast.bos.action;

import java.util.HashMap;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.StandardService;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class StandardAction extends ActionSupport implements ModelDriven<Standard> {

	private Standard standard = new Standard();

	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Autowired
	private StandardService standardService;

	@Override
	public Standard getModel() {
		return standard;
	}

	@Action(value = "standard_save", results = { @Result(name = "success", type = "redirect", location = "./index.html") })
	public String standardSave() {
		standardService.add(standard);
		return SUCCESS;
	}

	@Action(value="standard_search",results = { @Result(name = "success", type = "json") })
	public String standardSearch(){
		Pageable pageable = new PageRequest(page-1, rows);
		Page<Standard> pageData = standardService.findPageData(pageable);
		// 返回客户端数据 需要 total 和 rows
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());

		// 将map转换为json数据返回 ，使用struts2-json-plugin 插件
		ActionContext.getContext().getValueStack().push(result);
		return SUCCESS;
		
	}
}
