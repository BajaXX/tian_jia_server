package com.bemore.api.util;

import com.bemore.api.constant.CommonConstants;
import com.bemore.api.entity.*;
import com.bemore.api.exception.WebException;
import com.spire.doc.*;
import com.spire.doc.Document;
import com.spire.doc.collections.CellCollection;
import com.spire.doc.collections.TableCollection;
import com.spire.doc.documents.*;
import com.spire.doc.documents.BreakType;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TextRange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class DocUtil {

    /**
     * 替换doc文件
     */
    public static HWPFDocument replaceDoc(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) throw new RuntimeException("找不到需要的模板文件！");
        FileInputStream in = null;
        HWPFDocument doc = null;
//        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            doc = new HWPFDocument(in);
            Range range = doc.getRange();
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                range.replaceText(key, value);
            }
//            out = new FileOutputStream("C:\\h2o\\garden\\result.doc");
//            doc.write(out);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                in.close();
//                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

    /**
     * 替换docx文件
     *
     * @param filePath 文件路径
     * @param map      其中，key--替换的标记    value--替换的值
     */
    public static XWPFDocument replaceDocx(String filePath, Map<String, String> map) {
        File file = new File(filePath);
        if (!file.exists()) throw new RuntimeException("找不到需要的模板文件！");
        FileInputStream in = null;
//        FileOutputStream out = null;
        try {
            in = new FileInputStream(file);
            XWPFDocument doc = new XWPFDocument(in);

            //处理段落
            List<XWPFParagraph> paragraphs = doc.getParagraphs();
            for (XWPFParagraph paragraph : paragraphs) {
                List<XWPFRun> runs = paragraph.getRuns();
                for (XWPFRun run : runs) {
                    String text = run.getText(0);
                    if (text != null) {
                        boolean isSetText = false;
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            String key = entry.getKey();
                            String value = entry.getValue();
                            if (text.indexOf(key) != -1) {
                                isSetText = true;
                                text = text.replaceAll(key, value);
                            }
                            if (isSetText) {
                                run.setText(text, 0);
                            }
                        }
                    }
                }
            }

            //处理表格
            List<XWPFTable> tables = doc.getTables();
            for (XWPFTable table : tables) {
                List<XWPFTableRow> rows = table.getRows();
                for (XWPFTableRow row : rows) {
                    List<XWPFTableCell> cells = row.getTableCells();
                    for (XWPFTableCell cell : cells) {
                        String text = cell.getText();
                        if (text != null) {
                            for (Map.Entry<String, String> entry : map.entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (text.equals(key)) {
                                    //删除原单元格值
                                    cell.removeParagraph(0);
                                    //设置新单元格的值
                                    cell.setText(value);
                                }
                            }
                        }
                    }
                }
            }

//            out = new FileOutputStream("C:\\h2o\\garden\\result.doc");
//            doc.write(out);
            return doc;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
//                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * @param filePath class path
     * @param params
     */
    public static HWPFDocument replaceAll(String filePath, Map<String, String> params) {
        try {
//            Resource resource = new DefaultResourceLoader().getResource("/opt/java/"+filePath);
//            log.info("模板文件:{}",resource.getFile().getAbsoluteFile());
            HWPFDocument doc = new HWPFDocument(new FileInputStream(new File("/opt/java/" + filePath)));
            Range range = doc.getRange();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                range.replaceText(key, value);
            }

            return doc;
        } catch (Exception e) {
            log.error("发生错误:", e);
        }
        return null;
    }

    public static Document replace(String filePath, Map<String, String> params) {
        log.info("filePath:{}", filePath);
        log.info("params:{}", params);
        try {
            Document document = new Document(filePath);
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue() == null ? "" : entry.getValue();
                document.replace(key, value, false, true);
            }
            return document;
        } catch (Exception e) {
            log.error("发生错误:", e);
        }

        return null;
    }

    public static Document replaceDocument(Document document, Map<String, String> params) {
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue() == null ? "" : entry.getValue();
            document.replace(key, value, false, true);
        }
        return document;

    }

    public static Document insertImg(Document document, String imagePath) {

        Section section = document.getSections().get(0);
        Table table = section.getTables().get(0);
        DocPicture docPicture = table.getRows().get(3).getCells().get(0).getParagraphs().get(0).appendPicture(imagePath);
        docPicture.setWidth(300f);
        docPicture.setHeight(100f);
        return document;
    }

    public static Document tableAddStock(Document document, List<Member> memberList) {
        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            for (int i = 0; i < memberList.size(); i++) {
                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 1);//获取表格每一行
                Member member = memberList.get(i);
                row.getCells().get(0).addParagraph().appendText(member.getName());
                row.getCells().get(1).addParagraph().appendText(Objects.isNull(member.getPutAmount()) ? "" : Util.formatDouble(member.getPutAmount()) + "万元");
                row.getCells().get(2).addParagraph().appendText(Objects.isNull(member.getPutType()) ? "" : member.getPutType());
                row.getCells().get(3).addParagraph().appendText(Objects.isNull(member.getPutDate()) ? "" : member.getPutDate());

            }
        }
        return document;
    }

    public static Document addStock1407G(Document document, List<Member> memberList) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            for (int i = 0; i < memberList.size(); i++) {
//                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 8);//获取表格每一行
                Member member = memberList.get(i);
                row.getCells().get(1).addParagraph().appendText(member.getName());
                row.getCells().get(2).addParagraph().appendText(member.getIdcard());
            }
        }
        return document;
    }

    public static Document addStock2406G(Document document, List<Member> memberList, String capital) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            for (int i = 0; i < memberList.size(); i++) {
                TableRow row = table.getRows().get(i + 1).deepClone();//获取表格每一行
                Member member = memberList.get(i);
//                table.addRow();//默认在表格最下方插入一行
                row.getCells().get(0).getParagraphs().get(0).setText(member.getName());
                row.getCells().get(1).getParagraphs().get(0).setText(member.getCountry());
                row.getCells().get(2).getParagraphs().get(0).setText(member.getType());
                row.getCells().get(3).getParagraphs().get(0).setText(member.getIdcard());
                row.getCells().get(4).getParagraphs().get(0).setText(Util.formatDouble(member.getPutAmount()));
                row.getCells().get(5).getParagraphs().get(0).setText(Util.formatDouble(member.getRealPutAmount()));
                row.getCells().get(6).getParagraphs().get(0).setText(Util.formatDate(member.getPutDate(),"yyyy-MM-dd","yyyy年MM月dd日"));
                row.getCells().get(7).getParagraphs().get(0).setText(member.getPutType());
                table.getRows().insert(i + 1, row);
//                row.getCells().get(8).getParagraphs().get(0).setText(Util.formatDouble(Double.valueOf(member.getPutAmount()) / Double.valueOf(capital) * 100)+"%");
            }
        }
        return document;
    }
    public static Document addSupportInfo(Document document,EnterpriseSupportLog enterpriseSupportLog,int downDate ) {
        // 将downDate转换为字符串
        String downDateStr = String.valueOf(downDate);

        // 提取年份部分（前4位）
        String year = downDateStr.substring(0, 4);

        // 提取月份部分（后2位）
        String month = downDateStr.substring(4, 6);
        document.replace("BankAccount", enterpriseSupportLog.getBankAccount(), false, true);
        document.replace("DepositBank", enterpriseSupportLog.getDepositBank(), false, true);
        document.replace("support_areas", enterpriseSupportLog.getSupportAreas(), false, true);
        document.replace("support_month_amount", String.format("%.1f", enterpriseSupportLog.getMonthAmount() / 10000), false, true);
        document.replace("support_year", year, false, true);
        document.replace("support_month", month, false, true);

        return document;
    }
    public static Document addStock2007G(Document document, Person master,PersonLog oldMaster,Enterprise enterprise,List<Member> memberList) {

        if(master.getName().equals(oldMaster.getName())){
            document.replace("oldMasterBox", "", false, true);
        }else{
            document.replace("oldMasterBox", "，原合伙人不再担任"+enterprise.getName()+"的执行事务合伙人", false, true);
        }

        //新合伙人签字区域
        TextSelection textSelection = document.findString("newStockSignBox", true, true);
        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();

        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                paragraph.appendText("        " + member.getName() + "盖章");
                paragraph.appendText("\n\n");
                paragraph.appendText("        法人代表人（签字）：");
                TextRange t = paragraph.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph.appendText("\n\n\n\n");
            } else {
                paragraph.appendText("        " + member.getName() + "签字：");
                TextRange t = paragraph.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph.appendText("\n\n\n\n");
            }
        }
        textSelection.getSelectedText();
        document.replace("newStockSignBox", textSelection, false, true);
        document.replace("newStockSignBox", "", false, true);

        return document;
    }

    public static Document addInfo2003G(Document document, List<Member> memberList, List<MemberLog> oldMemberList, Person master, PersonLog oldMaster, String oldCapital) {
        oldMemberList = oldMemberList.stream().filter(t -> t.getIsStock() == 1 && t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());
        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

        if (!oldMemberList.isEmpty()) {
            String info = "";
            for (int i = 0; i < oldMemberList.size(); i++) {
                MemberLog ol = oldMemberList.get(i);
                info += ol.getName() + "出资人民币 " + Util.formatDouble(ol.getPutAmount()) + " 万元，占 " + (Util.formatDouble( Double.valueOf(ol.getPutAmount()) / Double.valueOf(oldCapital) * 100) + "%") + (ol.getPartnerType() != null ? "(" + (ol.getPartnerType() == 1 ? "普通合伙" : "有限合伙") : "") + ")";
                if (i < oldMemberList.size() - 1) info += ";\n      ";
            }
            document.replace("changeStockBefore", info, false, true);
        } else {
            document.replace("changeStockBefore", "    ", false, true);
        }

        //获取退出股东
        List<MemberLog> oldMember = new ArrayList<>();
        oldMember.addAll(oldMemberList);


        for (Member member : memberList) {
            for (MemberLog log : oldMemberList) {
                if (member.getIdcard().equals(log.getIdcard())) {
                    oldMember.remove(log);
                }
            }
        }
        if (!oldMember.isEmpty() && oldMember.size() > 0) {
            String[] exitstock = oldMember.stream().map(t -> t.getName()).toArray(String[]::new);
            document.replace("exitStock", StringUtils.join(exitstock, "、"), false, true);
        }

        if (!memberList.isEmpty()) {
            String info = "";
            for (int i = 0; i < memberList.size(); i++) {
                Member ol = memberList.get(i);
                info += ol.getName() + "出资人民币 " + Util.formatDouble(ol.getPutAmount()) + " 万元，占 " + (Util.formatDouble(Double.valueOf(ol.getPutAmount()) / Double.valueOf(oldCapital) * 100) + "%") + "(" + (ol.getPartnerType() == 1 ? "普通合伙" : "有限合伙") + ")";
                if (i < memberList.size() - 1) info += ";\n      ";
            }
            document.replace("changeStockAfter", info, false, true);
        } else {
            document.replace("changeStockAfter", "    ", false, true);
        }

        if (master.getIdcard().equals(oldMaster.getIdcard())) {
            document.replace("changeMasterInfo", "合伙人变更后，企业执行事务合伙人不变。", false, true);
        } else {
            document.replace("changeMasterInfo", "合伙人变更后委托" + master.getName() + "担任执行事务合伙人;", false, true);
        }

        //新合伙人签字区域
        TextSelection textSelection = document.findString("newStockSignBox", true, true);
        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();

        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                paragraph.appendText("        " + member.getName() + "盖章");
                paragraph.appendText("\n\n");
                paragraph.appendText("        法人代表人（签字）：");
                TextRange t = paragraph.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph.appendText("\n\n\n\n");
            } else {
                paragraph.appendText("        " + member.getName() + "签字：");
                TextRange t = paragraph.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph.appendText("\n\n\n\n");
            }
        }
        textSelection.getSelectedText();
        document.replace("newStockSignBox", textSelection, false, true);
        document.replace("newStockSignBox", "", false, true);


        //老合伙人签字区域
        TextSelection textSelection1 = document.findString("oldStockSignBox", true, true);
        Paragraph paragraph1 = textSelection1.getAsOneRange().getOwnerParagraph();

        for (int i = 0; i < oldMemberList.size(); i++) {
            MemberLog member = oldMemberList.get(i);
            if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                paragraph1.appendText("        " + member.getName() + "盖章");
                paragraph1.appendText("\n\n");
                paragraph1.appendText("        法人代表人（签字）：");
                TextRange t = paragraph1.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph1.appendText("\n\n\n\n");
            } else {
                paragraph1.appendText("        " + member.getName() + "签字：");
                TextRange t = paragraph1.appendText("                ");
                t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                paragraph1.appendText("\n\n\n\n");
            }
        }
        textSelection1.getSelectedText();
        document.replace("oldStockSignBox", textSelection1, false, true);
        document.replace("oldStockSignBox", "", false, true);



        return document;
    }

    public static Document addStock1408G(Document document, List<Member> memberList) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            for (int i = 0; i < memberList.size(); i++) {
//                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 6);//获取表格每一行
                Member member = memberList.get(i);
                row.getCells().get(1).addParagraph().appendText(member.getName());
                row.getCells().get(2).addParagraph().appendText(member.getPutType());
                row.getCells().get(3).addParagraph().appendText(Util.formatDouble(member.getPutAmount()));
                row.getCells().get(4).addParagraph().appendText(Util.formatDouble(member.getRealPutAmount()));
            }
        }
        return document;
    }

    public static Document addStock1208G(Document document, List<Member> memberList, String imagePath) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            for (int i = 0; i < memberList.size(); i++) {
//                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 10);//获取表格每一行
                Member member = memberList.get(i);
                row.getCells().get(1).addParagraph().appendText(member.getName());
                row.getCells().get(2).addParagraph().appendText(member.getIdcard());
            }

            Table table4 = tableList.get(3);
            TableRow row4 = table4.getRows().get(4);
            DocPicture docPicture = row4.getCells().get(0).getParagraphs().get(0).appendPicture(imagePath);
            docPicture.setWidth(300f);
            docPicture.setHeight(100f);


        }
        return document;
    }

    public static Document addPic1119G(Document document, String imagePath) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);

            TableRow row4 = table.getRows().get(3);
            DocPicture docPicture = row4.getCells().get(0).getParagraphs().get(0).appendPicture(imagePath);
            docPicture.setWidth(300f);
            docPicture.setHeight(100f);
        }
        return document;
    }

    public static Document addStockList(Document document, List<Member> memberList) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);
            memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());
            for (int i = 0; i < memberList.size(); i++) {
                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 1);//获取表格每一行
                Member member = memberList.get(i);
                row.getCells().get(0).addParagraph().setText(member.getName());
                row.getCells().get(1).addParagraph().setText(member.getCountry());
                row.getCells().get(2).addParagraph().setText(member.getAddress());
                row.getCells().get(3).addParagraph().setText(member.getType() + member.getIdcard());
                String partnerType = "";
                if (member.getPartnerType() == 1) {
                    partnerType = "无限责任";
                }
                if (member.getPartnerType() == 2) {
                    partnerType = "有限责任";
                }
                row.getCells().get(4).addParagraph().setText(partnerType);
                row.getCells().get(5).addParagraph().setText(member.getPutType());
