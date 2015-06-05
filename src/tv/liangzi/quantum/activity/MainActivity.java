package tv.liangzi.quantum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.fragment.ExploreFragment;
import tv.liangzi.quantum.fragment.LiveFragment;
import tv.liangzi.quantum.fragment.PeopleFragment;
import tv.liangzi.quantum.fragment.StartFragment;
import tv.liangzi.quantum.utils.ConstantValues;
import tv.liangzi.quantum.view.MyTabWidget;
import tv.liangzi.quantum.view.MyTabWidget.OnTabSelectedListener;

/**
 * 
 * 
 * @author dewyze
 * 
 */
public class MainActivity extends FragmentActivity implements
		OnTabSelectedListener {

	private static final String TAG = "MainActivity";
	private MyTabWidget mTabWidget;
	private ExploreFragment mHomeFragment;
	private LiveFragment mCategoryFragment;
	private StartFragment mCollectFragment;
	private PeopleFragment mSettingFragment;
	private int mIndex = ConstantValues.HOME_FRAGMENT_INDEX;
	private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		 Log.e(TAG, "onResum() is  " + mIndex);
		init();
		initEvents();
	}

	private void init() {
		mFragmentManager = getSupportFragmentManager();
		mTabWidget = (MyTabWidget) findViewById(R.id.tab_widget);
	}

	private void initEvents() {
		mTabWidget.setOnTabSelectedListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent intent=getIntent();//		 mIndex=intent.getIntExtra("index", 0);

		onTabSelected(mIndex);
		mTabWidget.setTabsDisplay(this, mIndex);
//		mTabWidget.setIndicateDisplay(this, 3, true);
		  Log.e(TAG, "onresum is  " + mIndex);
	}

	@Override
	public void onTabSelected(int index) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case ConstantValues.HOME_FRAGMENT_INDEX:
			if (null == mHomeFragment) {
				mHomeFragment = new ExploreFragment();
				transaction.add(R.id.center_layout, mHomeFragment);
			} else {
				transaction.show(mHomeFragment);
			}
			break;
		case ConstantValues.CATEGORY_FRAGMENT_INDEX:
			if (null == mCategoryFragment) {
				mCategoryFragment = new LiveFragment();
				transaction.add(R.id.center_layout, mCategoryFragment);
			} else {
				transaction.show(mCategoryFragment);
			}
			break;
		case ConstantValues.COLLECT_FRAGMENT_INDEX:
			if (null == mCollectFragment) {
//				startActivity(new Intent(MainActivity.this,StartLivingActivity.class
//				));
				mCollectFragment = new StartFragment();
				transaction.add(R.id.center_layout, mCollectFragment);
			} else {
				transaction.show(mCollectFragment);
			}
			break;
		case ConstantValues.SETTING_FRAGMENT_INDEX:
			if (null == mSettingFragment) {
				mSettingFragment = new PeopleFragment();
				transaction.add(R.id.center_layout, mSettingFragment);
			} else {
				transaction.show(mSettingFragment);
			}
			break;

		default:
			break;
		}
		mIndex = index;
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (null != mHomeFragment) {
			transaction.hide(mHomeFragment);
		}
		if (null != mCategoryFragment) {
			transaction.hide(mCategoryFragment);
		}
		if (null != mCollectFragment) {
			transaction.hide(mCollectFragment);
		}
		if (null != mSettingFragment) {
			transaction.hide(mSettingFragment);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// �Լ���¼fragment��λ��,��ֹactivity��ϵͳ����ʱ��fragment���ҵ�����
		// super.onSaveInstanceState(outState);
		outState.putInt("index", mIndex);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// super.onRestoreInstanceState(savedInstanceState);
		mIndex = savedInstanceState.getInt("index");
	}

}
