package com.tchvu3.capacitorvoicerecorder;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import java.io.File;
import java.io.IOException;
/* loaded from: /home/ubuntu/aichub_analysis/apk_contents/classes.dex */
public class CustomMediaRecorder {
    private final Context context;
    private CurrentRecordingStatus currentRecordingStatus = CurrentRecordingStatus.NONE;
    private MediaRecorder mediaRecorder;
    private File outputFile;

    public static boolean canPhoneCreateMediaRecorder(Context context) {
        return true;
    }

    public CustomMediaRecorder(Context context) throws IOException {
        this.context = context;
        generateMediaRecorder();
    }

    private void generateMediaRecorder() throws IOException {
        MediaRecorder mediaRecorder = new MediaRecorder();
        this.mediaRecorder = mediaRecorder;
        mediaRecorder.setAudioSource(1);
        this.mediaRecorder.setOutputFormat(6);
        this.mediaRecorder.setAudioEncoder(3);
        this.mediaRecorder.setAudioEncodingBitRate(96000);
        this.mediaRecorder.setAudioSamplingRate(44100);
        setRecorderOutputFile();
        this.mediaRecorder.prepare();
    }

    private void setRecorderOutputFile() throws IOException {
        File createTempFile = File.createTempFile("voice_record_temp", ".aac", this.context.getCacheDir());
        this.outputFile = createTempFile;
        createTempFile.deleteOnExit();
        this.mediaRecorder.setOutputFile(this.outputFile.getAbsolutePath());
    }

    public void startRecording() {
        this.mediaRecorder.start();
        this.currentRecordingStatus = CurrentRecordingStatus.RECORDING;
    }

    public void stopRecording() {
        this.mediaRecorder.stop();
        this.mediaRecorder.release();
        this.currentRecordingStatus = CurrentRecordingStatus.NONE;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public boolean pauseRecording() throws NotSupportedOsVersion {
        if (Build.VERSION.SDK_INT < 24) {
            throw new NotSupportedOsVersion();
        }
        if (this.currentRecordingStatus == CurrentRecordingStatus.RECORDING) {
            this.mediaRecorder.pause();
            this.currentRecordingStatus = CurrentRecordingStatus.PAUSED;
            return true;
        }
        return false;
    }

    public boolean resumeRecording() throws NotSupportedOsVersion {
        if (Build.VERSION.SDK_INT < 24) {
            throw new NotSupportedOsVersion();
        }
        if (this.currentRecordingStatus == CurrentRecordingStatus.PAUSED) {
            this.mediaRecorder.resume();
            this.currentRecordingStatus = CurrentRecordingStatus.RECORDING;
            return true;
        }
        return false;
    }

    public CurrentRecordingStatus getCurrentStatus() {
        return this.currentRecordingStatus;
    }

    public boolean deleteOutputFile() {
        return this.outputFile.delete();
    }

    private static boolean canPhoneCreateMediaRecorderWhileHavingPermission(Context context) {
        CustomMediaRecorder customMediaRecorder;
        CustomMediaRecorder customMediaRecorder2 = null;
        try {
            try {
                customMediaRecorder = new CustomMediaRecorder(context);
            } catch (Exception e) {
                e = e;
            }
        } catch (Throwable th) {
            th = th;
        }
        try {
            customMediaRecorder.startRecording();
            customMediaRecorder.stopRecording();
            customMediaRecorder.deleteOutputFile();
            return true;
        } catch (Exception e2) {
            e = e2;
            customMediaRecorder2 = customMediaRecorder;
            boolean startsWith = e.getMessage().startsWith("stop failed");
            if (customMediaRecorder2 != null) {
                customMediaRecorder2.deleteOutputFile();
            }
            return startsWith;
        } catch (Throwable th2) {
            th = th2;
            customMediaRecorder2 = customMediaRecorder;
            if (customMediaRecorder2 != null) {
                customMediaRecorder2.deleteOutputFile();
            }
            throw th;
        }
    }
}
