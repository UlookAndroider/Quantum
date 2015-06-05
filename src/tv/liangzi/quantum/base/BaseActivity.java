package tv.liangzi.quantum.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.widget.Toast;

public abstract class BaseActivity extends Activity {

	  protected int mScreenWidth;
	  protected int mScreenHeight;

	  @Override
	  protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    DisplayMetrics metric = new DisplayMetrics();
	    getWindowManager().getDefaultDisplay().getMetrics(metric);
	    mScreenWidth = metric.widthPixels;
	    mScreenHeight = metric.heightPixels;
	    
	    setContentView();
	    initViews();
	    initListeners();
	    initData();
	  }

	  Toast mToast;

	  public abstract void setContentView();

	  public abstract void initViews();

	  public abstract void initListeners();

	  public abstract void initData();

	  public void ShowToast(String text) {
	    if (!TextUtils.isEmpty(text)) {
	      if (mToast == null) {
	        mToast = Toast.makeText(getApplicationContext(), text,
	            Toast.LENGTH_SHORT);
	      } else {
	        mToast.setText(text);
	      }
	      mToast.show();
	    }
	  }

	  public int getStateBar() {
	    Rect frame = new Rect();
	    getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
	    int statusBarHeight = frame.top;
	    return statusBarHeight;
	  }

	  public static int dip2px(Context context, float dipValue) {
	    float scale = context.getResources().getDisplayMetrics().density;
	    return (int) (scale * dipValue + 0.5f);
	  }
	}
