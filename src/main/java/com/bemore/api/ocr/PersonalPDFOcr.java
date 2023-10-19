package com.bemore.api.ocr;

import com.bemore.api.entity.Enterprise;
import com.spire.doc.Document;

public class PersonalPDFOcr {

	public static Enterprise getBasicInfo(String path){		
		Document doc = new Document();
		doc.loadFromFile(path);
		
		Enterprise enterprise = new Enterprise();
		for(int i=doc.getSections().get(0).getParagraphs().getCount()-1;i>=0;i--){
			String text = doc.getSections().get(0).getParagraphs().get(i).getText();
			System.out.println(text);
			// 公司名
			if(text.equals("统一社会信用")){
				StringBuffer nameBuf = new StringBuffer();
				for(int j=i-1;;j--){
					text = doc.getSections().get(0).getParagraphs().get(j).getText();
					if(text.equals("填写）")){
						i=j;
						break;
					}
					if(text.equals("代码")
							|| text.equals("名 称")
							|| text.equals("（设立登记不")){
						continue;
					}else{
						nameBuf.append(text);
					}
				}
				enterprise.setName(nameBuf.toString());
			}
			// 邮政编码
			if(text.equals("邮政编码")){
				text = doc.getSections().get(0).getParagraphs().get(i+1).getText();
				enterprise.setZipcode(text);
			}			
			// 注册资本
			if(text.equals("出 资 额")){
				text = doc.getSections().get(0).getParagraphs().get(i+1).getText();
				enterprise.setCapital(text.replaceAll("万元（人民币）", ""));
			}
			// 住所
			if(text.equals("住 所")){
				text = doc.getSections().get(0).getParagraphs().get(i+1).getText();
				enterprise.setRegisterAddress(text);
			}
			// 经营范围
			if(text.equals("经营范围")){
				StringBuffer scopeBuf = new StringBuffer();
				for(int j=i-1;;j--){
					text = doc.getSections().get(0).getParagraphs().get(j).getText();
					if(text.equals("定填写）")){
						i=j;
						break;
					}
					if(text.equals("（根据《国民")
							|| text.equals("经济行业分")
							|| text.equals("类》、有关规")){
						continue;
					}else{
						scopeBuf.append(text);
					}
				}
				String scope = scopeBuf.toString();
				enterprise.setBusiness(scope.replace("一般项目：", ""));
			}
		}
		return enterprise;
	}
	
}
