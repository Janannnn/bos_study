package cn.itcast.bos.service;

import cn.itcast.bos.domain.pagebean.PageBean;
import cn.itcast.bos.domain.take_delivery.Promotion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.ws.rs.*;
import java.util.Date;

public interface PromotionService {
    void promotionSave(Promotion promotion);
    Page<Promotion> findAll(Pageable pageable);

    /*
        由于JpaRepository自带的Page我们无法在其上添加@XmlRootElement注解
        所以服务无法返回Page，只能自定义对象
    */
   @Path("/pageQuery")
   @GET
   @Produces({ "application/xml", "application/json" })
   public PageBean<Promotion> pageQuery(
           @QueryParam("page")int page,
           @QueryParam("rows")int rows);

    @Path("/promotion/{id}")
    @GET
    @Produces({ "application/xml", "application/json" })
    public Promotion findOne(@PathParam("id")int id);

    void updateStatus(Date date);
}
