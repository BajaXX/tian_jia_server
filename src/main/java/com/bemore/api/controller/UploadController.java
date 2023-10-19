package com.bemore.api.controller;

import com.bemore.api.config.FileUpSaveConfig;
import com.bemore.api.service.ParseFileService;
import com.bemore.api.service.SaveFileImpl;
import io.swagger.annotations.ApiOperation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.PersonDao;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.Person;
import com.bemore.api.exception.WebException;
import com.bemore.api.ocr.LtdPDFOcr;
import com.bemore.api.ocr.ParnterPDFOcr;
import com.bemore.api.ocr.PersonalPDFOcr;
import com.bemore.api.util.GsonUtil;
import com.spire.doc.Document;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

@RestController
@RequestMapping("/file")
public class UploadController {
	
	private static final Logger logger = LoggerFactory.getLogger(UploadController.class);

	private String pdfFileDir="//var//www//html//pdf//";
	private String idcardFileDir="//var//www//html//idcard//";
	private String bizFileDir="//var//www//html//biz//";
	private String docFileDir="//var//www//html//doc//";

//	private String pdfFileDir="D:\\园区\\pdf\\";
//	private String idcardFileDir="D:\\园区\\idcard\\";
//	private String bizFileDir="D:\\园区\\biz\\";
//	private String docFileDir="D:\\园区\\doc\\";

	private static String REGEX_CHINESE = "[\u4e00-\u9fa5]";
	
	@Autowired
	private PersonDao personDao;
	@Autowired
	private EnterpriseDao enterpriseDao;

	@Autowired
	SaveFileImpl saveFile;
	@Autowired
	ParseFileService parseFileService;

	@Autowired
	FileUpSaveConfig upSaveConfig;
		
	@ApiOperation(value = "上传身份证正面")
	@PostMapping(value = "/uploadFrontIDCard")
	public Object uploadFrontIDCard(@RequestParam("file")MultipartFile srcFile) {
        try {
//        	String name = srcFile.getOriginalFilename().substring(0, srcFile.getOriginalFilename().lastIndexOf("."));
//        	String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
//			String fileName = System.currentTimeMillis()+"."+suffix;
//			String path = idcardFileDir +fileName ;
//			File file = new File(path);
//			FileUtil.createFile(path);
//			srcFile.transferTo(file);
			String pathAndName = saveFile.saveIdCard(upSaveConfig.getIdCardFileDir(), srcFile);

			// 身份证OCR
//            Credential cred = new Credential("AKIDouoYLkg1tnRuGRFDpJYYdU3OiQt52pJt",
//            		"dIxOfgRwlOrEQL4XjfHmNjBNL2ZswxiF");
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            OcrClient client = new OcrClient(cred, "ap-shanghai", clientProfile);
//            IDCardOCRRequest req = new IDCardOCRRequest();
//            req.setImageUrl("http://52.82.29.181:8091/idcard/"+fileName);
//            req.setCardSide("FRONT");
//
//            IDCardOCRResponse resp = client.IDCardOCR(req);
//            Person person = new Person();
//            person.setAddress(resp.getAddress());
//            person.setBirthday(resp.getBirth());
//            person.setIdcard(resp.getIdNum());
//            person.setName(resp.getName());
//            person.setNation(resp.getNation());
//            person.setSex(resp.getSex());
//            person.setFront("http://52.82.29.181:8091/idcard/"+fileName);
//            if(!StringUtils.isEmpty(resp.getBirth())){
//	            String[] date = resp.getBirth().split("/");
//	            if(date[1].length()==1){
//	            	date[1] = "0"+date[1];
//	            }
//	            if(date[2].length()==1){
//	            	date[2] = "0"+date[2];
//	            }
//	            person.setBirthday(date[0]+"-"+date[1]+"-"+date[2]);
//            }
			Person person =parseFileService.idCardFront(pathAndName,new Person());

			return GsonUtil.build(person);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebException(500,"服务端异常");
		}
	}

