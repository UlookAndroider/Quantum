package tv.liangzi.quantum.activity;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;

public class AboutActivity extends BaseActivity{
private TextView tvRule;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_about);
	}

	@Override
	public void initViews() {
		tvRule= (TextView) findViewById(R.id.tv_about);
		tvRule.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public void initListeners() {

	}

	@Override
	public void initData() {

	}
}











