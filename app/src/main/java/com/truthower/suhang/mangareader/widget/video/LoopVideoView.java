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
 * any playback controls. Only the active page keeps a MediaPlayer, so decoders are not
 * exhausted while sliding through several videos.
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
        Uri uri = Uri.parse(url);
        if (uri.equals(videoUri)) {
            if (mediaPlayer == null && wantedPlaying) {
                openVideo();
            }
            return;
        }
        videoUri = uri;
        videoWidth = 0;
        videoHeight = 0;
        releasePlayer();
        requestLayout();
        if (wantedPlaying) {
            openVideo();
        }
    }

    public void start() {
        wantedPlaying = true;
        if (mediaPlayer == null) {
            openVideo();
        } else if (prepared && surface != null) {
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
        if (videoUri == null || !isAvailable()) {
            return;
        }
        if (surface == null) {
            surface = new Surface(getSurfaceTexture());
        }
        if (mediaPlayer != null) {
            mediaPlayer.setSurface(surface);
            if (prepared && wantedPlaying) {
                mediaPlayer.start();
            }
            return;
        }
        try {
            final MediaPlayer player = new MediaPlayer();
            mediaPlayer = player;
            player.setSurface(surface);
            player.setLooping(true);
            player.setVolume(0f, 0f);
            player.setOnPreparedListener(mp -> {
                if (mp != mediaPlayer) {
                    return;
                }
                prepared = true;
                videoWidth = mp.getVideoWidth();
                videoHeight = mp.getVideoHeight();
                requestLayout();
                if (wantedPlaying && surface != null) {
                    mp.start();
                }
            });
            player.setOnErrorListener((mp, what, extra) -> {
                if (mp == mediaPlayer) {
                    releasePlayer();
                }
                return true;
            });
            player.setDataSource(getContext(), videoUri);
            player.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            releasePlayer();
        }
    }

    private void releasePlayer() {
        prepared = false;
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setSurface(null);
            } catch (Exception ignored) {
            }
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
        if (mediaPlayer != null) {
            try {
                mediaPlayer.setSurface(null);
            } catch (Exception ignored) {
            }
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
        surface = new Surface(surfaceTexture);
        if (mediaPlayer == null) {
            openVideo();
        } else {
            mediaPlayer.setSurface(surface);
            if (prepared && wantedPlaying) {
                mediaPlayer.start();
            }
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
            } catch (Exception ignored) {
            }
            try {
                mediaPlayer.setSurface(null);
            } catch (Exception ignored) {
            }
        }
        if (surface != null) {
            surface.release();
            surface = null;
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
    }
}
