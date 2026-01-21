package androidx.browser.trusted;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface TokenStore {
    Token load();

    void store(Token token);
}
