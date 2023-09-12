
package com.bot.cqs.query.util.queryField;

public class DoubleBytesConverter {

    public static final String SOURCE_STRING = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    public static final String TARGET_STRING = "０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ　";

    public static int MODE_CONVERT_OR_IGNORE = 0;
    public static int MODE_CONVERT_OR_ERROR = 1;

    /**
     * 這項轉換是直接覆蓋 StringBuffer 的原有內容
     * 
     * @param text
     * @return
     */
    public static StringBuffer convertToDoubleBytes(StringBuffer text) {

        return convertToDoubleBytes(text, MODE_CONVERT_OR_IGNORE);
    }

    /**
     * 這項轉換是直接覆蓋 StringBuffer 的原有內容
     * 
     * @param text
     * @param mode
     *            決定 single byte 不轉換時的處理
     *            <p>
     *            MODE_CONVERT_OR_IGNORE: 未符合轉換條件則略過 <br>
     *            MODE_CONVERT_OR_ERROR: 未符合轉換條件則丟出 IllegalArgumentException
     * @return
     */
    public static StringBuffer convertToDoubleBytes(StringBuffer text, int mode) {

        if (text == null)
            return null;

        StringBuffer target = text;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (mode == MODE_CONVERT_OR_ERROR)
                ;

            if (c < 128) {
                int pos = SOURCE_STRING.indexOf(c);
                if (pos != -1)
                    target.setCharAt(i, TARGET_STRING.charAt(pos));
            }
        }

        return target;
    }

    public static void main(String[] args) {

        String aaa;
        if (args.length > 0)
            aaa = args[0];
        else
            aaa = "12一3 4a5";
        System.out.println("Source length = " + aaa.length() + ", text = [" + aaa + "]");

        long start = System.currentTimeMillis();
        // String bbb = convertToDoubleBytes( aaa );
        String bbb = convertToDoubleBytes(new StringBuffer(aaa)).toString();
        long end = System.currentTimeMillis();
        System.out.println("Target length = " + bbb.length() + ", text = [" + bbb + "]");
        System.out.println("finish in " + (end - start) + " ms");
    }
}
