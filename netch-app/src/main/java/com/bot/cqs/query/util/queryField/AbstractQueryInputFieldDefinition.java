
package com.bot.cqs.query.util.queryField;

import org.springframework.util.StringUtils;

public abstract class AbstractQueryInputFieldDefinition implements QueryInputFieldDefinition {

    // public static final String SOURCE_STRING =
    // "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ ";
    private static final String DOUBLE_BYTES_STRING = "０１２３４５６７８９ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ　";

    public static final int MODE_CONVERT_OR_IGNORE = 0;
    public static final int MODE_CONVERT_OR_ERROR = 1;
    public static final int MODE_CONVERT_OR_DISCARD = 2;

    private String fieldName;
    private String fieldDesc;
    private String targetFieldName;

    private int minLength;
    private int maxLength;
    private int convertToDoubleBytesMode;

    private boolean required;
    private boolean trimFirst;
    private boolean convertToDoubleBytes;
    private boolean toUpperCase;

    public AbstractQueryInputFieldDefinition() {

        setRequired(false);
        setTrimFirst(true);
        setConvertToDoubleBytes(false);
        setToUpperCase(true);
        setMinLength(1);
        setMaxLength(20);
        setConvertToDoubleBytesMode(MODE_CONVERT_OR_IGNORE);
    }

    public boolean isConvertToDoubleBytes() {

        return convertToDoubleBytes;
    }

    public void setConvertToDoubleBytes(boolean convertToDoubleBytes) {

        this.convertToDoubleBytes = convertToDoubleBytes;
    }

    public String getFieldDesc() {

        return fieldDesc;
    }

    public void setFieldDesc(String fieldDesc) {

        this.fieldDesc = fieldDesc;
    }

    public String getFieldName() {

        return fieldName;
    }

    public void setFieldName(String fieldName) {

        this.fieldName = fieldName;
    }

    public String getTargetFieldName() {

        return targetFieldName;
    }

    public void setTargetFieldName(String targetFieldName) {

        this.targetFieldName = targetFieldName;
    }

    public int getMaxLength() {

        return maxLength;
    }

    public void setMaxLength(int maxLength) {

        this.maxLength = maxLength;
    }

    public int getMinLength() {

        return minLength;
    }

    public void setMinLength(int minLength) {

        this.minLength = minLength;
    }

    public boolean isRequired() {

        return required;
    }

    public void setRequired(boolean required) {

        this.required = required;
    }

    public boolean isTrimFirst() {

        return trimFirst;
    }

    public void setTrimFirst(boolean trimFirst) {

        this.trimFirst = trimFirst;
    }

    public boolean isToUpperCase() {

        return toUpperCase;
    }

    public void setToUpperCase(boolean toUpperCase) {

        this.toUpperCase = toUpperCase;
    }

    public int getConvertToDoubleBytesMode() {

        return convertToDoubleBytesMode;
    }

    public void setConvertToDoubleBytesMode(int convertToDoubleBytesMode) {

        this.convertToDoubleBytesMode = convertToDoubleBytesMode;
    }

    /**
     * 依設定內容依序做以下資料轉換
     * <p>
     * 1. 檢查是否有值<br>
     * 2. 去頭尾空白 / 大寫轉換<br>
     * 3. 長度檢查<br>
     * 4. 轉全型<br>
     * 5. 自訂資料檢核<br>
     * 6. 自訂資料修改<br>
     * 
     * @param originalText
     * @return
     * @throws QueryFieldException
     */
    public String convert(String originalText) throws QueryFieldException {

        // check empty
        if (isEmpty(originalText)) {
            if (isRequired())
                throw createQueryFieldException("valueRequired.1", new Object[] { getFieldDesc() }, getFieldDesc() + " is empty");
            else
                return null;
        }

        // upper case
        StringBuffer target = new StringBuffer(isTrimFirst() ? originalText.trim() : originalText);
        if (isToUpperCase()) {
            for (int i = 0; i < target.length(); i++) {
                char c = target.charAt(i);
                target.setCharAt(i, Character.toUpperCase(c));
            }
        }

        // check length
        int len = target.length();
        if ((getMinLength() > 0 && getMinLength() > len) || (getMaxLength() > 0 && getMaxLength() < len)) {

            String lengthStr;
            if (getMinLength() != getMaxLength())
                lengthStr = getMinLength() + " - " + getMaxLength() + " 個字";
            else
                lengthStr = getMaxLength() + " 個字";

            throw createQueryFieldException("invalidFormat.2", new Object[] { getFieldDesc(), lengthStr }, getFieldDesc() + " length error");
        }

        // convert to double-bytes character
        if (isConvertToDoubleBytes())
            target = convertToDoubleBytes(target, getConvertToDoubleBytesMode());

        // 自訂的檢查
        target = onInternalDataCheck(target);
        // 自訂的調整
        target = customizeData(target);

        return target.toString();
    }

    public boolean isEmpty(String originalText) {

        if (isTrimFirst())
            return !StringUtils.hasText(originalText);
        else
            return !StringUtils.hasLength(originalText);
    }

    /**
     * 依各欄位不同自行 implements 的資料修改
     * 
     * @param text
     * @return
     */
    protected abstract StringBuffer customizeData(StringBuffer text);

    /**
     * 依各欄位不同自行 implements 的資料檢核
     * 
     * @param text
     * @return
     * @throws QueryFieldException
     */
    protected abstract StringBuffer onInternalDataCheck(StringBuffer text) throws QueryFieldException;

    protected QueryFieldException createQueryFieldException(String defaultMessage) {

        return createQueryFieldException("", null, defaultMessage);
    }

    protected QueryFieldException createQueryFieldException(String messageId, String defaultMessage) {

        return createQueryFieldException(messageId, null, defaultMessage);
    }

    protected QueryFieldException createQueryFieldException(String messageId, Object[] args, String defaultMessage) {

        return new QueryFieldException(messageId, args, defaultMessage);
    }

    /**
     * 這項轉換是直接覆蓋 StringBuffer 的原有內容
     * 
     * @param text
     * @return
     * @throws QueryFieldException
     *             如果有無法轉換的字元
     */
    public StringBuffer convertToDoubleBytes(StringBuffer text) throws QueryFieldException {

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
     * @throws QueryFieldException
     *             如果有無法轉換的字元
     */
    public StringBuffer convertToDoubleBytes(StringBuffer text, int mode) throws QueryFieldException {

        if (text == null)
            return null;

        StringBuffer target = text;
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); i++) {

            char c = text.charAt(i);

            if (c < 128) {

                int pos = getCharacterIndex(c);
                if (pos == -1) {
                    if (mode == MODE_CONVERT_OR_ERROR)
                        throw createQueryFieldException("invalidChar.2", new Object[] { getFieldDesc(), c }, "Invalid char [" + c + "]");
                    else if (mode == MODE_CONVERT_OR_DISCARD)
                        continue;
                } else {
                    c = DOUBLE_BYTES_STRING.charAt(pos);
                }
            }

            result.append(c);
        }

        return result;
    }

    private int getCharacterIndex(char c) {

        if (c >= '0' && c <= '9')
            return c - '0';
        if (c >= 'A' && c <= 'Z')
            return c - 'A' + 10;
        if (c == ' ')
            return 36;
        return -1;

    }
}
