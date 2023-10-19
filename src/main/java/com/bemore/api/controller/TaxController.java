package com.bemore.api.controller;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;

import com.bemore.api.dao.EnterpriseDao;
import com.bemore.api.dao.mapper.EnterpriseTaxMapper;
import com.bemore.api.entity.request.EnterpriseTaxParam;
import com.bemore.api.entity.request.ReportBaseRequest;
import com.bemore.api.entity.request.TaxExportRequest;
import com.bemore.api.entity.request.TaxRequest;
import com.bemore.api.service.EnterpriseTaxService;
import io.swagger.annotations.ApiOperation;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.bemore.api.dao.TaxDao;
import com.bemore.api.entity.Tax;
import com.bemore.api.util.GsonUtil;

@RestController
@RequestMapping("/tax")
public class TaxController {
	
	private static final Logger logger = LoggerFactory.getLogger(TaxController.class);
	
	@Autowired
	private TaxDao taxDao;
	@Autowired
	private EnterpriseTaxService enterpriseTaxService;

	@GetMapping("/cleanIndustry")
	@ApiOperation(value = "工具接口，清空企业表和税收表的行业")
	public void cleanIndustry() {
		enterpriseTaxService.cleanIndustryService();
	}

	@GetMapping("/cleanAll")
	@ApiOperation(value = "清空企业税收列表")
	public void cleanTable() {
		enterpriseTaxService.cleanTable();
	}

	@PostMapping("/getEnterpriseTaxByExcel")
	@ApiOperation(value = "通过excel查询企业税收列表")
	public String getEnterpriseTaxByExcel(TaxRequest request) {
		return GsonUtil.build(enterpriseTaxService.findEnterpriseTaxByExcel(request));
	}

	@PostMapping("/exportEnterpriseTax")
	@ApiOperation(value = "导出企业税收列表")
	public void exportEnterpriseTax(@RequestBody TaxExportRequest request, HttpServletResponse resp) {
		enterpriseTaxService.exportEnterpriseTax(request,resp);
	}

	@GetMapping("/getEnterpriseTaxTableData")
	@ApiOperation(value = "企业税收列表")
	public String getEnterpriseTaxTableData(TaxRequest request) {
		return GsonUtil.build(enterpriseTaxService.getEnterpriseTaxByPage(request));
	}

	// 导入预览
	@PostMapping("/preview")
	public String preview(@RequestParam MultipartFile[] file) {
		List<EnterpriseTaxParam> preview = enterpriseTaxService.preview(file);
		return GsonUtil.build(preview);
	}

	@GetMapping("/getNewestRollbackLog")
	@ApiOperation(value = "查询企业税收数据回溯日志")
	public String getNewestRollbackLog() {
		return GsonUtil.build(enterpriseTaxService.findNewestRollbackLog());
	}

	@DeleteMapping("/cleanMonthData")
	@ApiOperation(value = "企业税收数据回溯（按月清除）")
	public String cleanDataByMonth(@RequestParam String date) {
		enterpriseTaxService.cleanDataByMonthService(date);
		return GsonUtil.build("success");
	}

	// 按月导入企业税收数据
	@PostMapping(value = "/importEnterpriseTax")
	public String importEnterpriseTaxData(@RequestParam MultipartFile file,@RequestParam String date) {
		enterpriseTaxService.importEnterpriseTaxService(file,date);
		return GsonUtil.build("success");
	}

	// 按月批量导入企业税收数据
	@PostMapping(value = "/batchImportEnterpriseTax")
	public String batchImportEnterpriseTax(@RequestParam MultipartFile[] file) {
		enterpriseTaxService.batchImportEnterpriseTaxService(file);
		return GsonUtil.build("success");
	}

	@GetMapping("/getNewestTaxMonth")
	@ApiOperation(value = "查询已导入税收数据的最近月份")
	public String getNewestTaxMonth() {
		return GsonUtil.build(enterpriseTaxService.findNewestTaxMonth());
	}
	
	// 查询	
	@GetMapping("/fetch")
	public String fetch(){
		List<Tax> taxs = taxDao.findAll(new Specification<Tax>(){
			@Override
            public Predicate toPredicate(Root<Tax> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				
				Predicate[] p =	new Predicate[list.size()];
				return cb.and(list.toArray(p));				
			}
		});		
		return GsonUtil.build(taxs);	
	}
	
	// 查询	
	@GetMapping("/fetchThisYear")
	public String fetchThisYear(){
		List<Tax> taxs = taxDao.findByDate("202104");
		return GsonUtil.build(taxs);	
	}
	