//                row.getCells().get(6).addParagraph().appendText(member.getCountry());
                row.getCells().get(7).addParagraph().setText(Util.formatDouble(member.getPutAmount()));
                row.getCells().get(8).addParagraph().setText(member.getPutDate());
            }

        }
        return document;
    }

    public static Document generateFor2011G(Document document, List<TransferLog> transferLogList, List<Member> memberList, List<MemberLog> oldMemberList, String capital) {
        Document newDocument = new Document();
        for (TransferLog transferLog : transferLogList) {
            Document baseDocument = new Document(document);
            MemberLog oldMember = oldMemberList.stream().filter(t -> t.getIdcard().equals(transferLog.getOldStockIdcard())).findFirst().orElse(null);
            Member newMember = memberList.stream().filter(t -> t.getId().equals(transferLog.getNewStock())).findFirst().orElse(null);

            if (Objects.isNull(oldMember) || Objects.isNull(newMember)) throw new WebException(103, "数据错误");
            if (Objects.isNull(newMember.getAddress()) || Objects.isNull(oldMember.getAddress()))
                throw new WebException(103, "转让人或受让人的住址信息不完整");

            baseDocument.replace("transferor", oldMember.getName(), false, true);
            baseDocument.replace("transferee", newMember.getName(), false, true);
            baseDocument.replace("oldStockAddress", oldMember.getAddress(), false, true);
            baseDocument.replace("newStockAddress", newMember.getAddress(), false, true);
            baseDocument.replace("transferorAmount", Util.formatDouble(oldMember.getPutAmount()), false, true);
            baseDocument.replace("oldPutRate", Util.formatDouble( Double.valueOf(oldMember.getPutAmount()) / Double.valueOf(capital) * 100), false, true);
            baseDocument.replace("amountPutRate", Util.formatDouble( transferLog.getAmount() / Double.valueOf(capital) * 100), false, true);
            baseDocument.replace("transAmount", String.valueOf(transferLog.getTransAmount()), false, true);
            baseDocument.replace("amount", String.valueOf(transferLog.getAmount()), false, true);


            newDocument.addSection();
            for (int i = 0; i < baseDocument.getSections().getCount(); i++) {
                Section section = baseDocument.getSections().get(i);

                for (int j = 0; j < section.getBody().getChildObjects().getCount(); j++) {
                    Object object = section.getBody().getChildObjects().get(j);

                    //复制文档1中的正文内容添加到文档2
                    newDocument.getSections().get(0).getBody().getChildObjects().add(((DocumentObject) object).deepClone());
                }

            }
            Paragraph paragraph = newDocument.getLastParagraph();
            if (!Objects.isNull(paragraph)) paragraph.appendBreak(BreakType.Page_Break);
        }

        return newDocument;
    }

    public static Document generateFor2011GNew(Document document, List<TransferLog> transferLogList, List<Member> memberList, List<MemberLog> oldMemberList, String capital) {

        if (transferLogList.isEmpty()) {
            throw new WebException(101, "该公司没有股权变更信息");
        }

        Map<String, String> transferMap = new HashMap<>();
        List<String> transfers = new ArrayList<>();
        List<String> transferees = new ArrayList<>();
        int index = 0;
        String transferorList = "";
        String transfereeList = "";
        String transferDetails = "";
        List<String> transferDetailsList = new ArrayList<>();

        List<String> oldStockMemberAmount = new ArrayList<>();
        String oldStockMemberAmountStr = "";

        StringBuffer signBox = new StringBuffer();


        //出让人信息
        for (int i = 0; i < transferLogList.size(); i++) {
            TransferLog transferLog = transferLogList.get(i);
            if (Objects.isNull(transferMap.get(transferLog.getOldStock()))) {
                transferMap.put(transferLog.getOldStock(), Util.getIndexTag(index));
                MemberLog oldMember = oldMemberList.stream().filter(t -> t.getIdcard().equals(transferLog.getOldStockIdcard())).findFirst().orElse(null);
                Member newMember = memberList.stream().filter(t -> t.getIdcard().equals(transferLog.getOldStockIdcard())).findFirst().orElse(null);
                if (Objects.isNull(oldMember)) throw new WebException(103, "数据错误");
                if (Objects.isNull(newMember.getAddress()))
                    throw new WebException(103, "转让人" + transferLog.getOldStockName() + "的住址信息不完整");
                transferorList += (i != 0 ? "\t" : "") + transferLog.getOldStockName() + ",以下简称(" + Util.getIndexTag(index) + "方),住所:" + newMember.getAddress() + "；\n";

                oldStockMemberAmount.add(Util.getIndexTag(index) + "方出资" + Util.formatDouble(oldMember.getPutAmount()) + "万元人民币,占" + Util.formatDouble( Double.valueOf(oldMember.getPutAmount()) / Double.valueOf(capital) * 100) + "%");
                if (newMember.getMemberType() != null && !newMember.getMemberType().contains("自然人") && !newMember.getMemberType().contains("其他")) {

                    signBox.append("\t" + Util.getIndexTag(index) + "方   （" + transferLog.getOldStockName() + "盖章）\n\n");
                    signBox.append("\t法人代表人（签字）：_____________\n\n\n\n");

                } else {
                    signBox.append("\t" + Util.getIndexTag(index) + "方   （" + transferLog.getOldStockName() + "）：_____________\n\n\n\n");
                }
                transfers.add(Util.getIndexTag(index) + "方");
                index++;
            }
        }

        //受让人信息
        for (int i = 0; i < transferLogList.size(); i++) {
            TransferLog transferLog = transferLogList.get(i);
            if (Objects.isNull(transferMap.get(transferLog.getNewStock()))) {
                transferMap.put(transferLog.getNewStock(), Util.getIndexTag(index));
                Member newMember = memberList.stream().filter(t -> t.getId().equals(transferLog.getNewStock())).findFirst().orElse(null);
                if (Objects.isNull(newMember)) throw new WebException(103, "数据错误");
                if (Objects.isNull(newMember.getAddress()))
                    throw new WebException(103, "受让人" + transferLog.getNewStockName() + "的住址信息不完整");
                transfereeList += (i != 0 ? "\t" : "") + transferLog.getNewStockName() + ",以下简称(" + Util.getIndexTag(index) + "方),住所:" + newMember.getAddress() + "；\n";

                if (newMember.getMemberType() != null && !newMember.getMemberType().contains("自然人") && !newMember.getMemberType().contains("其他")) {

                    signBox.append("\t" + Util.getIndexTag(index) + "方   （" + transferLog.getNewStockName() + "盖章）\n\n");
                    signBox.append("\t法人代表人（签字）：_____________\n\n\n\n");

                } else {
                    signBox.append("\t" + Util.getIndexTag(index) + "方   （" + transferLog.getNewStockName() + "）：_____________\n\n\n\n");
                }
                transferees.add(Util.getIndexTag(index) + "方");
                index++;
            }
        }

//        for (MemberLog oldMember : oldMemberList) {
//            oldStockMemberAmount.add(transferMap.get(oldMember.getId()) + "方出资" + oldMember.getPutAmount() + "万元人民币,占" + String.valueOf(Double.valueOf(oldMember.getPutAmount()) / Double.valueOf(capital) * 100) + "%");
//        }
        if (oldStockMemberAmount.size() > 0) {
            oldStockMemberAmountStr = StringUtils.join(oldStockMemberAmount, "；");
        }
        oldStockMemberAmountStr += "。";


        for (TransferLog transferLog : transferLogList) {
            transferDetailsList.add(transferMap.get(transferLog.getOldStock()) + "方将其所持有标的公司" + Util.formatDouble( Double.valueOf(transferLog.getAmount()) / Double.valueOf(capital) * 100) + "%（原出资额" + transferLog.getAmount() + "万元）的股权作价" + transferLog.getTransAmount() + "元人民币转让给" + transferMap.get(transferLog.getNewStock()) + "方");
        }
        if (transferDetailsList.size() > 0) {
            transferDetails = StringUtils.join(transferDetailsList, "；");
        }
        transferDetails += "。";

        String promiseStr = StringUtils.join(transfers, "、") + "保证本合同第一条转让给" + StringUtils.join(transferees, "、") + "的股权为" + StringUtils.join(transfers, "、") + "合法拥有，" + StringUtils.join(transfers, "、") + "拥有完全、有效的处分权。" + StringUtils.join(transfers, "、") + "保证其所转让的股权没有设置任何质押或其他担保权，不受任何第三人的追索。";


        document.replace("transferorList", transferorList, false, true);
        document.replace("transfereeList", transfereeList, false, true);
        document.replace("transferDetails", transferDetails, false, true);
        document.replace("oldStockMemberAmount", oldStockMemberAmountStr, false, true);
        document.replace("OldStockSignBox", signBox.toString(), false, true);
        document.replace("PromiseStr", promiseStr, false, true);
        document.replace("contractNum", Util.numberToChinese(index + 1), false, true);


        return document;
    }

    public static Document generateFor2416G(Document document, List<TransferLog> transferLogList, List<Member> memberList, List<MemberLog> oldMemberList) {
        Document newDocument = new Document();
        for (MemberLog om : oldMemberList
        ) {

            for (TransferLog transferLog : transferLogList) {
                Document baseDocument = new Document(document);
                MemberLog oldMember = oldMemberList.stream().filter(t -> t.getIdcard().equals(transferLog.getOldStockIdcard())).findFirst().orElse(null);
                Member newMember = memberList.stream().filter(t -> t.getId().equals(transferLog.getNewStock())).findFirst().orElse(null);


                if (Objects.isNull(oldMember) || Objects.isNull(newMember)) throw new WebException(103, "数据错误");
                if (oldMember.getName().equals(om.getName()) || newMember.getName().equals(om.getName())) continue;

                baseDocument.replace("transferor", oldMember.getName(), false, true);
                baseDocument.replace("transferee", newMember.getName(), false, true);
                baseDocument.replace("amount", String.valueOf(transferLog.getAmount()), false, true);
                baseDocument.replace("otherStock", om.getName(), false, true);


                newDocument.addSection();
                for (int i = 0; i < baseDocument.getSections().getCount(); i++) {
                    Section section = baseDocument.getSections().get(i);

                    for (int j = 0; j < section.getBody().getChildObjects().getCount(); j++) {
                        Object object = section.getBody().getChildObjects().get(j);

                        //复制文档1中的正文内容添加到文档2
                        newDocument.getSections().get(0).getBody().getChildObjects().add(((DocumentObject) object).deepClone());
                    }

                }
                Paragraph paragraph = newDocument.getLastParagraph();
                if (!Objects.isNull(paragraph)) paragraph.appendBreak(BreakType.Page_Break);
            }
        }

        return newDocument;
    }

    public static Document addStockList1(Document document, List<Member> memberList, Enterprise enterprise) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
            Table table = tableList.get(0);
            memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

            for (int i = 0; i < memberList.size(); i++) {
                table.addRow();//默认在表格最下方插入一行
                TableRow row = table.getRows().get(i + 1);//获取表格每一行
                Member member = memberList.get(i);
                if (Objects.isNull(member.getPutAmount())) throw new WebException(102, "有股东出资额未填写");
                if (Objects.isNull(member.getPutType())) throw new WebException(102, "有股东出资方式未填写");
                if (Objects.isNull(enterprise.getCapital())) throw new WebException(103, "公司注册资本未填写");
                row.getCells().get(0).addParagraph().setText(member.getName());
                row.getCells().get(1).addParagraph().setText("出资" + Util.formatDouble(member.getPutAmount()) + "万元");
                row.getCells().get(2).addParagraph().setText("占" + Util.formatDouble( Double.valueOf(member.getPutAmount()) * 100 / Double.valueOf(enterprise.getCapital())) + "%");
                row.getCells().get(3).addParagraph().setText("出资方式：" + member.getPutType());
                String partnerType = "";
                if (member.getPartnerType() == 1) {
                    partnerType = "普通合伙人";
                }
                if (member.getPartnerType() == 2) {
                    partnerType = "有限合伙人";
                }
                row.getCells().get(4).addParagraph().setText("(" + partnerType + ")");
            }
            if (table.getRows().getCount() > 1) table.getRows().removeAt(0);

        }
        return document;
    }

    public static Document addStockList2(Document document, List<Member> memberList, Enterprise enterprise) {

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();


        if (tableList.getCount() > 1) {
            memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

            for (int i = 0; i < memberList.size(); i++) {
                Table table0 = tableList.get(0);
                Member member = memberList.get(i);
                if (Objects.isNull(member.getPutAmount())) throw new WebException(102, "有股东出资额未填写");
                if (Objects.isNull(member.getPutType())) throw new WebException(102, "有股东出资方式未填写");
                if (Objects.isNull(enterprise.getCapital())) throw new WebException(103, "公司注册资本未填写");
                TableRow row0 = table0.getRows().get(i).deepClone();
                String partnerType = "";
                if (member.getPartnerType() == null) throw new WebException(104, "合伙人类型未填写");
                if (member.getPartnerType() == 1) {
                    partnerType = "普通合伙人";
                }
                if (member.getPartnerType() == 2) {
                    partnerType = "有限合伙人";
                }

                row0.getCells().get(0).getParagraphs().get(0).setText(partnerType);
                row0.getCells().get(1).getParagraphs().get(0).setText(member.getName());
                row0.getCells().get(2).getParagraphs().get(0).setText(member.getAddress());
                table0.getRows().insert(i + 1, row0);

                Table table1 = tableList.get(1);
                TableRow row1 = table1.getRows().get(i).deepClone();

                row1.getCells().get(0).getParagraphs().get(0).setText(member.getName());
                row1.getCells().get(1).getParagraphs().get(0).setText(member.getPutType());
                row1.getCells().get(2).getParagraphs().get(0).setText(Util.formatDouble(member.getPutAmount()) + "万元");
                row1.getCells().get(3).getParagraphs().get(0).setText(Util.formatDouble( Double.valueOf(member.getPutAmount()) * 100 / Double.valueOf(enterprise.getCapital())) + "%");
                row1.getCells().get(4).getParagraphs().get(0).setText(member.getPutDate());
                table1.getRows().insert(i + 1, row1);

            }

        }
        return document;
    }

    public static Document addStockList2419G(Document document, List<Member> memberList, List<MemberLog> oldMemberList) {
        //获取新股东
        List<Member> newMember = new ArrayList<>();
        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());
        newMember.addAll(memberList);

        for (Member member : memberList) {
            for (MemberLog log : oldMemberList) {
                if (member.getIdcard() == null) throw new WebException(101, "新股东身份证信息不完整。");
                if (member.getIdcard().equals(log.getIdcard())) {
                    newMember.remove(member);
                }
            }
        }

        if (newMember.size() == 0) throw new WebException(103, "没有新入伙的合伙人");


        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 0) {
//            newMember = newMember.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

            Table table = tableList.get(0);
            for (int i = 0; i < newMember.size(); i++) {
                Member member = newMember.get(i);
                if (Objects.isNull(member.getPutAmount())) throw new WebException(102, "有股东出资额未填写");
                if (Objects.isNull(member.getPutType())) throw new WebException(102, "有股东出资方式未填写");

                if (i == 0) {
                    TableRow row0 = table.getRows().get(i);
                    TableRow row1 = table.getRows().get(i + 1);
                    TableRow row2 = table.getRows().get(i + 2);
                    TableRow row3 = table.getRows().get(i + 3);

                    row0.getCells().get(1).getParagraphs().get(0).setText(member.getName());
                    if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                        row1.getCells().get(0).getParagraphs().get(0).setText("社会信用代码：");
                    } else {
                        row1.getCells().get(0).getParagraphs().get(0).setText("身份证：");
                    }
                    row1.getCells().get(1).getParagraphs().get(0).setText(member.getIdcard());
                    row2.getCells().get(1).getParagraphs().get(0).setText(member.getAddress());
                    row3.getCells().get(1).getParagraphs().get(0).setText(Util.formatDouble(member.getPutAmount()) + "万元");
                    row3.getCells().get(3).getParagraphs().get(0).setText(member.getPutType());
                } else {
                    TableRow row0 = table.getRows().get(0).deepClone();
                    TableRow row1 = table.getRows().get(1).deepClone();
                    TableRow row2 = table.getRows().get(2).deepClone();
                    TableRow row3 = table.getRows().get(3).deepClone();

                    row0.getCells().get(1).getParagraphs().get(0).setText(member.getName());
                    if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                        row1.getCells().get(0).getParagraphs().get(0).setText("社会信用代码：");
                    } else {
                        row1.getCells().get(0).getParagraphs().get(0).setText("身份证：");
                    }
                    row1.getCells().get(1).getParagraphs().get(0).setText(member.getIdcard());
                    row2.getCells().get(1).getParagraphs().get(0).setText(member.getAddress());
                    row3.getCells().get(1).getParagraphs().get(0).setText(Util.formatDouble(member.getPutAmount()) + "万元");
                    row3.getCells().get(3).getParagraphs().get(0).setText(member.getPutType());

                    table.getRows().insert(i * 4 + 0, row0);
                    table.getRows().insert(i * 4 + 1, row1);
                    table.getRows().insert(i * 4 + 2, row2);
                    table.getRows().insert(i * 4 + 3, row3);
                }

            }


            document.replace("newStockNum", Util.numberToChinese(newMember.size() + 1), true, true);

            //新合伙人签字区域
            TextSelection textSelection = document.findString("newStockSignBox", true, true);
            Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();

            for (int i = 0; i < newMember.size(); i++) {
                Member member = newMember.get(i);
                if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                    paragraph.appendText("        " + member.getName() + "盖章");
                    paragraph.appendText("\n\n");
                    paragraph.appendText("        法人代表人（签字）：");
                    TextRange t = paragraph.appendText("                ");
                    t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                    paragraph.appendText("\n\n\n\n");
                } else {
                    paragraph.appendText("        " + member.getName() + "签字：");
                    TextRange t = paragraph.appendText("                ");
                    t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                    paragraph.appendText("\n\n\n\n");
                }
            }
            textSelection.getSelectedText();
            document.replace("newStockSignBox", textSelection, false, true);
            document.replace("newStockSignBox", "", false, true);


            //老合伙人签字区域
            TextSelection textSelection1 = document.findString("oldStockSignBox", true, true);
            Paragraph paragraph1 = textSelection1.getAsOneRange().getOwnerParagraph();

            for (int i = 0; i < oldMemberList.size(); i++) {
                MemberLog member = oldMemberList.get(i);
                if (member.getMemberType() != null && !member.getMemberType().contains("自然人") && !member.getMemberType().contains("其他")) {
                    paragraph1.appendText("        " + member.getName() + "盖章");
                    paragraph1.appendText("\n\n");
                    paragraph1.appendText("        法人代表人（签字）：");
                    TextRange t = paragraph1.appendText("                ");
                    t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                    paragraph1.appendText("\n\n\n\n");
                } else {
                    paragraph1.appendText("        " + member.getName() + "签字：");
                    TextRange t = paragraph1.appendText("                ");
                    t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
                    paragraph1.appendText("\n\n\n\n");
                }
            }
            textSelection1.getSelectedText();
            document.replace("oldStockSignBox", textSelection1, false, true);
            document.replace("oldStockSignBox", "", false, true);

        }
        return document;
    }

    public static Document addStockSign(Document document, List<Member> memberList) {

        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

        TextSelection textSelection = document.findString("StockSignBox", true, true);
        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();

        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);

            paragraph.appendText("        " + member.getName() + " 签字：_____________\n\n\n\n");
