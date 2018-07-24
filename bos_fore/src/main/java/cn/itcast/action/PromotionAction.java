package cn.itcast.action;

import cn.itcast.bos.domain.Constants;
import cn.itcast.bos.domain.pagebean.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import com.opensymphony.xwork2.ActionContext;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.FileUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class PromotionAction extends BaseAction<Promotion> {


    //分页查询前台参数
    private int page;
    private int rows;

    public void setRows(int rows) {
        this.rows = rows;
    }

    public void setPage(int page) {

        this.page = page;
    }

    @Action(value = "promotion_pageQuery", results = {@Result(name = "success", type = "json")})
    public String pageQuery() {
        //需要从bos_management获取分页的数据
        PageBean pageBean = WebClient.create(
                Constants.BOS_MANAGEMENT_URL+"/services/promotionService/pageQuery?page=" + page + "&rows=" + rows)
                .accept(MediaType.APPLICATION_JSON).get(PageBean.class);
        ActionContext.getContext().getValueStack().push(pageBean);
        return SUCCESS;
    }

    @Action(value = "promotion_showDetail")
    public String showDetail() throws Exception {
        String htmlRealPaht = ServletActionContext.getServletContext().getRealPath("/freemarker");
        File htmlFile = new File(htmlRealPaht +"/"+ model.getId() + ".html");
        if (!htmlFile.exists()) {
            //如果文件不存在，根据模板生成html文件
            Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
            configuration.setDirectoryForTemplateLoading(new File(
                    ServletActionContext.getServletContext()
                            .getRealPath("/WEB-INF/freemarker_templates")));

            //获取模板对象
            Template template = configuration.getTemplate("promotion_detail.ftl");

            //动态数据
            Promotion promotion = WebClient.create(
                    Constants.BOS_MANAGEMENT_URL+"/services/promotionService/promotion/"+model.getId())
                    .accept(MediaType.APPLICATION_JSON).get(Promotion.class);
            Map<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("promotion", promotion);

            //合并输出
            template.process(parameterMap, new OutputStreamWriter(
                    new FileOutputStream(htmlFile), "utf-8"));


        }
        //存在，将文件返回
        ServletActionContext.getResponse().setContentType("type/html;charset=utf-8");
        FileUtils.copyFile(htmlFile, ServletActionContext.getResponse().getOutputStream());
        return NONE;

    }
}
