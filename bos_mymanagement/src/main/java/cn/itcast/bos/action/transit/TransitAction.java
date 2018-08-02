package cn.itcast.bos.action.transit;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.TransitInfoService;
import com.opensymphony.xwork2.ActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class TransitAction extends BaseAction<TransitInfo> {
    @Autowired
    private TransitInfoService transitInfoService;

    private String wayBillIds;

    public void setWayBillIds(String wayBillIds) {
        this.wayBillIds = wayBillIds;
    }

    @Action(value = "transit_create",results = {@Result(name = "success",type = "json")})
    public String create(){
        Map<String ,Object> map = new HashMap<>();
        try{
            transitInfoService.create(wayBillIds);
            map.put("success",true);
            map.put("msg","配送成功");
        }catch (Exception e){
            e.printStackTrace();
            map.put("success",false);
            map.put("msg","配送失败");
        }
        ActionContext.getContext().getValueStack().push(map);
        return SUCCESS;
    }
}
