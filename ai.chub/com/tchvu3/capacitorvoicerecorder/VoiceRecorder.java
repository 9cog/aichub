package com.tchvu3.capacitorvoicerecorder;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Base64;
import com.getcapacitor.PermissionState;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
@CapacitorPlugin(name = "VoiceRecorder", permissions = {@Permission(alias = VoiceRecorder.RECORD_AUDIO_ALIAS, strings = {"android.permission.RECORD_AUDIO"})})
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class VoiceRecorder extends Plugin {
    static final String RECORD_AUDIO_ALIAS = "voice recording";
    private CustomMediaRecorder mediaRecorder;

    @PluginMethod
    public void canDeviceVoiceRecord(PluginCall pluginCall) {
        if (CustomMediaRecorder.canPhoneCreateMediaRecorder(getContext())) {
            pluginCall.resolve(ResponseGenerator.successResponse());
        } else {
            pluginCall.resolve(ResponseGenerator.failResponse());
        }
    }

    @PluginMethod
    public void requestAudioRecordingPermission(PluginCall pluginCall) {
        if (doesUserGaveAudioRecordingPermission()) {
            pluginCall.resolve(ResponseGenerator.successResponse());
        } else {
            requestPermissionForAlias(RECORD_AUDIO_ALIAS, pluginCall, "recordAudioPermissionCallback");
        }
    }

    @PermissionCallback
    private void recordAudioPermissionCallback(PluginCall pluginCall) {
        hasAudioRecordingPermission(pluginCall);
    }

    @PluginMethod
    public void hasAudioRecordingPermission(PluginCall pluginCall) {
        pluginCall.resolve(ResponseGenerator.fromBoolean(doesUserGaveAudioRecordingPermission()));
    }

    @PluginMethod
    public void startRecording(PluginCall pluginCall) {
        if (!CustomMediaRecorder.canPhoneCreateMediaRecorder(getContext())) {
            pluginCall.reject(Messages.CANNOT_RECORD_ON_THIS_PHONE);
        } else if (!doesUserGaveAudioRecordingPermission()) {
            pluginCall.reject(Messages.MISSING_PERMISSION);
        } else if (isMicrophoneOccupied()) {
            pluginCall.reject(Messages.MICROPHONE_BEING_USED);
        } else if (this.mediaRecorder != null) {
            pluginCall.reject(Messages.ALREADY_RECORDING);
        } else {
            try {
                CustomMediaRecorder customMediaRecorder = new CustomMediaRecorder(getContext());
                this.mediaRecorder = customMediaRecorder;
                customMediaRecorder.startRecording();
                pluginCall.resolve(ResponseGenerator.successResponse());
            } catch (Exception e) {
                this.mediaRecorder = null;
                pluginCall.reject(Messages.FAILED_TO_RECORD, e);
            }
        }
    }

    @PluginMethod
    public void stopRecording(PluginCall pluginCall) {
        RecordData recordData;
        CustomMediaRecorder customMediaRecorder = this.mediaRecorder;
        if (customMediaRecorder == null) {
            pluginCall.reject(Messages.RECORDING_HAS_NOT_STARTED);
            return;
        }
        try {
            try {
                customMediaRecorder.stopRecording();
                File outputFile = this.mediaRecorder.getOutputFile();
                recordData = new RecordData(readRecordedFileAsBase64(outputFile), getMsDurationOfAudioFile(outputFile.getAbsolutePath()), "audio/aac");
            } catch (Exception e) {
                pluginCall.reject(Messages.FAILED_TO_FETCH_RECORDING, e);
            }
            if (recordData.getRecordDataBase64() != null && recordData.getMsDuration() >= 0) {
                pluginCall.resolve(ResponseGenerator.dataResponse(recordData.toJSObject()));
            }
            pluginCall.reject(Messages.EMPTY_RECORDING);
        } finally {
            this.mediaRecorder.deleteOutputFile();
            this.mediaRecorder = null;
        }
    }

    @PluginMethod
    public void pauseRecording(PluginCall pluginCall) {
        CustomMediaRecorder customMediaRecorder = this.mediaRecorder;
        if (customMediaRecorder == null) {
            pluginCall.reject(Messages.RECORDING_HAS_NOT_STARTED);
            return;
        }
        try {
            pluginCall.resolve(ResponseGenerator.fromBoolean(customMediaRecorder.pauseRecording()));
        } catch (NotSupportedOsVersion unused) {
            pluginCall.reject(Messages.NOT_SUPPORTED_OS_VERSION);
        }
    }

    @PluginMethod
    public void resumeRecording(PluginCall pluginCall) {
        CustomMediaRecorder customMediaRecorder = this.mediaRecorder;
        if (customMediaRecorder == null) {
            pluginCall.reject(Messages.RECORDING_HAS_NOT_STARTED);
            return;
        }
        try {
            pluginCall.resolve(ResponseGenerator.fromBoolean(customMediaRecorder.resumeRecording()));
        } catch (NotSupportedOsVersion unused) {
            pluginCall.reject(Messages.NOT_SUPPORTED_OS_VERSION);
        }
    }

    @PluginMethod
    public void getCurrentStatus(PluginCall pluginCall) {
        CustomMediaRecorder customMediaRecorder = this.mediaRecorder;
        if (customMediaRecorder == null) {
            pluginCall.resolve(ResponseGenerator.statusResponse(CurrentRecordingStatus.NONE));
        } else {
            pluginCall.resolve(ResponseGenerator.statusResponse(customMediaRecorder.getCurrentStatus()));
        }
    }

    private boolean doesUserGaveAudioRecordingPermission() {
        return getPermissionState(RECORD_AUDIO_ALIAS).equals(PermissionState.GRANTED);
    }

    private String readRecordedFileAsBase64(File file) {
        byte[] bArr = new byte[(int) file.length()];
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            bufferedInputStream.read(bArr);
            bufferedInputStream.close();
            return Base64.encodeToString(bArr, 0);
        } catch (IOException unused) {
            return null;
        }
    }

    private int getMsDurationOfAudioFile(String str) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(str);
            mediaPlayer.prepare();
            return mediaPlayer.getDuration();
        } catch (Exception unused) {
            return -1;
        }
    }

    private boolean isMicrophoneOccupied() {
        AudioManager audioManager = (AudioManager) getContext().getSystemService("audio");
        return audioManager == null || audioManager.getMode() != 0;
    }
}
