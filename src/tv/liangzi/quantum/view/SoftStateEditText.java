package tv.liangzi.quantum.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 搜索主界面，用于监听键盘的隐藏
 * @author naren
 *
 */
public class SoftStateEditText extends EditText {

    public static final int SOFT_HIDE = 1;
    public static final int SOFT_SHOW = 2;
    public static final int MESSAGE = 9528;
    public static final int DISTANCE = 95;

    public interface OnSoftStateListener {
        void onSoftDisplay();

        void onSoftHide();
    }

    private OnSoftStateListener onSoftStateListener;


    public void setOnSoftStateListener(OnSoftStateListener onSoftStateListener) {
        this.onSoftStateListener = onSoftStateListener;
    }

    public SoftStateEditText(Context context) {
        super(context);
    }

    public SoftStateEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int type = SOFT_HIDE;
        if((oldh >0&&h < oldh)||Math.abs(h - oldh) < DISTANCE){
            type = SOFT_SHOW;
        }
   /*     Message msg = new Message();
        msg.what = MESSAGE;
        msg.arg1 = type;
        new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MESSAGE:
                        if (msg.arg1 == SOFT_HIDE) {
                            onSoftStateListener.onSoftHide();
                        } else {
                            onSoftStateListener.onSoftDisplay();
                        }
                        break;
                }
                return true;
            }
        }).sendMessage(msg);*/
    }

}