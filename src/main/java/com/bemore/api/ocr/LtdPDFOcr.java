package com.bemore.api.ocr;

import com.bemore.api.entity.Enterprise;
import com.spire.doc.Document;
import com.spire.doc.Section;
import com.spire.doc.Table;
import com.spire.doc.TableRow;
import com.spire.doc.collections.RowCollection;
import com.spire.doc.collections.TableCollection;

public class LtdPDFOcr {

    public static Enterprise getBasicInfo(String path) {
        Document doc = new Document();
        doc.loadFromFile(path);

        Enterprise enterprise = new Enterprise();


        for (int i = doc.getSections().get(0).getParagraphs().getCount() - 1; i >= 0; i--) {
            String text = doc.getSections().get(0).getParagraphs().get(i).getText();
            System.out.println(i + ":" + text);
            // 名称
            if (text.equals("基本信息")) {
                text = doc.getSections().get(0).getParagraphs().get(i - 1).getText();
                enterprise.setName(text);
            }
            // 注册地
            if (text.startsWith("住所")) {
                text = doc.getSections().get(0).getParagraphs().get(i + 1).getText();
                enterprise.setRegisterAddress(text.trim());
            }

            // 邮政编码
            if (text.equals("邮政编码")) {
                text = doc.getSections().get(0).getParagraphs().get(i + 1).getText();
                enterprise.setZipcode(text);
            }
            // 注册资本
            if (text.equals("注册资本")) {
                text = doc.getSections().get(0).getParagraphs().get(i + 1).getText();
//                text = text.replaceAll("（万元）", "");
//                text = text.replaceAll("（币种： ", "");
                text = text.replaceAll(" .*$", "");
                enterprise.setCapital(text.trim());
            }
            // 经营范围
            if (text.startsWith("一般项目：")) {
                StringBuffer scopeBuffer = new StringBuffer();
                scopeBuffer.append(text);
                for (int j = i - 2; j > 0; j = j - 2) {
                    text = doc.getSections().get(0).getParagraphs().get(j).getText();
                    if (text.equals("经营范围")) {
                        continue;
                    } else if (text.endsWith(" 个")) {
                        break;
                    } else {
                        scopeBuffer.append(text);
                    }
                }
                String scope = scopeBuffer.toString();
                if (scope.contains("许可项目：")) {
                    String[] s = scope.split("许可项目：");
                    enterprise.setBusiness(s[0].replace("一般项目：", ""));
//					enterprise.setPermitBusiness(s[1]);
                } else {
                    enterprise.setBusiness(scope.replace("一般项目：", ""));
                }
            }
        }
        return enterprise;
    }
}
