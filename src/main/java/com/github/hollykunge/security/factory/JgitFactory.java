package com.github.hollykunge.security.factory;

import com.github.hollykunge.security.strategy.BaseJgitManager;
import com.github.hollykunge.security.util.SpringUtils;

public class JgitFactory {

    public static BaseJgitManager getJgit(String templateId) {
        BaseJgitManager payCallbackTemplate = (BaseJgitManager) SpringUtils.getBean(templateId);
        return payCallbackTemplate;
    }
}
