package cn.itcast.action;

import cn.itcast.bos.domain.Constants;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.crm.domain.Customer;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.ws.rs.core.MediaType;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class OrderAction extends BaseAction<Order>{

    //属性驱动得到寄件人和收件人的省市区
    private String sendAreaInfo;
    private String recAreaInfo;

    public void setSendAreaInfo(String sendAreaInfo) {
        this.sendAreaInfo = sendAreaInfo;
    }

    public void setRecAreaInfo(String recAreaInfo) {
        this.recAreaInfo = recAreaInfo;
    }
    @Action(value = "order_add",results = {@Result(name = "success",type = "json")})
    public String orderAdd(){
        //封装寄件人区域
        Area sendArea = new Area();
        String[] strings = sendAreaInfo.split("/");
        sendArea.setProvince(strings[0]);
        sendArea.setCity(strings[1]);
        sendArea.setDistrict(strings[2]);
        //封装收件人区域
        Area recArea = new Area();
        String[] ss = sendAreaInfo.split("/");
        recArea.setProvince(ss[0]);
        recArea.setCity(ss[1]);
        recArea.setDistrict(ss[2]);

        model.setSendArea(sendArea);
        model.setRecArea(recArea);

        //需要登录才能提交订单
        Customer customer = (Customer) ServletActionContext.getRequest().getSession().getAttribute("customer");
        if(customer==null){
            System.out.println("请先登录");
            return NONE;
        }
        model.setCustomer_id(customer.getId());

        //将order对象交给bos_management进行自动分单
        WebClient.create(Constants.BOS_MANAGEMENT_URL+"/services/orderService/orderAdd")
                .type(MediaType.APPLICATION_JSON).post(model);


        return SUCCESS;
    }
}