	@PostMapping(value = "/uploadBackIDCard")
	public Object uploadBackIDCard(@RequestParam("file")MultipartFile srcFile) {
        try {
//        	String name = srcFile.getOriginalFilename().substring(0, srcFile.getOriginalFilename().lastIndexOf("."));
//        	String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
//			String fileName = System.currentTimeMillis()+"."+suffix;
//			String path = idcardFileDir +fileName ;
//			File file = new File(path);
//			FileUtil.createFile(path);
//			srcFile.transferTo(file);
//
//			Credential cred = new Credential("AKIDouoYLkg1tnRuGRFDpJYYdU3OiQt52pJt",
//            		"dIxOfgRwlOrEQL4XjfHmNjBNL2ZswxiF");
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            OcrClient client = new OcrClient(cred, "ap-shanghai", clientProfile);
//            IDCardOCRRequest req = new IDCardOCRRequest();
//            req.setImageUrl("http://52.82.29.181:8091/idcard/"+fileName);
//            req.setCardSide("BACK");
//
//            IDCardOCRResponse resp = client.IDCardOCR(req);
//            Person person = new Person();
//            person.setAuthority(resp.getAuthority());
//            person.setBack("http://52.82.29.181:8091/idcard/"+fileName);
//            if(!StringUtils.isEmpty(resp.getValidDate())){
//	            String[] date = resp.getValidDate().split("-");
//	            System.out.println(date);
//	            person.setStartDate(date[0].replaceAll(".", "-"));
//	            person.setEndDate(date[1].replaceAll(".", "-"));
//            }
			String pathAndName = saveFile.saveIdCardReverse(upSaveConfig.getIdCardFileDir(), srcFile);

			Person person = parseFileService.idCardBack(pathAndName, new Person());


			return GsonUtil.build(person);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebException(500,"服务端异常");
		}
	}
	
	@PostMapping(value = "/uploadBizLicense")
	public Object uploadBizLicense(@RequestParam("file")MultipartFile srcFile) {
        try {
//        	String name = srcFile.getOriginalFilename().substring(0, srcFile.getOriginalFilename().lastIndexOf("."));
//        	String suffix = srcFile.getOriginalFilename().substring(srcFile.getOriginalFilename().lastIndexOf(".") + 1);
//			String fileName = System.currentTimeMillis()+"."+suffix;
//			String path = bizFileDir +fileName ;
//			File file = new File(path);
//			FileUtil.createFile(path);
//			srcFile.transferTo(file);
//			String pathAndName = saveFile.saveBusinessLicense(upSaveConfig.getBizFileDir(), srcFile);

//			Credential cred = new Credential("AKIDouoYLkg1tnRuGRFDpJYYdU3OiQt52pJt",
//            		"dIxOfgRwlOrEQL4XjfHmNjBNL2ZswxiF");
//            HttpProfile httpProfile = new HttpProfile();
//            httpProfile.setEndpoint("ocr.tencentcloudapi.com");
//            ClientProfile clientProfile = new ClientProfile();
//            clientProfile.setHttpProfile(httpProfile);
//            OcrClient client = new OcrClient(cred, "ap-shanghai", clientProfile);
//            BizLicenseOCRRequest req = new BizLicenseOCRRequest();
//            req.setImageUrl("http://52.82.29.181:8091/biz/"+fileName);
//            BizLicenseOCRResponse resp = client.BizLicenseOCR(req);
//
//            Enterprise enterprise = new Enterprise();
//            enterprise.setName(resp.getName());
//            enterprise.setType(resp.getType());
//            enterprise.setBusiness(resp.getBusiness());
//            enterprise.setRegisterAddress(resp.getAddress());
//            enterprise.setRegisterNum(resp.getRegNum());
//            enterprise.setCapital(resp.getCapital().replaceAll(REGEX_CHINESE, ""));
//            String[] dates = resp.getPeriod().split("至");
//            enterprise.setStartDate(dates[0].replaceAll(REGEX_CHINESE, "-"));
//            enterprise.setEndDate(dates[1].replaceAll(REGEX_CHINESE, "-"));
//            enterprise.setPaperName("http://52.82.29.181:8091/biz/1656126244831.pdf"+fileName);
            String pathAndName = saveFile.saveBusinessLicense(upSaveConfig.getBizFileDir(), srcFile);
            Enterprise enterprise = parseFileService.bizLicense(pathAndName);
//			Enterprise enterprise = parseFileService.bizLicense(srcFile);
			return GsonUtil.build(enterprise);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebException(500,"服务端异常");
		}
	}

