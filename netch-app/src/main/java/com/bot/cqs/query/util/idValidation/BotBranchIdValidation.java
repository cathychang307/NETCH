
package com.bot.cqs.query.util.idValidation;

public class BotBranchIdValidation {

    private static final int[] PARAM_ARRAY = new int[] { 3, 7, 9, 3, 7, 9 };

    /**
     * @param id
     *            七位數分行代碼
     * @return
     */
    public static boolean isBranchIdValid(String id) {

        if (id == null)
            return false;
        if (id.length() != 7)
            return false;
        char[] chars = id.toCharArray();
        int sum = 0;
        for (int i = 0; i < PARAM_ARRAY.length; i++) {
            if (chars[i] < '0' || chars[i] > '9')
                return false;
            sum += (chars[i] - '0') * PARAM_ARRAY[i];
        }

        char checkCode = '0';
        int remainder = sum % 10;
        if (remainder != 0)
            checkCode = (char) ((10 - remainder) + '0');

        return checkCode == chars[6];
    }

    public static boolean isTchIdValid(String id) {

        if (id == null)
            return false;
        if (id.length() != 2)
            return false;

        return Character.isDigit(id.charAt(0)) && Character.isDigit(id.charAt(1));
    }

    public static void main(String[] args) {

        String id = "0040036";
        System.out.println(id + " = " + isBranchIdValid(id));
    }
}
