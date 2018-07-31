package cn.itcast.bos.action.take_delivery;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.service.OrderService;
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
@ParentPackage("json-default")
@Namespace("/")
@Scope("prototype")
public class OrderAction extends BaseAction<Order> {

    @Autowired
    private OrderService orderService;

    //根据订单号查询订单
    @Action(value = "order_query",results = {@Result(name = "success",type = "json")})
    public String orderQuery(){

        Order orderData = orderService.findByOrderNum(model.getOrderNum());
        if(orderData==null){
            String msg ="订单号不存在" ;
            setMsg(false,msg,null);
            return SUCCESS;
        }
        if(orderData.getWayBill()!=null){
           String msg = "订单已关联运单";
            setMsg(false,msg,null);
            return SUCCESS;
        }
        //订单存在，并且未关联运单
         setMsg(true,null,orderData);
        return SUCCESS;
    }

    private void setMsg(Boolean success,String msg,Order orderData) {
        Map<String,Object> map = new HashMap<>();
        map.put("success",success);
        map.put("msg",msg);
        map.put("orderData",orderData);
        ActionContext.getContext().getValueStack().push(map);
    }
}
