package cn.itcast.action;

import cn.itcast.bos.domain.Constants;
import cn.itcast.bos.utils.BlankISUtils;
import cn.itcast.bos.utils.MailUtils;
import cn.itcast.crm.domain.Customer;
import com.aliyuncs.exceptions.ClientException;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Controller;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@Namespace("/")
@ParentPackage("json-default")
@Scope("prototype")
public class CustomerAction extends BaseAction<Customer> {


    @Autowired
    @Qualifier("jmsQueueTemplate")
    private JmsTemplate jmsTemplate;

    @Autowired
    // 缓存
    private RedisTemplate<String, String> redisTemplate;
    // 接收前台的验证码
    private String code;

    public void setCode(String code) {
        this.code = code;
    }

    // 接收邮件激活的激活码，电话号被模型驱动接收了
    private String activeCode;

    public void setActivecode(String activeCode) {
        this.activeCode = activeCode;
    }

    //用户登录
    @Action(value = "customer_login",results = {
            @Result(name = "login",type = "redirect",location = "./login.html"),
            @Result(name = "success",type = "redirect",location = "index.html#/myhome")
    })
    public String customerLogin(){
        System.out.println(model.getPassword());
        Customer customer = WebClient.create(Constants.CRM_MANAGEMENT_URL +
                "/services/customerService/customerlogin" +
                "?telephone="+model.getTelephone()+"&password="+model.getPassword())
                .accept(MediaType.APPLICATION_JSON).get(Customer.class);
        if(customer==null){
            return LOGIN;
        }else{
            ServletActionContext.getRequest().getSession().setAttribute("customer",customer);
            System.out.println(customer);
            return SUCCESS;
        }

    }

    // 发送短信
    @Action(value = "customer_sendSms", results = {@Result(name = "success", type = "json")})
    public String sendSms() throws ClientException {

        Map<String, Object> tag = new HashMap<>();
        // 进行账号唯一性检验（手机号）
        Customer customer = WebClient.create(
                Constants.CRM_MANAGEMENT_URL+"/services/customerService/findCustomer/" + model.getTelephone())
                .accept(MediaType.APPLICATION_JSON).get(Customer.class);
        if (!BlankISUtils.isBlank(customer)) {
            tag.put("account", false);
        } else {
            tag.put("account", true);
            // 生成随机的验证码
            final String randomCode = RandomStringUtils.randomNumeric(4);
            // 将验证码存入session,验证时要根据电话号进行验证码的校验
            ServletActionContext.getRequest().getSession().setAttribute(model.getTelephone(), randomCode);
           //利用生产者向消息队列消息，让sms系统发送短信
           jmsTemplate.send("bos_sms", new MessageCreator() {
               @Override
               public Message createMessage(Session session) throws JMSException {
                   MapMessage mapMessage = session.createMapMessage();
                   mapMessage.setString("telephone",model.getTelephone());
                   mapMessage.setString("randomCode",randomCode);
                   return mapMessage;
               }
           });
        }
        ActionContext.getContext().getValueStack().push(tag);
        return SUCCESS;
    }

    // 进行注册+发送邮件
    @Action(value = "customer_regist", results = {
            @Result(name = "success", type = "redirect", location = "signup-success.html"),
            @Result(name = "fail", type = "redirect", location = "signup-fail.html")})
    public String regist() {
        try {
            System.out.println("注册手机号：" + model.getTelephone());
            System.out.println("注册邮箱" + model.getEmail());
            // 用户注册
            // 先进行短信验证码的校验
            HttpSession session = ServletActionContext.getRequest().getSession();
            String randomCode = (String) session.getAttribute(model.getTelephone());
            // 获得验证码后进行销毁session
            session.invalidate();
            if (BlankISUtils.isEqual(randomCode, code)) {
                // 用户注册
                WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/addCustomer/")
                        .type(MediaType.APPLICATION_JSON).post(model);
                // 发送邮件
                // 生成激活码
                String activeCode = RandomStringUtils.randomNumeric(32);
                // 将激活码绑定到电话
                redisTemplate.opsForValue().set(model.getTelephone(), activeCode, 24, TimeUnit.HOURS);
                // 激活的地址：需要电话和激活码
                String content = "尊敬的客户您好，请于24小时内，进行邮箱账户的绑定，点击下面地址完成绑定:<br/><a href='" + MailUtils.activeUrl
                        + "?telephone=" + model.getTelephone() + "&activecode=" + activeCode + "'>速运快递邮箱绑定地址</a>";
                // 向邮箱发送短信
                MailUtils.sendMail("Janan集团激活邮件", content, model.getEmail());

                return SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "fail";
    }

    // 邮件激活
    @Action(value = "customer_activeMail")
    public String activeMail() {

        try {
            HttpServletResponse response = ServletActionContext.getResponse();
            response.setContentType("text/html;charset=UTF-8");
            String telephone = model.getTelephone();

            // 获取缓存中的激活码
            String redisActiveCode = redisTemplate.opsForValue().get(telephone);
            if (BlankISUtils.isBlank(redisActiveCode)) {
                response.getWriter().print("<h1>邮箱激活码失效</h1>");
            }
            // 验证通过,根据进行激活
            if (activeCode.equals(redisActiveCode)) {
                WebClient.create(Constants.CRM_MANAGEMENT_URL+"/services/customerService/activeCustomer")
                        .type(MediaType.APPLICATION_JSON).put(model.getTelephone());
                response.getWriter().println("邮箱激活成功");
                redisTemplate.delete(telephone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return NONE;
    }

}
