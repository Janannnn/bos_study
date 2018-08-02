package cn.itcast.bos.service.impl;

import cn.itcast.bos.dao.WayBillReository;
import cn.itcast.bos.domain.take_delivery.WayBill;
import cn.itcast.bos.index.WayBillIndexRepository;
import cn.itcast.bos.service.WayBillService;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WayBillServiceImpl implements WayBillService {
    @Autowired
    private WayBillIndexRepository wayBillIndexRepository;
    @Autowired
    private WayBillReository wayBillReository;

    @Override
    public Page<WayBill> pageQuery(WayBill wayBill, Pageable pageable) {
        //判断分页查询中条件是否存在
        if (StringUtils.isBlank(wayBill.getWayBillNum())
                && StringUtils.isBlank(wayBill.getSendAddress())
                && StringUtils.isBlank(wayBill.getRecAddress())
                && StringUtils.isBlank(wayBill.getSendProNum())
                && (wayBill.getSignStatus() == null || wayBill.getSignStatus() == 0)) {
            //无条件查询，查询数据库
            return wayBillReository.findAll(pageable);
        } else {
            //条件查询
            //must 条件必须成立 and
            //must not 条件必须不成立 not
            //should 条件可以成立 or

            //布尔查询，多条件组合查询
            BoolQueryBuilder query = new BoolQueryBuilder();
            //向组合查询对象添加条件
            if (StringUtils.isNoneBlank(wayBill.getWayBillNum())) {
                //termQuery等值查询  运单号查询
                QueryBuilder termQuery = new TermQueryBuilder("wayBillNum", wayBill.getWayBillNum());
                query.must(termQuery);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendAddress())) {
                //发货地址 模糊查询
                //第一种情况，条件为词条一部分
                QueryBuilder wildcardQuery = new WildcardQueryBuilder("sendAddress", "*" + wayBill.getSendAddress() + "*");
                //第二种情况，条件为多个词条,进行分词后 每个词条匹配查询
                QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getSendAddress())
                        .field("sendAddress")
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND);
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.should(wildcardQuery);
                boolQueryBuilder.should(queryStringQueryBuilder);
                query.must(boolQueryBuilder);
            }
            if (StringUtils.isNoneBlank(wayBill.getRecAddress())) {
                //收货地址 模糊查询
                QueryBuilder wildcardQuery = new WildcardQueryBuilder("recAddress", "*" + wayBill.getRecAddress() + "*");
                //第二种情况，条件为多个词条,进行分词后 每个词条匹配查询
                QueryStringQueryBuilder queryStringQueryBuilder = new QueryStringQueryBuilder(wayBill.getRecAddress())
                        .field("recAddress")
                        .defaultOperator(QueryStringQueryBuilder.Operator.AND);
                BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
                boolQueryBuilder.should(wildcardQuery);
                boolQueryBuilder.should(queryStringQueryBuilder);
                query.must(boolQueryBuilder);
            }
            if (StringUtils.isNoneBlank(wayBill.getSendProNum())) {
                //速运类型查询  等值查询
                QueryBuilder termQuery = new TermQueryBuilder("sendProNum", wayBill.getSendProNum());
                query.must(termQuery);
            }
            if (wayBill.getSignStatus() != null && wayBill.getSignStatus() != 0) {
                //签收状态查询
                QueryBuilder termQuery = new TermQueryBuilder("signStatus", wayBill.getSignStatus());
                query.must(termQuery);
            }
            SearchQuery searchQuery = new NativeSearchQuery(query);
            searchQuery.setPageable(pageable);//分页效果
            //有条件查询，查询索引库
            return wayBillIndexRepository.search(searchQuery);

        }

    }

    @Override
    public void add(WayBill wayBill) {
        //根据运单Id查出运单，判断是否待发货
        WayBill wayBillOne = wayBillReository.findOne(wayBill.getId());
        if(wayBillOne.getSignStatus()==1){
            //运单号有ID，执行更新操作
            wayBillReository.save(wayBill);
            wayBillIndexRepository.save(wayBill);
        }else {
            throw new RuntimeException("运单已发出，无法修改");
        }

    }

    @Override
    public WayBill findByWayBillNum(String wayBillNum) {

        return wayBillReository.findByWayBillNum(wayBillNum);
    }
}
