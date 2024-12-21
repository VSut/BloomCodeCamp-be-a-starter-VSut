package com.hcc.entities;

import org.springframework.security.core.GrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;


public class User implements org.springframework.security.core.userdetails.UserDetails{
    private long id;
    private String username;
    private String password;
    private List<Authority> authorities;
    private Date cohortStartDate;

    public User() {
    }
    public User(long id, String username, String password, List<Authority> authorities, Date cohortStartDate) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.cohortStartDate = cohortStartDate;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setPassword(String password) {
        this.password = password;
    }



    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public Date getCohortStartDate() {
        return cohortStartDate;
    }

    public void setCohortStartDate(Date cohortStartDate) {
        this.cohortStartDate = cohortStartDate;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new Authority("role_student"));
        return roles;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
