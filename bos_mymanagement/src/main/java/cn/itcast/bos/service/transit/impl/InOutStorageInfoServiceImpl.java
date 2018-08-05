package cn.itcast.bos.service.transit.impl;

import cn.itcast.bos.dao.transit.InOutStorageInfoRepository;
import cn.itcast.bos.dao.transit.TransitRepository;
import cn.itcast.bos.domain.transit.InOutStorageInfo;
import cn.itcast.bos.domain.transit.TransitInfo;
import cn.itcast.bos.service.transit.InOutStorageInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InOutStorageInfoServiceImpl implements InOutStorageInfoService {
    @Autowired
    private InOutStorageInfoRepository inOutStorageInfoRepository;
    @Autowired
    private TransitRepository transitRepository;
    @Override
    public void save(String transitInfoId, InOutStorageInfo model) {

        //保存出入库信息
        inOutStorageInfoRepository.save(model);
        //查询出配送信息，进行外键维护
        int transitId = Integer.parseInt(transitInfoId);
        TransitInfo transitInfo = transitRepository.findOne(transitId);
        transitInfo.getInOutStorageInfos().add(model);

        //修改状态（一级缓存特点）
        if(model.getOperation().equals("到达网点")){
            transitInfo.setStatus("到达网点");
            //更新网点地址，显示配送路径
            transitInfo.setOutletAddress(model.getAddress());
        }

    }
}
