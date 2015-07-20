package tv.liangzi.quantum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.fragment.LiveFragmenttest;
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
		OnTabSelectedListener{

	private static final String TAG = "MainActivity";
	private MyTabWidget mTabWidget;
//	private ExploreFragment exploreFragment;
	private LiveFragmenttest liveFragment;
	private StartFragment startFragment;
	private PeopleFragment peopleFragment;
	private int mIndex = ConstantValues.HOME_FRAGMENT_INDEX;
	private FragmentManager mFragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		 Log.e(TAG, "Mainactivity->onResum() is  " + mIndex);
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
		case 0:
			if (null == liveFragment) {
//				mHomeFragment = new ExploreFragment();
				liveFragment = new LiveFragmenttest();
				transaction.add(R.id.center_layout, liveFragment);
			} else {
				transaction.show(liveFragment);
			}
			break;
		case 1:
			if (null == startFragment) {
				startFragment = new StartFragment();
				transaction.add(R.id.center_layout, startFragment);
			} else {
				transaction.show(startFragment);
			}
			break;
		case 2:
			if (null == peopleFragment) {
//				startActivity(new Intent(MainActivity.this,StartLivingActivity.class
//				));
				peopleFragment = new PeopleFragment();
				transaction.add(R.id.center_layout, peopleFragment);
			} else {
				transaction.show(peopleFragment);
			}
			break;
		case ConstantValues.SETTING_FRAGMENT_INDEX:
//			if (null == mSettingFragment) {
//				mSettingFragment = new PeopleFragment();
//				transaction.add(R.id.center_layout, mSettingFragment);
//			} else {
//				transaction.show(mSettingFragment);
//			}
			break;

		default:
			break;
		}
		mIndex = index;
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (null != liveFragment) {
			transaction.hide(liveFragment);
		}
		if (null != startFragment) {
			transaction.hide(startFragment);
		}
		if (null != peopleFragment) {
			transaction.hide(peopleFragment);
		}
//		if (null != mSettingFragment) {
//			transaction.hide(mSettingFragment);
//		}
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
