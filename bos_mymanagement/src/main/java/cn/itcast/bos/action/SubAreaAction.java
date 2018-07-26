package cn.itcast.bos.action;

import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.service.AreaService;
import cn.itcast.bos.service.SubAreaService;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@ParentPackage("json-default")
@Namespace("/")
@Scope("prototype")
public class SubAreaAction extends BaseAction<SubArea> {

    @Autowired
    private AreaService areaService;

    @Autowired
    private SubAreaService subAreaService;

    //属性驱动接收文件
    private File file;
    private String fileFileName;

    public void setFile(File file) {
        this.file = file;
    }

    public void setFileFileName(String fileFileName) {
        this.fileFileName = fileFileName;
    }

    @Action(value = "sub_area_upload",results = {@Result(name = "success",type = "json")})
    public String subAreaUpload() throws Exception {
        Map<String,Object> message = new HashMap<>();
        Sheet sheet = null;
        try {
            List<SubArea> subAreas = new ArrayList<>();
            if (fileFileName.endsWith("xlsx")) {
                XSSFWorkbook xssfSheets = new XSSFWorkbook(new FileInputStream(file));
                sheet = xssfSheets.getSheetAt(0);
            }
            if (fileFileName.endsWith("xls")) {
                HSSFWorkbook hssfSheets = new HSSFWorkbook(new FileInputStream(file));
                sheet = hssfSheets.getSheetAt(0);
            }
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    continue;
                }
                // 跳过空行
                if (row.getCell(0) == null
                        || StringUtils.isBlank(row.getCell(0).getStringCellValue())) {
                    continue;
                }
                SubArea subArea = new SubArea();
                Area area = new Area();
                subArea.setId(row.getCell(0).getStringCellValue());
                //封装省市区到区域
                area.setProvince(row.getCell(1).getStringCellValue());
                area.setCity(row.getCell(2).getStringCellValue());
                area.setDistrict(row.getCell(3).getStringCellValue());
                //从区域中查找区域
                Area persistArea = areaService.findByProvinceAndCityAndDistrict(area.getProvince(),area.getCity(),area.getDistrict());
                //将区域对象封装到分区
                subArea.setArea(persistArea);
                //继续封装其他数据
                subArea.setKeyWords(row.getCell(4).getStringCellValue());
                subArea.setStartNum(row.getCell(5).getStringCellValue());
                //单双号不要了
                subArea.setEndNum(row.getCell(7).getStringCellValue());
                subArea.setAssistKeyWords(row.getCell(8).getStringCellValue());

                //将每一个分区添加到集合
                subAreas.add(subArea);
            }
            subAreaService.saveBatch(subAreas);
            message.put("message","文件上传成功");
            message.put("tag", true);

        }catch (Exception e){
            e.printStackTrace();
            message.put("message","文件上传失败");
            message.put("tag", false);
        }finally {
            ActionContext.getContext().getValueStack().push(message);
        }


        return SUCCESS;
    }
}
