package cn.itcast.bos.service;

import cn.itcast.bos.domain.take_delivery.Order;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface OrderService {
    @Path("/orderAdd")
    @POST
    public void orderAdd(Order order);

    Order  findByOrderNum(String orderNum);
}
