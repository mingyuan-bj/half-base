package com.halfroom.distribution.common.constant.dictmap.factory;

import com.halfroom.distribution.common.constant.dictmap.base.AbstractDictMap;
import com.halfroom.distribution.common.constant.dictmap.base.SystemDict;
import com.halfroom.distribution.common.exception.BizExceptionEnum;
import com.halfroom.distribution.common.exception.BussinessException;

/**
 * 字典的创建工厂
 */
public class DictMapFactory {

    private static final String BASE_PATH = "com.halfroom.distribution.common.constant.dictmap.";

    /**
     * 通过类名创建具体的字典类
     */
    public static AbstractDictMap createDictMap(String className) {
        if("SystemDict".equals(className)){
            return new SystemDict();
        }else{
            try {
                Class<AbstractDictMap> clazz = (Class<AbstractDictMap>) Class.forName(BASE_PATH + className);
                return clazz.newInstance();
            } catch (Exception e) {
                throw new BussinessException(BizExceptionEnum.ERROR_CREATE_DICT);
            }
        }
    }
}
