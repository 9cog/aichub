package androidx.browser.trusted;

import androidx.concurrent.futures.ResolvableFuture;
import com.google.common.util.concurrent.ListenableFuture;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
class FutureUtils {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static <T> ListenableFuture<T> immediateFailedFuture(Throwable th) {
        ResolvableFuture create = ResolvableFuture.create();
        create.setException(th);
        return create;
    }

    private FutureUtils() {
    }
}
