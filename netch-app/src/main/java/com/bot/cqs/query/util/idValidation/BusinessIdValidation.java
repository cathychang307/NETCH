
package com.bot.cqs.query.util.idValidation;

public class BusinessIdValidation {

    public static final int[] PARAM_ARRAY = new int[] { 1, 2, 1, 2, 1, 2, 4, 1 };

    public static boolean isBusinessIdValid(String id) {

        if (id == null)
            return false;
        if (id.length() != 8)
            return false;

        // check numeric
        char[] chars = id.toCharArray();
        for (int i = 0; i < 8; i++)
            if (chars[i] < '0' || chars[i] > '9')
                return false;

        /*
         * PS: 第 7 位為 7 的話, 7 * 4 = 28 (固定) 2 + 8 = (1,0), 所以其它總和加 1 或 0 即可 換個做法, 反正全加就對了, 是 7 則允許 -1 為 10 的倍數
         */
        int sum = 0;
        int temp;
        for (int i = 0; i < 8; i++) {
            temp = PARAM_ARRAY[i] * (chars[i] - '0');
            if (!(i == 6 && chars[i] == '7')) {
                sum += (temp / 10 + temp % 10);
            }
        }
        int dividend = 5; //2022年修正統一編號之檢查邏輯由可被「10」整除改為可被「5」整除
        if (chars[6] == '7')
            return (sum % dividend == 0) || ((sum + 1) % dividend == 0);
        else
            return (sum % dividend == 0);
    }

    public static boolean isObuBusinessIdValid(String id) {

        if (id == null)
            return false;
        if (id.length() != 8)
            return false;

        ///檢查前四碼英文，後四碼數字
        if (!id.matches("[a-zA-Z]{4}[0-9]{4}")) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {

        String id = "04595257";
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                id = args[i];
                System.out.println(id + " isBusinessIdValid = " + isBusinessIdValid(id));
            }
        } else {
            System.out.println(id + " isBusinessIdValid = " + isBusinessIdValid(id));
        }

        String obuId = "BNAM0218";
        if (args.length != 0) {
            for (int i = 0; i < args.length; i++) {
                obuId = args[i];
                System.out.println(obuId + " isObuBusinessIdValid = " + isObuBusinessIdValid(obuId));
            }
        } else {
            System.out.println(obuId + " isObuBusinessIdValid= " + isObuBusinessIdValid(obuId));
        }
    }
}
