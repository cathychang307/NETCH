
package com.bot.cqs.query.util;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

public class StringProcess {

    private static IllegalArgumentException hexStringLengthErr = new IllegalArgumentException("Length of hex character must be mutiple of 2");
    private static IllegalArgumentException hexPairLengthErr = new IllegalArgumentException("Length of hex character must be 2");
    private static IllegalArgumentException bit4LengthErr = new IllegalArgumentException("Length of nit string must be 4");
    private static IllegalArgumentException bit8LengthErr = new IllegalArgumentException("Length of nit string must be 8");
    private static IllegalArgumentException bitErr = new IllegalArgumentException("Input string must be 0000 - 1111");
    private static IllegalArgumentException charErr = new IllegalArgumentException("Input charater must be 0 - F");
    private static IllegalArgumentException arrayLengthErr = new IllegalArgumentException("The length of two array must be equal.");

    public static final char[] charTable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    public static final String[] bitTable = { "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111" };

    private static final String NUMBER_FILTER = "000000";
    private static final String HEXVALUE_FILTER = "                                                ";
    private static final String STRING_FILTER = "                ";

    private StringProcess() {

    }

    // hex string 與 byte 互轉
    public static byte[] hexStringToByte(String hexString) {

        return hexStringToByte(hexString, false);
    }

    public static byte[] hexStringToByte(String hexString, boolean ignoreInvalidChar) {

        char[] source = hexString.toUpperCase().toCharArray();

        if (ignoreInvalidChar) {
            char[] temp = new char[source.length];
            int pos = 0;
            for (int i = 0; i < source.length; i++) {
                if ((source[i] >= '0' && source[i] <= '9') || (source[i] >= 'A' && source[i] <= 'F'))
                    temp[pos++] = source[i];
            }
            source = new char[pos];
            System.arraycopy(temp, 0, source, 0, pos);
        }

        // char[] source = hexString.toUpperCase().toCharArray();
        int r = source.length % 2;
        if (r != 0)
            throw hexStringLengthErr;
        byte[] target = new byte[source.length / 2];
        for (int i = 0; i < target.length; i++)
            target[i] = (byte) (hexToValue(source[2 * i]) << 4 | hexToValue(source[2 * i + 1]));
        return target;
    }

    public static char[] byteToHexChar(byte[] value) {

        char[] result = new char[value.length * 2];
        for (int i = 0; i < result.length; i++) {
            int j = i / 2;
            int r = i % 2;
            // int k = value[j] + 128;
            if (r == 0)
                result[i] = valueToHex((value[j] & 0xf0) >> 4);
            else
                result[i] = valueToHex(value[j] & 0x0f);
        }
        return result;
    }

    public static byte[] intToByteArray(int value) {

        byte[] result = new byte[4];
        for (int i = 0; i < 4; i++)
            result[i] = (byte) (value >>> (4 - i - 1) * 8);
        return result;
    }

