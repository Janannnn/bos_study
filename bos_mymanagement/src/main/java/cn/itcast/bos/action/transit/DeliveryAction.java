package cn.itcast.bos.action.transit;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.transit.DeliveryInfo;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class DeliveryAction extends BaseAction<DeliveryInfo> {
    private String transitInfoId;

    public void setTransitInfoId(String transitInfoId) {
        this.transitInfoId = transitInfoId;
    }

    @Action(value = "delivery_add",results = {@Result(name = "success",type = "redirect",location = "./pages/transitinfo.html")})
    public String add(){
        return SUCCESS;
    }

}
