package tv.liangzi.quantum.view;




import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import tv.liangzi.quantum.R;

/**菊花进度条
 * @author Raleigh.luo
 * 2014-7-14
 */
public class CusProgress extends Dialog{ 


	private Context context;
	private View contentView;
	private ProgressTimeOutListener listener;
	private Handler handler;


	/**
	 * @param context
	 * @param handler handler 可为null
	 */
	public CusProgress(Context context,Handler handler) {
		super(context,R.style.CusProgress);
		try{

			this.context=context;
			this.handler=handler;
			if(handler!=null)
				handler.postDelayed(run, 60000);//请求超时
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	@Override
	public void show() {
		try{
			super.show();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	public CusProgress(Context context) {
		super(context, R.style.CusProgress);
		this.context=context;
	}

	public CusProgress(Context context, int theme) {
		super(context, theme);
		this.context=context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try{
			contentView=LayoutInflater.from(getContext()).inflate(R.layout.progress_dialog_layout, null);
			setContentView(contentView);
			getWindow().getAttributes().gravity = Gravity.CENTER;
		}catch(Exception e){
			e.printStackTrace();
		}
	}





	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		try{

			ImageView imageView = (ImageView) contentView
					.findViewById(R.id.loadingImageView);
			AnimationDrawable animationDrawable = (AnimationDrawable) imageView
					.getBackground();
			animationDrawable.start();     
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private Runnable run=new Runnable() {

		@Override
		public void run() {
			try{
				if(isShowing()){
					dismiss();
					if(listener!=null)
						listener.back();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	};

	public void setTimeOutListener(ProgressTimeOutListener listener){
		this.listener=listener;
	}
	public interface ProgressTimeOutListener{
		public void back();
	}

}
