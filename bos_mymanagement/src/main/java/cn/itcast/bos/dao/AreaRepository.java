package cn.itcast.bos.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import cn.itcast.bos.domain.base.Area;
import org.springframework.data.jpa.repository.Query;

public interface AreaRepository extends JpaRepository<Area, Integer>,JpaSpecificationExecutor<Area> {

   @Query("from Area where province=? and city = ? and district = ?")
   public Area findByProvinceAndCityAndDistrict(String province, String city, String district);
}
