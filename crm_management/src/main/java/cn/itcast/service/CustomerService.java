package cn.itcast.service;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import cn.itcast.crm.domain.Customer;

public interface CustomerService {

	// 查询所有未关联的客户信息
	@Path("/noassociationcustomers")
	@GET
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findNoAssociationCustomers();

	// 查询所有已关联某定区的客户信息
	@Path("/associationcustomers/{fixedAreaId}")
	@GET
	@Produces({ "application/xml", "application/json" })
	public List<Customer> findAssociationCustomers(@PathParam("fixedAreaId") String fixedAreaId);

	// 将客户关联到定区上,将所有客户id拼成字符串
	@Path("/associationCustomersToFixedArea")
	@PUT
	public void associationCustomersToFixedArea(
			@QueryParam("customerIdStr")String customerIdStr,
			@QueryParam("fixedAreaId")String fixedAreaId);
	
	//账号查询
	@Path("/findCustomer/{telephone}")
	@GET
	@Produces({ "application/xml", "application/json" })
	@Consumes({ "application/xml", "application/json" })
	public Customer findCustomer(@PathParam("telephone")String telephone);
	
	//添加用户
	@Path("/addCustomer")
	@POST
	@Consumes({ "application/xml", "application/json" })
	public void addCustomer(Customer customer);
	
	//激活账号
	@Path("/activeCustomer")
	@PUT
	@Consumes({ "application/xml", "application/json" })
	public void activeCustomer(String telephone);
}
