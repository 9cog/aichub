package net.openid.appauth;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class RedirectUriReceiverActivity extends AppCompatActivity {
    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceBundle) {
        super.onCreate(savedInstanceBundle);
        startActivity(AuthorizationManagementActivity.createResponseHandlingIntent(this, getIntent().getData()));
        finish();
    }
}
