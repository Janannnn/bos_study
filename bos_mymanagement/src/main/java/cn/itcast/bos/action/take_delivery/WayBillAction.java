package cn.itcast.bos.action.take_delivery;

import cn.itcast.bos.action.common.BaseAction;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.service.WayBillService;
import com.opensymphony.xwork2.ActionContext;
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

import java.util.HashMap;
import java.util.Map;

@Controller
@ParentPackage("json-default")
@Namespace("/")
@Scope("prototype")
public class WayBillAction extends BaseAction<WayBill> {
    @Autowired
    private WayBillService wayBillService;


    private int page;
    private int rows;

    public void setPage(int page) {
        this.page = page;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    //运单的分页查询
    @Action(value = "wayBill_pageQuery",results = {@Result(name = "success",type = "json")})
    public String pageQuery(){

        Pageable pageable = new PageRequest(page-1,rows);
        Page<WayBill> page = wayBillService.pageQuery(model,pageable);
        pushPageToValueStack(page);
        return SUCCESS;
    }

    //添加运单
    @Action(value = "wayBill_add",results = {@Result(name = "success",type = "json")})
    public String add(){
        //运单的订单信息：
        Order order = model.getOrder();
        System.out.println("运单的ID："+model.getId());
        System.out.println("运单的订单数据："+order);
        System.out.println(order.getCourier().getName());
        if(order==null||order.getId()==null||order.getId()==0){
            model.setOrder(null);
        }
        wayBillService.add(model);
        Map<String,Object> map = new HashMap<>();
        map.put("success",true);
        map.put("msg", "保存成功");
        ActionContext.getContext().getValueStack().push(map);
        return SUCCESS;
    }
    //根据运单号查询运单并回显
    @Action(value = "wayBill_query",results = {@Result(name = "success",type = "json")})
    public String orderQuery(){
        WayBill wayBillData = wayBillService.findByWayBillNum(model.getWayBillNum());
        if(wayBillData==null){
            String msg ="运单号不存在" ;
            setMsg(false,msg,null);
            return SUCCESS;
        }
        if(wayBillData.getOrder()!=null){
            String msg = "订单已关联运单";
            setMsg(false,msg,null);
            return SUCCESS;
        }
        //运单存在，且未关联订单
        setMsg(true,null,wayBillData);
        return SUCCESS;
    }

    /**
     * 向前台传递的信息
     * @param success
     * @param msg
     * @param wayBillData
     */
    private void setMsg(Boolean success,String msg,WayBill wayBillData) {
        Map<String,Object> map = new HashMap<>();
        map.put("success",success);
        map.put("msg",msg);
        map.put("wayBillData",wayBillData);
        ActionContext.getContext().getValueStack().push(map);
    }


}
