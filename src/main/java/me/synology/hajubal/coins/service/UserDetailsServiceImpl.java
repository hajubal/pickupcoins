package me.synology.hajubal.coins.service;

import lombok.RequiredArgsConstructor;
import me.synology.hajubal.coins.entity.SiteUser;
import me.synology.hajubal.coins.respository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SiteUser siteUser = userRepository.findByLoginId(username).orElseThrow(() -> new UsernameNotFoundException(username));

        return new SiteUserDetailsImpl(siteUser);
    }

    static final class SiteUserDetailsImpl extends SiteUser implements UserDetails {

        private static final List<GrantedAuthority> ROLE_USER = Collections
                .unmodifiableList(AuthorityUtils.createAuthorityList("ROLE_USER"));

        public SiteUserDetailsImpl(SiteUser siteUser) {
            super(siteUser.getLoginId(), siteUser.getUserName(), siteUser.getPassword());
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return ROLE_USER;
        }
        @Override
        public String getUsername() {
            return this.getUserName();
        }

        @Override
        public boolean isAccountNonExpired() {
            return false;
        }

        @Override
        public boolean isAccountNonLocked() {
            return false;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return false;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
