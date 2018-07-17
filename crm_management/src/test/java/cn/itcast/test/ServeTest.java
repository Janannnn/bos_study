package cn.itcast.test;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cn.itcast.crm.domain.Customer;
import cn.itcast.service.CustomerService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="classpath:applicationContext.xml")
public class ServeTest {

	@Autowired
	private CustomerService customerService;
	
	@Test
	public void test01(){
		List<Customer> customers = customerService.findNoAssociationCustomers();
		for (Customer customer : customers) {
			System.out.println(customer);
		}
	}
	@Test
	public void test02(){
		List<Customer> customers = customerService.findAssociationCustomers("qb01");
		for (Customer customer : customers) {
			System.out.println(customer);
		}
	}
}
