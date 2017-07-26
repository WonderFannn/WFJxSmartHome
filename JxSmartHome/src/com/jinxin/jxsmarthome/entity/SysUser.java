package com.jinxin.jxsmarthome.entity;

import com.tgb.lk.ahibernate.annotation.Column;
import com.tgb.lk.ahibernate.annotation.Id;
import com.tgb.lk.ahibernate.annotation.Table;


/**
 * 系统用户对象
 * @author JackeyZhang
 * @company 金鑫智慧
 */
@Table(name = "sys_user")
public class SysUser extends Base {
	
	private static final long serialVersionUID = 1L;
	
	/**用户ID，主键**/
	@Id
	@Column(name = "userId")
	private Integer userId;

	/**用户登录系统账号**/
	@Column(name = "account")
	private String account ;
	/**用户登录系统子账号**/
	@Column(name = "subAccunt")
	private String subAccunt ;

	/**登录密码**/
	@Column(name = "password")
	private String password ;

	/**用户姓名**/
	@Column(name = "userName")
	private String userName ;

	/**身份证ID**/
	@Column(name = "idNo")
	private String idNo ;

	/**用户昵称**/
	@Column(name = "nickyName")
	private String nickyName ;

	/**用户类型 1=系统用户，2=代理商账号，3=客户账号**/
	@Column(name = "userType")
	private Integer userType ;

	/**所属区域ID**/
	@Column(name = "areaId")
	private Integer areaId ;

	/**所属组织结构ID**/
	@Column(name = "orgId")
	private Integer orgId ;

	/**是否可用 0=禁用；1=启用**/
	@Column(name = "enable")
	private Integer enable ;

	/**性别，1=男性，2=女性**/
	@Column(name = "sex")
	private Integer sex ;

	/**年龄**/
	@Column(name = "age")
	private Integer age ;

	/**头像**/
	@Column(name = "avatar")
	private String avatar ;

	/**快速检索代码，用户姓名拼音首字母**/
	@Column(name = "inxCode")
	private String inxCode ;

	/**常用联系电话**/
	@Column(name = "mobile")
	private String mobile ;

	/**紧急联系人电话**/
	@Column(name = "urgContacts")
	private String urgContacts ;

	/**固定电话**/
	@Column(name = "tel")
	private String tel ;

	/**电子邮箱**/
	@Column(name = "email")
	private String email ;

	/**联系地址**/
	@Column(name = "address")
	private String address ;

	/**创建人ID**/
	@Column(name = "creatorId")
	private Integer creatorId ;

	/**创建时间**/
	@Column(name = "createdTime")
	private String createdTime ;

	/**修改人ID**/
	@Column(name = "editorId")
	private Integer editorId ;

	/**修改时间**/
	@Column(name = "editedTime")
	private String editedTime ;

	/**备注**/
	@Column(name = "comments")
	private String comments ;

	/**密保问题1**/
	@Column(name = "question1")
	private String question1 ;

	/**密保问题2**/
	@Column(name = "question2")
	private String question2 ;

	/**密保答案2**/
	@Column(name = "answer1")
	private String answer1 ;

	/**密保答案1**/
	@Column(name = "answer2")
	private String answer2 ;
	
	private String orgPassword ;
	private String newPassword;
	
	public SysUser() {
		
	}

	public SysUser(Integer userId, String account, String password,
			String userName, String idNo, String nickyName, Integer userType,
			Integer areaId, Integer orgId, Integer enable, Integer sex,
			Integer age, String avatar, String inxCode, String mobile,
			String urgContacts, String tel, String email, String address,
			Integer creatorId, String createdTime, Integer editorId,
			String editedTime, String comments, String question1,
			String question2, String answer1, String answer2) {
		super();
		this.userId = userId;
		this.account = account;
		this.password = password;
		this.userName = userName;
		this.idNo = idNo;
		this.nickyName = nickyName;
		this.userType = userType;
		this.areaId = areaId;
		this.orgId = orgId;
		this.enable = enable;
		this.sex = sex;
		this.age = age;
		this.avatar = avatar;
		this.inxCode = inxCode;
		this.mobile = mobile;
		this.urgContacts = urgContacts;
		this.tel = tel;
		this.email = email;
		this.address = address;
		this.creatorId = creatorId;
		this.createdTime = createdTime;
		this.editorId = editorId;
		this.editedTime = editedTime;
		this.comments = comments;
		this.question1 = question1;
		this.question2 = question2;
		this.answer1 = answer1;
		this.answer2 = answer2;
	}

