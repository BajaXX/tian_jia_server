package com.bemore.api.util;

import com.spire.doc.*;
import com.spire.doc.collections.CellCollection;
import com.spire.doc.collections.ParagraphCollection;
import com.spire.doc.collections.RowCollection;
import com.spire.doc.collections.TableCollection;
import com.spire.doc.documents.*;
import com.spire.doc.fields.DocPicture;
import com.spire.doc.fields.TextRange;
import com.spire.doc.formatting.RowFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 代码填充文档测试工具类，基于spireOffice
 */
@Slf4j
public class WordUtil {

    private static final Float FONT_SIZE18 = 18f;   // 小二
    private static final Float FONT_SIZE16 = 16f;   // 三号
    private static final Float FONT_SIZE15 = 15f;   // 小三
    private static final Float FONT_SIZE14 = 14f;   // 四号
    private static final Float FONT_SIZE12 = 12f;   // 小四
    private static final Float FONT_SIZE10 = 10.5f;   // 五号
    private static final Float FONT_SIZE9 = 9f;   // 小五

    /**
     * 生成表格测试
     */
    public static Document createTable() {
        Document document = new Document();
        Section section = document.addSection();
        // 纸张设置
        PageSetup pageSetup = section.getPageSetup();
        // 设置纸张大小为信纸
        pageSetup.setPageSize(PageSize.Letter);
        // 把纸张方向设置为横向
        pageSetup.setOrientation(PageOrientation.Landscape);

        // 顶部标题样式
        ParagraphStyle hTitleStyle = new ParagraphStyle(document);
        hTitleStyle.setName("hTitleStyle");
        hTitleStyle.getCharacterFormat().setBold(true);     // 是否加粗
        hTitleStyle.getCharacterFormat().setFontName("宋体"); // 字体
        hTitleStyle.getCharacterFormat().setFontSize(FONT_SIZE18);  // 字号
//		hTitleStyle.getCharacterFormat().setTextBackgroundColor(Color.LIGHT_GRAY);  // 文字背景
        document.getStyles().add(hTitleStyle);

        //正文样式
        ParagraphStyle fontStyle = new ParagraphStyle(document);
        fontStyle.setName("fontStyle");
        fontStyle.getCharacterFormat().setFontName("宋体");
        fontStyle.getCharacterFormat().setFontSize(10.5f); // 五号
        document.getStyles().add(fontStyle);

        //标题
        Paragraph title = section.addParagraph();
        title.appendText("股东（发起人）、外国投资者出资情况");
        title.applyStyle("hTitleStyle");
        title.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        title.getFormat().setAfterSpacing(15f);

        // 单位说明
        Paragraph explain = section.addParagraph();
//		TextRange textRange = explain.appendText("标题一：");
//		textRange.getCharacterFormat().setBold(true);
//		textRange.getCharacterFormat().setFontSize(14f);
        //内容
        explain.appendText("单位：万元（币种：□人民币  □其他________）");
        explain.getFormat().setHorizontalAlignment(HorizontalAlignment.Right);
        explain.applyStyle("fontStyle");
        explain.getFormat().setAfterSpacing(5f);

        // 表格
        // 表头的数据
        String[] interviewHeader = new String[]{"股东（发起人）、外国投资者\n" +
                "名称或姓名", "国别\n" +
                "（地区）", "证件类型","证件号码","认缴出资额","实缴出资额","出资（认缴）\n" +
                "时间","出资\n" +
                "方式","出资\n" +
                "比例"};
        Table table1 = section.addTable(true);
        // 设置表格边框样式
        RowFormat tableFormat = table1.getTableFormat();
//		tableFormat.getBorders().setBorderType(BorderStyle.Triple);
        tableFormat.getBorders().setLineWidth(2);

        // 行 和 列
        // 这里是先生成表格, 然后再定位表格行和列 , 插入数据
        table1.resetCells(9, interviewHeader.length);
        // 表头
        TableRow row = table1.getRows().get(0);
        row.isHeader(true);
        row.setHeightType(TableRowHeightType.Exactly);
        // 填充表头数据
        for (int i = 0; i < interviewHeader.length; i++) {
            WordUtil.putTableData(row, i, interviewHeader[i], null,null);
        }
        // 表体 - 数据填充
        String [] info1 = new String[]{"王炼","中国","身份证","310107197102021211","27000","27000","","货币","10%"};
        TableRow dataRow = table1.getRows().get(1);
        for (int j = 0; j < info1.length; j++) {
            WordUtil.putTableData(dataRow, j, info1[j], null,35f);
        }


        String [] info2 = new String[]{"张国富","中国","身份证","310113197503080834","3000","0","","货币","10%"};
        dataRow = table1.getRows().get(2);
        dataRow.setHeightType(TableRowHeightType.Exactly);
        for (int n = 0; n < info1.length; n++) {
            WordUtil.putTableData(dataRow, n, info2[n], null,35f);
        }
        return document;

        //导出文档-静态代码
//        document.saveToFile("C:\\Users\\estar\\Desktop\\test\\test1.doc", FileFormat.Doc);
//        document.dispose();
    }

