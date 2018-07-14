package cn.itcast.bos.action;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;

import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.service.FixedAreaService;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class FixedAreaAction extends BaseAction<FixedArea> {

	@Autowired
	private FixedAreaService fixAreaService;
	
	private int page;
	private int rows;
	
	@Action(value="fixedArea_add",results={@Result(name="success",type="redirect",location="./pages/base/fixed_area.html")})
	public String fixedAreaAdd(){
		fixAreaService.add(model);
		return SUCCESS;
	}
	
	@Action(value="fixedArea_pageQuery",results={@Result(name="success",type="json")})
	public String fixedAreapageQuery(){
		Pageable pageable  = new PageRequest(page-1, rows);
		List<Predicate> list = new ArrayList<>();
		Specification<FixedArea> specification = new Specification<FixedArea>() {
			//id,company,subareaName
			@Override
			public Predicate toPredicate(Root<FixedArea> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				if(StringUtils.isNotBlank(model.getId())){
					Predicate p1 = cb.equal(root.get("id").as(String.class), model.getId());
				}
				if(StringUtils.isNotBlank(model.getCompany())){
					Predicate p2 = cb.like(root.get("company").as(String.class), model.getCompany());
				}
				
				return null;
			}
		};
		return SUCCESS;
	}
}
