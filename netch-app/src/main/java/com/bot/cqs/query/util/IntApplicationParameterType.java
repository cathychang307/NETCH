
package com.bot.cqs.query.util;

public class IntApplicationParameterType implements ApplicationParameterType {

    private int minValue;
    private int maxValue;

    public IntApplicationParameterType() {

        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntApplicationParameterType(int min, int max) {

        setMinValue(min);
        setMaxValue(max);
    }

    public String getDescription() {

        return "數值 ( " + getMinValue() + " - " + getMaxValue() + " )";
    }

    public boolean isValueValid(Object value) {

        if (value == null)
            return false;

        int number = 0;
        if (value instanceof Integer)
            number = ((Integer) value).intValue();
        else if (value instanceof String) {
            try {
                number = Integer.parseInt(value.toString());
            } catch (Exception e) {
                return false;
            }
        } else
            return false;

        return number >= getMinValue() && number <= getMaxValue();
    }

    public int getMaxValue() {

        return maxValue;
    }

    public void setMaxValue(int maxValue) {

        this.maxValue = maxValue;
    }

    public int getMinValue() {

        return minValue;
    }

    public void setMinValue(int minValue) {

        this.minValue = minValue;
    }

}
