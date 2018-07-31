package cn.itcast.bos.service.impl;

import cn.itcast.bos.dao.AreaRepository;
import cn.itcast.bos.dao.FixedAreaRepository;
import cn.itcast.bos.dao.OrderRepository;
import cn.itcast.bos.dao.WorkBillRepository;
import cn.itcast.bos.domain.Constants;
import cn.itcast.bos.domain.base.Area;
import cn.itcast.bos.domain.base.Courier;
import cn.itcast.bos.domain.base.FixedArea;
import cn.itcast.bos.domain.base.SubArea;
import cn.itcast.bos.domain.take_delivery.Order;
import cn.itcast.bos.domain.take_delivery.WorkBill;
import cn.itcast.bos.service.OrderService;
import cn.itcast.bos.utils.BlankISUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;
    @Autowired
    private WorkBillRepository workBillRepository;
    @Autowired
    private AreaRepository areaRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private FixedAreaRepository fixedAreaRepository;
    @Override
    public void orderAdd(final Order order) {
        try {
            //进行自动分单功能
            System.out.println("实现自动分单");
            //先查询寄件人区域和收件人区域
            final Area sendArea = order.getSendArea();
            Area persistSendArea = areaRepository.findByProvinceAndCityAndDistrict(sendArea.getProvince(),sendArea.getCity(),sendArea.getDistrict());
            Area recArea = order.getRecArea();
            Area persistRecArea = areaRepository.findByProvinceAndCityAndDistrict(recArea.getProvince(),recArea.getCity(),recArea.getDistrict());

            order.setRecArea(persistRecArea);
            order.setSendArea(persistSendArea);
            //先根据寄件人地址去crm系统查找是否有定区id
            String fixedAreaId = WebClient.create(Constants.CRM_MANAGEMENT_URL + "/services/customerService/fixedAreaIdByAddress?address=" + order.getSendAddress())
                    .accept(MediaType.APPLICATION_JSON).get(String.class);
            if(!BlankISUtils.isBlank(fixedAreaId)){
                System.out.println("定区ID："+fixedAreaId);
                //完全匹配后查询此定区
                FixedArea fixedArea = fixedAreaRepository.findOne(fixedAreaId);
                //自动分单完成
                autoAddOrder(order, fixedArea);
                //生成工单，发送短信给快递员
                createWorkBill(order);
                return;
            }else {
                System.out.println("不能完全匹配");
                //先去查找寄件人区域

                //根据寄件人区域找到所有的分区
                Set<SubArea> subareas = persistSendArea.getSubareas();
                //遍历分区，让寄件人详细地址去匹配分区的关键字
                System.out.println("寄件人的详细地址：" + order.getSendAddress());
                System.out.println("寄件人的所在区域：" + order.getSendArea().getProvince() + order.getSendArea().getCity() + order.getSendArea().getDistrict());
                for (SubArea subarea : subareas) {
                    //如果可以找到匹配的分区
                    System.out.println("分区的关键字：" + subarea.getAssistKeyWords());

                    if (order.getSendAddress().contains(subarea.getAssistKeyWords())
                            || order.getSendAddress().contains(subarea.getKeyWords())) {
                        //获得此分区对应的定区的id
                        String fixedId = subarea.getFixedArea().getId();
                        System.out.println("没有完全匹配得到的定区ID：" + fixedId);
                        //获得定区对象
                        FixedArea fixedArea = fixedAreaRepository.findOne(fixedId);
                        autoAddOrder(order, fixedArea);
                        //生成工单，发送短信给快递员
                        createWorkBill(order);
                        return;
                    }

                }
            }
            //自动分单失败，进入人工调度
            order.setStatus("2");
            orderRepository.save(order);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



    //生成工单，发送短信给快递员
    private void createWorkBill(final Order order) throws JMSException {

        WorkBill workBill = new WorkBill();
        workBill.setType("新");
        workBill.setPickstate("新单");
        workBill.setBuildtime(new Date());
        workBill.setRemark(order.getRemark());
        final String smsNumber = RandomStringUtils.randomNumeric(4);
        workBill.setSmsNumber(smsNumber); // 短信序号
        workBill.setOrder(order);
        workBill.setCourier(order.getCourier());
        workBillRepository.save(workBill);

        jmsTemplate.send("bos_sms_order", new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                //快递员手机号
                mapMessage.setString("telephone",order.getCourier().getTelephone());
                //信息
                mapMessage.setString("msg",
                        "短信序号：" + smsNumber + ",取件地址：" + order.getSendAddress()
                        + ",联系人:" + order.getSendName() + ",手机:"
                        + order.getSendMobile() + "，快递员捎话："
                        + order.getSendMobileMsg());
                return mapMessage;
            }
        });
    }

    //生成工单，发送短信

    //自动分单
    private void autoAddOrder(Order order, FixedArea fixedArea) {
        //遍历此定区所有快递员，找到时间符合的一个,然后进行自动分单
        Iterator<Courier> iterator = fixedArea.getCouriers().iterator();
        if(iterator.hasNext()){
            System.out.println("自动分单成功");
            //保存快递员到订单
            order.setCourier(iterator.next());
            //设置状态为1表示为自动分单
            order.setStatus("1");
            //保存订单..需要订单号
            order.setOrderNum(UUID.randomUUID().toString());
            orderRepository.save(order);
        }
    }

    @Override
    public Order findByOrderNum(String orderNum) {
        return orderRepository.findByOrderNum(orderNum);
    }
}