    /**
     * 例子
     */
    public static void exportWord() {
        try {
            String date = "2021-03-09";
            Document document = new Document();
            Section section = document.addSection();


            // 顶部标题样式
            ParagraphStyle hTitleStyle = new ParagraphStyle(document);
            hTitleStyle.setName("hTitleStyle");
            hTitleStyle.getCharacterFormat().setBold(true);     // 是否加粗
            hTitleStyle.getCharacterFormat().setFontName("宋体"); // 字体
            hTitleStyle.getCharacterFormat().setFontSize(28f);  // 字号
            hTitleStyle.getCharacterFormat().setTextBackgroundColor(Color.LIGHT_GRAY);  // 文字背景
            document.getStyles().add(hTitleStyle);

            // 普通标题样式
            ParagraphStyle titleStyle = new ParagraphStyle(document);
            titleStyle.setName("titleStyle");
            titleStyle.getCharacterFormat().setBold(true);
            titleStyle.getCharacterFormat().setFontName("宋体");
            titleStyle.getCharacterFormat().setFontSize(14f);
            document.getStyles().add(titleStyle);

            //正文样式
            ParagraphStyle fontStyle = new ParagraphStyle(document);
            fontStyle.setName("fontStyle");
            fontStyle.getCharacterFormat().setFontName("宋体");
            fontStyle.getCharacterFormat().setFontSize(12f);
            document.getStyles().add(fontStyle);

            //总标题
            Paragraph pph1 = section.addParagraph();
            pph1.appendText("spire office导出word文字段落和表格的模板");
            pph1.applyStyle("hTitleStyle");
            pph1.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            pph1.getFormat().setAfterSpacing(15f);

            // 副标题 - 时间
            //报表时间
            StringBuffer titleDate = new StringBuffer();
            SimpleDateFormat sd1 = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sd = new SimpleDateFormat("yyyy年MM月dd日");
            titleDate.append(sd.format(sd1.parse(date)));

            Paragraph pph2 = section.addParagraph();
            pph2.appendText(titleDate.toString());
            pph2.applyStyle("titleStyle");
            pph2.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
            pph2.getFormat().setAfterSpacing(15f);

            // 1.总体进展
            // 标题
            Paragraph pph3 = section.addParagraph();
            TextRange textRange = pph3.appendText("标题一：");
            textRange.getCharacterFormat().setBold(true);
            textRange.getCharacterFormat().setFontSize(14f);

            //内容
            pph3.appendText("不要温和地走进那良夜，老年应当在日暮时燃烧咆哮;咆哮吧咆哮，痛斥那光的退缩。");
            pph3.getFormat().setAfterSpacing(5f);

            // 2.
            Paragraph pph5 = section.addParagraph();
            textRange = pph5.appendText("标题二：");
            textRange.getCharacterFormat().setBold(true);
            textRange.getCharacterFormat().setFontSize(14f);

            pph5.appendText("这是段文字用于测试内容");
            pph5.getFormat().setAfterSpacing(5f);

            // 表格
            // 表头的数据
            String[] interviewHeader = new String[]{"销售片区", "片区负责人", "任务栋数"};
            Table table1 = section.addTable(true);
            // 行 和 列
            // 这里是先生成表格, 然后再定位表格行和列 , 插入数据
            table1.resetCells(3, interviewHeader.length);
            // 要合并单元格
            // 合并列
            table1.applyHorizontalMerge(2,0,1);
            // 合并行
            table1.applyVerticalMerge(2,1,2);
            // 表头
            TableRow row = table1.getRows().get(0);
            row.isHeader(true);
            row.setHeightType(TableRowHeightType.Exactly);
            // 填充表头数据
            for (int i = 0; i < interviewHeader.length; i++) {
                putTableData(row, i, interviewHeader[i], null,null);
            }

            // 表体 - 数据填充
            int j = 0;
            TableRow dataRow = table1.getRows().get(1);
            dataRow.setHeightType(TableRowHeightType.Exactly);
            putTableData(dataRow, j++, "第一片区", null,null);
            putTableData(dataRow, j++, "张三", null,null);
            putTableData(dataRow, j++, "31", null,null);

            // 充值
            j = 0;
            dataRow = table1.getRows().get(2);
            dataRow.setHeightType(TableRowHeightType.Exactly);
            // 最后一行
            putTableData(dataRow, j, "总计", null,null);

            //导出文档-静态代码
            document.saveToFile("C:\\Users\\estar\\Desktop\\test\\test.doc", FileFormat.Doc);
            document.dispose();

            // 导出文档-web端
            // document.saveToFile("导出word文档.docx", FileFormat.Docx);
            // doExport(fileName, fileName, request, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 填充word表格数据
     */
    public static void putTableData(TableRow row, int index, String data, Float width,Float height) {
        row.getCells().get(index).getCellFormat().setVerticalAlignment(VerticalAlignment.Middle);
        // 设置行高
        if (null != height) {
            row.setHeight(height);
        }
        if (null != width) {
            row.getCells().get(index).setWidth(width);
        }
        Paragraph p = row.getCells().get(index).addParagraph();
        p.getFormat().setHorizontalAlignment(HorizontalAlignment.Center);
        p.applyStyle("fontStyle");
        p.appendText(data);
    }

    private static void doExport(String aFileName, String aFilePath, HttpServletRequest request, HttpServletResponse response) {
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File file = new File(aFilePath);
        try {
            request.setCharacterEncoding("UTF-8");
            String agent = request.getHeader("User-Agent").toUpperCase();
            if ((agent.indexOf("MSIE") > 0) || ((agent.indexOf("RV") != -1) && (agent.indexOf("FIREFOX") == -1)))
                aFileName = URLEncoder.encode(aFileName, "UTF-8");
            else {
//                aFileName = new String(aFileName.getBytes("UTF-8"), "ISO8859-1");
                aFileName = new String(aFileName.getBytes("UTF-8"), "UTF-8");
            }
            response.setContentType("application/x-msdownload;");
            response.setHeader("Content-disposition", "attachment; filename=" + aFileName);
            response.setHeader("Content-Length", String.valueOf(file.length()));
            bis = new BufferedInputStream(new FileInputStream(file));
            bos = new BufferedOutputStream(response.getOutputStream());
            byte[] buff = new byte[2048];
            int bytesRead;
            while (-1 != (bytesRead = bis.read(buff, 0, buff.length)))
                bos.write(buff, 0, bytesRead);
            log.info("<--------------------------文档导出成功------------------------------>");
            bos.flush();
        } catch (Exception e) {
            // TODO: handle exception
            log.error("导出文档失败！：" + e.getMessage());
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                file.delete();
            } catch (Exception e) {
                log.error("导出文档关闭流出错！", e);
            }
        }
    }

    // 指定表格位置插入图片 ID 3 19
    public static Document insertImg3And19(Document document, List<String> imagePathList) {
        if (imagePathList.isEmpty()) return document;
        Section section = document.getSections().get(0);
        // 获取文档的所有表格
        TableCollection tables = section.getTables();
        // 获取目标表格
        Table table = tables.get(2);
        RowCollection rows = table.getRows();
        // 获取目标行 列
        TableRow tableRow = rows.get(1);
        CellCollection cells = tableRow.getCells();
        TableCell tableCell = cells.get(0);

        // 目标段落
        ParagraphCollection paragraphs = tableCell.getParagraphs();
        Paragraph paragraph = paragraphs.get(0);
        // 替换掉文字提示
        String text = paragraph.getText();
        if (!StringUtils.isEmpty(text)) {
            paragraph.replace(text,"",false,true);
        }

        imagePathList.forEach(
                imagePath -> {
                    // 将图片插入到表格段落
                    DocPicture docPicture = paragraph.appendPicture(imagePath);
                    // 设置文字环绕方式
//		            docPicture.setTextWrappingStyle(TextWrappingStyle.Square);
//		            docPicture.setHorizontalAlignment(ShapeHorizontalAlignment.Center);
                    // 指定图片位置
//		            docPicture.setHorizontalPosition(50);
//		            docPicture.setVerticalPosition(50);

                    // 设置图片大小
                    docPicture.setWidth(300f);
                    docPicture.setHeight(100f);
                }
        );

        return document;
    }

    // 指定表格位置插入图片 ID13
    public static Document insertImg13(Document document, List<String> imagePathList){
        log.info("imagePathList...{}",imagePathList);
        if (imagePathList.isEmpty()) return document;
        Section section = document.getSections().get(0);
        Table table = section.getTables().get(0);

        Paragraph paragraph = table.getRows().get(3).getCells().get(0).getParagraphs().get(0);

        String text = paragraph.getText();
        if (!StringUtils.isEmpty(text)) {
            paragraph.replace(text,"",false,true);
        }
        imagePathList.forEach(
                imagePath -> {
                    DocPicture docPicture = paragraph.appendPicture(imagePath);
                    docPicture.setWidth(300f);
                    docPicture.setHeight(100f);
                }
        );
        return document;
    }
}
