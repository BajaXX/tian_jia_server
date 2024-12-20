package com.bemore.api.controller;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bemore.api.config.FileUpSaveConfig;
import com.bemore.api.dto.req.downDocReq;
import com.bemore.api.exception.WebException;
import com.bemore.api.util.DocUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.FilesDao;
import com.bemore.api.entity.Enterprise;
import com.bemore.api.entity.Files;
import com.bemore.api.util.GsonUtil;
import com.bemore.api.util.Util;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.Section;
import com.spire.doc.Table;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/print")
public class DownloadController {

    private String fileDir = "//var//www//html//files//";

    @Autowired
    private EnterpriseDao enterpriseDao;

    @Autowired
    private FilesDao filesDao;

    @Autowired
    FileUpSaveConfig upSaveConfig;

    @PostMapping("/fetchFiles")
    public String fetchFiles(@RequestParam String id) {
        Enterprise enterprise = enterpriseDao.findById(id).get();
        List<Files> files = filesDao.findByEnterpriseTypeAndType(Util.getEnterpriseType(enterprise.getName()), enterprise.getProcess());
        return GsonUtil.build(files);
    }

//	// 获取流程
//	@GetMapping("/fetch/{id}")
//	public void fetch(@PathVariable String id, HttpServletResponse response) throws IOException {		
//		List<EnterpriseProcess> processes = enterpriseProcessDao.findByEnterpriseIdAndStatus(id,0);
//		EnterpriseProcess process = null;
//		if(processes.size()>0){
//			process = processes.get(0);
//		}
//		Enterprise enterprise = enterpriseDao.findById(id).get();
//		
//		String filename = System.currentTimeMillis()+".docx";		
//		OutputStream os = response.getOutputStream();
//		OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");		
//        // 读取字符编码
//        String utf = "UTF-8";
//        // 设置响应
////        response.setContentType("application/octet-stream");
//        response.setContentType("multipart/form-data");
//        response.setCharacterEncoding(utf);
////        response.setHeader("Pragma", "public");
////        response.setHeader("Cache-Control", "max-age=30");
////        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, utf));
//        response.setHeader("Content-Disposition", "attachment;fileName=" +
//                new String(filename.getBytes(utf), utf));
//		
//		Document doc = new Document();
//        doc.loadFromFile(fileDir + "book.docx");
//        
//        //Get the first section
//        Section section = doc.getSections().get(0);
//        //Get the first table in the section
//        Table table = section.getTables().get(0);
//        Table table2 = section.getTables().get(1);
//        
//        int update = 0;
//        table.replace("{0}", "√", false, true);                
//        table.replace("{1}", "", false, true);
//        table.replace("{2}", "", false, true);
//        table.replace("{3}", "", false, true);
//        if(!process.getRegisterAddress().equals(enterprise.getRegisterAddress())){
//        	table.replace("{4}", "√", false, true);
//        	update++;
//        }else{
//        	table.replace("{4}", "", false, true);
//        }
//        if(!process.getBusiness().equals(enterprise.getBusiness())){
//        	table.replace("{5}", "√", false, true);
//        	update++;
//        }else{
//        	table.replace("{5}", "", false, true);
//        }
//        table.replace("{6}", "", false, true);
//        table.replace("{7}", "", false, true);
//        table.replace("{8}", "", false, true);
//        table.replace("{9}", "", false, true);
//        table.replace("{10}", "", false, true);
//        table.replace("{11}", "", false, true);
//        table.replace("{12}", "", false, true);
//        table.replace("{13}", "", false, true);
//        table.replace("{14}", "", false, true);
//        table.replace("{15}", "", false, true);
//        
//        table2.replace("{16}", process.getName(), false, true);
//        table2.replace("{17}", process.getRegisterNum(), false, true);
//        table2.replace("{18}", "", false, true);
//        table2.replace("{19}", "", false, true);
//        
//        if(update==2){        	
//            table2.replace("{20}", "主要经营场所", false, true);
//            table2.replace("{21}", enterprise.getRegisterAddress(), false, true);
//            table2.replace("{22}", process.getRegisterAddress(), false, true);            
//            table2.replace("{23}", "经营范围", false, true);
//            table2.replace("{24}", enterprise.getBusiness(), false, true);
//            table2.replace("{25}", process.getBusiness(), false, true);
//        }else if(update==1){
//        	if(!process.getRegisterAddress().equals(enterprise.getRegisterAddress())){
//        		table2.replace("{20}", "主要经营场所", false, true);
//                table2.replace("{21}", enterprise.getRegisterAddress(), false, true);
//                table2.replace("{22}", process.getRegisterAddress(), false, true);
//        	}else{
//        		table2.replace("{20}", "经营范围", false, true);
//                table2.replace("{21}", enterprise.getBusiness(), false, true);
//                table2.replace("{22}", process.getBusiness(), false, true);
//        	}
//        	table2.replace("{23}", "", false, true);
//            table2.replace("{24}", "", false, true);
//            table2.replace("{25}", "", false, true);
//        }
//        
//        doc.saveToFile(fileDir + filename, FileFormat.Docx_2013);
//        
//        BufferedInputStream bis = null;
//        bis = new BufferedInputStream(new FileInputStream(fileDir + filename));
//        byte[] b = new byte[1024];
//        int i = 0;
//        os = response.getOutputStream();
//        while ((i = bis.read(b)) != -1) {
//            os.write(b, 0, i);
//        }
//        os.flush();
//        os.close();
//	}


