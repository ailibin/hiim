package com.aiitec.openapi.constant;

/**
 * 协议返回状态吗常量类
 * @author Anthony
 *
 */
public class AIIStatus {

	/**返回成功 值为0*/
	public static final int SUCCESS = 0x00;
	
	/**未知错误，值为1000*/
	public static final int UNKNOWN = 1000;
	
	/**协议版本过低，服务器不支持，值为*/
	public static final int VERSION_LOW = 1001;
	
	/**session为空或不存在，值为1002*/
	public static final int NO_SESSION = 1002;
	
	/**验证码错误，值为1003*/
	public static final int CODE_ERROR = 1003;
	
	/**请求参数不完整，值为1004*/
	public static final int INCOMPLETE_PARAMETERS = 1004;
	
	/**请求超时，值为1010*/
	public static final int TIME_OUT = 1010;
	
	/**数据已删除或不存在，值为1011*/
	public static final int NO_DATA = 1011;
	
	/**会话已过期， 长时间未登录后要求登录，值为1012*/
	public static final int SESSION_EXPIRATION = 1012;
	
	/**未更新任何数据，值为1013， 如新密码与旧密码相同*/
	public static final int NO_UPDATE = 1013;
	/**已经提交，无需重复提交， 如已收藏的再次收藏, 值为1014*/
	public static final int REPEAT_SUBMIT = 1014;
	
	/**短信发送失败, 值为1015*/
	public static final int SMS_SEND_FAILS = 1015;
	
	/**数据包含敏感词汇, 值为1016*/
	public static final int SENSITIVE_VOCABULARY = 1016;
	
	/**安全验证不通过, 值为1017*/
	public static final int VERIFICATION_NO_PASS = 1017;
	
	/**缓存可用, 值为1020*/
	public static final int CACHE_AVAILABLE = 1020;
	/**操作太快*/
	public static final int OPTION_FAST = 1021;
	
	/**(用户)未登录, 值为1100*/
	public static final int UNLOGIN = 1100;
	
	/**(用户)用户名或密码错误, 值为1101*/
	public static final int ERROR_NAME_OR_PASSWORD = 1101;
	
	/**(用户)标识非法, 值为1102*/
	public static final int IDENTIFICATION_ILLEGAL = 1102;
	
	/**(用户)不存在， 值为1103*/
	public static final int USER_INEXISTENCE = 1103;

	/**(用户)已存在, 值为1104*/
	public static final int USER_EXIST = 1104;
	
	/**(用户)已注销, 值为1105*/
	public static final int USER_CANCELLED = 1105;
	
	/**(用户)被锁定, 值为1106*/
	public static final int USER_BELOCKED = 1106;
	
	/**(用户)在别处登录, 值为1107*/
	public static final int LOGIN_AT_OTHER = 1107;
	
	/**(用户)个人资料未填写, 值为1108*/
	public static final int DATA_NOT_FILLED = 1108;
	
	/**(用户)财务认证未通过, 值为1109*/
	public static final int Financial_NO_PASS = 1109;
	
	/**(用户)身份证已经注册, 值为1110*/
	public static final int ID_EXIST = 1110;
	
	/**(用户)身份证验证错误, 值为1111*/
	public static final int ID_ERROR = 1111;
	
	/**(用户)昵称已存在, 值为1112*/
	public static final int NICKNAME_EXIST = 1112;
	
	/**文件大小超限制, 值为1200*/
	public static final int FILE_SIZE_OVER_RESTRICTIONS = 1200;
	/**文件类型非法, 值为1201*/
	public static final int FILE_TYPE_ILLEGAL = 1201;
	/**库存不足 1301*/
	public static final int INVENTORY_SHORTAGE = 1301;
	
//	/**积分不足, 值为1300*/
//	INTEGRAL_SHORTAGE,
//	/**金钱（应币）不足, 值为1301*/
//	MONEY_SHORTAGE,
//	
//	/**活动已过期, 值为1400*/
//	ACTIVITY_EXPIRED,
//	/**参加活动的人数已达上限, 值为1401*/
//	PEOPLE_NUMBER_CEILING,
//	
	/**没有返回状态码, 值为9000*/
	public static final int NO_CODE = 9000;
	/**请求参数格式不正确, 值为9001*/
	public static final int INCORRECT_PARAMETERS = 9001;
	/**协议不存在, 值为9002*/
	public static final int NO_AGREEMENT = 9002;
	
//	public static final int[] values = {-1, 0, 1000, 1000, 1001, 1002, 1003, 1004, 1010, 1011, 1012, 
//		1013, 1014, 1015, 1016, 1017, 1010, 1101, 1020, 1100, 1101, 1102, 1103,
//		1104, 1105, 1106, 1107, 1108, 1109 , 1110, 1111, 1112, 1200, 1201, 9000, 9001, 9002}; 
	
}