	@PostMapping(value = "/uploadNewPDF")
	public Object uploadNewPDF(@RequestParam("file")MultipartFile srcFile) {
		try {
//			String name = System.currentTimeMillis() + "";
//			String fileName = pdfFileDir + name +".pdf";
//			File file = new File(fileName);
//			FileUtil.createFile(fileName);
//			srcFile.transferTo(file);
			String name = System.currentTimeMillis() + "";
			String pathAndSave = saveFile.saveCompanyInfoPdf(upSaveConfig.getPdfFileDir() + name + ".pdf", srcFile);

			String newFileName = upSaveConfig.getDocFileDir() + name +".docx";
			PdfDocument pdf = new PdfDocument();
			pdf.loadFromFile(pathAndSave);
			pdf.saveToFile(newFileName, FileFormat.DOCX);

			Document doc = new Document();
			doc.loadFromFile(newFileName);

			Enterprise enterprise = new Enterprise();
//			Person person=new Person();
//			Person person1=new Person();
//			Person person2=new Person();
			int index = doc.getSections().get(0).getParagraphs().getCount()-2;
			String type = doc.getSections().get(0).getParagraphs().get(index).getText();
			if(type.equals("合伙企业登记（备案）申请书")){
				enterprise = ParnterPDFOcr.getBasicInfo(newFileName);
////				财务人员信息
//				for (int i = 0; i < doc.getSections().get(4).getParagraphs().getCount() - 1; i++) {
//					String text = doc.getSections().get(4).getParagraphs().get(i).getText();
//					System.out.println(i + ":" + text);
//					if (text.equals("姓名")){
//						text = doc.getSections().get(4).getParagraphs().get(i+1).getText();
//						person.setEnterpriseId(enterprise.getId());
//						person.setName(text);
//						person.setIsFinance(1);
//						person.setSex("0");
////						person.setType(1);
//						person.setAddress("");
//					}
//					if (text.equals("电子邮箱")){
//						text = doc.getSections().get(4).getParagraphs().get(i+1).getText();
//						person.setEmail(text);
//					}
//					if (text.equals("固定电话")){
//						text = doc.getSections().get(4).getParagraphs().get(i+1).getText();
//						if (text.equals("财务负责人信息")){
//							text="";
////							person.setGphone(text.trim());
//						}else {
////							person.setGphone(text.trim());
//						}
//					}
//					if (text.equals("移动电话")){
//						text = doc.getSections().get(4).getParagraphs().get(i+1).getText();
////						person.setYphone(text.trim());
//					}
//					if (text.startsWith("身份证件号码")){
//						text = doc.getSections().get(4).getParagraphs().get(i).getText();
//						person.setIdcard(text.replaceAll("身份证件号码","").trim());
//					}
//				}
//				personDao.save(person);
////				联络人信息
//				for (int i = 0; i < doc.getSections().get(3).getParagraphs().getCount() - 1; i++) {
//					String text = doc.getSections().get(3).getParagraphs().get(i).getText();
//					System.out.println(i + ":" + text);
//					if (text.equals("姓名")) {
//						text = doc.getSections().get(3).getParagraphs().get(i + 1).getText();
//						person1.setName(text.trim());
//						person1.setEnterpriseId(enterprise.getId());
//						person1.setIsContact(1);
//						person1.setSex("0");
////						person1.setType(1);
//						person1.setAddress("");
//					}
//					if (text.equals("固定电话")){
//						text = doc.getSections().get(3).getParagraphs().get(i+1).getText();
//						if (text.equals("联络员信息")){
//							text="";
////							person1.setGphone(text.trim());
//						}else {
////							person1.setGphone(text.trim());
//						}
//					}
//					if (text.equals("移动电话")){
//						text = doc.getSections().get(3).getParagraphs().get(i+1).getText();
////						person1.setYphone(text.trim());
//					}
//					if (text.equals("电子邮箱")){
//						text = doc.getSections().get(3).getParagraphs().get(i+1).getText();
//						person1.setEmail(text.trim());
//					}
//					if (text.startsWith("身份证件号码")) {
//						text = doc.getSections().get(3).getParagraphs().get(i).getText();
//						person1.setIdcard(text.replaceAll("身份证件号码", "").trim());
//					}
//				}
//				personDao.save(person1);
////				委派人信息
//				for (int i = 0; i < doc.getSections().get(1).getParagraphs().getCount() - 1; i++) {
//					String text = doc.getSections().get(1).getParagraphs().get(i).getText();
//					System.out.println(i + ":" + text);
//					if (text.equals("姓名")){
//						text = doc.getSections().get(1).getParagraphs().get(i+1).getText();
//						person2.setName(text.trim());
//						person2.setEnterpriseId(enterprise.getId());
//						person2.setIsMaster(1);
//						person2.setSex("0");
////						person2.setType(1);
//						person2.setAddress("");
//					}
//					if (text.equals("固定电话")){
//						text = doc.getSections().get(1).getParagraphs().get(i+1).getText();
//						if (text.equals("执行事务合伙人（委派代表）信息")){
//							text="";
////							person2.setGphone(text.trim());
//						}else {
////							person2.setGphone(text.trim());
//						}
//					}
//					if (text.equals("移动电话")){
//						text = doc.getSections().get(1).getParagraphs().get(i+1).getText();
////						person2.setYphone(text);
//					}
//					if (text.equals("电子邮箱")){
//						text = doc.getSections().get(1).getParagraphs().get(i+1).getText();
//						person2.setEmail(text.trim());
//					}
//					if (text.startsWith("身份证件号码")){
//						text = doc.getSections().get(1).getParagraphs().get(i).getText();
//						person2.setIdcard(text.replaceAll("身份证件号码","").trim());
//					}
//				}
//				personDao.save(person2);
			}else if(type.equals("个人独资企业登记（备案）申请书")){
				enterprise = PersonalPDFOcr.getBasicInfo(newFileName);
////				个独财务信息
//				for (int i=0;i<doc.getSections().get(0).getParagraphs().getCount()-1;i++) {
//					String text = doc.getSections().get(0).getParagraphs().get(i).getText();
////              姓名
//					if (text.equals("姓 名")){
//						text=doc.getSections().get(0).getParagraphs().get(i+1).getText();
//						person.setEnterpriseId(enterprise.getId());
//						person.setIsFinance(1);
//						person.setSex("0");
////						person.setType(1);
//						person.setAddress("");
//						person.setName(text);
//					}
////                固定电话
//					if (text.equals("固定电话")){
//						text=doc.getSections().get(0).getParagraphs().get(i+1).getText();
//
////						person.setGphone(text.trim());
//					}
////                移动电话
//					if (text.startsWith("移动电话")){
//						text=doc.getSections().get(0).getParagraphs().get(i).getText();
////						person.setYphone(text.replaceAll("移动电话","").trim());
//					}
////                电子邮箱
//					if (text.equals("电子邮箱")){
//						text=doc.getSections().get(0).getParagraphs().get(i+1).getText();
//						person.setEmail(text.trim());
//					}
////                身份证件号码
//					if (text.startsWith("身份证件号码")){
//						text=doc.getSections().get(0).getParagraphs().get(i).getText();
//						person.setIdcard(text.replaceAll("身份证件号码","").trim());
//					}
//				}
////				委派人信息
//				for (int i=0;i<doc.getSections().get(1).getParagraphs().getCount()-1;i++) {
//					String text = doc.getSections().get(1).getParagraphs().get(i).getText();
//					System.out.println(i + ":" + text);
//					if (text.equals("身份证类型")){
//						text= doc.getSections().get(1).getParagraphs().get(i-1).getText();
//						person1.setEnterpriseId(enterprise.getId());
//						person1.setIsMaster(1);
//						person1.setSex("0");
////						person1.setType(1);
//						person1.setAddress("");
//						person1.setName(text);
//					}
//					if (text.equals("指定代表/委托")){
//						text= doc.getSections().get(1).getParagraphs().get(i-2).getText();
//						person1.setIdcard(text.trim());
//					}
////                固定电话
//					if (text.endsWith("核对登记材料中的复印件并签署核对意见； ")){
//						text= doc.getSections().get(1).getParagraphs().get(i+1).getText();
////						person1.setGphone(text.replaceAll("固定电话","").trim());
//					}
//					if (text.equals("号码")){
//						text= doc.getSections().get(1).getParagraphs().get(i-1).getText();
//					}
//				}
////				联络员信息
//				for (int i = 0; i<doc.getSections().get(1).getParagraphs().getCount()-1 ; i++) {
//					String text = doc.getSections().get(1).getParagraphs().get(i).getText();
//					if (text.equals("联络员信息（仅限设立及备案联络员填写）")){
//						text=doc.getSections().get(1).getParagraphs().get(i-3).getText();
//						String gd=doc.getSections().get(1).getParagraphs().get(i-1).getText();
//						person2.setEnterpriseId(enterprise.getId());
//						person2.setIsContact(1);
//						person2.setSex("0");
////						person2.setType(1);
//						person2.setAddress("");
//						person2.setName(text);
////						person2.setGphone(gd);
//					}
//					if (text.equals("电子邮箱")){
//						String t=doc.getSections().get(1).getParagraphs().get(i+1).getText();
//						text=doc.getSections().get(1).getParagraphs().get(i-1).getText();
//						person2.setEmail(t.trim());
////						person2.setYphone(text.replaceAll("移动电话","").trim());
//					}
//					if (text.startsWith("身份证件号码")){
//						text=doc.getSections().get(1).getParagraphs().get(i).getText();
//						person2.setIdcard(text.replaceAll("身份证件号码","").trim());
//					}
//				}
			}else{
				enterprise = LtdPDFOcr.getBasicInfo(newFileName);
			}
			return GsonUtil.build(enterprise);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WebException(500,"服务端异常");
		}
	}
	
}
