
package com.bot.cqs.query.util.queryField;

public class NumericInputField extends AbstractQueryInputFieldDefinition {

    public static final int FILL_AS_PREFIX = 0;
    public static final int FILL_AS_SUFFIX = 1;

    private int fillMode;
    private char fillChar;

    public NumericInputField() {

        super();
        setFillMode(FILL_AS_PREFIX);
        setFillChar('0');
    }

    @Override
    protected StringBuffer onInternalDataCheck(StringBuffer text) throws QueryFieldException {

        int len = text.length();
        for (int i = 0; i < len; i++) {
            char c = text.charAt(i);
            if (c < '0' || c > '9')
                throw createQueryFieldException("invalidFormat.1", new Object[] { getFieldDesc() }, "invalidFormat");
        }
        return text;
    }

    @Override
    protected StringBuffer customizeData(StringBuffer text) {

        while (text.length() < getMaxLength()) {
            if (getFillMode() == FILL_AS_SUFFIX)
                text.append(getFillChar());
            else {
                int len = getMaxLength() - text.length();
                char[] prefix = new char[len];
                for (int i = 0; i < len; i++)
                    prefix[i] = getFillChar();
                text.insert(0, prefix, 0, len);
            }
        }
        return text;
    }

    public char getFillChar() {

        return fillChar;
    }

    public void setFillChar(char fillChar) {

        this.fillChar = fillChar;
    }

    public int getFillMode() {

        return fillMode;
    }

    public void setFillMode(int fillMode) {

        this.fillMode = fillMode;
    }

}