    // 定义一个GetMapping方法，接收文件的路径作为参数
    @PostMapping("/download/{fileName:.+}")
    public void downloadFile(downDocReq req, HttpServletResponse response)  {
        try{

        // 从文件系统中加载文件作为资源
        Resource resource = loadFileAsResource(upSaveConfig.getSupportFilesDir(), req.getFilePath());

        // 尝试确定文件的内容类型
        String contentType = "application/octet-stream";
//        try {
//            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
//        } catch (IOException ex) {
//            // 如果无法确定，则使用默认的内容类型
//            contentType = "application/octet-stream";
//        }

        // 返回响应实体，设置内容类型和文件名，并将资源作为响应体
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(contentType))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(req.getFilePath(), "UTF-8"))
//                .body(resource);
        response.setContentType("application/octet-stream");

        //这后面可以设置导出Excel的名称，此例中名为student.xls
        response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(req.getFilePath(), "UTF-8"));
        response.flushBuffer();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(downDocReq req, HttpServletRequest request) throws UnsupportedEncodingException {
        // 对文件名进行解码
        String decodedFileName = URLDecoder.decode(req.getFilePath(), "UTF-8");

        // 从文件系统中加载文件作为资源
        Resource resource = loadFileAsResource(upSaveConfig.getSupportFilesDir(), decodedFileName);

        // 尝试确定文件的内容类型
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            // 如果无法确定，则使用默认的内容类型
            contentType = "application/octet-stream";
        }

        // 返回响应实体，设置内容类型和文件名，并将资源作为响应体
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + URLEncoder.encode(decodedFileName, "UTF-8"))
                .body(resource);
    }

//    @PostMapping("/downloadFile")
//    public void downloadFile(downDocReq req, HttpServletResponse response) {
//        try{
//
//            DocUtil.DocWriteResponse(fileName, response, document, FileFormat.Doc);
//
//        }catch (WebException e){
//            throw e;
//        }
//    }

    // 定义一个辅助方法，根据文件名加载文件作为资源
    private Resource loadFileAsResource(String path,String fileName) {
        try {
            // 假设文件存储在项目的files目录下，可以根据需要修改
            Path filePath = Paths.get("files").resolve(path+fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                // 如果文件不存在，抛出异常
                throw new RuntimeException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            // 如果文件路径无效，抛出异常
            throw new RuntimeException("File not found " + fileName, ex);
        }
    }



}
