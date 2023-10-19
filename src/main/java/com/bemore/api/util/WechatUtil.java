package com.bemore.api.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.json.GsonJsonParser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Map;

public class WechatUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(WechatUtil.class);

	public static final String APP_ID = "wx245794c7f7c4e531";

	public static final String APP_SECRET = "f7bb729d4b369a0b86a7cbab74f216bc";

//	public static String ACCESS_TOKEN;
//
//	public static Calendar ACCESS_TOKEN_EXPIRE_TIME;
//
//	public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+APP_ID+"&secret="+APP_SECRET;
//	public static final String ACCESS_TOKEN_URL_OTHER = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
//	
//	public static String OAUTH_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APP_ID+"&secret="+APP_SECRET+"&grant_type=authorization_code&code=";
	
	public static String CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
	
	public static String ACESS_TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=ww79ea88807600f696&corpsecret=pIeiHYb9hQGXfpYyQNLIv64gO8JHFMbRc4qRmm2YL_E";
	
	public static String RECORDS = "https://qyapi.weixin.qq.com/cgi-bin/corp/getapprovaldata?access_token=AlNYnG6yb3iPFRoe4hpwT3etKmbJC-5y650xLoKjO2p_AuxC8rnufzW8rc4Y2Ow0Yfo7aUTBvsjpt2-WgYVq_0aiM3sn5bBkGwxMCNO9k0i6OugV73CbRD9RibU-5BRDwVFpuC2G-z3cSGXIopJvcIXvulPVyja79J6fr5fhZ6j24OUnD_R5FE5aBtszrMUWp7KKx5YJqe4RpyzXxvZMIw";
	
//	public static String USER_INFO = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=";
//	
//	public static String SEND_MESSAGE_TEMPLATE = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=";
//	
//	public static String SEND_SUBSCRIB_MESSAGE_TEMPLATE = "https://mp.weixin.qq.com/mp/subscribemsg?action=get_confirm";
//	
//	public static String SEND_MESSAGE = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";
//	
//	public static String GET_MATERIALS = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";
//
//	public static final String AUTHORIZE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=APPID&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
//
//	public static  final String HOST = "http://bd.cesp365.com/";
	
	public static boolean getToken(){
		String url = ACESS_TOKEN_URL;		
		String result = RestTempComponent.sendGetRequest(url);
		logger.info(result);		
		return true;
	}
	
	public static boolean getRecords(){
		String url = RECORDS;
		JsonObject object = new JsonObject();
		object.addProperty("starttime", "1633073483");
		object.addProperty("endtime", "1633087883");
		String result = RestTempComponent.sendPostRequest(url, object.toString());
		logger.info(result);		
		return true;
	}
	
	public static String getAccessToken() {		
		return "51_6zuaC2Fb3fgmwlHYe18GSXDIt3g_Rl4Kz94MG622AVsvj0eEZIVQu33nBESDlgXvst0GZC6qtcCSjWz-whGQxPqfgtUYVKUZCqnUG_v9uEOshlJbXU_aoJ5UlM4xQjjxemGudaXET4PX4IOYQTKdAGADGX";
	}
		
	// 微信菜单
	public static String createMenu(){
		JsonObject object = new JsonObject();
		JsonArray buttons = new JsonArray();
		// 车主中心
		JsonObject button1 = new JsonObject();
		
		JsonArray subButtons1 = new JsonArray();
		JsonObject subMenu1Button1 = new JsonObject();		
		subMenu1Button1.addProperty("name", "个人信息");
		subMenu1Button1.addProperty("type", "view");
//		subMenu1Button1.put("url", "http://wechat.mypeugeot.com.cn/wxapi/Home/index/getJsInfo?public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk"
//				+ "&url=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/mycenter?isMain=true")
//				+ "&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&project_name=聚合平台");
		subMenu1Button1.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/mycenter?isMain=true")
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/mycenter?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		subButtons1.add(subMenu1Button1);
		
		JsonObject subMenu1Button2 = new JsonObject();		
		subMenu1Button2.addProperty("name", "预约维保");
		subMenu1Button2.addProperty("type", "view");
		subMenu1Button2.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/biaozhi/index.php?mod=dealer&child=yuyue&op=&id=")
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http%3A%2F%2Fm.mypeugeot.com.cn%2Fbiaozhi%2Findex.php%3Fmod%3Ddealer%26child%3Dyuyue%26op%3D%26id%3D")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/biaozhi/index.php?mod=dealer&child=yuyue&op=&id=")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		subButtons1.add(subMenu1Button2);
		
