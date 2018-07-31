package cn.itcast.bos.service;

import cn.itcast.bos.domain.take_delivery.WayBill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WayBillService {

    Page<WayBill> pageQuery(WayBill wayBill, Pageable pageable);

    void add(WayBill wayBill);

    WayBill findByWayBillNum(String wayBillNum);
}