	// 上传税收
	@PostMapping(value = "/uploadTax")	
	@ResponseBody
	public String uploadTaxExcel(@RequestParam(value = "file", required = true) MultipartFile file) {
		InputStream is = null;			
		try {
			is = file.getInputStream();
			HSSFWorkbook book = new HSSFWorkbook(is);
			HSSFSheet sheet = book.getSheetAt(0);
			for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
	            HSSFRow row = sheet.getRow(i);
//	            for(int j=0;j<44;j++) {
//	            	if(null!=row.getCell(j)){
//	            		row.getCell(j).setCellType(CellType.STRING);
//	            	}
//	            }
	            String date = String.valueOf(row.getCell(1).getNumericCellValue());	            
	            String lastDate = String.valueOf(row.getCell(1).getNumericCellValue()-1);	            
	            Tax tax = new Tax();
	            tax.setDate(date.substring(0, 6));
				tax.setYear(Integer.parseInt(date.substring(0, 4)));
				tax.setMonth(Integer.parseInt(date.substring(4, 6)));
	            tax.setEnterpriseName(row.getCell(0).getStringCellValue());
	            String sales = String.valueOf(row.getCell(2).getNumericCellValue());
	            if(!sales.equals("0.0")){
	            	List<Tax> taxs = taxDao.findByEnterpriseNameAndDate(tax.getEnterpriseName(), lastDate.substring(0, 6));
	            	tax.setSales(e2String(row.getCell(2).getNumericCellValue()));
//		            tax.setSalesTotal(e2String(row.getCell(2).getNumericCellValue()));	            
		            tax.setAddedTax(e2String(row.getCell(3).getNumericCellValue()));
//		            tax.setAddedTaxTotal(e2String(row.getCell(3).getNumericCellValue()));	            
		            tax.setBusinessTax(e2String(row.getCell(4).getNumericCellValue()));
//		            tax.setBusinessTaxTotal(e2String(row.getCell(4).getNumericCellValue()));	            
		            tax.setIncomeTax(e2String(row.getCell(5).getNumericCellValue()));
//		            tax.setIncomeTaxTotal(e2String(row.getCell(5).getNumericCellValue()));
		            tax.setPersonTax(e2String(row.getCell(6).getNumericCellValue()));
//		            tax.setPersonTaxTotal(e2String(row.getCell(6).getNumericCellValue()));	            
		            tax.setHouseTax(e2String(row.getCell(7).getNumericCellValue()));
//		            tax.setHouseTaxTotal(e2String(row.getCell(7).getNumericCellValue()));
		            tax.setStampTax(e2String(row.getCell(8).getNumericCellValue()));
//		            tax.setStampTaxTotal(e2String(row.getCell(8).getNumericCellValue()));	            
		            tax.setToAddedTax(e2String(row.getCell(9).getNumericCellValue()));
//		            tax.setToAddedTaxTotal(e2String(row.getCell(9).getNumericCellValue()));
		            tax.setCityTax(e2String(row.getCell(10).getNumericCellValue()));
//		            tax.setCityTaxTotal(e2String(row.getCell(10).getNumericCellValue()));		            
		            tax.setTotalTax(e2String(row.getCell(19).getNumericCellValue()));
//		            tax.setTotalTaxTotal(e2String(row.getCell(19).getNumericCellValue()));
	            	tax(taxs.get(0),row,tax);		            	            	
	            }
	            taxDao.save(tax);
	            Thread.sleep(10);
	        }			
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}		
		return GsonUtil.build();
	}
	
	private void tax(Tax lastTax, HSSFRow row, Tax tax){
        tax.setSalesTotal(e2String(row.getCell(2).getNumericCellValue(), lastTax.getSalesTotal()));	
        tax.setAddedTaxTotal(e2String(row.getCell(3).getNumericCellValue(), lastTax.getAddedTaxTotal()));        
        tax.setBusinessTaxTotal(e2String(row.getCell(4).getNumericCellValue(), lastTax.getBusinessTaxTotal()));        
        tax.setIncomeTaxTotal(e2String(row.getCell(5).getNumericCellValue(), lastTax.getIncomeTaxTotal()));
        tax.setPersonTaxTotal(e2String(row.getCell(6).getNumericCellValue(), lastTax.getPersonTaxTotal()));	
        tax.setHouseTaxTotal(e2String(row.getCell(7).getNumericCellValue(), lastTax.getHouseTaxTotal()));
        tax.setStampTaxTotal(e2String(row.getCell(8).getNumericCellValue(), lastTax.getStampTaxTotal()));	
        tax.setToAddedTaxTotal(e2String(row.getCell(9).getNumericCellValue(), lastTax.getToAddedTaxTotal()));
        tax.setCityTaxTotal(e2String(row.getCell(10).getNumericCellValue(), lastTax.getCityTaxTotal()));
        tax.setTotalTaxTotal(e2String(row.getCell(19).getNumericCellValue(), lastTax.getTotalTaxTotal()));        
	}	
	
	private String e2String(double d, String s){
		BigDecimal bDecimal = new BigDecimal(d);
		if(!StringUtils.isEmpty(s)){
			bDecimal.add(new BigDecimal(s));
		}
		return bDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
	
	private String e2String(double d){
		BigDecimal bDecimal = new BigDecimal(d);
		return bDecimal.setScale(2, RoundingMode.HALF_UP).toPlainString();
	}
	
}