//		JsonObject subMenu1Button3 = new JsonObject();		
//		subMenu1Button3.addProperty("name", "道路救援");
//		subMenu1Button3.addProperty("type", "view");
//		subMenu1Button3.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
////				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/biaozhi/?mod=dealer&child=help")
//				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http%3A%2F%2Fm.mypeugeot.com.cn%2Fbiaozhi%2F%3Fmod%3Ddealer%26child%3Dhelp")
////				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/biaozhi/?mod=dealer&child=help")
//				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
////		subMenu1Button3.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/sellers"));
//		subButtons1.add(subMenu1Button3);
		
		JsonObject subMenu1Button3 = new JsonObject();		
		subMenu1Button3.addProperty("name", "车品商城");
		subMenu1Button3.addProperty("type", "view");
		subMenu1Button3.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=https://ntsp.dpca.com.cn/ntsp-mall/#/openid?brandCode=DPAD&channel=1")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/pn_blue_care?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu1Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons1.add(subMenu1Button3);
		
//		JsonObject subMenu1Button4 = new JsonObject();		
//		subMenu1Button4.addProperty("name", "蓝色关爱");
//		subMenu1Button4.addProperty("type", "view");
//		subMenu1Button4.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
//				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/pn_blue_care?isMain=true")
////				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/pn_blue_care?isMain=true")
//				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
////		subMenu1Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
//		subButtons1.add(subMenu1Button4);
		
		JsonObject subMenu1Button4 = new JsonObject();		
		subMenu1Button4.addProperty("name", "备件扫码");
		subMenu1Button4.addProperty("type", "view");
		subMenu1Button4.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/biaozhi/scanIndex.php")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/pn_car_class?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu1Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons1.add(subMenu1Button4);
		
		JsonObject subMenu1Button5 = new JsonObject();		
		subMenu1Button5.addProperty("name", "投诉建议");
		subMenu1Button5.addProperty("type", "view");
		subMenu1Button5.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/customer_service")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=3&isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu2Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons1.add(subMenu1Button5);		
		
		button1.addProperty("name", "车主中心");
		button1.add("sub_button", subButtons1);
		buttons.add(button1);
		
		// 品致DNA
		JsonObject button2 = new JsonObject();
		JsonArray subButtons2 = new JsonArray();
		JsonObject subMenu2button1 = new JsonObject();		
		subMenu2button1.addProperty("name", "设计之美");
		subMenu2button1.addProperty("type", "view");
		subMenu2button1.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/")
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=0&isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=0&isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu2button2.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/venue_appointment"));
		subButtons2.add(subMenu2button1);
		JsonObject subMenu2Button2 = new JsonObject();		
		subMenu2Button2.addProperty("name", "品致之实");
		subMenu2Button2.addProperty("type", "view");
		subMenu2Button2.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=1&isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=1&isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		subButtons2.add(subMenu2Button2);
		JsonObject subMenu2Button3 = new JsonObject();		
		subMenu2Button3.addProperty("name", "科技之悦");
		subMenu2Button3.addProperty("type", "view");
		subMenu2Button3.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=2&isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=2&isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu2Button3.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/sellers"));
		subButtons2.add(subMenu2Button3);
		
		JsonObject subMenu2Button4 = new JsonObject();		
		subMenu2Button4.addProperty("name", "五心守护");
		subMenu2Button4.addProperty("type", "view");
		subMenu2Button4.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=3&isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/beauty_design?bannerType=3&isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu2Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons2.add(subMenu2Button4);
		
		JsonObject subMenu2Button5 = new JsonObject();		
		subMenu2Button5.addProperty("name", "爱车课堂");
		subMenu2Button5.addProperty("type", "view");
		subMenu2Button5.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/pn_car_class?isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/pn_car_class?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu1Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons2.add(subMenu2Button5);
		
		button2.addProperty("name", "品致DNA");
		button2.add("sub_button", subButtons2);
		buttons.add(button2);
		
		// 品致之旅
		JsonObject button3 = new JsonObject();
		JsonArray subButtons3 = new JsonArray();
		JsonObject subMenu3button1 = new JsonObject();		
		subMenu3button1.addProperty("name", "品致活动");
		subMenu3button1.addProperty("type", "view");
		subMenu3button1.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/activity/wechat/#/auality_active?isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/auality_active?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu3button3.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/venue_appointment"));
		subButtons3.add(subMenu3button1);
		
		JsonObject subMenu3button2 = new JsonObject();		
//		subMenu3button2.addProperty("name", "致者荐致");
//		subMenu3button2.addProperty("type", "view");
//		subMenu3button2.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
////				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://act.kachuu.com/zzjz/10/")
//				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://act.mypeugeot.com.cn/zzjz/10/#/?isMain=true")
////				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://act.mypeugeot.com.cn/zzjz/10/#/?isMain=true")
//				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		subMenu3button2.addProperty("name", "狮王联盟");
		subMenu3button2.addProperty("type", "miniprogram");
		subMenu3button2.addProperty("appid", "wxcc62ce67346fb84c");
		subMenu3button2.addProperty("pagepath", "pages/index/index");
		subMenu3button2.addProperty("url", "pages/index/index");
		subButtons3.add(subMenu3button2);

		JsonObject subMenu3Button3 = new JsonObject();		
		subMenu3Button3.addProperty("name", "积分商城");
		subMenu3Button3.addProperty("type", "view");
		subMenu3Button3.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://cshop.mypeugeot.com.cn/#/?isMain=true")
