package com.truthower.suhang.mangareader.business.read;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.ViewParent;

import androidx.viewpager.widget.ViewPager;

/**
 * Found at http://stackoverflow.com/questions/7814017/is-it-possible-to-disable-scrolling-on-a-viewpager.
 * Convenient way to temporarily disable ViewPager navigation while interacting with ImageView.
 * 
 * Julia Zudikova
 */

/**
 * Hacky fix for Issue #4 and
 * http://code.google.com/p/android/issues/detail?id=18990
 * <p/>
 * ScaleGestureDetector seems to mess up the touch events, which means that
 * ViewGroups which make use of onInterceptTouchEvent throw a lot of
 * IllegalArgumentException: pointerIndex out of range.
 * <p/>
 * There's not much I can do in my code for now, but we can mask the result by
 * just catching the problem and ignoring it.
 * <p/>
 * A zoomable child (PhotoView) is allowed to pan until it reaches its edge;
 * only then does the pager take over the same gesture. Once the pager owns the
 * gesture it uses fake drags so the page follows the finger smoothly instead of
 * jumping by everything the finger moved while panning the photo.
 *
 * @author Chris Banes
 */
public class HackyViewPager extends ViewPager {

    private boolean isLocked;
    private final int touchSlop;
    private float downX, downY, lastX, lastY;
    private boolean childScrolling;
    private boolean fakeDragging;
    private boolean multiTouch;

    public HackyViewPager(Context context) {
        super(context);
        isLocked = false;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public HackyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = false;
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * The child (PhotoView) asks us to stop intercepting while it pans. We answer
     * false anyway so this pager keeps seeing every move and can detect the edge;
     * the interception decision is made in onInterceptTouchEvent instead.
     */
    @Override
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        super.requestDisallowInterceptTouchEvent(false);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isLocked) {
            return false;
        }
        try {
            final float x = ev.getX();
            final float y = ev.getY();
            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    if (fakeDragging) {
                        endFakeDrag();
                        fakeDragging = false;
                    }
                    downX = lastX = x;
                    downY = lastY = y;
                    childScrolling = false;
                    multiTouch = false;
                    return super.onInterceptTouchEvent(ev);
                case MotionEvent.ACTION_POINTER_DOWN:
                    multiTouch = true;
                    return false;
                case MotionEvent.ACTION_POINTER_UP:
                    final int index = ev.getActionIndex() == 0 ? 1 : 0;
                    lastX = ev.getX(index);
                    lastY = ev.getY(index);
                    return false;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    childScrolling = false;
                    multiTouch = false;
                    return super.onInterceptTouchEvent(ev);
                case MotionEvent.ACTION_MOVE:
                    break;
                default:
                    return false;
            }
            if (multiTouch) {
                return false;
            }

            if (childScrolling) {
                final float mdx = x - lastX;
                final float mdy = y - lastY;
                if (Math.abs(mdx) < 1f || Math.abs(mdx) <= Math.abs(mdy)) {
                    lastX = x;
                    lastY = y;
                    return false;
                }
                if (canScroll(this, false, (int) mdx, (int) x, (int) y)) {
                    lastX = x;
                    lastY = y;
                    return false;
                }
                if (!startFakeDrag()) {
                    lastX = x;
                    lastY = y;
                    return false;
                }
                fakeDragBy(x - lastX);
                lastX = x;
                lastY = y;
                return true;
            }

            final float dx = x - downX;
            final float dy = y - downY;
            if (Math.abs(dx) > touchSlop && Math.abs(dx) * 0.5f > Math.abs(dy)) {
                if (canScroll(this, false, (int) (x - lastX), (int) x, (int) y)) {
                    childScrolling = true;
                    lastX = x;
                    lastY = y;
                    return false;
                }
                return super.onInterceptTouchEvent(ev);
            }
            return false;
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean startFakeDrag() {
        if (!beginFakeDrag()) {
            return false;
        }
        fakeDragging = true;
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true);
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isLocked) {
            return false;
        }
        if (fakeDragging) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    fakeDragBy(event.getX() - lastX);
                    lastX = event.getX();
                    lastY = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    endFakeDrag();
                    fakeDragging = false;
                    childScrolling = false;
                    return true;
                default:
                    return true;
            }
        }
        try {
            return super.onTouchEvent(event);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void toggleLock() {
        isLocked = !isLocked;
    }

    public void setLocked(boolean isLocked) {
        this.isLocked = isLocked;
    }

    public boolean isLocked() {
        return isLocked;
    }

}
