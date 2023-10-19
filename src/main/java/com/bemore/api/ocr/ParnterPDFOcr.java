package com.bemore.api.ocr;

import com.bemore.api.entity.Enterprise;
import com.spire.doc.Document;

public class ParnterPDFOcr {

	public static Enterprise getBasicInfo(String path){		
		Document doc = new Document();
		doc.loadFromFile(path);
		
		Enterprise enterprise = new Enterprise();
		for(int i=doc.getSections().get(0).getParagraphs().getCount()-1;i>=0;i--){
			String text = doc.getSections().get(0).getParagraphs().get(i).getText();
			// 名称
			if(text.equals("申请名称")){
				text = doc.getSections().get(0).getParagraphs().get(i+1).getText();
				enterprise.setName(text);
			}
			// 注册地
			if(text.startsWith("生产经营地")){
				enterprise.setRegisterAddress(text.replace("生产经营地", "").trim());
			}
			// 邮政编码
			if(text.equals("邮政编码")){
				text = doc.getSections().get(0).getParagraphs().get(i+1).getText();
				enterprise.setZipcode(text);
			}
			// 注册资本
			if(text.startsWith("出资额(万元)")){
				text=text.replace("其中：实缴 ___万元，认缴 ___万元。", "").replace("出资额(万元)", "").trim();
				enterprise.setCapital(text.substring(0,text.length()-2));
			}
			// 经营范围
			if(text.startsWith("一般项目：")){
				StringBuffer scopeBuffer = new StringBuffer();
				scopeBuffer.append(text);
				for(int j=i-1;;j--){
					text = doc.getSections().get(0).getParagraphs().get(j).getText();
					if(text.equals("经营范围")){
						continue;
					}else if(text.trim().equals("")){
						break;
					}else{
						scopeBuffer.append(text);
					}
				}
				String scope = scopeBuffer.toString();
				if(scope.contains("许可项目：")){
					String[] s = scope.split("许可项目：");
					enterprise.setBusiness(s[0].replace("一般项目：", ""));
				}else{
					enterprise.setBusiness(scope.replace("一般项目：", ""));
				}
			}
		}
		return enterprise;
	}
}
