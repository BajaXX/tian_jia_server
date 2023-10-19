package com.bemore.api.util;

import com.bemore.api.config.OCRConfig;
import com.bemore.api.constant.OCRConstants;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.ocr.v20181119.OcrClient;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.BizLicenseOCRResponse;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRRequest;
import com.tencentcloudapi.ocr.v20181119.models.IDCardOCRResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;

@Slf4j
public class OCRParseUtil {

    /**
     * @param config    ocr配置参数
     * @param idCardUrl 身份证地址
     * @param frontBack 正反面 1：正面 0 反面
     * @return
     */
    public static IDCardOCRResponse parseIdCard(OCRConfig config, String idCardUrl, Integer frontBack) {
        IDCardOCRResponse resp = new IDCardOCRResponse();
        Credential cred = new Credential(config.getSecretId(),
                config.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(config.getEndPoint());
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        OcrClient client = new OcrClient(cred, config.getRegion(), clientProfile);
        IDCardOCRRequest req = new IDCardOCRRequest();
        req.setImageUrl(idCardUrl);
        if (OCRConstants.ID_CARD.FRONT.getCode() == frontBack) {
            req.setCardSide(OCRConstants.ID_CARD.FRONT.getValue());
        } else {
            req.setCardSide(OCRConstants.ID_CARD.BACK.getValue());
        }


        try {
            resp = client.IDCardOCR(req);
        } catch (TencentCloudSDKException e) {
            log.error("身份证解析发生异常:", e);
            log.error("config:{},idUrl:{}", config, idCardUrl);
        }
        return resp;
    }

    //    public static BizLicenseOCRResponse parseBizLicense(OCRConfig config, InputStream inputStream){
    public static BizLicenseOCRResponse parseBizLicense(OCRConfig config, String bizLicenseUrl) throws Exception {
        BizLicenseOCRResponse resp = new BizLicenseOCRResponse();
        Credential cred = new Credential(config.getSecretId(),
                config.getSecretKey());
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setProtocol(HttpProfile.REQ_HTTPS);
        httpProfile.setEndpoint(config.getEndPoint());

        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        clientProfile.setDebug(true);
        OcrClient client = new OcrClient(cred, config.getRegion(), clientProfile);
        BizLicenseOCRRequest req = new BizLicenseOCRRequest();
        req.setImageUrl(bizLicenseUrl);
//        req.setImageUrl("http://52.82.105.214:8090/file/biz/1656126244831.pdf");
//        String imgInput = ImgBase64.getImgInput(inputStream);
//        req.setImageBase64(imgInput);
        try {
//            SslUtil.ignoreSsl();
            resp = client.BizLicenseOCR(req);
        } catch (TencentCloudSDKException e) {
            log.error("营业执照解析发生异常:", e);
            log.error("config:{},idUrl:{}", config, bizLicenseUrl);
        }
        return resp;
    }
}
