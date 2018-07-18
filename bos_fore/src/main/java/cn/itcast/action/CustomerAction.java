package cn.itcast.action;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;

import cn.itcast.crm.domain.Customer;
import cn.itcast.sendSms.SmsDemo;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer>{

	private String code;
	
	public void setCode(String code) {
		this.code = code;
	}
	//发送短信
	@Action(value="courier_sendSms",results={@Result(name="success",type="json")})
	public String sendSms(){
		//生成随机的验证码
		String randomCode = RandomStringUtils.randomNumeric(4);
		//将验证码存入session,验证时要根据电话号进行验证码的校验
		ServletActionContext.getRequest().getSession().setAttribute(model.getTelephone(), randomCode);
		
		try {
			SendSmsResponse sendSms = SmsDemo.sendSms(randomCode,model.getTelephone());
			String staCode = sendSms.getCode();
			if("OK".equalsIgnoreCase(staCode)){
				//发送成功
				Map<String,Boolen> tag = new HashMap<>()
			}
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return NONE;
	}
	//进行注册
	@Action(value="customer_regist",results={@Result(name="success",type="redirect",location="signup-success.html")})
	public String regist(){
		try {
			System.out.println(model.getTelephone());
			
		String randomCode = (String) ServletActionContext.getRequest().getSession().getAttribute(model.getTelephone());
		if(code ==null ||!code.equals(randomCode)){
			System.out.println("注册失败");
			return NONE;
		}else{
			WebClient.create("http://localhost:9002/crm_management/services/customerService/addCustomer/")
						.type(MediaType.APPLICATION_JSON).post(model);
			System.out.println("注册成功");
			return SUCCESS;
		}
		} catch (Exception e) {
			e.printStackTrace();
			return NONE;
		}
		
		
	}
}
