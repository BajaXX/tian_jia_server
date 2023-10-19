package com.bemore.api.util;

import cn.hutool.poi.excel.ExcelReader;
import com.bemore.api.entity.request.EnterpriseParam;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class FileUtil {

	public static List<String> getEnterpriseNameByExcel(MultipartFile file) {
		ExcelReader excelReader = null;
		try {
			excelReader = cn.hutool.poi.excel.ExcelUtil.getReader(file.getInputStream());
			excelReader.addHeaderAlias("企业名称","name");
			List<EnterpriseParam> paramList = excelReader.readAll(EnterpriseParam.class);
			// 去重并保持原顺序
			Set<String> nameSet = paramList.stream().map(EnterpriseParam::getName).collect(Collectors.toCollection(LinkedHashSet::new));
			return new ArrayList<>(nameSet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return Collections.emptyList();
	}

	/**
	 *导出
	 */
	public static void outputToResponse(HttpServletResponse response, File file, String fileName) {
		response.setHeader("content-type", "application/octet-stream");
		response.setContentType("application/octet-stream");
		response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
		byte[] buff = new byte[1024];
		BufferedInputStream bis = null;
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(response.getOutputStream());
			bis = new BufferedInputStream(new FileInputStream(file));
			int i = bis.read(buff);
			while (i != -1) {
				os.write(buff, 0, buff.length);
				os.flush();
				i = bis.read(buff);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("不支持UTF-8编码");
		} catch (FileNotFoundException e) {
			throw new RuntimeException("报告文件不存在");
		} catch (IOException e) {
			throw new RuntimeException("下载I/O出错");
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	* @Title: writeDataToFile
	* @Description: 创建文件
	* @param path
	* @author Jadan
	* @date 2019年11月28日
	* @throws
	 */
	public static void createFile(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				String parent = file.getParent();
				File dir = new File(parent);
				if(!dir.exists()){
					dir.mkdirs();
				}
				file.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			file.delete();
		}
	}
	
	public static void createFileDir(String path){
		File file = new File(path);
		if(!file.exists()){
			try {
				String parent = file.getParent();
				File dir = new File(parent);
				if(!dir.exists()){
					dir.mkdirs();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			file.delete();
		}
	}
	
	/**
	 * 
	* @Title: deleteFile
	* @Description: 删除文件
	* @author Jadan
	* @date 2019年11月28日
	* @throws
	 */
	public static void deleteFile(File file){
		if(file.isFile() && file.exists()) {
			file.delete();
		}else if(file.isDirectory()) {
			File[] list = file.listFiles();
			for(File detailFile : list) {
				deleteFile(detailFile);
			}
		}
		file.delete();
	}
	/**
	 * 
	* @Title: copFile
	* @Description: 拷贝文件
	* @param originFile
	* @param newFile  void
	* @author user009
	* @date 2020年6月10日
	* @throws
	 */
	public static void copFile(String originFile,String newFile) {
		File oldpaths = new File(originFile);
		File newpaths = new File(newFile);
		FileUtil.createFileDir(newFile);
		try {
			Files.copy(oldpaths.toPath(), newpaths.toPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 
	* @Title: getBase64FromInputStream
	* @Description: 文件流转成base64
	* @param in
	* @return  String
	* @author user009
	* @date 2020年6月16日
	* @throws
	 */
	public static String getBase64FromInputStream(InputStream in) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[100];
            int rc = 0;
            while ((rc = in.read(buff, 0, 100)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            data = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return new String(Base64.getEncoder().encode(data));
    }
}
