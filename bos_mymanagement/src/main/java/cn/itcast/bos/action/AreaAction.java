package cn.itcast.bos.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.service.AreaService;
import cn.itcast.bos.utils.PinYinDuoYinUtils;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class AreaAction extends BaseAction<Area>{



	//接收上传的文件
	private File file;
	private String fileFileName;
	private int page;
	private int rows;
	
	
	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setFile(File file) {
		this.file = file;
	}
	@Autowired
	private AreaService areaService;
	
	//有条件的分页查询
	@Action(value="area_pageSearch",results={@Result(name="success",type="json")})
	public String pageSearch(){
		Pageable pageable = new PageRequest(page-1,rows );
		//绑定条件
		Specification<Area> specification = new Specification<Area>() {
			@Override
			public Predicate toPredicate(Root<Area> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				//city,district
				//条件被模型接收
				if(StringUtils.isNotBlank(model.getProvince())){
					Predicate p1 = cb.like(root.get("province").as(String.class),"%"+model.getProvince()+"%" );
					list.add(p1);
				}
				if(StringUtils.isNotBlank(model.getCity())){
					Predicate p2 = cb.like(root.get("city").as(String.class), "%"+model.getCity()+"%");
					list.add(p2);
				}
				if(StringUtils.isNotBlank(model.getDistrict())){
					Predicate p3 = cb.like(root.get("district").as(String.class), "%"+model.getDistrict()+"%");
					list.add(p3);
				}
				return cb.and(list.toArray(new Predicate[0]));
			}
		};
		Page<Area> page = areaService.findPageData(specification, pageable);
		pushPageToValueStack(page);
		return SUCCESS;
	}
	//数据导入
	@Action(value="area_upload",results={@Result(name="success",type="json")})
	public String areaUpload(){
		Map<String,Object> message = new HashMap<>();
		try {
			List<Area> array = new ArrayList<Area>();
			Sheet sheet =null;
			if(fileFileName.endsWith("xlsx")){
				XSSFWorkbook xssfWorkbook = new XSSFWorkbook(new FileInputStream(file));
				sheet = xssfWorkbook.getSheetAt(0);
			}
			if(fileFileName.endsWith("xls")){
				HSSFWorkbook hssfWorkbook = new HSSFWorkbook(new FileInputStream(file));
				sheet = hssfWorkbook.getSheetAt(0);
			}
			for (Row row : sheet) {
				if(row.getRowNum()==0){
					continue;
				}
				// 跳过空行
				if (row.getCell(0) == null
						|| StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
					continue;
				}
				Area area = new Area();
				area.setId(row.getCell(0).getStringCellValue());
				area.setProvince(row.getCell(1).getStringCellValue());
				area.setCity(row.getCell(2).getStringCellValue());
				area.setDistrict(row.getCell(3).getStringCellValue());
				area.setPostcode(row.getCell(4).getStringCellValue());
				
				// 基于pinyin4j生成城市编码和简码
				String province = area.getProvince();
				String city = area.getCity();
				String district = area.getDistrict();
				province = province.substring(0, province.length() - 1);
				city = city.substring(0, city.length() - 1);
				district = district.substring(0, district.length() - 1);
				// 简码
				String shortCode = PinYinDuoYinUtils.convertChineseTogetHeadByString(province+city+district, true);
				area.setShortcode(shortCode);
				//城市编码
				String citycode = PinYinDuoYinUtils.convertChineseToPinyin(city, true);
				area.setCitycode(citycode);
				array.add(area);
			}
			areaService.saveBatch(array);
			int i = 10/0;
			message.put("message","文件上传成功");
			message.put("tag", true);
		} catch (Exception e) {
			e.printStackTrace();
			message.put("message","文件上传失败");
			message.put("tag", false);
		}finally {
			ActionContext.getContext().getValueStack().push(message);
		}
		return SUCCESS;
	}
}
