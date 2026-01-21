package net.openid.appauth;

import android.util.Base64;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;
import net.openid.appauth.internal.Logger;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class CodeVerifierUtil {
    public static final int DEFAULT_CODE_VERIFIER_ENTROPY = 64;
    public static final int MAX_CODE_VERIFIER_ENTROPY = 96;
    public static final int MAX_CODE_VERIFIER_LENGTH = 128;
    public static final int MIN_CODE_VERIFIER_ENTROPY = 32;
    public static final int MIN_CODE_VERIFIER_LENGTH = 43;
    private static final int PKCE_BASE64_ENCODE_SETTINGS = 11;
    private static final Pattern REGEX_CODE_VERIFIER = Pattern.compile("^[0-9a-zA-Z\\-\\.\\_\\~]{43,128}$");

    private CodeVerifierUtil() {
        throw new IllegalStateException("This type is not intended to be instantiated");
    }

    public static void checkCodeVerifier(String codeVerifier) {
        Preconditions.checkArgument(43 <= codeVerifier.length(), "codeVerifier length is shorter than allowed by the PKCE specification");
        Preconditions.checkArgument(codeVerifier.length() <= 128, "codeVerifier length is longer than allowed by the PKCE specification");
        Preconditions.checkArgument(REGEX_CODE_VERIFIER.matcher(codeVerifier).matches(), "codeVerifier string contains illegal characters");
    }

    public static String generateRandomCodeVerifier() {
        return generateRandomCodeVerifier(new SecureRandom(), 64);
    }

    public static String generateRandomCodeVerifier(SecureRandom entropySource, int entropyBytes) {
        Preconditions.checkNotNull(entropySource, "entropySource cannot be null");
        Preconditions.checkArgument(32 <= entropyBytes, "entropyBytes is less than the minimum permitted");
        Preconditions.checkArgument(entropyBytes <= 96, "entropyBytes is greater than the maximum permitted");
        byte[] bArr = new byte[entropyBytes];
        entropySource.nextBytes(bArr);
        return Base64.encodeToString(bArr, 11);
    }

    public static String deriveCodeVerifierChallenge(String codeVerifier) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(codeVerifier.getBytes("ISO_8859_1"));
            return Base64.encodeToString(messageDigest.digest(), 11);
        } catch (UnsupportedEncodingException e) {
            Logger.error("ISO-8859-1 encoding not supported on this device!", e);
            throw new IllegalStateException("ISO-8859-1 encoding not supported", e);
        } catch (NoSuchAlgorithmException e2) {
            Logger.warn("SHA-256 is not supported on this device! Using plain challenge", e2);
            return codeVerifier;
        }
    }

    public static String getCodeVerifierChallengeMethod() {
        try {
            MessageDigest.getInstance("SHA-256");
            return AuthorizationRequest.CODE_CHALLENGE_METHOD_S256;
        } catch (NoSuchAlgorithmException unused) {
            return AuthorizationRequest.CODE_CHALLENGE_METHOD_PLAIN;
        }
    }
}
