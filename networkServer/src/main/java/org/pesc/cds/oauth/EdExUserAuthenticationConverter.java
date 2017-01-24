/*
 * Copyright (c) 2017. California Community Colleges Technology Center
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.pesc.cds.oauth;

import org.pesc.cds.model.AuthUser;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by james on 1/24/17.
 */
public class EdExUserAuthenticationConverter implements UserAuthenticationConverter {

    private Integer orgID;
    private String orgName;

    public EdExUserAuthenticationConverter(Integer orgID, String orgName) {
        this.orgID = orgID;
        this.orgName = orgName;
    }

    @Override
    public Map<String, ?> convertUserAuthentication(Authentication authentication) {

        Map<String, Object> response = new LinkedHashMap<String, Object>();
        response.put(USERNAME, authentication.getName());
        if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
            response.put(AUTHORITIES, AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
        }
        return response;

    }

    @Override
    public Authentication extractAuthentication(Map<String, ?> map) {

        Integer organizationId = (Integer)map.get("organization_id");
        if (organizationId.intValue() == orgID.intValue()) {
            if (map.containsKey(USERNAME)) {
                Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
                AuthUser authUser = new AuthUser((String)map.get(USERNAME),
                        "", true,true,true,true, authorities);

                if (map.containsKey("user_id")) {
                    authUser.setId((Integer)map.get("user_id"));
                }
                if (map.containsKey("organization_id")) {
                    authUser.setOrganizationId((Integer) map.get("organization_id"));
                }

                return new UsernamePasswordAuthenticationToken(authUser, "N/A", authorities);

            }
        }

        throw new BadCredentialsException(String.format("User is not a member of %s.",orgName));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
        Object authorities = map.get(AUTHORITIES);
        if (authorities instanceof String) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
        }
        if (authorities instanceof Collection) {
            return AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
                    .collectionToCommaDelimitedString((Collection<?>) authorities));
        }
        throw new IllegalArgumentException("Authorities must be either a String or a Collection");
    }


}
