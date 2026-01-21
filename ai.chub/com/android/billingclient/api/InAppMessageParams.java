package com.android.billingclient.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
/* compiled from: com.android.billingclient:billing@@5.0.0 */
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public final class InAppMessageParams {
    private final ArrayList<Integer> mInAppMessageCategories;

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public static final class Builder {
        private final Set<Integer> mInAppMessageCategories = new HashSet();

        public Builder addAllInAppMessageCategoriesToShow() {
            this.mInAppMessageCategories.add(2);
            return this;
        }

        public Builder addInAppMessageCategoryToShow(int i) {
            this.mInAppMessageCategories.add(Integer.valueOf(i));
            return this;
        }

        public InAppMessageParams build() {
            return new InAppMessageParams(this.mInAppMessageCategories);
        }
    }

    /* compiled from: com.android.billingclient:billing@@5.0.0 */
    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public @interface InAppMessageCategoryId {
        public static final int TRANSACTIONAL = 2;
        public static final int UNKNOWN_IN_APP_MESSAGE_CATEGORY_ID = 0;
    }

    private InAppMessageParams(Set<Integer> set) {
        this.mInAppMessageCategories = new ArrayList<>(Collections.unmodifiableList(new ArrayList(set)));
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Integer> getInAppMessageCategoriesToShow() {
        return this.mInAppMessageCategories;
    }
}
