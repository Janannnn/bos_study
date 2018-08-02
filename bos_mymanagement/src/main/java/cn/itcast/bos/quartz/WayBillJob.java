package cn.itcast.bos.quartz;

import cn.itcast.bos.dao.WayBillReository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.WayBillService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class WayBillJob implements Job {
    @Autowired
    private WayBillReository wayBillReository;
    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("****************更新索引库**************");
        List<WayBill> wayBills = wayBillReository.findAll();
        wayBillIndexRepository.save(wayBills);
    }
}
