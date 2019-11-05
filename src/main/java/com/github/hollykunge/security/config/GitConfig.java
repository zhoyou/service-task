package com.github.hollykunge.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author: zhhongyu
 * @description:
 * @since: Create in 15:24 2019/9/4
 */
@Configuration
public class GitConfig {

    public static String address;

    public static String defultMergeBranch;

    @Value("${git.repository.address}")
    public void setCreateReposityAddress(String address) {
        GitConfig.address = address;
    }
    @Value("${git.repository.defultMergeBranch}")
    public void setDefultMergeBranch(String defultMergeBranch) {
        GitConfig.defultMergeBranch = defultMergeBranch;
    }
}
