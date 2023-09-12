
package com.bot.cqs.query.util;

/**
 * 參數型態
 * 
 * @author Damon Lu
 *
 */
public interface ApplicationParameterType {

    public boolean isValueValid(Object value);

    public String getDescription();
}
