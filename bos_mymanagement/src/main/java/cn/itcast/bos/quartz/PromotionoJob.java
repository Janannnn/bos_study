package cn.itcast.bos.quartz;

import cn.itcast.bos.service.PromotionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class PromotionoJob implements Job {
    @Autowired
    private PromotionService promotionService;
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("活动过期处理程序执行......");
        promotionService.updateStatus(new Date());
    }
}
