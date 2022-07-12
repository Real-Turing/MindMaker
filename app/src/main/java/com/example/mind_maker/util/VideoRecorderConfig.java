package com.example.mind_maker.util;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.text.TextUtils;

public class VideoRecorderConfig {
    //Camera
    private Camera camera;
    //摄像头预览宽度
    private int videoWidth;
    //摄像头预览高度
    private int videoHeight;
    //摄像头预览偏转角度
    private int cameraRotation;
    //保存的文件路径
    private String path;
    //由于Camera使用的是SurfaceTexture，所以这里使用了SurfaceTexture
    //也可使用SurfaceHolder
    private SurfaceTexture mSurfaceTexture;

    private int cameraId = 0;

    public SurfaceTexture getSurfaceTexture() {
        return mSurfaceTexture;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture) {
        mSurfaceTexture = surfaceTexture;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public int getVideoHeight() {
        return videoHeight;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

    public int getCameraRotation() {
        return cameraRotation;
    }

    public void setCameraRotation(int cameraRotation) {
        this.cameraRotation = cameraRotation;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public boolean checkParam() {
        return mSurfaceTexture != null && camera != null && videoWidth > 0 && videoHeight > 0 && !TextUtils.isEmpty(path);
    }
}
