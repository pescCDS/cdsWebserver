package org.pesc.oauth;

import org.jvnet.hk2.annotations.Service;
import org.pesc.api.StringUtils;
import org.pesc.api.model.OAuthClientDetails;
import org.pesc.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import java.util.Arrays;
import java.util.List;

/**
 * Created by james on 10/5/16.
 */
@Service
public class EdExClientDetailsService implements ClientDetailsService {

    @Value("${authentication.oauth.tokenValidityInSeconds}")
    private Integer tokenValiditySeconds;

    @Autowired
    private OrganizationService organizationService;




    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {

        OAuthClientDetails org = organizationService.getOAuthClientDetails(Integer.valueOf(clientId));

        if (org == null || org.getEnabled() == false || StringUtils.isEmpty(org.getOauthSecret()) ) {
            return null;
        }

        BaseClientDetails clientDetails = new BaseClientDetails();

        List<String> scopes = Arrays.asList("read", "write");

        clientDetails.setClientId(clientId);
        clientDetails.setScope(scopes);
        clientDetails.setAutoApproveScopes(scopes);
        clientDetails.setAuthorizedGrantTypes(Arrays.asList("client_credentials", "refresh_token"));
        clientDetails.setAuthorities(AuthorityUtils.createAuthorityList("ROLE_SENDER"));
        clientDetails.setAccessTokenValiditySeconds(tokenValiditySeconds);
        clientDetails.setClientSecret(org.getOauthSecret());

        return clientDetails;
    }
}
