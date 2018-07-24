package cn.itcast.bos.domain.pagebean;

import cn.itcast.bos.domain.take_delivery.Promotion;
import org.apache.poi.ss.formula.functions.T;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.List;

@XmlRootElement(name = "pagebean")
@XmlSeeAlso({Promotion.class})
public class PageBean<T> {
    private int totalCount;
    private List<T> dataPage;

    public List<T> getDataPage() {
        return dataPage;
    }

    public void setDataPage(List<T> dataPage) {
        this.dataPage = dataPage;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}
