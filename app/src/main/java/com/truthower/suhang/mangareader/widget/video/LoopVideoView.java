package com.truthower.suhang.mangareader.widget.video;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

/**
 * A TextureView based player that behaves like a GIF: muted, looping, centered and without
 * any playback controls. TextureView is used instead of VideoView so the video follows page
 * transforms and never leaves a stale SurfaceView frame on top of the neighbouring page.
 */
public class LoopVideoView extends TextureView implements TextureView.SurfaceTextureListener {
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private Uri videoUri;
    private int videoWidth;
    private int videoHeight;
    private boolean prepared;
    private boolean wantedPlaying;

    public LoopVideoView(Context context) {
        super(context);
        init();
    }

    public LoopVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoopVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setSurfaceTextureListener(this);
    }

    public void setVideoUrl(String url) {
        videoUri = Uri.parse(url);
        openVideo();
    }

    public void start() {
        wantedPlaying = true;
        if (prepared && mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    public void pause() {
        wantedPlaying = false;
        if (mediaPlayer != null && prepared) {
            mediaPlayer.pause();
        }
    }

    public void stopPlayback() {
        wantedPlaying = false;
        releasePlayer();
    }

    private void openVideo() {
        releasePlayer();
        if (videoUri == null || !isAvailable()) {
            return;
        }
        try {
            mediaPlayer = new MediaPlayer();
            surface = new Surface(getSurfaceTexture());
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(true);
            mediaPlayer.setVolume(0f, 0f);
            mediaPlayer.setOnPreparedListener(mp -> {
                prepared = true;
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                requestLayout();
                if (wantedPlaying) {
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                releasePlayer();
                return true;
            });
            mediaPlayer.setDataSource(getContext(), videoUri);
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            releasePlayer();
        }
    }

    private void releasePlayer() {
        prepared = false;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception ignored) {
            }
            mediaPlayer = null;
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0 && widthSize > 0 && heightSize > 0) {
            float scale = Math.min((float) widthSize / videoWidth, (float) heightSize / videoHeight);
            setMeasuredDimension(
                    Math.max(1, Math.round(videoWidth * scale)),
                    Math.max(1, Math.round(videoHeight * scale)));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        openVideo();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        releasePlayer();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }

    @Override
    protected void onDetachedFromWindow() {
        pause();
        super.onDetachedFromWindow();
    }
}