	public Integer getUserId( ) {
		return userId;
	}

	public void setUserId( Integer userId ) {
		this.userId = userId;
	}

	public String getAccount( ) {
		return account;
	}

	public void setAccount( String account ) {
		this.account = account;
	}

	public String getPassword( ) {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public String getUserName( ) {
		return userName;
	}

	public void setUserName( String userName ) {
		this.userName = userName;
	}

	public String getIdNo( ) {
		return idNo;
	}

	public void setIdNo( String idNo ) {
		this.idNo = idNo;
	}

	public String getNickyName( ) {
		return nickyName;
	}

	public void setNickyName( String nickyName ) {
		this.nickyName = nickyName;
	}

	public Integer getUserType( ) {
		return userType;
	}

	public void setUserType( Integer userType ) {
		this.userType = userType;
	}

	public Integer getAreaId( ) {
		return areaId;
	}

	public void setAreaId( Integer areaId ) {
		this.areaId = areaId;
	}

	public Integer getOrgId( ) {
		return orgId;
	}

	public void setOrgId( Integer orgId ) {
		this.orgId = orgId;
	}

	public Integer getEnable( ) {
		return enable;
	}

	public void setEnable( Integer enable ) {
		this.enable = enable;
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

	public String getAvatar( ) {
		return avatar;
	}

	public void setAvatar( String avatar ) {
		this.avatar = avatar;
	}

	public String getInxCode( ) {
		return inxCode;
	}

	public void setInxCode( String inxCode ) {
		this.inxCode = inxCode;
	}

	public String getMobile( ) {
		return mobile;
	}

	public void setMobile( String mobile ) {
		this.mobile = mobile;
	}

	public String getUrgContacts( ) {
		return urgContacts;
	}

	public void setUrgContacts( String urgContacts ) {
		this.urgContacts = urgContacts;
	}

	public String getTel( ) {
		return tel;
	}

	public void setTel( String tel ) {
		this.tel = tel;
	}

	public String getEmail( ) {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	public String getAddress( ) {
		return address;
	}

	public void setAddress( String address ) {
		this.address = address;
	}

	public Integer getCreatorId( ) {
		return creatorId;
	}

	public void setCreatorId( Integer creatorId ) {
		this.creatorId = creatorId;
	}

	public String getCreatedTime( ) {
		return createdTime;
	}

	public void setCreatedTime( String createdTime ) {
		this.createdTime = createdTime;
	}

	public Integer getEditorId( ) {
		return editorId;
	}

	public void setEditorId( Integer editorId ) {
		this.editorId = editorId;
	}

	public String getEditedTime( ) {
		return editedTime;
	}

	public void setEditedTime( String editedTime ) {
		this.editedTime = editedTime;
	}

	public String getComments( ) {
		return comments;
	}

	public void setComments( String comments ) {
		this.comments = comments;
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

	public String getAnswer1( ) {
		return answer1;
	}

	public void setAnswer1( String answer1 ) {
		this.answer1 = answer1;
	}

	public String getAnswer2( ) {
		return answer2;
	}

	public void setAnswer2( String answer2 ) {
		this.answer2 = answer2;
	}

	
	public String getOrgPassword ( ) {
		return orgPassword;
	}

	
	public void setOrgPassword ( String orgPassword ) {
		this.orgPassword = orgPassword;
	}

	
	public String getNewPassword ( ) {
		return newPassword;
	}

	
	public void setNewPassword ( String newPassword ) {
		this.newPassword = newPassword;
	}

	public String getSubAccunt() {
		return subAccunt;
	}

	public void setSubAccunt(String subAccunt) {
		this.subAccunt = subAccunt;
	}
    
}
