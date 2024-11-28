package com.bemore.api.controller;

import com.bemore.api.dto.req.downDocReq;
import com.bemore.api.dto.resp.DocResp;
import com.bemore.api.entity.request.DocumentRequest;
import com.bemore.api.exception.WebException;
import com.bemore.api.service.DocumentService;
import com.bemore.api.service.NewEnterpriseDocGeneService;
import com.bemore.api.util.DocUtil;
import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/gardenApi/document")
public class DocumentController {

    @Autowired
    private DocumentService documentService;


    @GetMapping("/getCompanyListByCompanyId")
    public List<DocResp> getCompanyListByCompanyId(String companyId) throws Throwable {
        return documentService.getCompanyListByCompanyId(companyId);
    }

    @PostMapping("/downloadDoc")
    public void downloadDoc(downDocReq req, HttpServletResponse response) {
        try {
            documentService.downLoadDoc(req.getFilePath(), req.getEnterpriseId(), response);

        } catch (WebException e) {
            throw e;
        }
    }

    @PostMapping("/downloadSupport")
    public void downloadSupport(downDocReq req, HttpServletResponse response) {
        try {

            documentService.downloadSupport(req.getFilePath(), req.getEnterpriseId(),req.getDownDate(), response);

        } catch (WebException e) {
            throw e;
        }
    }

    @Autowired
    NewEnterpriseDocGeneService docGeneService;

    @GetMapping("/downLoad")
    public void documentDownLoad(HttpServletResponse response, DocumentRequest documentRequest) {
        documentService.downLoadDocumentById(response, documentRequest);
    }

    @GetMapping("/down1401")
    public void down1401(String enterpriseId, HttpServletResponse response) {
        docGeneService.generate1401Doc(enterpriseId, "1401-G-个人独资设立登记申请书（一窗通）2019", response);
    }

    @GetMapping("/down1409")
    public void down1409(String enterpriseId, HttpServletResponse response) {
        docGeneService.generate1409Doc(enterpriseId, "1409-G-个人独资设立登记申请书（一窗通）2019", response);
    }

    @GetMapping("/down1501")
    public void down1501(String enterpriseId, HttpServletResponse response) {
        docGeneService.generate1501Doc(enterpriseId, "1501-G-长三角金融产业园办证服务卡", response);
    }

    @GetMapping("/down1010")
    public void down1010(String enterpriseId, HttpServletResponse response) {
        docGeneService.generate1010Doc(enterpriseId, "1010-G-个人独资企业投资人（合伙企业全体合伙人）委托代理人的委托书", response);
    }
}
