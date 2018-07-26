package cn.itcast.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import cn.itcast.crm.domain.Customer;


public interface CustomerRepository extends JpaRepository<Customer, Integer>, JpaSpecificationExecutor<Customer> {

	List<Customer> findByFixedAreaIdIsNull();

	List<Customer> findByFixedAreaId(String fixedAreaId);

	@Query("update Customer set fixedAreaId=? where id=?")
	@Modifying
	void associationCustomersToFixedArea(String fixedAreaId, Integer customerId);

	@Query("update Customer set fixedAreaId = null where fixedAreaId = ?")
	@Modifying
	void clearFixedAreaId(String fixedAreaId);

	@Query("update Customer set type= 1 where telephone = ?")
	@Modifying
	void updateTypeByTelephone(String telephone);

	@Query("from Customer where telephone = ?")
	Customer findByTelephone(String telephone);

	Customer findByTelephoneAndPassword(String telephone,String password);

	@Query("select fixedAreaId from Customer where address = ?")
    String findFixedAreaIdByAddress(String address);
}
