package cn.itcast.bos.action.take_delivery;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.take_delivery.Promotion;
import cn.itcast.bos.service.PromotionService;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;
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

import java.io.File;
import java.util.UUID;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {

    @Autowired
    private PromotionService promotionService;

    //利用拦截器进行文件上传
    private File titleImgFile;
    private String titleImgFileFileName;

    public void setTitleImgFile(File titleImgFile) {
        this.titleImgFile = titleImgFile;
    }

    public void setTitleImgFileFileName(String titleImgFileFileName) {
        this.titleImgFileFileName = titleImgFileFileName;
    }

    //进行分页查询
    private int page;
    private int rows;

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    @Action(value = "promotion_save", results = {@Result(type = "redirect", location = "./pages/take_delivery/promotion.html")})
    public String promotionSave() throws Exception {
        //先进行宣传图片的保存
        //文件保存目录路径
        String savePath = ServletActionContext.getServletContext().getRealPath("/") + "upload/";
        //文件保存目录URL
        String saveUrl = ServletActionContext.getRequest().getContextPath() + "/upload/";
        //生成随机图片名称
        UUID randomUUID = UUID.randomUUID();
        String ext = titleImgFileFileName.substring(titleImgFileFileName.lastIndexOf("."));
        String randomFileName = randomUUID + ext;
        //保存图片
        FileUtils.copyFile(titleImgFile, new File(savePath + randomFileName));
        model.setTitleImg(saveUrl + randomFileName);
        promotionService.promotionSave(model);
        return "success";
    }

    @Action(value = "promotion_pageQuery", results = {@Result(name = "success", type = "json")})
    public String promotionQuery() {
        Pageable pageable = new PageRequest(page - 1, rows);
        Page<Promotion> page = promotionService.findAll(pageable);
        ActionContext.getContext().getValueStack().push(page);
        return "success";
    }
}
