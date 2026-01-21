package androidx.activity.result;

import androidx.activity.result.contract.ActivityResultContract;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public interface ActivityResultCaller {
    <I, O> ActivityResultLauncher<I> registerForActivityResult(ActivityResultContract<I, O> activityResultContract, ActivityResultCallback<O> activityResultCallback);

    <I, O> ActivityResultLauncher<I> registerForActivityResult(ActivityResultContract<I, O> activityResultContract, ActivityResultRegistry activityResultRegistry, ActivityResultCallback<O> activityResultCallback);
}
