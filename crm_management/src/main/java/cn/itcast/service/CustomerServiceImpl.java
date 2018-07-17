package cn.itcast.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.itcast.crm.domain.Customer;
import cn.itcast.dao.CustomerRepository;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {
	
	@Autowired
	private CustomerRepository customerRepository;

	@Override
	public List<Customer> findNoAssociationCustomers() {
		return customerRepository.findByFixedAreaIdIsNull();
	}

	@Override
	public List<Customer> findAssociationCustomers(String fixedAreaId) {
		return customerRepository.findByFixedAreaId(fixedAreaId);
	}

	@Override
	public void associationCustomersToFixedArea(String customerIdStr, String fixedAreaId) {
		customerRepository.clearFixedAreaId(fixedAreaId);
		String[] customerIdss = customerIdStr.split(",");
		for (String customerIds : customerIdss) {
			Integer customerId = Integer.parseInt(customerIds);
			customerRepository.associationCustomersToFixedArea(fixedAreaId,customerId);
		}
		
	}

}
