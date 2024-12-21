package com.hcc.entities;

import org.springframework.security.core.GrantedAuthority;

public class Authority  implements GrantedAuthority {
    private long id;
    private String authority;
    private User user;
    public Authority() {
    }
    public Authority(String authority) {
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
