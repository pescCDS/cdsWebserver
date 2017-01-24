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

package org.pesc.oauth;

import org.pesc.api.model.AuthUser;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by james on 1/22/17.
 */
public class CustomTokenEnhancer implements TokenEnhancer {


    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Object principal = authentication.getPrincipal();

        final Map<String, Object> additionalInfo = new HashMap<>();


        if (principal instanceof AuthUser) {

            AuthUser user = (AuthUser)principal;

            additionalInfo.put("user_id", user.getId());
            additionalInfo.put("authorities", AuthorityUtils.authorityListToSet(user.getAuthorities()));
            additionalInfo.put("organization_id", user.getOrganizationId());
            //((DefaultOAuth2AccessToken) accessToken).setScope(new HashSet(Arrays.asList("read_inbox", "read_transactions", "write_outbox", "read_outbox"));

        }
        else {
            additionalInfo.put("authorities", new String[]{ "ROLE_NETWORK_SERVER" });
            additionalInfo.put("organization_id", principal);

        }

        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);

        return accessToken;
    }
}