    public static byte[] longToByteArray(long value) {

        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++)
            result[i] = (byte) (value >>> (8 - i - 1) * 8);
        return result;
    }

    public static String longToHexString(long value) {

        return byteToHexString(longToByteArray(value));
    }

    public static String intToHexString(int value) {

        return byteToHexString(intToByteArray(value));
    }

    public static String byteToHexString(byte value) {

        return byteToHexString(new byte[] { value });
    }

    public static String byteToHexString(byte[] value) {

        return new String(byteToHexChar(value));
    }

    public static String byteToFormattedHexString(byte[] value) {

        return byteToFormattedHexString(value, false);
    }

    public static String byteToFormattedHexString(byte[] value, boolean newLine) {

        char[] charValue = byteToHexChar(value);
        StringBuffer s = new StringBuffer();

        int flag = 0;
        int pos = 0;
        int total = charValue.length;

        for (int i = 0; i < charValue.length; i++) {
            s.append(charValue[i]);
            if (flag == 1) {
                s.append(' ');
                flag = 0;

                pos++;
                switch (pos) {
                case 8:
                    s.append(' ');
                    break;
                case 16:
                    s.append(' ');
                    if (newLine && ((charValue.length - i) != 1))
                        s.append('\n');
                    pos = 0;
                    break;
                default:
                    break;
                }

            } else
                flag++;
        }
        return s.toString();
    }

    // hex 與 bit string 轉換
    public static String hexCharToBitString(char hexChar) {

        char tempChar = Character.toUpperCase(hexChar);
        for (int i = 0; i < charTable.length; i++) {
            if (tempChar == charTable[i])
                return bitTable[i];
        }
        throw charErr;
    }

    public static char bitStringToHexChar(String bitString) {

        if (bitString.length() != 4)
            throw bit4LengthErr;
        for (int i = 0; i < bitTable.length; i++) {
            if (bitString.equals(bitTable[i]))
                return charTable[i];
        }
        throw bitErr;
    }

    public static String hexPairToBitString(char[] hexChar) {

        if (hexChar.length != 2)
            throw hexPairLengthErr;
        return hexCharToBitString(hexChar[0]) + hexCharToBitString(hexChar[1]);
    }

    public static char[] bitStringToHexPair(String bitString) {

        if (bitString.length() != 8)
            throw bit8LengthErr;
        return new char[] { bitStringToHexChar(bitString.substring(0, 4)), bitStringToHexChar(bitString.substring(4)) };
    }

    public static String[] hexStringToBitStringArray(String hexString) {

        char[] source = hexString.toUpperCase().toCharArray();
        int r = source.length % 2;
        if (r != 0)
            throw hexStringLengthErr;
        int len = source.length / 2;
        String[] result = new String[len];
        for (int i = 0; i < len; i++) {
            result[i] = hexPairToBitString(new char[] { source[2 * i], source[2 * i + 1] });
        }
        return result;
    }

    public static String bitStringArrayToHexString(String[] bitStringArray) {

        StringBuffer s = new StringBuffer();
        for (int i = 0; i < bitStringArray.length; i++)
            s.append(new String(bitStringToHexPair(bitStringArray[i])));
        return s.toString();
    }

    // hex char 與 int 互轉 ( 0 - F )
    public static int hexToValue(char c) {

        for (int i = 0; i < charTable.length; i++) {
            if (c == charTable[i])
                return i;
        }
        throw charErr;
    }

    public static char valueToHex(int num) {

        return charTable[num];
    }

    // bitString 與 int 互轉 ( 0000 - 1111 )
    public static int bitToValue(String bitString) {

        for (int i = 0; i < bitTable.length; i++) {
            if (bitString.equals(bitTable[i]))
                return i;
        }
        throw bitErr;
    }

    public static String valueToBit(int num) {

        return bitTable[num];
    }

    // 以 byte 值來做 bit 運算
    public static byte[] getByteArrayFromAndCaculation(byte[] byteArray1, byte[] byteArray2) {

        arrayCheck(byteArray1, byteArray2);
        byte[] byteArray3 = new byte[byteArray1.length];
        for (int i = 0; i < byteArray3.length; i++)
            byteArray3[i] = (byte) (byteArray1[i] & byteArray2[i]);
        return byteArray3;
    }

    public static byte[] getByteArrayFromOrCaculation(byte[] byteArray1, byte[] byteArray2) {

        arrayCheck(byteArray1, byteArray2);
        byte[] byteArray3 = new byte[byteArray1.length];
        for (int i = 0; i < byteArray3.length; i++)
            byteArray3[i] = (byte) (byteArray1[i] | byteArray2[i]);
        return byteArray3;
    }

    public static byte[] getByteArrayFromXorCaculation(byte[] byteArray1, byte[] byteArray2) {

        arrayCheck(byteArray1, byteArray2);
        byte[] byteArray3 = new byte[byteArray1.length];
        for (int i = 0; i < byteArray3.length; i++)
            byteArray3[i] = (byte) (byteArray1[i] ^ byteArray2[i]);
        return byteArray3;
    }

    public static byte[] getByteArrayFromNotCaculation(byte[] byteArray1) {

        byte[] byteArray3 = new byte[byteArray1.length];
        for (int i = 0; i < byteArray3.length; i++)
            byteArray3[i] = (byte) (~byteArray1[i]);
        return byteArray3;
    }

    // 直接以 hexString 來做 bit 運算
    public static String getStringFromAndCaculation(String hexString1, String hexString2) {

        return byteToHexString(getByteArrayFromAndCaculation(hexStringToByte(hexString1), hexStringToByte(hexString2)));
    }

    public static String getStringFromOrCaculation(String hexString1, String hexString2) {

        return byteToHexString(getByteArrayFromOrCaculation(hexStringToByte(hexString1), hexStringToByte(hexString2)));
    }

    public static String getStringFromXorCaculation(String hexString1, String hexString2) {

        return byteToHexString(getByteArrayFromXorCaculation(hexStringToByte(hexString1), hexStringToByte(hexString2)));
    }

    public static String getStringFromNotCaculation(String hexString1) {

        return byteToHexString(getByteArrayFromNotCaculation(hexStringToByte(hexString1)));
    }

    // 過濾不需要的 char, 原意是用來過濾空白
    public static char[] charArrayFilter(char[] array) {

        return charArrayFilter(array, ' ');
    }

    public static char[] charArrayFilter(char[] array, char filterChar) {

        char[] temp = new char[array.length];
        int j = 0;
        for (int i = 0; i < array.length; i++) {
            if (array[i] != filterChar)
                temp[j++] = array[i];
        }
        char[] result = new char[j];
        System.arraycopy(temp, 0, result, 0, j);
        return result;
    }

    public static String stringFilter(String str) {

        return stringFilter(str, ' ');
    }

    public static String stringFilter(String str, char filterChar) {

        return new String(charArrayFilter(str.toCharArray(), filterChar));
    }

    public static String oddParity(String hexString) {

        byte[] temp = parity(1, hexStringToByte(hexString));
        return byteToHexString(temp);
    }

    public static byte[] oddParity(byte[] byteArray) {

        return parity(1, byteArray);
    }

    public static String evenParity(String hexString) {

        byte[] temp = parity(0, hexStringToByte(hexString));
        return byteToHexString(temp);
    }

    public static byte[] evenParity(byte[] byteArray) {

        return parity(0, byteArray);
    }

    private static byte[] parity(int mode, byte[] source) {

        byte tmp1 = 1;
        byte tmp2;
        int cnt;
        byte[] target = new byte[source.length];
        for (int i = 0; i < source.length; i++) {
            cnt = 0;
            tmp2 = source[i];
            for (int j = 0; j < 8; j++) {
                if ((tmp2 & tmp1) != 0)
                    cnt++;
                tmp2 = (byte) (tmp2 >> 1);
            }

            if (((cnt & 1) > 0) ^ (mode > 0))
                target[i] = (byte) (source[i] ^ tmp1);
            else
                target[i] = source[i];
        }
        return target;
    }

    private static void arrayCheck(byte[] byteArray1, byte[] byteArray2) {

        if (byteArray1.length != byteArray2.length)
            throw arrayLengthErr;
    }

    public static String getHexLogAsAscii(byte[] hexValue, boolean addMemoChar) {

        return getHexLog(StringProcess.byteToHexString(hexValue), hexValue, addMemoChar);
    }

    public static String getHexLogAsEbcdic(byte[] hexValue, boolean addMemoChar) {

        String temp1 = StringProcess.byteToHexString(hexValue);
        byte[] temp2 = ConvertCode.ebcdic2ascii(hexValue);
        return getHexLog(temp1, temp2, addMemoChar);
    }

    public static String getHexLog(String hexString, byte[] charValue, boolean addMemoChar) {

        StringBuffer s = new StringBuffer(charValue.length * 5);
        int count = 0;
        int pos = 0;
        int lineSize = 16;
        int currentSize;

        while (true) {

            if (pos >= charValue.length)
                break;

            if (count != 0)
                s.append('\n');

            ++count;
            currentSize = (charValue.length - pos) >= lineSize ? lineSize : charValue.length - pos;

            if (addMemoChar)
                s.append("# ");

            s.append(getLineNumberString(count));
            s.append(" | ");

            // write hex value
            for (int i = pos; i < pos + currentSize; i++) {
                s.append(hexString.charAt(i + i));
                s.append(hexString.charAt(i + i + 1));
                s.append(' ');
            }

            s.append(HEXVALUE_FILTER.substring(0, (lineSize - currentSize) * 3));
            s.append("| ");

            // write asciiString
            for (int i = pos; i < pos + currentSize; i++)
                if (charValue[i] >= ' ')
                    s.append((char) (charValue[i] & 0xff));
                else
                    s.append('.');

            s.append(STRING_FILTER.substring(0, (lineSize - currentSize)));
            pos += currentSize;
        }

        return s.toString();
    }

    public static String getLineNumberString(int num) {

        String numStr = String.valueOf(num);
        return NUMBER_FILTER.substring(0, NUMBER_FILTER.length() - numStr.length()) + numStr;
    }

    public static String stripXSS(String value) {
        if (value != null) {
            // NOTE: It's highly recommended to use the ESAPI library and uncomment the following line to
            // avoid encoded attacks.
            // value = ESAPI.encoder().canonicalize(value);
            // Avoid null characters
            value = value.replaceAll("", "");
            // Avoid anything between script tags
            Pattern scriptPattern = Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid anything in a src='...' type of expression
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            scriptPattern = Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome </script> tag
            scriptPattern = Pattern.compile("</script>", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Remove any lonesome <script ...> tag
            scriptPattern = Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid eval(...) expressions
            scriptPattern = Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid expression(...) expressions
            scriptPattern = Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid javascript:... expressions
            scriptPattern = Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid vbscript:... expressions
            scriptPattern = Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE);
            value = scriptPattern.matcher(value).replaceAll("");
            // Avoid onload= expressions
            scriptPattern = Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            value = scriptPattern.matcher(value).replaceAll("");
        }
        return value;

    }

    public static String escapeHtml(String value) {
        return StringEscapeUtils.escapeHtml(value);
    }
}
