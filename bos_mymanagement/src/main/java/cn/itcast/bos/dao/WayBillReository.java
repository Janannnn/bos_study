package cn.itcast.bos.dao;

import cn.itcast.bos.domain.take_delivery.WayBill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WayBillReository extends JpaRepository<WayBill,Integer> {
    WayBill findByWayBillNum(String wayBillNum);
}
