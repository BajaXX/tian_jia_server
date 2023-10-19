package com.bemore.api.util;

import com.bemore.api.exception.WebException;
import lombok.NonNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Util {

    public static String getDateValue() {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
            return sdf.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

    public static String formatDate(String dateStr, String sourceFormat, String targetFormat) {
        try {
            if(dateStr==null || "".equals(dateStr)) return "";
            SimpleDateFormat sdf = new SimpleDateFormat(sourceFormat);
            Date date = sdf.parse(dateStr);
            SimpleDateFormat tdf = new SimpleDateFormat(targetFormat);
            return tdf.format(date);
        } catch (Exception ex) {
            return "";
        }
    }

    public static int getEnterpriseType(String name) {
        if (name.indexOf("有限") > 0) {
            return Constant.LTD_COMPANY;
        } else if (name.indexOf("合伙") > 0) {
            return Constant.PATTERN_COMPANY;
        } else {
            return Constant.PERSONAL_COMPANY;
        }
    }

    /**
     * 根據企業小類獲取企業大類
     *
     * @param type 企業小類
     * @return 企業大類
     */
    public static int getEnterpriseTypeByType(@NonNull String type) {
        if (type.contains("有限合伙")) {
            return Constant.LTD_PATTERN_COMPANY;
        } else if (type.contains("合伙")) {
            return Constant.PATTERN_COMPANY;
        } else if (type.contains("有限")) {
            return Constant.LTD_COMPANY;
        } else {
            return Constant.PERSONAL_COMPANY;
        }
    }

    public static String byteToStr(byte[] byteArray) {
        String strDigest = "";
        for (int i = 0; i < byteArray.length; i++) {
            strDigest += byteToHexStr(byteArray[i]);
        }
        return strDigest;
    }

    public static String byteToHexStr(byte mByte) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] tempArr = new char[2];
        tempArr[0] = Digit[(mByte >>> 4) & 0X0F];
        tempArr[1] = Digit[mByte & 0X0F];

        String s = new String(tempArr);
        return s;
    }

    public static String getDateTime() {
        Date d = new Date();
        return (d.getYear() + 1900) + "年" + (d.getMonth() + 1) + "月" + d.getDate() + "日" + d.getHours() + "时" + d.getMinutes() + "分";
    }

    /**
     * @Description Double保留length位小数，进行四舍五入
     * @Title roundDouble
     * @Param [num, length]
     * @Return double
     * @Author Louis
     * @Date 2022/04/25 19:53
     */
    public static double roundDouble(double num, int length) {
        double multiple = Math.pow(10, length);
        return Math.round(num * multiple) / multiple;
    }

    public static String formatCapital(String capital) {
        if (capital == null || capital.equals("")) return "";

        String[] s = capital.split("\\.");
        if (s.length == 1) return capital;
        if (s.length == 2) {
            if (Integer.valueOf(s[1]) == 0) {
                return s[0];
            } else {
                return String.format("%.4f", Double.valueOf(capital));
            }
        }

        return "";
    }

    public static String formatDouble(String doubleStr) {
        if (doubleStr == null || "".equals(doubleStr)) return "";

        DecimalFormat decimalFormat = new DecimalFormat("0.####");
        double l = Double.valueOf(doubleStr);
        String format = decimalFormat.format(l);
        return format;
    }

    public static String formatDouble(Double doubleStr) {
        if (doubleStr == null) return "";

        DecimalFormat decimalFormat = new DecimalFormat("0.####");
        String format = decimalFormat.format(doubleStr);
        return format;
    }

    public static double formatDouble(double number, int decimalPlaces) {
        DecimalFormat df = new DecimalFormat("#." + getDecimalPlacesPattern(decimalPlaces));
        df.setRoundingMode(RoundingMode.HALF_UP);
        String result = df.format(number);
        return Double.parseDouble(result);
    }

    private static String getDecimalPlacesPattern(int decimalPlaces) {
        StringBuilder pattern = new StringBuilder();
        for (int i = 0; i < decimalPlaces; i++) {
            pattern.append("#");
        }
        return pattern.toString();
    }

    /**
     * 比较两个数组是否一样
     *
     * @param arr1
     * @param arr2
     * @return
     */
    public static boolean isEqual(String[] arr1, String[] arr2) {
        if (arr1 == null || arr2 == null) {
            return false;
        }
        int count1 = arr1.length;
        int count2 = arr2.length;
        if (count1 != count2) {
            return false;
        }
        Arrays.sort(arr1);
        Arrays.sort(arr2);
        for (int i = 0; i < count1; i++) {
            if (!arr1[i].contentEquals(arr2[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取序列标签
     *
     * @param index
     * @return
     */
    public static String getIndexTag(int index) {
        String[] tags = new String[]{"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸", "子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥", "甲子", "甲丑", "甲寅", "甲卯", "甲辰", "甲巳", "甲午", "甲未", "甲申", "甲酉", "甲戌", "甲亥", "乙子", "乙丑", "乙寅", "乙卯", "乙辰", "乙巳", "乙午", "乙未", "乙申", "乙酉", "乙戌", "乙亥", "丙子", "丙丑", "丙寅", "丙卯", "丙辰", "丙巳", "丙午", "丙未", "丙申", "丙酉", "丙戌", "丙亥"};
        if (index > tags.length - 1) return "";
        return tags[index];
    }

    /**
     * 数字转汉字
     *
     * @param number
     * @return
     */
    public static String numberToChinese(int number) {
        String[] numbers = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        String[] units = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十"};
        String sign = number < 0 ? "负" : "";
        if (number < 0) {
            number = -number;
        }
        StringBuilder result = new StringBuilder(sign);
        String string = String.valueOf(number);
        int n = string.length();
        char[] numberCharArray = string.toCharArray();
        for (int i = 0; i < n; i++) {
            int digNum = n - i; // 位数
            int num = numberCharArray[i] - '0';
            if (num != 0) {
                result.append(numbers[num]).append(units[digNum - 1]);
                continue;
            }

            if (result.toString().endsWith(numbers[0])) {
                // 如果是单位所在的位数，则去除上一个0，加上单位
                if (digNum % 4 == 1) {
                    result.deleteCharAt(result.length() - 1);
                    result.append(units[digNum - 1]);
                }
            } else {
                result.append(numbers[0]);
            }
        }
        return result.toString();
    }


}
