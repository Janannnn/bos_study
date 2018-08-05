package cn.itcast.bos.action.transit;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.transit.InOutStorageInfo;
import cn.itcast.bos.service.transit.InOutStorageInfoService;
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
public class InOutStorageInfoAction extends BaseAction<InOutStorageInfo> {

    @Autowired
    private InOutStorageInfoService inOutStorageInfoService;
    private String transitInfoId;

    public void setTransitInfoId(String transitInfoId) {
        this.transitInfoId = transitInfoId;
    }

    @Action(value = "inOutStorage_add", results = {@Result(name = "success", type = "redirect", location = "pages/transit/transitinfo.html")})
    public String add(){
        inOutStorageInfoService.save(transitInfoId,model);
        return SUCCESS;
    }
}
