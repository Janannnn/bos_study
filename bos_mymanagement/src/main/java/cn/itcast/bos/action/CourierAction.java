package cn.itcast.bos.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.Standard;
import cn.itcast.bos.service.CourierService;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class CourierAction extends ActionSupport implements ModelDriven<Courier> {

	private Courier courier = new Courier();
	@Override
	public Courier getModel() {
		return courier;
	}
	@Autowired
	private CourierService courierService;

	private int page;
	private int rows;
	private String ids;
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}
	
	public void setIds(String ids) {
		this.ids = ids;
	}

	@Action(value = "courier_add", results = {@Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String courieradd() {
		courierService.add(courier);
		System.out.println(courier.getStandard());
		return SUCCESS;
	}

	@Action(value = "courier_findAll", results = { @Result(name = "success", type = "json") })
	public String courierfindAll() {
		List<Courier> list = courierService.findAll();
		ActionContext.getContext().getValueStack().push(list);
		return SUCCESS;
	}

	@Action(value = "courier_del", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/courier.html") })
	public String courierDel() {
		String[] idss = ids.split(",");
		for (String idst : idss) {
			Integer id = Integer.parseInt(idst);
			courierService.updateTag(id);
		}
		return SUCCESS;
	}

	@Action(value = "courier_search", results = { @Result(name = "success", type = "json") })
	public String courierdSearch() {
		// 封装Pageable对象
		Pageable pageable = new PageRequest(page - 1, rows);
		// 封装条件查询对象 Specification
		Specification<Courier> specification = new Specification<Courier>() {
			@Override
			// Root 用于获取属性字段，CriteriaQuery可以用于简单条件查询，CriteriaBuilder 用于构造复杂条件查询
			public Predicate toPredicate(Root<Courier> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();

				// 简单单表查询
				if (StringUtils.isNotBlank(courier.getCourierNum())) {
					Predicate p1 = cb.equal(root.get("courierNum").as(String.class), courier.getCourierNum());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(courier.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class), "%" + courier.getCompany() + "%");
					list.add(p2);
				}
				if (StringUtils.isNotBlank(courier.getType())) {
					Predicate p3 = cb.equal(root.get("type").as(String.class), courier.getType());
					list.add(p3);
				}
				// 多表查询
				Join<Courier, Standard> standardJoin = root.join("standard", JoinType.INNER);
				if (courier.getStandard() != null && StringUtils.isNotBlank(courier.getStandard().getName())) {
					Predicate p4 = cb.like(standardJoin.get("name").as(String.class),
							"%" + courier.getStandard().getName() + "%");
					list.add(p4);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};

		// 调用业务层 ，返回 Page
		Page<Courier> pageData = courierService.findPageData(specification, pageable);
		// 将返回page对象 转换datagrid需要格式
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pageData.getTotalElements());
		result.put("rows", pageData.getContent());
		// 将结果对象 压入值栈顶部
		ActionContext.getContext().getValueStack().push(result);

		return SUCCESS;
	}

	@Action(value = "courier_delByID", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/courier2.html") })
	public String courierdDelete() {
		String[] idArray = ids.split(",");
		for (String idst : idArray) {
			int id = Integer.parseInt(idst);
			courierService.delByID(id);
		}
		return SUCCESS;
	}

}
