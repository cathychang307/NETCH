
package com.bot.cqs.query.util.idValidation;

public class PersonalIdValidation {

    public static final int[] LOCATION_NUMBER = { 10, 11, 12, 13, 14, 15, 16, 17, 34, 18, 19, 20, 21, 22, 35, 23, 24, 25, 26, 27, 28, 29, 32, 30, 31, 33 };
    private static final int PARAM_NUM[] = new int[] { 1, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

    /**
     * 驗身份證字號
     * 
     * @param id
     * @return
     */
    public static boolean isPersonalIdValid(String id) {

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

        // 第二位辨別男女
        if (chars[1] != '1' && chars[1] != '2')
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

        String id = "xx";
        System.out.println(id + "==>" + isPersonalIdValid(id));
    }
}