//				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://cshop.mypeugeot.com.cn/#/?isMain=true")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu3Button3.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/sellers"));
		subButtons3.add(subMenu3Button3);
		
		JsonObject subMenu3Button4 = new JsonObject();		
		subMenu3Button4.addProperty("name", "0元升舱");
		subMenu3Button4.addProperty("type", "view");
		subMenu3Button4.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
		+ "&redirect_uri=" +  URLEncoder.encode("http://m.mypeugeot.com.cn/peugeot_case_activity/#/qmyhj/index1")
		+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu3Button4.addProperty("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx245794c7f7c4e531"
//				+ "&redirect_uri=" +  URLEncoder.encode("http://wechat.mypeugeot.com.cn/wxapi/Home/index/getOpenid?project_name=%E8%81%9A%E5%90%88%E5%B9%B3%E5%8F%B0&public_key=8vx2j8j78hykyr7npm9q76r8wt9u6duk&secret_key=8ed6a8a147c83e8e9205f20701c1ebc3&redirect_url=http://m.mypeugeot.com.cn/peugeot_car_activity/news")
//				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
		subButtons3.add(subMenu3Button4);
		
		JsonObject subMenu3Button5 = new JsonObject();		
		subMenu3Button5.addProperty("name", "狮粉俱乐部");
		subMenu3Button5.addProperty("type", "miniprogram");
		subMenu3Button5.addProperty("appid", "wxb26c1f4bf13fbe68");
		subMenu3Button5.addProperty("pagepath", "pages/act/index/index");
		subMenu3Button5.addProperty("url", "pages/act/index/index");
		subButtons3.add(subMenu3Button5);
		
		/*
		JSONObject subMenu3Button5 = new JSONObject();		
		subMenu3Button5.put("name", "爱车课堂");
		subMenu3Button5.put("type", "view");
		subMenu3Button5.put("url", "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxc6c1be6a42cfc317"
				+ "&redirect_uri=" +  URLUtil.getURLEncoderString("http://m.mypeugeot.com.cn/activity/wechat/#/pn_car_class")
				+ "&response_type=code&scope=snsapi_base&state=123#wechat_redirect");
//		subMenu3Button4.put("url", URLUtil.getURLEncoderString("http://wechat.kachuu.com/server/api/wechat/map"));
		subButtons3.add(subMenu3Button5);
		*/
		button3.addProperty("name", "活动福利");
		button3.add("sub_button", subButtons3);
		buttons.add(button3);

		object.add("button", buttons);
		
		String result = RestTempComponent.sendPostRequest(CREATE_MENU+getAccessToken(),object.toString());		
		logger.info(result);
		return result;
	}
	
	public static String SEND_SUBSCRIBE_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="
			+ "51_XDMzAf3PZ4pKF198ElI7f4w91KkALkDENUZsWPaPGxblozPhPzSiitW8BjTpFeB0pBTiTZmcjijg-00jGr1FahvSrk7c2BdFhZXVtkvyKMAXG4VxfdTjcIw_rooOyYb9vufTYcjGmfmJQrtvENJjAAASBX";	
	
	public static boolean sendMessage(String openid){
		String url = SEND_SUBSCRIBE_MESSAGE_URL;
		JsonObject object = new JsonObject();
		object.addProperty("touser", openid);
		object.addProperty("template_id", "gn9zi0N7U1_jNuzDxTGLKFCcfF6zDRP-wvbZnPaJOZw");
		object.addProperty("url", "http://wx.df-citroenclub.com.cn/static/wechat/dx/index.html?openid="+openid+"&unionid=oIDxVuKE0WIA57FqoJDI9CwNVKV0#/ad");
		
		JsonObject data = new JsonObject();
		JsonObject v1 = new JsonObject();
		v1.addProperty("value", "尊敬的车主，天逸车友福利来袭~");
		data.add("first", v1);
		JsonObject v2 = new JsonObject();
		v2.addProperty("value", "定速巡航加装季正式启动");
		data.add("keyword1", v2);
		JsonObject v3 = new JsonObject();
		v3.addProperty("value", "针对不带定速巡航的天逸，现全国经销商推出原厂改装定速巡航活动，省油更省心！让爱车更懂你！快来加装吧~");
		data.add("keyword2", v3);
		JsonObject v4 = new JsonObject();
		v4.addProperty("value", "点击查看详情");
		data.add("remark", v4);
		
		object.add("data", data);
		String result = RestTempComponent.sendPostRequest(url,object.toString());
		logger.info(result);
		if (result != null && result.contains("ok")) {
			return true;
		}else{
			logger.info(object.toString());			
		}
		return false;
	}
	
	public static boolean sendMessage2(String openid, String url, String name, String vin, String workNo, String serviceType, String dealer){
		String wurl = SEND_SUBSCRIBE_MESSAGE_URL;
		JsonObject object = new JsonObject();
		object.addProperty("touser", openid);
//		object.addProperty("template_id", "8UDVljLq3Ji5sH-U0jT2rUw2tScFl9hmpT_Hi5VKk6o");
		object.addProperty("template_id", "Dlmypdb5aPzNWugg_4YlYRG_Oyt093awxYrYxP7TKCc");
		object.addProperty("url", url);
		
		JsonObject data = new JsonObject();
		JsonObject v1 = new JsonObject();
		v1.addProperty("value", "尊敬的"+name+",您好!为提升售后服务质量，更好的为您提供服务，我们诚邀您对最近一次在东风标致特约店的服务体验做评价。");
		data.add("first", v1);
		
		JsonObject v2 = new JsonObject();
		v2.addProperty("value", vin);
		data.add("keyword1", v2);
		JsonObject v3 = new JsonObject();
		v3.addProperty("value", workNo);
		data.add("keyword2", v3);
		JsonObject v4 = new JsonObject();
		v4.addProperty("value", serviceType);
		data.add("keyword3", v4);
		JsonObject v5 = new JsonObject();
		v5.addProperty("value", dealer);
		data.add("keyword4", v5);
		
		JsonObject v6 = new JsonObject();
		v6.addProperty("value", "请点击详情参与评价。感谢您的支持，祝您用车愉快！如有疑问，请致电东风标致品牌热线4008877108进行相关咨询");
		data.add("remark", v6);
		
		object.add("data", data);
		String result = RestTempComponent.sendPostRequest(wurl,object.toString());
		logger.info(result);
		if (result != null && result.contains("ok")) {
			return true;
		}else{
			logger.info(object.toString());			
		}
		return false;
	}
	
	public static void main(String[] args) {
//        System.out.println(createMenu());
//		System.out.println(getRecords());		
		
		Workbook wb = new Workbook();
        wb.loadFromFile("fail.xls");
        Worksheet worksheet = wb.getWorksheets().get(0);
        for(int i=2;i<=6420;i++){
//        	System.out.println(worksheet.get(i, 2).getValue());        	
        	boolean r = false;
        	try {
	        	JsonObject data = new JsonParser().parse(worksheet.get(i, 3).getText()).getAsJsonObject();
//	        	System.out.println(i);
	        	String openid = "";
	        	if(data.has("openid")){
	        		openid = data.get("openid").getAsString();
	        		System.out.println(openid);
	        	}        	
	        	String url = "";
	        	if(data.has("url")){
	        		url = data.get("url").getAsString();
	//        		System.out.println(url);
	        	}
	        	String name = "";
	        	if(data.has("name")){
	        		name = data.get("name").getAsString();
	//        		System.out.println(name);
	        	}        	
	        	String vin = "";
	        	if(data.has("vin")){
	        		vin = data.get("vin").getAsString();
	//        		System.out.println(vin);
	        	}
	        	String workNo = "";
	        	if(data.has("workNo")){
	        		try{
	        			workNo = data.get("workNo").getAsString();
	//        			System.out.println(workNo);
	        		}catch(Exception ex){
	        			
	        		}
	        	}
	        	String serviceType = "";
	        	if(data.has("serviceType")){
	        		serviceType = data.get("serviceType").getAsString();
	//        		System.out.println(serviceType);
	        	}
	        	String dealer = "";
	        	if(data.has("dealer")){
	        		dealer = data.get("dealer").getAsString();
	//        		System.out.println(dealer);
	        	}
	        	r = sendMessage2(openid, url, name, vin, workNo, serviceType, dealer);
			} catch (Exception e) {
//				e.printStackTrace();
//				break;
			}
        	try {
				write(""+r);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }        
		
//		sendMessage("o2Ld3jv6qif8qJEdw--rywnXhtxI");		
//		sendMessage("o2Ld3jlfOB2_Ar_3WVDrD7QV7WVM");
    }
	
	public static void write(String txt) throws IOException {
		//将写入转化为流的形式
		FileWriter fw = null;
		try {
		//如果文件存在，则追加内容；如果文件不存在，则创建文件
		File f=new File("Q:\\1.txt");
		fw = new FileWriter(f, true);//true,进行追加写。
		} catch (IOException e) {
		e.printStackTrace();
		}
		PrintWriter pw = new PrintWriter(fw);
		pw.println(txt);
		pw.flush();
		try {
		fw.flush();
		pw.close();
		fw.close();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}
	
}
