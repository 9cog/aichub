package org.apache.cordova;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.widget.EditText;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CordovaDialogsHelper {
    private final Context context;
    private AlertDialog lastHandledDialog;

    /* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
    public interface Result {
        void gotResult(boolean success, String value);
    }

    public CordovaDialogsHelper(Context context) {
        this.context = context;
    }

    public void showAlert(String message, final Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);
        builder.setTitle("Alert");
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.apache.cordova.CordovaDialogsHelper.2
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.apache.cordova.CordovaDialogsHelper.3
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    result.gotResult(true, null);
                    return false;
                }
                return true;
            }
        });
        this.lastHandledDialog = builder.show();
    }

    public void showConfirm(String message, final Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);
        builder.setTitle("Confirm");
        builder.setCancelable(true);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.4
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, null);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.5
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.apache.cordova.CordovaDialogsHelper.6
            @Override // android.content.DialogInterface.OnCancelListener
            public void onCancel(DialogInterface dialog) {
                result.gotResult(false, null);
            }
        });
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: org.apache.cordova.CordovaDialogsHelper.7
            @Override // android.content.DialogInterface.OnKeyListener
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == 4) {
                    result.gotResult(false, null);
                    return false;
                }
                return true;
            }
        });
        this.lastHandledDialog = builder.show();
    }

    public void showPrompt(String message, String defaultValue, final Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setMessage(message);
        final EditText editText = new EditText(this.context);
        if (defaultValue != null) {
            editText.setText(defaultValue);
        }
        builder.setView(editText);
        builder.setCancelable(false);
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.8
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(true, editText.getText().toString());
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: org.apache.cordova.CordovaDialogsHelper.9
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                result.gotResult(false, null);
            }
        });
        this.lastHandledDialog = builder.show();
    }

    public void destroyLastDialog() {
        AlertDialog alertDialog = this.lastHandledDialog;
        if (alertDialog != null) {
            alertDialog.cancel();
        }
    }
}
