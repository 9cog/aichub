package androidx.lifecycle;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import kotlin.Metadata;
import okhttp3.HttpUrl;
/* compiled from: HasDefaultViewModelProviderFactory.kt */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001R\u0014\u0010\u0002\u001a\u00020\u00038VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005R\u0012\u0010\u0006\u001a\u00020\u0007X¦\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\tø\u0001\u0000\u0082\u0002\u0006\n\u0004\b!0\u0001¨\u0006\nÀ\u0006\u0001"}, d2 = {"Landroidx/lifecycle/HasDefaultViewModelProviderFactory;", HttpUrl.FRAGMENT_ENCODE_SET, "defaultViewModelCreationExtras", "Landroidx/lifecycle/viewmodel/CreationExtras;", "getDefaultViewModelCreationExtras", "()Landroidx/lifecycle/viewmodel/CreationExtras;", "defaultViewModelProviderFactory", "Landroidx/lifecycle/ViewModelProvider$Factory;", "getDefaultViewModelProviderFactory", "()Landroidx/lifecycle/ViewModelProvider$Factory;", "lifecycle-viewmodel_release"}, k = 1, mv = {1, 8, 0}, xi = 48)
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface HasDefaultViewModelProviderFactory {
    CreationExtras getDefaultViewModelCreationExtras();

    ViewModelProvider.Factory getDefaultViewModelProviderFactory();

    /* compiled from: HasDefaultViewModelProviderFactory.kt */
    @SynthesizedClassV2(kind = 8, versionHash = "b33e07cc0d03f9f0e6c4c883743d0373fd130388f5a551bfa15ea60a927a2ecb")
    /* renamed from: androidx.lifecycle.HasDefaultViewModelProviderFactory$-CC  reason: invalid class name */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public final /* synthetic */ class CC {
    }
}
