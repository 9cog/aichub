package net.openid.appauth;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import java.util.ArrayList;
import java.util.List;
import net.openid.appauth.AuthorizationException;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class IdToken {
    private static final String KEY_AUDIENCE = "aud";
    private static final String KEY_EXPIRATION = "exp";
    private static final String KEY_ISSUED_AT = "iat";
    private static final String KEY_ISSUER = "iss";
    private static final String KEY_NONCE = "nonce";
    private static final String KEY_SUBJECT = "sub";
    private static final Long MILLIS_PER_SECOND = 1000L;
    private static final Long TEN_MINUTES_IN_SECONDS = 600L;
    public final List<String> audience;
    public final Long expiration;
    public final Long issuedAt;
    public final String issuer;
    public final String nonce;
    public final String subject;

    IdToken(String issuer, String subject, List<String> audience, Long expiration, Long issuedAt, String nonce) {
        this.issuer = issuer;
        this.subject = subject;
        this.audience = audience;
        this.expiration = expiration;
        this.issuedAt = issuedAt;
        this.nonce = nonce;
    }

    private static JSONObject parseJwtSection(String section) throws JSONException {
        return new JSONObject(new String(Base64.decode(section, 8)));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Multi-variable type inference failed */
    public static IdToken from(String token) throws JSONException, IdTokenException {
        List list;
        String[] split = token.split("\\.");
        if (split.length <= 1) {
            throw new IdTokenException("ID token must have both header and claims section");
        }
        parseJwtSection(split[0]);
        JSONObject parseJwtSection = parseJwtSection(split[1]);
        String string = JsonUtil.getString(parseJwtSection, KEY_ISSUER);
        String string2 = JsonUtil.getString(parseJwtSection, KEY_SUBJECT);
        try {
            list = JsonUtil.getStringList(parseJwtSection, KEY_AUDIENCE);
        } catch (JSONException unused) {
            List arrayList = new ArrayList();
            arrayList.add(JsonUtil.getString(parseJwtSection, KEY_AUDIENCE));
            list = arrayList;
        }
        return new IdToken(string, string2, list, Long.valueOf(parseJwtSection.getLong(KEY_EXPIRATION)), Long.valueOf(parseJwtSection.getLong(KEY_ISSUED_AT)), JsonUtil.getStringIfDefined(parseJwtSection, KEY_NONCE));
    }

    void validate(TokenRequest tokenRequest, Clock clock) throws AuthorizationException {
        validate(tokenRequest, clock, false);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void validate(TokenRequest tokenRequest, Clock clock, boolean skipIssuerHttpsCheck) throws AuthorizationException {
        AuthorizationServiceDiscovery authorizationServiceDiscovery = tokenRequest.configuration.discoveryDoc;
        if (authorizationServiceDiscovery != null) {
            if (!this.issuer.equals(authorizationServiceDiscovery.getIssuer())) {
                throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Issuer mismatch"));
            }
            Uri parse = Uri.parse(this.issuer);
            if (!skipIssuerHttpsCheck && !parse.getScheme().equals("https")) {
                throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Issuer must be an https URL"));
            }
            if (TextUtils.isEmpty(parse.getHost())) {
                throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Issuer host can not be empty"));
            }
            if (parse.getFragment() != null || parse.getQueryParameterNames().size() > 0) {
                throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Issuer URL should not containt query parameters or fragment components"));
            }
        }
        if (!this.audience.contains(tokenRequest.clientId)) {
            throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Audience mismatch"));
        }
        Long valueOf = Long.valueOf(clock.getCurrentTimeMillis() / MILLIS_PER_SECOND.longValue());
        if (valueOf.longValue() > this.expiration.longValue()) {
            throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("ID Token expired"));
        }
        if (Math.abs(valueOf.longValue() - this.issuedAt.longValue()) > TEN_MINUTES_IN_SECONDS.longValue()) {
            throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Issued at time is more than 10 minutes before or after the current time"));
        }
        if (GrantTypeValues.AUTHORIZATION_CODE.equals(tokenRequest.grantType)) {
            if (!TextUtils.equals(this.nonce, tokenRequest.nonce)) {
                throw AuthorizationException.fromTemplate(AuthorizationException.GeneralErrors.ID_TOKEN_VALIDATION_ERROR, new IdTokenException("Nonce mismatch"));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static class IdTokenException extends Exception {
        IdTokenException(String message) {
            super(message);
        }
    }
}