//            TextRange t = paragraph.appendText("                ");
//            t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
//            paragraph.appendText("\n\n\n\n");
        }
        textSelection.getSelectedText();
        document.replace("StockSignBox", textSelection, false, true);
        document.replace("StockSignBox", "", false, true);
        return document;
    }

    public static Document addOldStockSign(Document document, List<MemberLog> memberList) {

        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

//        TextSelection textSelection = document.findString("OldStockSignBox", true, true);

//        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < memberList.size(); i++) {
            MemberLog member = memberList.get(i);

//            paragraph.appendText("\t" + member.getName() + "：");
//            TextRange t = paragraph.appendText("                ");
//            t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
//            paragraph.appendText("\n\n\n\n");
            str.append("\t" + member.getName() + "：_____________\n\n\n\n");

        }
//        textSelection.getSelectedText();
        document.replace("OldStockSignBox", str.toString(), false, true);
        document.replace("OldStockSignBox", "", false, true);
        return document;
    }

    public static Document addChange2009G(Document document, Enterprise enterprise, EnterpriseLog oldEnterprise, List<TransferLog> transferLogList, List<TransferLog> incrAndDecrList, List<Member> memberList, List<MemberLog> oldMemberList, Project project) {

        List<String> changes = new ArrayList<>();

        if (!enterprise.getBusiness().equals(oldEnterprise.getBusiness())) {
            changes.add("决定变更公司经营范围，变更后的经营范围：" + enterprise.getBusiness());
        }
        if (!enterprise.getRegisterAddress().equals(oldEnterprise.getRegisterAddress())) {
            changes.add("决定变更公司注册地址，变更后的注册地址：" + enterprise.getRegisterAddress());
        }

        if (!enterprise.getType().equals(oldEnterprise.getType())) {
            changes.add("决定企业类型由" + oldEnterprise.getType() + "变更为" + enterprise.getType());
        }
//        if (!enterprise.getCapital().equals(oldEnterprise.getCapital())) {
//            changes.add("决定变更公司注册资本，变更后的注册资本：" + enterprise.getCapital());
//        }
        if (!enterprise.getName().equals(oldEnterprise.getName())) {
            changes.add("决定企业名称由" + oldEnterprise.getName() + "变更为" + enterprise.getName());
        }
        double capital = Double.valueOf(enterprise.getCapital());
//        List<String> newStocks=new ArrayList<>();
//        memberList.stream().map(t->{
//            String s=oldMemberList.stream().filter(x->t.getName().equals(x.getName())).findFirst().orElse(null).getName();
//            if(!s.isEmpty() && !s.equals("")) newStocks.add(s);
//        });

        List<String> list1 = memberList.stream().map(Member::getName).collect(Collectors.toList());
        List<String> list2 = oldMemberList.stream().map(MemberLog::getName).collect(Collectors.toList());

        List<String> reduce1 = list1.stream().filter(item -> !list2.contains(item)).collect(Collectors.toList());

        if (!reduce1.isEmpty()) {
            changes.add("同意吸纳" + reduce1.stream().map(String::toString).collect(Collectors.joining("、")) + "为公司新股东");
        }

        if (!incrAndDecrList.isEmpty()) {
            double incrCount = incrAndDecrList.stream().mapToDouble(TransferLog::getAmount).sum();
            String str = "注册资本由" + Util.formatDouble(oldEnterprise.getCapital()) + "万元" + (incrCount >= 0 ? "增" : "减") + "至" + Util.formatDouble(String.valueOf(capital)) + "万元。其中:\n";
            for (TransferLog t : incrAndDecrList) {
                str += "\t" + t.getOldStockName() + "本次认缴" + Util.formatDouble(String.valueOf(t.getAmount())) + "万元，出资方式货币；\n";
            }

            changes.add(str);
        }

        if (!transferLogList.isEmpty()) {
            for (TransferLog log : transferLogList) {
                changes.add("同意" + log.getNewStockName() + "受让" + log.getOldStockName() + "持有的本公司" + Util.formatDouble( log.getAmount() / Double.valueOf(enterprise.getCapital()) * 100) + "%（原出资额" + Util.formatDouble(String.valueOf(log.getAmount())) + "万）的股权;");
            }

        }
        if (!transferLogList.isEmpty() || !incrAndDecrList.isEmpty()) {

            String stockInfo = "";
            if (!transferLogList.isEmpty() && !incrAndDecrList.isEmpty()) {
                stockInfo += "增资股权转让后，股东的出资额和持股比例如下：\n";
            } else if (!transferLogList.isEmpty()) {
                stockInfo += "股权转让后，股东的出资额和持股比例如下：\n";
            } else if (!incrAndDecrList.isEmpty()) {
                stockInfo += "增资后，股东的出资额和持股比例如下：\n";
            }

            for (Member member : memberList) {
                stockInfo += "\t" + member.getName() + ",出资额" + Util.formatDouble(member.getPutAmount()) + "万元人民币,占" + Util.formatDouble( Double.valueOf(member.getPutAmount()) / Double.valueOf(capital) * 100) + "%;出资方式：货币；\n";
            }
            changes.add(stockInfo);
        }

        StringBuffer result = new StringBuffer();
        int i = 0;
        String[] zh = {"一、", "二、", "三、", "四、", "五、", "六、", "七、", "八、", "九、", "十、", "十一、", "十二、", "十三、"};


        for (String str : changes) {
            if ("".equals(str)) continue;
            result.append("\t" + zh[i] + str + "\n");
            i++;
        }

        if (Objects.isNull(project) || Objects.isNull(project.getEnterpriseArticle()))
            throw new WebException(104, "未选择企业章程类型");

        if (project.getEnterpriseArticle().equals("章程修正案")) {
            result.append("\t" + zh[i] + "通过公司章程修正案。\n");
        } else {
            result.append("\t" + zh[i] + "通过公司新章程。\n");
        }

        document.replace("ChangesBox", result.toString(), false, true);


        return document;
    }

    public static Document addChange2400G(Document document, Enterprise enterprise, EnterpriseLog oldEnterprise, Person master, PersonLog oldMaster, List<Member> memberList, List<MemberLog> oldMemberList) {
        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

        Map<String, String> params = new HashMap<>();

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() > 2) {
            Table table = tableList.get(1);
            int i = 2;


            if (!enterprise.getBusiness().equals(oldEnterprise.getBusiness())) {
                TableRow row = table.getRows().get(1).deepClone();
                params.put("old_Business", oldEnterprise.getBusiness());
                params.put("new_Business", enterprise.getBusiness());

                row.getCells().get(0).getParagraphs().get(0).setText("经营范围");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getBusiness());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getBusiness());

                row.setHeightType(TableRowHeightType.Auto);

                table.getRows().insert(i, row);
                i++;


            } else {
                params.put("old_Business", "");
                params.put("new_Business", "");
            }

            if (!enterprise.getRegisterAddress().equals(oldEnterprise.getRegisterAddress())) {
                params.put("old_RegisterAddress", oldEnterprise.getRegisterAddress());
                params.put("new_RegisterAddress", enterprise.getRegisterAddress());

                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("注册地址");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getRegisterAddress());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getRegisterAddress());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("old_RegisterAddress", "");
                params.put("new_RegisterAddress", "");
            }

            if (Double.valueOf(enterprise.getCapital()).compareTo(Double.valueOf(oldEnterprise.getCapital())) != 0) {
                params.put("old_capital", Util.formatDouble(oldEnterprise.getCapital()));
                params.put("new_capital", Util.formatDouble(enterprise.getCapital()));
                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("注册资本");
                row.getCells().get(1).getParagraphs().get(0).setText(Util.formatDouble(oldEnterprise.getCapital()) + "万元");
                row.getCells().get(2).getParagraphs().get(0).setText(Util.formatDouble(enterprise.getCapital()) + "万元");

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("old_capital", "");
                params.put("new_capital", "");
            }

            if (!enterprise.getName().equals(oldEnterprise.getName())) {
                params.put("oldEnterpriseName", oldEnterprise.getName());
                params.put("newEnterpriseName", enterprise.getName());
                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("公司名称");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getName());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getName());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldEnterpriseName", "");
                params.put("newEnterpriseName", "");
            }

            if (!enterprise.getType().equals(oldEnterprise.getType())) {
                params.put("oldType", oldEnterprise.getType());
                params.put("newType", enterprise.getType());
                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("公司类型");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getType());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getType());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldType", "");
                params.put("newType", "");
            }

            if (!master.getName().equals(oldMaster.getName())) {
                params.put("oldMaster", oldMaster.getName());
                params.put("newMaster", master.getName());
                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("公司法人");
                row.getCells().get(1).getParagraphs().get(0).setText(oldMaster.getName());
                row.getCells().get(2).getParagraphs().get(0).setText(master.getName());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldMaster", "");
                params.put("newMaster", "");
            }


            List<String> oldList = oldMemberList.stream().map(MemberLog::getName).sorted().collect(Collectors.toList());
            List<String> newList = memberList.stream().map(Member::getName).sorted().collect(Collectors.toList());

            if (!oldList.toString().equals(newList.toString())) {
                params.put("oldMember", StringUtils.join(oldList, "、"));
                params.put("members", StringUtils.join(newList, "、"));
                TableRow row = table.getRows().get(1).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("股东");
                row.getCells().get(1).getParagraphs().get(0).setText(StringUtils.join(oldList, "、"));
                row.getCells().get(2).getParagraphs().get(0).setText(StringUtils.join(newList, "、"));

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldMember", "");
                params.put("members", "");
            }


            document = replaceDocument(document, params);

        }

        return document;
    }

    public static Document addStockTagIndex(Document document, List<Member> memberList) {

        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

        TextSelection textSelection = document.findString("StockTagIndex", true, true);
        Paragraph paragraph = textSelection.getAsOneRange().getOwnerParagraph();

        for (int i = 0; i < memberList.size(); i++) {
            Member member = memberList.get(i);
            String tag = Util.getIndexTag(i);
            paragraph.appendText(tag + "方：");
            TextRange t = paragraph.appendText(member.getName());
            t.getCharacterFormat().setUnderlineStyle(UnderlineStyle.Single);
            t.getCharacterFormat().setBold(true);
            paragraph.appendText("\n");
        }
        textSelection.getSelectedText();
        document.replace("StockTagIndex", textSelection, false, true);
        document.replace("StockTagIndex", "", false, true);
        return document;
    }

    public static Document addChangeInfo2602G(Document document, Enterprise enterprise, EnterpriseLog oldEnterprise, Person master, PersonLog oldMaster, List<Member> memberList, List<MemberLog> oldMemberList) {
        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());

        Map<String, String> params = new HashMap<>();

        Section section = document.getSections().get(0);
        TableCollection tableList = section.getTables();
        if (tableList.getCount() >= 2) {
            Table table = tableList.get(0);
            int i = 21;


            if (!enterprise.getBusiness().equals(oldEnterprise.getBusiness())) {
                TableRow row = table.getRows().get(i).deepClone();
                params.put("old_Business", oldEnterprise.getBusiness());
                params.put("new_Business", enterprise.getBusiness());

                row.getCells().get(0).getParagraphs().get(0).setText("经营范围");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getBusiness());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getBusiness());

                row.setHeightType(TableRowHeightType.Auto);

                table.getRows().insert(i, row);
                i++;


            } else {
                params.put("old_Business", "");
                params.put("new_Business", "");
            }

            if (!enterprise.getRegisterAddress().equals(oldEnterprise.getRegisterAddress())) {
                params.put("old_RegisterAddress", oldEnterprise.getRegisterAddress());
                params.put("new_RegisterAddress", enterprise.getRegisterAddress());

                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("注册地址");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getRegisterAddress());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getRegisterAddress());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("old_RegisterAddress", "");
                params.put("new_RegisterAddress", "");
            }

            if (Double.valueOf(enterprise.getCapital()).compareTo(Double.valueOf(oldEnterprise.getCapital())) != 0) {
                params.put("old_capital", Util.formatDouble(oldEnterprise.getCapital()));
                params.put("new_capital", Util.formatDouble(enterprise.getCapital()));
                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("注册资本");
                row.getCells().get(1).getParagraphs().get(0).setText(Util.formatDouble(oldEnterprise.getCapital()) + "万元");
                row.getCells().get(2).getParagraphs().get(0).setText(Util.formatDouble(enterprise.getCapital()) + "万元");
                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("old_capital", "");
                params.put("new_capital", "");
            }

            if (!enterprise.getName().equals(oldEnterprise.getName())) {
                params.put("old_EnterpriseName", oldEnterprise.getName());
                params.put("newEnterpriseName", enterprise.getName());
                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("公司名称");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getName());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getName());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("old_EnterpriseName", "");
                params.put("newEnterpriseName", "");
            }

            if (!enterprise.getType().equals(oldEnterprise.getType())) {
                params.put("oldType", oldEnterprise.getType());
                params.put("newType", enterprise.getType());
                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("公司类型");
                row.getCells().get(1).getParagraphs().get(0).setText(oldEnterprise.getType());
                row.getCells().get(2).getParagraphs().get(0).setText(enterprise.getType());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldType", "");
                params.put("newType", "");
            }

            if (!master.getName().equals(oldMaster.getName())) {
                params.put("oldMaster", oldMaster.getName());
                params.put("newMaster", master.getName());
                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("执行事务合伙人");
                row.getCells().get(1).getParagraphs().get(0).setText(oldMaster.getName());
                row.getCells().get(2).getParagraphs().get(0).setText(master.getName());

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldMaster", "");
                params.put("newMaster", "");
            }


            List<String> oldList = oldMemberList.stream().map(MemberLog::getName).sorted().collect(Collectors.toList());
            List<String> newList = memberList.stream().map(Member::getName).sorted().collect(Collectors.toList());

            if (!oldList.toString().equals(newList.toString())) {
                params.put("oldMember", StringUtils.join(oldList, "、"));
                params.put("members", StringUtils.join(newList, "、"));
                TableRow row = table.getRows().get(i).deepClone();
                row.getCells().get(0).getParagraphs().get(0).setText("合伙人");
                row.getCells().get(1).getParagraphs().get(0).setText(StringUtils.join(oldList, "、"));
                row.getCells().get(2).getParagraphs().get(0).setText(StringUtils.join(newList, "、"));

                table.getRows().insert(i, row);
                i++;
            } else {
                params.put("oldMember", "");
                params.put("members", "");
            }


            document = replaceDocument(document, params);

        }

        return document;
    }

