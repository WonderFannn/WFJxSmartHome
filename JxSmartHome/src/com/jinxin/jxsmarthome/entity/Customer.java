package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;


/**
 * 客户详情
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "customer")
public class Customer extends Base{
	@Id
	@Column(name = "id")
	private int id;
	/**客户ID**/
	@Column(name = "customerId")
	private String customerId ;

	/**客户姓名**/
	@Column(name = "customerName")
	private String customerName ;

	/**性别，1=男，2=女**/
	@Column(name = "sex")
	private Integer sex ;

	/**年龄**/
	@Column(name = "age")
	private Integer age ;

	/**通讯地址**/
	@Column(name = "address")
	private String address ;

	/**联系电话（座机）**/
	@Column(name = "tel")
	private String tel ;

	/**联系电话（手机）**/
	@Column(name = "mobile")
	private String mobile ;

	/**所属省**/
	@Column(name = "provence")
	private String provence ;

	/**市**/
	@Column(name = "city")
	private String city ;

	/**县**/
	@Column(name = "contry")
	private String contry ;

	/**所属代理商ID**/
	@Column(name = "agentId")
	private String agentId ;

	/**客户信息创建人**/
	@Column(name = "createUser")
	private String createUser ;

	/**创建时间**/
	@Column(name = "createTime")
	private String createTime ;

	/**客户信息修改人**/
	@Column(name = "updateUser")
	private String updateUser ;

	/**修改时间**/
	@Column(name = "updateTime")
	private String updateTime ;

	/**客户状态，1=启用，0=禁用**/
	@Column(name = "status")
	private Integer status ;

	/**账号类别 1=主账户，0=子账户**/
	@Column(name = "accountType")
	private Integer accountType ;

	/**密保问题1**/
	@Column(name = "question1")
	private String question1 ;

	/**密保问题2**/
	@Column(name = "question2")
	private String question2 ;

	/**备注**/
	@Column(name = "comments")
	private String comments ;

	/**身份证ID**/
	@Column(name = "idCard")
	private String idCard ;

	public String getCustomerId( ) {
		return customerId;
	}

	public void setCustomerId( String customerId ) {
		this.customerId = customerId;
	}

	public String getCustomerName( ) {
		return customerName;
	}

	public void setCustomerName( String customerName ) {
		this.customerName = customerName;
	}

	public Integer getSex( ) {
		return sex;
	}

	public void setSex( Integer sex ) {
		this.sex = sex;
	}

	public Integer getAge( ) {
		return age;
	}

	public void setAge( Integer age ) {
		this.age = age;
	}

	public String getAddress( ) {
		return address;
	}

	public void setAddress( String address ) {
		this.address = address;
	}

	public String getTel( ) {
		return tel;
	}

	public void setTel( String tel ) {
		this.tel = tel;
	}

	public String getMobile( ) {
		return mobile;
	}

	public void setMobile( String mobile ) {
		this.mobile = mobile;
	}

	public String getProvence( ) {
		return provence;
	}

	public void setProvence( String provence ) {
		this.provence = provence;
	}

	public String getCity( ) {
		return city;
	}

	public void setCity( String city ) {
		this.city = city;
	}

	public String getContry( ) {
		return contry;
	}

	public void setContry( String contry ) {
		this.contry = contry;
	}

	public String getAgentId( ) {
		return agentId;
	}

	public void setAgentId( String agentId ) {
		this.agentId = agentId;
	}

	public String getCreateUser( ) {
		return createUser;
	}

	public void setCreateUser( String createUser ) {
		this.createUser = createUser;
	}

	public String getCreateTime( ) {
		return createTime;
	}

	public void setCreateTime( String createTime ) {
		this.createTime = createTime;
	}

	public String getUpdateUser( ) {
		return updateUser;
	}

	public void setUpdateUser( String updateUser ) {
		this.updateUser = updateUser;
	}

	public String getUpdateTime( ) {
		return updateTime;
	}

	public void setUpdateTime( String updateTime ) {
		this.updateTime = updateTime;
	}

	public Integer getStatus( ) {
		return status;
	}

	public void setStatus( Integer status ) {
		this.status = status;
	}

	public Integer getAccountType( ) {
		return accountType;
	}

	public void setAccountType( Integer accountType ) {
		this.accountType = accountType;
	}

	public String getQuestion1( ) {
		return question1;
	}

	public void setQuestion1( String question1 ) {
		this.question1 = question1;
	}

	public String getQuestion2( ) {
		return question2;
	}

	public void setQuestion2( String question2 ) {
		this.question2 = question2;
	}

	public String getComments( ) {
		return comments;
	}

	public void setComments( String comments ) {
		this.comments = comments;
	}

	public String getIdCard( ) {
		return idCard;
	}

	public void setIdCard( String idCard ) {
		this.idCard = idCard;
	}

	public Customer(){
		
	}
	public Customer(String customerId, String customerName, Integer sex,
			Integer age, String address, String tel, String mobile,
			String provence, String city, String contry, String agentId,
			String createUser, String createTime, String updateUser,
			String updateTime, Integer status, Integer accountType,
			String question1, String question2, String comments, String idCard) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.sex = sex;
		this.age = age;
		this.address = address;
		this.tel = tel;
		this.mobile = mobile;
		this.provence = provence;
		this.city = city;
		this.contry = contry;
		this.agentId = agentId;
		this.createUser = createUser;
		this.createTime = createTime;
		this.updateUser = updateUser;
		this.updateTime = updateTime;
		this.status = status;
		this.accountType = accountType;
		this.question1 = question1;
		this.question2 = question2;
		this.comments = comments;
		this.idCard = idCard;
	}
	
	public Customer(String customerId, String customerName, Integer status) {
		super();
		this.customerId = customerId;
		this.customerName = customerName;
		this.status = status;
	}

	
}
