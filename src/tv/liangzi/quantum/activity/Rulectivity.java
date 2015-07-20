package tv.liangzi.quantum.activity;

import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;

public class Rulectivity extends BaseActivity{
private TextView tvRule;

	@Override
	public void setContentView() {
		setContentView(R.layout.activity_rule);
	}

	@Override
	public void initViews() {
		tvRule= (TextView) findViewById(R.id.textview);
		tvRule.setMovementMethod(new ScrollingMovementMethod());
	}

	@Override
	public void initListeners() {

	}

	@Override
	public void initData() {

	}
}