//    public static Document addChangeInfo2602G(Document document, List<Member> memberList, List<MemberLog> oldMemberList, PersonLog oldPerson, Person masterPerson) {
//
//        memberList = memberList.stream().filter(t -> t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0).collect(Collectors.toList());
//
//        Section section = document.getSections().get(0);
//        TableCollection tableList = section.getTables();
//        if (tableList.getCount() > 0) {
//            Table table = tableList.get(0);
//            int i = 20;
//
//            if (!oldPerson.getName().equals(masterPerson.getName())) {
////                table.addRow(3);//默认在表格最下方插入一行
//                TableRow row = table.getRows().get(i).deepClone();
//                row.getCells().get(0).getParagraphs().get(0).setText("执行事务合伙人");
//                row.getCells().get(1).getParagraphs().get(0).setText(oldPerson.getName());
//                row.getCells().get(2).getParagraphs().get(0).setText(masterPerson.getName());
//                table.getRows().insert(i + 1, row);
////                table.getRows().insert(i , table.addRow(true,3));
////                TableRow row = table.getRows().get(i);//获取表格每一行
//                i++;
//            }
//
//            String[] oldStock = oldMemberList.stream().map(t -> t.getName()).toArray(String[]::new);
//            String[] stock = memberList.stream().map(t -> t.getName()).toArray(String[]::new);
//            if (!Util.isEqual(oldStock, stock)) {
////                table.addRow(3);//默认在表格最下方插入一行
////                table.getRows().insert(i , table.addRow(true,3));
//                TableRow row = table.getRows().get(i).deepClone();
////                TableRow row = table.getRows().get(i);//获取表格每一行
//                row.getCells().get(0).getParagraphs().get(0).setText("合伙人");
//                row.getCells().get(1).getParagraphs().get(0).setText(StringUtils.join(oldStock, "、"));
//                row.getCells().get(2).getParagraphs().get(0).setText(StringUtils.join(stock, "、"));
//                table.getRows().insert(i + 1, row);
//                i++;
//            }
//
//        }
//        return document;
//    }


    /**
     * 获取导出文件的参数
     *
     * @param enterprise
     * @return
     */
    public static Map<String, String> getDocParams(Enterprise enterprise, Person masterPerson, Person contactor, Person finance, EnterpriseLog oldEnterpriseInfo, List<Member> memberList, Project project, List<MemberLog> oldMemberList, PersonLog oldMasterPerson) {
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseName", enterprise.getName());
        params.put("year", String.valueOf(LocalDate.now().getYear()));
        params.put("month", String.valueOf(LocalDate.now().getMonthValue()));
        params.put("day", String.valueOf(LocalDate.now().getDayOfMonth()));

        //六个月后
        int six = Integer.valueOf(params.get("month")) + 6;
        if (six > 12) {
            params.put("after6Month", six - 12 + "");
            params.put("afterYear", Integer.valueOf(params.get("year")) + 1 + "");
        } else {
            params.put("after6Month", six + "");
            params.put("afterYear", params.get("year"));
        }

        /**
         * 项目年月日
         */
        if (!Objects.isNull(project) && !Objects.isNull(project.getMaterialDate()) && !"".equals(project.getMaterialDate())) {
            String y = project.getMaterialDate().substring(0, 4);
            String m = project.getMaterialDate().substring(5, 7);
            String d = project.getMaterialDate().substring(8, 10);
            params.put("projectYYYY", y);
            params.put("project5YYYY", String.valueOf(Integer.valueOf(y) + 5));
            params.put("projectMM", m);
            params.put("projectDD", d);
        } else {
            params.put("projectYYYY", params.get("year"));
            params.put("project5YYYY", String.valueOf(Integer.valueOf(params.get("year")) + 5));
            params.put("projectMM", params.get("month"));
            params.put("projectDD", params.get("day"));
        }

        if (Objects.nonNull(oldEnterpriseInfo)) {


            if (oldEnterpriseInfo.getRegisterAddress().equals(enterprise.getRegisterAddress())) {
                params.put("registerAddressBefore", "");
                params.put("registerAddressNow", "");
            } else {
                params.put("registerAddressBefore", "第二条  公司住所：" + oldEnterpriseInfo.getRegisterAddress());
                params.put("registerAddressNow", "第二条  公司住所：" + enterprise.getRegisterAddress());
            }

            if (oldEnterpriseInfo.getBusiness().equals(enterprise.getBusiness())) {
                params.put("businessBefore", "");
                params.put("businessNow", "");
            } else {
                params.put("businessBefore", "第三条  公司经营范围：" + oldEnterpriseInfo.getBusiness());
                params.put("businessNow", "第三条  公司经营范围：" + enterprise.getBusiness());
            }

        }

        /**
         * 受理年月日
         */
        params.put("handlerYear", params.get("year"));
        params.put("handlerMonth", params.get("month"));
        params.put("handlerDay", params.get("day"));

        /**
         * 注册号
         */
        params.put("registerNum", enterprise.getRegisterNum());
        /**
         *法人信息
         */

        params.put("oldMasterName", oldMasterPerson == null ? "" : oldMasterPerson.getName());
        params.put("masterName", masterPerson == null ? "" : masterPerson.getName());
        params.put("masterPhone", masterPerson == null ? "" : masterPerson.getPhone());
        params.put("masterMobile", masterPerson == null ? "" : masterPerson.getMobile());
        params.put("idCardFirst", masterPerson == null ? "" : masterPerson.getFront());
        params.put("idCardSecond", masterPerson == null ? "" : masterPerson.getBack());
        params.put("idcard", masterPerson == null ? "" : masterPerson.getIdcard());
        params.put("masterType", masterPerson == null ? "" : masterPerson.getType());
        params.put("email", masterPerson == null ? "" : masterPerson.getEmail());
        params.put("masterAddress", masterPerson == null ? "" : masterPerson.getAddress());
        params.put("country", masterPerson == null ? "" : masterPerson.getCountry()); // 国别
        /**
         * 联系人信息
         */
        params.put("contactorName", contactor == null ? "" : contactor.getName());
        params.put("contactorPhone", contactor == null ? "" : contactor.getPhone());
        params.put("contactorMobile", contactor == null ? "" : contactor.getMobile());
        params.put("contactorIdCard", contactor == null ? "" : contactor.getIdcard());

        /**
         * 财务信息
         */
        params.put("financeName", finance == null ? "" : finance.getName());
        params.put("financePhone", finance == null ? "" : finance.getPhone());
        params.put("financeMobile", finance == null ? "" : finance.getMobile());
        params.put("financeIdCard", finance == null ? "" : finance.getIdcard());

        params.put("capital", Util.formatDouble(enterprise.getCapital()));
        params.put("newCapital", Util.formatDouble(enterprise.getCapital()));
        params.put("realCapital", enterprise.getRealCapital());
        params.put("contactPhone", enterprise.getContactPhone());
        params.put("remake", enterprise.getRemake());
        params.put("actContactAddress", enterprise.getActContactAddress());  // 实际联系地址
        params.put("zipCode", enterprise.getZipcode());

        //经营范围
        params.put("business", enterprise.getBusiness());
        // 所属行业
        params.put("belongIndustry", enterprise.getBelongIndustry());
        params.put("introducer", enterprise.getIntroducer());
        //营业执照的有效期
        params.put("startDate", enterprise.getStartDate());
        params.put("endDate", enterprise.getEndDate());
        //注册地址
        params.put("registerAddress", Objects.isNull(enterprise.getRegisterAddress()) ? "           " : enterprise.getRegisterAddress());
        //企业类型
        params.put("type", enterprise.getType());
        oldEnterpriseInfo = oldEnterpriseInfo == null ? new EnterpriseLog() : oldEnterpriseInfo;
        //变更前的企业信息
        params.put("oldEnterpriseName", oldEnterpriseInfo.getName());
        params.put("oldRegisterNum", oldEnterpriseInfo.getRegisterNum());
        params.put("oldRegisterAddress", oldEnterpriseInfo.getRegisterAddress());
        params.put("oldBusiness", oldEnterpriseInfo.getBusiness());


        if (!memberList.isEmpty()) {
            //计算股东数量
            List<Member> stockList = memberList.stream().filter(t ->
                    t.getIsStock() == 1 && t.getPutAmount() != null && Double.valueOf(t.getPutAmount()) > 0
            ).collect(Collectors.toList());
            params.put("stockNum", stockList.size() + "");
            String[] allStock = stockList.stream().map(t -> t.getName()).toArray(String[]::new);
            params.put("allStock", StringUtils.join(allStock, "、"));
//            params.put("changeStockAfter", StringUtils.join(allStock, "、"));


            //计算董事数量
            List<Member> directorList = memberList.stream().filter(t -> {
                        if (t.getIsDirector() != null && t.getIsDirector() == 1) return true;
                        if (t.getHoldPost() != null && t.getHoldPost().contains("董事")) return true;
                        return false;
                    }
            ).collect(Collectors.toList());
            params.put("directorNum", directorList.size() + "");
            String[] allDirector = directorList.stream().map(t -> t.getName()).toArray(String[]::new);
            params.put("allDirector", StringUtils.join(allDirector, "、"));
            //计算监事数量
            List<Member> supervisorList = memberList.stream().filter(t ->
                    {
                        if (t.getIsSupervisor() != null && t.getIsSupervisor() == 1) return true;
                        if (t.getHoldPost() != null && t.getHoldPost().contains("监事")) return true;
                        return false;
                    }
            ).collect(Collectors.toList());
            params.put("supervisorNum", supervisorList.size() + "");
            String[] allSupervisor = supervisorList.stream().map(t -> t.getName()).toArray(String[]::new);
            params.put("allSupervisor", StringUtils.join(allSupervisor, "、"));

            Member chairman = memberList.stream().filter(t -> t.getHoldPost() != null && t.getHoldPost().contains("董事长")).findFirst().orElse(null);
            Member manager = memberList.stream().filter(t -> t.getHoldPost() != null && t.getHoldPost().contains("总经理")).findFirst().orElse(null);
            Member supervisorChairman = memberList.stream().filter(t -> t.getHoldPost() != null && t.getHoldPost().contains("监事会主席")).findFirst().orElse(null);

            if (!Objects.isNull(chairman))
                params.put("chairman", Objects.isNull(chairman.getName()) ? "    " : chairman.getName());
            else
                params.put("chairman", "    ");

            if (!Objects.isNull(manager))
                params.put("manager", Objects.isNull(manager.getName()) ? "    " : manager.getName());
            else
                params.put("manager", "    ");
            if (!Objects.isNull(supervisorChairman))
                params.put("supervisorChairman", Objects.isNull(supervisorChairman.getName()) ? "    " : supervisorChairman.getName());
            else
                params.put("supervisorChairman", "    ");

        } else {
            params.put("allStock", "    ");
//            params.put("changeStockAfter", "    ");
            params.put("stockNum", "    ");
            params.put("directorNum", "    ");
            params.put("allDirector", "    ");
            params.put("supervisorNum", "    ");
            params.put("allSupervisor", "    ");
            params.put("chairman", "     ");
            params.put("manager", "    ");
            params.put("supervisorChairman", "    ");
        }

        return params;
    }

    /**
     * 获取导出文件的参数
     *
     * @param enterprise
     * @return
     */
    public static Map<String, String> getDocParams(Enterprise enterprise, Person masterPerson, Person contactor) {
        HashMap<String, String> params = new HashMap<>();
        params.put("enterpriseName", enterprise.getName());
        params.put("year", String.valueOf(LocalDate.now().getYear()));
        params.put("month", String.valueOf(LocalDate.now().getMonthValue()));
        params.put("day", String.valueOf(LocalDate.now().getDayOfMonth()));

        //六个月后
        int six = Integer.valueOf(params.get("month")) + 6;
        if (six > 12) {
            params.put("after6Month", six - 12 + "");
            params.put("afterYear", Integer.valueOf(params.get("year")) + 1 + "");
        } else {
            params.put("after6Month", six + "");
            params.put("afterYear", params.get("year"));
        }





        /**
         * 受理年月日
         */
        params.put("handlerYear", params.get("year"));
        params.put("handlerMonth", params.get("month"));
        params.put("handlerDay", params.get("day"));

        /**
         * 注册号
         */
        params.put("registerNum", enterprise.getRegisterNum());
        /**
         *法人信息
         */

        params.put("masterName", masterPerson == null ? "" : masterPerson.getName());
        params.put("masterPhone", masterPerson == null ? "" : masterPerson.getPhone());
        params.put("masterMobile", masterPerson == null ? "" : masterPerson.getMobile());
        params.put("idCardFirst", masterPerson == null ? "" : masterPerson.getFront());
        params.put("idCardSecond", masterPerson == null ? "" : masterPerson.getBack());
        params.put("idcard", masterPerson == null ? "" : masterPerson.getIdcard());
        params.put("masterType", masterPerson == null ? "" : masterPerson.getType());
        params.put("email", masterPerson == null ? "" : masterPerson.getEmail());
        params.put("masterAddress", masterPerson == null ? "" : masterPerson.getAddress());
        params.put("country", masterPerson == null ? "" : masterPerson.getCountry()); // 国别
        /**
         * 联系人信息
         */
        params.put("contactorName", contactor == null ? "" : contactor.getName());
        params.put("contactorPhone", contactor == null ? "" : contactor.getPhone());
        params.put("contactorMobile", contactor == null ? "" : contactor.getMobile());
        params.put("contactorIdCard", contactor == null ? "" : contactor.getIdcard());
        params.put("DesignatedContactPhone", enterprise.getDesignatedContactPhone());


        params.put("capital", Util.formatDouble(enterprise.getCapital()));
        params.put("newCapital", Util.formatDouble(enterprise.getCapital()));
        params.put("realCapital", enterprise.getRealCapital());
        params.put("contactPhone", enterprise.getContactPhone());
        params.put("remake", enterprise.getRemake());
        params.put("actContactAddress", enterprise.getActContactAddress());  // 实际联系地址
        params.put("zipCode", enterprise.getZipcode());

        //经营范围
        params.put("business", enterprise.getBusiness());
        // 所属行业
        params.put("belongIndustry", enterprise.getBelongIndustry());
        params.put("introducer", enterprise.getIntroducer());
        //营业执照的有效期
        params.put("startDate", enterprise.getStartDate());
        params.put("endDate", enterprise.getEndDate());
        //注册地址
        params.put("registerAddress", Objects.isNull(enterprise.getRegisterAddress()) ? "           " : enterprise.getRegisterAddress());
        //企业类型
        params.put("type", enterprise.getType());


        return params;
    }

    public static void DocWriteResponse(String fileName, HttpServletResponse response, Document document, FileFormat fileFormat) {
        try {
            response.setContentType("application/octet-stream");

            //这后面可以设置导出Excel的名称，此例中名为student.xls
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName + ".doc", "UTF-8"));
            response.flushBuffer();
            document.saveToStream(response.getOutputStream(), fileFormat);
        } catch (IOException e) {
            log.error("错误:", e);
        }
    }

    public static void DocWriteOutStream(OutputStream out, Document document, FileFormat fileFormat) {
        document.saveToStream(out, fileFormat);
    }
}
