package cn.itcast.bos.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
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

import com.opensymphony.xwork2.ActionContext;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.FixedAreaService;
import cn.itcast.crm.domain.Customer;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixAreaService;

	private int page;
	private int rows;
	private String[] customerIds;
	
	

	public void setCustomerIds(String[] customerIds) {
		this.customerIds = customerIds;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	@Action(value = "fixedArea_add", results = {
			@Result(name = "success", type = "redirect", location = "./pages/base/fixed_area.html") })
	public String fixedAreaAdd() {
		fixAreaService.add(model);
		return SUCCESS;
	}

	//分页条件查询
	@Action(value = "fixedArea_pageQuery", results = { @Result(name = "success", type = "json") })
	public String fixedAreapageQuery() {
		Pageable pageable = new PageRequest(page - 1, rows);
	
		Specification<FixedArea> specification = new Specification<FixedArea>() {
			
			// id,company,fixedAreaName
			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<>();
				if (StringUtils.isNotBlank(model.getId())) {
					Predicate p1 = cb.equal(root.get("id").as(String.class), model.getId());
					list.add(p1);
				}
				if (StringUtils.isNotBlank(model.getCompany())) {
					Predicate p2 = cb.like(root.get("company").as(String.class), model.getCompany());
					list.add(p2);
				}
				if (StringUtils.isNotBlank(model.getFixedAreaName())) {
					Predicate p3 = cb.like(root.get("fixedAreaName").as(String.class), model.getFixedAreaName());
					list.add(p3);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<FixedArea> page = fixAreaService.findPageData(specification,pageable);
		pushPageToValueStack(page);
		return SUCCESS;
	}

	// 查询未关联定区的客户记录
	@Action(value = "findNoAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findNoAssociationCustomers() {
		Collection<? extends Customer> collection = WebClient.create("http://localhost:9002/crm_management/services/customerService/noassociationcustomers")
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}
	//查询关联此定区的客户记录
	@Action(value = "findAssociationCustomers", results = { @Result(name = "success", type = "json") })
	public String findAssociationCustomers() {
		Collection<? extends Customer> collection = WebClient.create("http://localhost:9002/crm_management/services/customerService/associationcustomers/"+model.getId())
				.accept(MediaType.APPLICATION_JSON).getCollection(Customer.class);
		ActionContext.getContext().getValueStack().push(collection);
		return SUCCESS;
	}
	//定区关联客户
	@Action(value="associationCustomersToFixedArea",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String associationCustomersToFixedArea(){
		//访问服务器端接口
		String customerIdStr = StringUtils.join(customerIds, ",");
		System.out.println("别选择定区的ID"+model.getId());
		WebClient.create(
				"http://localhost:9002/crm_management/services/customerService"
						+ "/associationCustomersToFixedArea?customerIdStr="
						+ customerIdStr + "&fixedAreaId=" + model.getId()).put(
				null);
		return SUCCESS;
	}
}
