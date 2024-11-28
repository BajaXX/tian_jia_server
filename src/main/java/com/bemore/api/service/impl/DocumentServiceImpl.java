package com.bemore.api.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bemore.api.config.FileUpSaveConfig;
import com.bemore.api.constant.CommonConstants;
import com.bemore.api.constant.ErrorCodeConstants;
import com.bemore.api.dao.*;
import com.bemore.api.dao.mapper.EnterpriseLogMapper;
import com.bemore.api.dto.resp.DocResp;
import com.bemore.api.entity.*;
import com.bemore.api.entity.request.DocumentRequest;
import com.bemore.api.enums.BylawsEnum;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.DocumentService;
import com.bemore.api.util.*;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private static final String path = "template/";
//    private static final String path = "src/main/resources/templates/";

    @Value("${company.doc}")
    private String companyDocsBasePath;
//    @Value("${company.url}")
//    private String companyDocsBaseUrl;


    @Autowired
    private FilesDao filesDao;
    @Autowired
    private EnterpriseDao enterpriseDao;
    @Autowired
    private MemberDao memberDao;
    @Autowired
    private PersonDao personDao;
    @Autowired
    private PersonLogDao personLogDao;
    @Autowired
    private ProjectDao projectDao;
    @Autowired
    private MemberLogDao memberLogDao;

    @Autowired
    private EnterpriseLogMapper enterpriseLogMapper;

    @Autowired
    private TransferLogDao transferLogDao;

    @Autowired
    private FileUpSaveConfig fileUpSaveConfig;

    @Autowired
    private SupportContractDao supportContractDao;
    @Autowired
    private EnterpriseSupportLogDao enterpriseSupportLogDao;



    @Override
    public void downLoadDocumentById(HttpServletResponse response, DocumentRequest documentRequest) {
        // 根据fileId查询文件名
        Files files = filesDao.findById(documentRequest.getFileId()).get();
        String fileName = files.getName();
        log.info("fileName...{}", fileName);

        HashMap<String, String> params = new HashMap<>();
        List<String> pathList = new ArrayList<>();

        switch (Integer.parseInt(files.getId())) {
            case 10:
                /**
                 * 新开：1022-G-刻章.doc
                 */
                params = setParam10(params, documentRequest.getEnterpriseId());
                break;
            case 11:
                /**
                 * 新开：1037-G-租房协议.doc
                 */
                params = setParam11(params, documentRequest.getEnterpriseId());
                break;
            case 12:
                params = setParam12(params, documentRequest.getEnterpriseId());
                break;
            case 13:
                /**
                 * 新开：1119-G-指定代表或者共同委托代理人授权委托书.doc
                 */
                params = setParam13(params, documentRequest.getEnterpriseId());
                if (!StringUtils.isEmpty(params.get("idCardFirst"))) {
//                    pathList.add("C:\\Users\\Administrator\\Desktop\\garden\\身份证复印.png");
                    String idCardFirst = params.get("idCardFirst");
                    String path = idCardFirst.substring(idCardFirst.indexOf("idcard"));
                    pathList.add("/var/www/html/" + path);
                }
                if (!StringUtils.isEmpty(params.get("idCardSecond"))) {
                    String idCardSecond = params.get("idCardSecond");
                    String path = idCardSecond.substring(idCardSecond.indexOf("idcard"));
                    pathList.add("/var/www/html/" + path);
                }
                break;
            case 1:
            case 17:
            case 26:
            case 33:
                /**
                 * 迁入：2019-G-备案审核表.doc
                 */
                params = setParam1And17And26And33(params, documentRequest.getEnterpriseId());
                break;
            case 27:
            case 34:
                /**
                 * 迁入：2400-G-内资公司变更登记申请书2019.doc
                 */
                params = setParam27And34(params, documentRequest.getEnterpriseId());
                break;
            case 28:
            case 35:
//                params = setParam28And35(params,documentRequest.getEnterpriseId());
                Document document = WordUtil.createTable();
                DocUtil.DocWriteResponse(fileName, response, document, FileFormat.Doc);
                return;
            case 5:
            case 21:
            case 29:
            case 36:
                /**
                 * 迁入：2415-G-遗失承诺书.doc
                 */
                params = setParam5And21And29And36(params, documentRequest.getEnterpriseId());
                break;
            case 6:
            case 22:
            case 30:
            case 37:
                params = setParam6And22And30And37(params, documentRequest.getEnterpriseId());
                break;
            case 7:
            case 23:
            case 31:
            case 38:
                params = setParam7And23And31And38(params, documentRequest.getEnterpriseId());
                break;
            case 25:
            case 32:
            case 39:
                params = setParam25And32And39(params, documentRequest.getEnterpriseId());
                break;
            case 2:
            case 18:
                /**
                 * 新开：1108-G-个人独资企业设立登记申请书.doc
                 * 变更：2106-G-个人独资企业变更登记申请书.doc
                 */
                params = setParam2And18(params, documentRequest.getEnterpriseId());
                break;
            case 20:
                params = setParam20(params, documentRequest.getEnterpriseId());
                break;
            case 8:
            case 24:
                params = setParam8And24(params, documentRequest.getEnterpriseId());
                break;
            case 3:
            case 19:
                params = setParam3And19(params, documentRequest.getEnterpriseId());
                if (!StringUtils.isEmpty(params.get("idCardFirst"))) {
                    pathList.add("C:\\Users\\Administrator\\Desktop\\garden\\身份证复印.png");
//                    String idCardFirst = params.get("idCardFirst");
//                    String path = idCardFirst.substring(idCardFirst.indexOf("idcard"));
//                    pathList.add("/var/www/html/" + path);
                }
                if (!StringUtils.isEmpty(params.get("idCardSecond"))) {
//                    String idCardSecond = params.get("idCardSecond");
//                    String path = idCardSecond.substring(idCardSecond.indexOf("idcard"));
//                    pathList.add("/var/www/html/" + path);
                }
                break;

        }

//        if (pathList.size() > 0) {
//            params.put("（身份证复印件粘贴处）", StrUtils.blackIfNull(""));
//        }
        Document document = DocUtil.replace(path + fileName + ".doc", params);
        if (Integer.parseInt(files.getId()) == 13) document = WordUtil.insertImg13(document, pathList);

        if (Integer.parseInt(files.getId()) == 3 || Integer.parseInt(files.getId()) == 19)
            document = WordUtil.insertImg3And19(document, pathList);

        DocUtil.DocWriteResponse(fileName, response, document, FileFormat.Doc);
    }

    @Override
    public List<DocResp> getCompanyListByCompanyId(String companyId) throws Throwable {
        /**
         * 文件存储格式
         * basePath/流转状态code/企业类型code/文件名
         */
        Enterprise enterprise = enterpriseDao.findById(companyId).orElseThrow((Supplier<Throwable>) () -> new WebException(ErrorCodeConstants.RESULT_NOT_FOUND_CODE, ErrorCodeConstants.RESULT_NOT_FOUND_MSG));

        CommonConstants.COMPANY_PROCESS_STATUS statusCode = null;
        if (enterprise.getProcess() == 1) {
            statusCode = CommonConstants.COMPANY_PROCESS_STATUS.NEW;
        } else if (enterprise.getProcess() == 2 || enterprise.getProcess() == 3) {
            statusCode = CommonConstants.COMPANY_PROCESS_STATUS.UPDATE;
        } else {
            statusCode = CommonConstants.COMPANY_PROCESS_STATUS.CANCEL;
        }
        int enterpriseType = Util.getEnterpriseTypeByType(enterprise.getType());
        //获取企业状态和企业类型对应的文件
        File filesParentDir = new File(String.format(companyDocsBasePath + "%d/%d", statusCode.getCode(), enterpriseType));
        if (!filesParentDir.exists()) {
            return null;
        }
        File[] baseFiles = filesParentDir.listFiles();

        List<File> fileList = new ArrayList<>();
        fileList.addAll(Arrays.stream(baseFiles).collect(Collectors.toList()));

        //获取企业章程文件
        Project project = projectDao.findByEnterpriseId(enterprise.getId());
        if (!Objects.isNull(project)) {
            File filesParentBylawsDir = new File(String.format(companyDocsBasePath + "/BYLAWS/%s", BylawsEnum.getType(project.getEnterpriseArticle())));
            if (filesParentBylawsDir.exists()) {
                File[] bylawsFiles = filesParentBylawsDir.listFiles();
                if (bylawsFiles.length > 0) {
                    fileList.addAll(Arrays.stream(bylawsFiles).collect(Collectors.toList()));
                }
            }
        }

        List<DocResp> filesUrl = new ArrayList<>();
        for (File file : fileList) {
            DocResp doc = new DocResp();
            doc.setName(file.getName());
            doc.setDocPath(file.getAbsolutePath());
            filesUrl.add(doc);
        }
        return filesUrl;
    }

    @Override
    public void downLoadDoc(String filePath, String enterpriseId, HttpServletResponse response) throws WebException {
        /**
         * 遗失承诺书无参数
         */
        //法人
        Person masterPerson = personDao.findByEnterpriseIdAndIsMaster(enterpriseId, CommonConstants.YesOrNo.YES.getCode());
        PersonLog oldMasterPerson = personLogDao.findByEnterpriseIdAndIsMasterAndValid(enterpriseId, CommonConstants.YesOrNo.YES.getCode(), "0");
        //财务
        Person financePerson = personDao.findByEnterpriseIdAndIsFinance(enterpriseId, CommonConstants.YesOrNo.YES.getCode());
        //联系人
        Person contactorPerson = personDao.findByEnterpriseIdAndIsContact(enterpriseId, CommonConstants.YesOrNo.YES.getCode());
        //股东，监事，董事
        List<Member> memberList = memberDao.findByEnterpriseId(enterpriseId);

        QueryWrapper<EnterpriseLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("enterprise_id", enterpriseId).orderByDesc("create_time").last("limit 1");
        //变更前的历史记录
        EnterpriseLog oldEnterpriseInfo = enterpriseLogMapper.selectOne(queryWrapper);
        //项目资料
        Project project = projectDao.findByEnterpriseId(enterpriseId);

        List<MemberLog> oldMemberList = memberLogDao.findByEnterpriseIdAndValid(enterpriseId, "0");
        Enterprise enterprise = enterpriseDao.getOne(enterpriseId);

        Map<String, String> params = DocUtil.getDocParams(enterprise, masterPerson, contactorPerson, financePerson, oldEnterpriseInfo, memberList, project, oldMemberList, oldMasterPerson);

        Document document = DocUtil.replace(filePath, params);
        //获取股权变更详情
        List<TransferLog> transferLogList = transferLogDao.findTransferLogsByEnterpriseIdAndTransType(enterpriseId, 1);
        //获取增减资
        List<TransferLog> incrAndDecrList = transferLogDao.findTransferLogsByEnterpriseIdAndTransType(enterpriseId, 2);
        //找出股东
        List<Member> stockList = memberList.stream().filter(t ->
                t.getIsStock() == 1
        ).collect(Collectors.toList());

        if (stockList.size() > 0 && filePath.contains("章程") && filePath.contains("120")) {
            document = DocUtil.tableAddStock(document, stockList);
            document = DocUtil.addStockSign(document, stockList);

        }

        if (stockList.size() > 0 && filePath.contains("2412-G")) {
            document = DocUtil.addStockList(document, stockList);
        }
        if (stockList.size() > 0 && filePath.contains("2009-G")) {
//            if (stockList.size() == 1) {
//                throw new WebException(101, "公司只有一名股东时，请下载2010-G股东决定");
//            }
            document = DocUtil.addStockSign(document, stockList);
            document = DocUtil.addChange2009G(document, enterprise, oldEnterpriseInfo, transferLogList, incrAndDecrList, memberList, oldMemberList, project);
        }
        if (stockList.size() > 0 && filePath.contains("2010-G")) {
//            if (stockList.size() >1) {
//                throw new WebException(101, "公司一名股东以上时，请下载2009-G股东会决议");
//            }
            document = DocUtil.addStockSign(document, stockList);
            document = DocUtil.addChange2009G(document, enterprise, oldEnterpriseInfo, transferLogList, incrAndDecrList, memberList, oldMemberList, project);
        }
        if (stockList.size() > 0 && filePath.contains("2400-G")) {
            document = DocUtil.addChange2400G(document, enterprise, oldEnterpriseInfo, masterPerson, oldMasterPerson, memberList, oldMemberList);
        }
        if (stockList.size() > 0 && filePath.contains("2011-G")) {
            document = DocUtil.generateFor2011GNew(document, transferLogList, memberList, oldMemberList, enterprise.getCapital());
        }
        if (stockList.size() > 0 && filePath.contains("2416-G")) {
            document = DocUtil.generateFor2416G(document, transferLogList, memberList, oldMemberList);
            document = DocUtil.addOldStockSign(document, oldMemberList);
        }
        if (stockList.size() > 0 && filePath.contains("2003-G")) {
            document = DocUtil.addInfo2003G(document, stockList, oldMemberList, masterPerson, oldMasterPerson, oldEnterpriseInfo.getCapital());
        }
        if (stockList.size() > 0 && filePath.contains("2419-G")) {
            document = DocUtil.addStockList2419G(document, stockList, oldMemberList);
        }
        try {
            if (stockList.size() > 0 && filePath.contains("2031-G")) {
                document = DocUtil.addStockList1(document, stockList, enterprise);
                document = DocUtil.addStockSign(document, stockList);
            }

        } catch (WebException e) {
            throw e;
        }
        if (stockList.size() > 0 && filePath.contains("2502-G")) {
            document = DocUtil.addStockList2(document, stockList, enterprise);
            document = DocUtil.addStockSign(document, stockList);
            document = DocUtil.addStockTagIndex(document, stockList);
        }
        if (stockList.size() > 0 && filePath.contains("2602-G")) {
            document = DocUtil.addChangeInfo2602G(document, enterprise, oldEnterpriseInfo, masterPerson, oldMasterPerson, memberList, oldMemberList);
        }

        if (filePath.contains("2406-G")) {
            document = DocUtil.addStock2406G(document, stockList, enterprise.getCapital());
        }

        if (filePath.contains("2007-G")) {
            document = DocUtil.addStock2007G(document, masterPerson, oldMasterPerson, enterprise, memberList);
        }

        if (filePath.contains("1408-G")) {
            document = DocUtil.addStock1408G(document, stockList);
        }
        if (filePath.contains("1119-G") || filePath.contains("1023-G")) {
            String path = companyDocsBasePath + "/pyc_idcard.png";
            document = DocUtil.addPic1119G(document, path);
        }
        if (filePath.contains("1028-G")) {
            String path = companyDocsBasePath + "/pyc_idcard.png";
            document = DocUtil.addStock1208G(document, stockList, path);
        }
        if (filePath.contains("1027-G")) {
            String path = companyDocsBasePath + "/pyc_idcard.png";
            document = DocUtil.addStock1208G(document, stockList, path);
        }

        String fileName = org.apache.commons.lang3.StringUtils.substringBeforeLast(filePath, "\\.");

        DocUtil.DocWriteResponse(fileName, response, document, FileFormat.Doc);
    }

    @Override
    public void downloadSupport(String filePath, String enterpriseName,int downDate, HttpServletResponse response) throws WebException {
        Enterprise enterprise = enterpriseDao.findByName(enterpriseName);
        if (enterprise == null) throw new WebException(101, "未找到对应企业");
        String enterpriseId = enterprise.getId();

        //法人
        Person masterPerson = personDao.findByEnterpriseIdAndIsMaster(enterpriseId, CommonConstants.YesOrNo.YES.getCode());

        //联系人
        Person contactorPerson = personDao.findByEnterpriseIdAndIsContact(enterpriseId, CommonConstants.YesOrNo.YES.getCode());


        Map<String, String> params = DocUtil.getDocParams(enterprise, masterPerson, contactorPerson);

        filePath = fileUpSaveConfig.getSupportFilesDir() + "doc/" + filePath;

        Document document = DocUtil.replace(filePath, params);


                //获取三方协议
        SupportContract supportContract = null;

        EnterpriseSupportLog enterpriseSupportLog=enterpriseSupportLogDao.findByEnterpriseNameAndDate(enterpriseName,downDate);


        //1 获取当前企业有效的扶持协议
        List<SupportContract> supportContractList = supportContractDao.findByEnterpriseNameAndDate(enterpriseName, downDate);



        if (supportContractList.size() > 1)
            throw new WebException(101, enterprise + "存在两份有效协议，无法计算，请检查数据。");

        //没数据退出当前循环
        if (!Objects.isNull(supportContractList) &&  !supportContractList.isEmpty()){
            supportContract = supportContractList.get(0);
        }

        if(Objects.isNull(supportContract)){
            throw new WebException(101, enterprise + "无有效协议，无法计算，请检查数据。");
        }

        document = DocUtil.addSupportInfo(document, enterpriseSupportLog,downDate);


//
//        if (filePath.contains("青发集团财政专项扶持资金申请表")) {
//            document = DocUtil.addStock2406G(document, stockList, enterprise.getCapital());
//        }


        String fileName = org.apache.commons.lang3.StringUtils.substringBeforeLast(filePath, "\\.");

        DocUtil.DocWriteResponse(fileName, response, document, FileFormat.Doc);
    }

    // todo 固定文件占位符，占位符为key，表字段为value，封装一个工具方法根据key把对应value动态替换为库中查出的值
    private HashMap<String, String> setParam10(HashMap<String, String> params, String enterpriseId) {
        // 公司信息
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();

        // 股东信息
        List<Member> memberList = memberDao.findByEnterpriseId(enterpriseId);
        memberList = memberList.stream().filter(member -> member.getIsStock() == 1).collect(Collectors.toList());
        Member member = new Member();
        if (memberList.size() > 0) member = memberList.get(0);

        // 法人信息
        List<Person> personList = personDao.findByEnterpriseId(enterpriseId);
        personList = personList.stream().filter(person -> person.getIsMaster() == 1).collect(Collectors.toList());
        Person person = new Person();
        if (personList.size() > 0) person = personList.get(0);

        params.put("gardenName", StringUtils.isEmpty(enterprise.getSource()) ? "测试园区" : enterprise.getSource());
        params.put("companyName", enterprise.getName());
        params.put("member", StringUtils.isEmpty(member.getName()) ? "测试股东" : member.getName());
        params.put("person", StringUtils.isEmpty(person.getName()) ? "测试法人" : person.getName());
        return params;
    }

    private HashMap<String, String> setParam11(HashMap<String, String> params, String enterpriseId) {
        // todo 租房合同，甲方firstParty为项目承房出租人，area为租赁面积，charterMoney为年租金，
        //  monthPay为月租金（年租金/租赁时间？），startTime、endTime为租赁起止时间，signTime合同日期
        //  项目资料暂时没有！！！！
        // 项目信息
        // 公司资料 乙方为公司
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();

        params.put("firstParty", "测试甲方");
        params.put("secondParty", enterprise.getName());
        params.put("gardenName", StringUtils.isEmpty(enterprise.getSource()) ? "测试园区" : enterprise.getSource());
        params.put("area", "150");
        params.put("charterMoney", "500000");
        params.put("monthPay", "5000");
        params.put("startTime", "2021年3月5日");
        params.put("endTime", "2022年3月4日");
        params.put("signTime", "2021年3月6日");
        return params;
    }

    private HashMap<String, String> setParam12(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        // 股东信息
        List<Member> memberList = memberDao.findByEnterpriseId(enterpriseId);
        memberList = memberList.stream().filter(member -> member.getIsStock() == 1).collect(Collectors.toList());
        Member member = new Member();
        if (memberList.size() > 0) member = memberList.get(0);

        // 联系人信息
        List<Person> personList = personDao.findByEnterpriseId(enterpriseId);
        List<Person> contactList = personList.stream().filter(person -> person.getIsContact() == 1).collect(Collectors.toList());
        Person contact = new Person();
        if (contactList.size() > 0) contact = contactList.get(0);

        // 财务信息
        List<Person> financeList = personList.stream().filter(person -> person.getIsFinance() == 1).collect(Collectors.toList());
        Person finance = new Person();
        if (financeList.size() > 0) finance = financeList.get(0);

        params.put("companyName", enterprise.getName());
        params.put("registerNum", StrUtils.blackIfNull(enterprise.getRegisterNum()));
        params.put("registerAddress", StrUtils.blackIfNull(enterprise.getRegisterAddress()));
        params.put("phone", StrUtils.blackIfNull(""));
        params.put("zipCode", StrUtils.blackIfNull(enterprise.getZipcode()));
        params.put("capital", StrUtils.blackIfNull(enterprise.getCapital()));
        params.put("employee", StrUtils.blackIfNull(""));
        params.put("business", StrUtils.blackIfNull(enterprise.getBusiness()));
        params.put("signTime", "2021年3月6日");

        params.put("member", StrUtils.blackIfNull(member.getName()));
        params.put("memberSex", StrUtils.blackIfNull(member.getSex()));
        params.put("memberBirthday", StrUtils.blackIfNull(member.getBirthday()));
        params.put("memberNation", StrUtils.blackIfNull(member.getNation()));
        params.put("memberPhone", StrUtils.blackIfNull(member.getPhone()));
        params.put("memberZipcode", StrUtils.blackIfNull(member.getOfficeZipcode()));
        params.put("memberEmail", StrUtils.blackIfNull(member.getEmail()));
        params.put("memberType", StrUtils.blackIfNull(member.getType()));
        params.put("memberIdCard", StrUtils.blackIfNull(member.getIdcard()));
        params.put("memberAddress", StrUtils.blackIfNull(member.getAddress()));
        params.put("memberIdCardFirst", StrUtils.blackIfNull(""));
        params.put("memberIdCardSecond", StrUtils.blackIfNull(""));
        // 联络人
        params.put("contactName", StrUtils.blackIfNull(contact.getName()));
        params.put("contactTel", StrUtils.blackIfNull(contact.getPhone()));
        params.put("contactPhone", StrUtils.blackIfNull(contact.getMobile()));
        params.put("contactEmil", StrUtils.blackIfNull(contact.getEmail()));
        params.put("contactType", StrUtils.blackIfNull(contact.getType()));
        params.put("contactIdCard", StrUtils.blackIfNull(contact.getIdcard()));
        params.put("contactIdCardFirst", StrUtils.blackIfNull(""));
        params.put("contactIdCardSecond", StrUtils.blackIfNull(""));
        // 财务人
        params.put("financialName", StrUtils.blackIfNull(finance.getName()));
        params.put("financialTel", StrUtils.blackIfNull(finance.getPhone()));
        params.put("financialPhone", StrUtils.blackIfNull(finance.getMobile()));
        params.put("financialEmil", StrUtils.blackIfNull(finance.getEmail()));
        params.put("financialType", StrUtils.blackIfNull(finance.getType()));
        params.put("financialIdCard", StrUtils.blackIfNull(finance.getIdcard()));
        params.put("financialIdCardFirst", StrUtils.blackIfNull(""));
        params.put("financialIdCardSecond", StrUtils.blackIfNull(""));

        return params;
    }

    private HashMap<String, String> setParam13(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();

        List<Person> personList = personDao.findByEnterpriseId(enterpriseId);
        personList = personList.stream().filter(person -> person.getIsMaster() == 1).collect(Collectors.toList());
        String idCardFirst = "";
        String idCardSecond = "";
        if (personList.size() > 0) {
            idCardFirst = personList.get(0).getFront();
            idCardSecond = personList.get(0).getBack();
        }

        params.put("proposer", "测试申请人");
        params.put("agent", "测试代理人");
        params.put("companyName", enterprise.getName());
        params.put("agentTel", "59729968");
        params.put("agentPhone", "13916946605");
        params.put("signTime", "2021年3月6日");
        params.put("idCardFirst", idCardFirst);
        params.put("idCardSecond", idCardSecond);
        return params;
    }

    private HashMap<String, String> setParam1And17And26And33(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        params.put("companyName", enterprise.getName());
        params.put("dateTime", "2021年3月8日");
        return params;
    }

    private HashMap<String, String> setParam27And34(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        params.put("companyName", enterprise.getName());
        params.put("registerNum", StrUtils.blackIfNull(enterprise.getRegisterNum()));
        params.put("contactPhone", StrUtils.blackIfNull(enterprise.getContactPhone()));
        params.put("zipcode", StrUtils.blackIfNull(enterprise.getZipcode()));
        return params;
    }

    private HashMap<String, String> setParam28And35(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        String capital = "";
        if (!StringUtils.isEmpty(enterprise.getCapital())) {
            capital = enterprise.getCapital().substring(0, enterprise.getCapital().indexOf("."));
        }
        List<Member> memberList = memberDao.findMembersByEnterpriseIdAndIsStockEquals(enterpriseId, 1);
        if (memberList.size() > 0) {
            for (int i = 0; i < memberList.size(); i++) {
                String realPutAmount = memberList.get(i).getRealPutAmount();
                String ratio = "";
                if (Integer.parseInt(capital) > 0 && !StringUtils.isEmpty(realPutAmount)) {
                    double result = Double.parseDouble(realPutAmount) / Double.parseDouble(capital);
                    int v = (int) (result * 100);
                    ratio = v + "%";
                }
                params.put("member" + (i + 1), memberList.get(i).getName());
                params.put("country" + (i + 1), StrUtils.blackIfNull(memberList.get(i).getCountry()));
                params.put("type" + (i + 1), StrUtils.blackIfNull(memberList.get(i).getType()));
                params.put("idCard" + (i + 1), StrUtils.blackIfNull(memberList.get(i).getIdcard()));
                params.put("putAmount" + (i + 1), StringUtils.isEmpty(memberList.get(i).getPutAmount()) ? "" : memberList.get(i).getPutAmount() + "万元");
                params.put("realPutAmount" + (i + 1), StringUtils.isEmpty(realPutAmount) ? "" : realPutAmount + "万元");
                params.put("realPutDate" + (i + 1), StrUtils.blackIfNull(memberList.get(i).getRealPutDate()));
                params.put("putType" + (i + 1), StrUtils.blackIfNull(memberList.get(i).getPutType()));
                params.put("ratio" + (i + 1), StrUtils.blackIfNull(ratio));
            }
        }
        if (memberList.size() < 8) {
            for (int j = memberList.size() + 1; j <= 8; j++) {
                params.put("member" + j, StrUtils.blackIfNull(""));
                params.put("country" + j, StrUtils.blackIfNull(""));
                params.put("type" + j, StrUtils.blackIfNull(""));
                params.put("idCard" + j, StrUtils.blackIfNull(""));
                params.put("putAmount" + j, StrUtils.blackIfNull(""));
                params.put("realPutAmount" + j, StrUtils.blackIfNull(""));
                params.put("realPutDate" + j, StrUtils.blackIfNull(""));
                params.put("putType" + j, StrUtils.blackIfNull(""));
                params.put("ratio" + j, StrUtils.blackIfNull(""));
            }
        }
        return params;
    }

    private HashMap<String, String> setParam5And21And29And36(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        params.put("companyName", enterprise.getName());
        return params;
    }

    private HashMap<String, String> setParam6And22And30And37(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        String capital = "";
        if (!StringUtils.isEmpty(enterprise.getCapital())) {
            capital = enterprise.getCapital().substring(0, enterprise.getCapital().indexOf("."));
        }

        Person person = personDao.findByEnterpriseIdAndIsMaster(enterpriseId, 1);
        if (person == null) {
            person = new Person();
        }

        params.put("dateTime", "2021年3月9日");
        params.put("companyName", enterprise.getName());
        params.put("contactPhone", StrUtils.blackIfNull(enterprise.getContactPhone()));
        params.put("capital", StrUtils.blackIfNull(capital));
        params.put("registerAddress", StrUtils.blackIfNull(enterprise.getRegisterAddress()));
        params.put("zipcode", StrUtils.blackIfNull(enterprise.getZipcode()));
        params.put("person", StrUtils.blackIfNull(person.getName()));
        params.put("personPhone", StrUtils.blackIfNull(person.getMobile()));
        return params;
    }

    private HashMap<String, String> setParam7And23And31And38(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        Person person = personDao.findByEnterpriseIdAndIsMaster(enterpriseId, 1);
        if (person == null) {
            person = new Person();
        }
        params.put("signTime", "2021年3月9日");
        params.put("companyName", enterprise.getName());
        params.put("gardenName", StringUtils.isEmpty(enterprise.getSource()) ? "测试园区" : enterprise.getSource());
        params.put("business", StrUtils.blackIfNull(enterprise.getBusiness()));
        params.put("person", StrUtils.blackIfNull(person.getName()));
        return params;
    }

    private HashMap<String, String> setParam25And32And39(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        params.put("signTime", "2021年3月9日");
        params.put("companyName", enterprise.getName());
        params.put("registerAddress", StrUtils.blackIfNull(enterprise.getRegisterAddress()));
        return params;
    }

    private HashMap<String, String> setParam2And18(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();

        List<Member> memberList = memberDao.findByEnterpriseId(enterpriseId);
        memberList = memberList.stream().filter(member -> member.getIsStock() == 1).collect(Collectors.toList());
        Member member = new Member();
        if (memberList.size() > 0) member = memberList.get(0);

        Person person = personDao.findByEnterpriseIdAndIsFinance(enterpriseId, 1);
        if (person == null) {
            person = new Person();
        }

        params.put("companyName", enterprise.getName());
        params.put("registerNum", StrUtils.blackIfNull(enterprise.getRegisterNum()));
        params.put("registerAddress", StrUtils.blackIfNull(enterprise.getRegisterAddress()));
        params.put("contactPhone", StrUtils.blackIfNull(enterprise.getContactPhone()));
        params.put("zipcode", StrUtils.blackIfNull(enterprise.getZipcode()));
        params.put("signTime", "2021年3月10日");
        params.put("member", StrUtils.blackIfNull(member.getName()));
        params.put("memberSex", StrUtils.blackIfNull(member.getSex()));
        params.put("memberBirthday", StrUtils.blackIfNull(member.getBirthday()));
        params.put("memberNation", StrUtils.blackIfNull(member.getNation()));
        params.put("memberMobile", StrUtils.blackIfNull(member.getMobile()));
        params.put("memberPhone", StrUtils.blackIfNull(member.getPhone()));
        params.put("memberZipcode", StrUtils.blackIfNull(member.getZipcode()));
        params.put("memberEmail", StrUtils.blackIfNull(member.getEmail()));
        params.put("memberType", StrUtils.blackIfNull(member.getType()));
        params.put("memberIdCard", StrUtils.blackIfNull(member.getIdcard()));
        params.put("memberAddress", StrUtils.blackIfNull(member.getAddress()));
        params.put("finance", StrUtils.blackIfNull(person.getName()));
        params.put("financePhone", StrUtils.blackIfNull(person.getPhone()));
        params.put("financeMobile", StrUtils.blackIfNull(person.getMobile()));
        params.put("financeEmil", StrUtils.blackIfNull(person.getEmail()));
        params.put("financeType", StrUtils.blackIfNull(person.getType()));
        params.put("financeIdCard", StrUtils.blackIfNull(person.getIdcard()));
        return params;
    }

    private HashMap<String, String> setParam20(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();

        List<Member> memberList = memberDao.findByEnterpriseId(enterpriseId);
        memberList = memberList.stream().filter(member -> member.getIsStock() == 1).collect(Collectors.toList());
        Member member = new Member();
        if (memberList.size() > 0) member = memberList.get(0);

        params.put("companyName", enterprise.getName());
        params.put("member", StrUtils.blackIfNull(member.getName()));
        params.put("sex", StrUtils.blackIfNull(member.getSex()));
        params.put("birthday", StrUtils.blackIfNull(member.getBirthday()));
        params.put("nation", StrUtils.blackIfNull(member.getNation()));
        params.put("mobile", StrUtils.blackIfNull(member.getMobile()));
        params.put("phone", StrUtils.blackIfNull(member.getPhone()));
        params.put("zipcode", StrUtils.blackIfNull(member.getZipcode()));
        params.put("type", StrUtils.blackIfNull(member.getType()));
        params.put("email", StrUtils.blackIfNull(member.getEmail()));
        params.put("address", StrUtils.blackIfNull(member.getType()));
        params.put("idCard", StrUtils.blackIfNull(member.getIdcard()));
        params.put("dateTime", "2021年3月11日");
        return params;
    }

    private HashMap<String, String> setParam8And24(HashMap<String, String> params, String enterpriseId) {
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        params.put("companyName", enterprise.getName());
        params.put("singTime", "2021年3月11日");
        return params;
    }

    private HashMap<String, String> setParam3And19(HashMap<String, String> params, String enterpriseId) {
        // 企业
        Enterprise enterprise = enterpriseDao.findById(enterpriseId).get();
        // 法人
        Person person = personDao.findByEnterpriseIdAndIsMaster(enterpriseId, 1);
        String idCardFirst = "";
        String idCardSecond = "";

        if (person == null) {
            person = new Person();
        } else {
            idCardFirst = person.getFront();
            idCardSecond = person.getBack();
        }

        params.put("companyName", enterprise.getName());
        params.put("registerNum", StrUtils.blackIfNull(enterprise.getRegisterNum()));
        params.put("contactPhone", StrUtils.blackIfNull(enterprise.getContactPhone()));
        params.put("zipCode", StrUtils.blackIfNull(enterprise.getZipcode()));
        params.put("person", StrUtils.blackIfNull(person.getName()));
        params.put("personPhone", StrUtils.blackIfNull(person.getPhone()));
        params.put("personMobile", StrUtils.blackIfNull(person.getMobile()));
        params.put("personType", StrUtils.blackIfNull(person.getType()));
        params.put("personIdCard", StrUtils.blackIfNull(person.getIdcard()));
        params.put("dateTime", "2021年3月12日");
        params.put("idCardFirst", idCardFirst);
        params.put("idCardSecond", idCardSecond);
        return params;
    }
}
