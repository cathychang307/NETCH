
package com.bot.cqs.query.util.idValidation;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * <pre>
 * 
 * </pre>
 * 
 * @author Sunkist Wang
 * @version
 *          <ul>
 *          <li>2016年6月22日,Sunkist Wang,update NEW_PATTERN
 *          </ul>
 */
public class ForeignerIdValidation {

    public static final Pattern OLD_PATTERN_1 = Pattern.compile("(([1][9])|([2][0]))\\d{6}[A-Za-z][A-Za-z]");

    public static final Pattern OLD_PATTERN_2 = Pattern.compile("[A-Za-z][0-9]{6}");

    public static final Pattern NEW_PATTERN = Pattern.compile("[A-Za-z][A-Za-z][0-9]{8}");

    private static final char[] pidCharArray = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

    // 原居留證第一碼英文字應轉換為10~33，十位數*1，個位數*9，這裡直接作[(十位數*1) mod 10] + [(個位數*9) mod 10]
    private static final int[] pidResidentFirstInt = { 1, 10, 9, 8, 7, 6, 5, 4, 9, 3, 2, 2, 11, 10, 8, 9, 8, 7, 6, 5, 4, 3, 11, 3, 12, 10 };

    // 原居留證第二碼英文字應轉換為10~33，並僅取個位數*6，這裡直接取[(個位數*6) mod 10]
    private static final int[] pidResidentSecondInt = { 0, 8, 6, 4, 2, 0, 8, 6, 2, 4, 2, 0, 8, 6, 0, 4, 2, 0, 8, 6, 4, 2, 6, 0, 8, 4 };
    
    // 新式外來人口證號對應換算數字表
    public static final int[] LOCATION_NUMBER = { 10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25, 26, 27, 28, 29, 32, 30, 31, 33 };

    // 新式外來人口證號對應特定數
    private static final int PARAM_NUM[] = new int[] { 1, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

    public static boolean isForeignerIdValid(String id) {
        return isForeignerIdValidOld(id) || isForeignerIdValidNew(id) || isForeignerIdValidNew2020(id);
    }

    public static boolean isForeignerIdValidOld(String id) {

        if (id == null)
            return false;
        if (id.length() != 10)
            return false;

        if (!OLD_PATTERN_1.matcher(id).matches()) {
            return false;
        }

        char[] chars = id.toCharArray();

        // check first 2 char
        if (!id.startsWith("19") && id.startsWith("20"))
            return false;

        // check last 2 char
        for (int i = 8; i < 10; i++) {
            if (chars[i] < 'A' || chars[i] > 'Z')
                if (chars[i] < 'a' || chars[i] > 'z')
                    return false;
        }

        //
        for (int i = 2; i < 8; i++)
            if (chars[i] < '0' || chars[i] > '9')
                return false;

        return true;
    }

    public static boolean isForeignerIdValidOld2(String id) {
        if (id == null)
            return false;
        if (id.length() != 7)
            return false;

        if (!OLD_PATTERN_2.matcher(id).matches()) {
            return false;
        }

        return true;
    }

    public static boolean isForeignerIdValidNew(String id) {

        if (id == null)
            return false;
        if (id.length() != 10)
            return false;

        if (!NEW_PATTERN.matcher(id).matches()) {
            return false;
        }

        char[] chars = id.toCharArray();
        int verifyNum = 0;

        verifyNum += pidResidentFirstInt[Arrays.binarySearch(pidCharArray, chars[0])];
        // 第二碼
        verifyNum += pidResidentSecondInt[Arrays.binarySearch(pidCharArray, chars[1])];
        // 第三~八碼
        for (int i = 2, j = 7; i < 9; i++, j--) {
            verifyNum += Character.digit(chars[i], 10) * j;
        }
        // 檢查碼
        verifyNum = (10 - (verifyNum % 10)) % 10;

        return verifyNum == Character.digit(chars[9], 10);
    }
    
    public static boolean isForeignerIdValidNew2020(String id) {
        if (id == null)
            return false;
        if (id.length() != 10)
            return false;

        char[] chars = id.toCharArray();
        int baseNum[] = new int[10];

        // 1st char
        int pos = chars[0] - 'A';
        if (pos < 0 || pos > 25)
            return false;

        baseNum[0] = LOCATION_NUMBER[pos] / 10;
        baseNum[1] = LOCATION_NUMBER[pos] % 10;

        // check numeric
        for (int i = 1; i < 9; i++) {
            pos = chars[i] - '0';
            if (pos < 0 || pos > 9)
                return false;
            else
                baseNum[i + 1] = pos;
        }

        // 第二位8為男性，9為女性
        if (chars[1] != '8' && chars[1] != '9')
            return false;

        // 加總
        int sum = 0;
        for (int i = 0; i < PARAM_NUM.length; i++)
            sum += PARAM_NUM[i] * baseNum[i];

        // 取得驗證碼
        int remainder = sum % 10;
        char checkCode = (remainder == 0 ? '0' : (char) ('0' + (10 - remainder)));

        if (checkCode == chars[9])
            return true;
        else
            return false;
    }

    public static void main(String[] args) {

        // String id = "19000000ab";
        // String id = "A123456";
        // String id = "AA23456787";
        // String id = "AB23456789";
        // args = new String[] { "A800000014", "O800000017" };// 新式外來人口統一證號
    	
        int max = 1;

        // long start = System.currentTimeMillis();

        for (int i = 0; i < max; i++) {
            for (String id : args) {
                System.out.println(id + " = " + isForeignerIdValid(id));
            }
        }
        long end = System.currentTimeMillis();
        // System.out.println(end - start);
    }
}
