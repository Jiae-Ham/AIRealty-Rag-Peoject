package com.Realty.RealtyWeb.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity implements UserDetails {

    @Id
    @Column(name = "userid", length = 50, nullable = false)
    private String userId;

    @Column(name = "userpw", length = 255, nullable = false)
    private String userPw;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String userName;

    @Column(name = "useremail", length = 100, unique = true)
    private String userEmail;

    @Column(name = "userphone", length = 20, unique = true)
    private String userPhone;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();  // 현재는 ROLE 없음
    }

    @Override
    public String getPassword() {
        return userPw;
    }

    @Override
    public String getUsername() {
        return userId;
    }

    public String getDisplayName() {
        return userName;
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

}