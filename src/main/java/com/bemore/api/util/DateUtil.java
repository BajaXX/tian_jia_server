package com.bemore.api.util;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class DateUtil {

    public static Date string2Date(String date) {
        if (!StringUtils.hasLength(date)) return null;
        Date parse = new Date();
        SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd");
        try {
            parse = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    public static String getDefaultQueryDateOnTax() {
        int year = LocalDate.now().getYear();
        int month = LocalDate.now().getMonthValue() - 1;
        if (month == 0) {
            year = year - 1;
            month = 12;
        }
        return year + "-" + (month > 9 ? month : "0" + month);
    }

    /**
     * @Description 当前年份
     * @Title currentYear
     * @Param []
     * @Return int
     * @Author Louis
     * @Date 2022/04/25 17:58
     */
    public static int currentYear() {
        return LocalDate.now().getYear();
    }

    /**
     * @Description 当前月份
     * @Title currentMonth
     * @Param []
     * @Return int
     * @Author Louis
     * @Date 2022/04/25 19:28
     */
    public static int currentMonth() {
        return LocalDate.now().getMonthValue();
    }

}
