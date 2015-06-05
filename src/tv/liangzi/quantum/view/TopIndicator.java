package tv.liangzi.quantum.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CheckedTextView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.utils.LogUtils;

/**
 * ����indicator
 * 
 * @author dewyze
 * 
 */
public class TopIndicator extends LinearLayout {

	private static final String TAG = "TopIndicator";
	private int[] mDrawableIds = new int[] { R.drawable.bg_explore,
			R.drawable.bg_live, R.drawable.bg_sned,
			R.drawable.bg_people };
	private List<CheckedTextView> mCheckedList = new ArrayList<CheckedTextView>();
	private List<View> mViewList = new ArrayList<View>();
	// �����˵�����������
	private CharSequence[] mLabels = new CharSequence[] { "��ѡ", "����", "��",
			"�Ź�" };
	private int mScreenWidth;
	private int mUnderLineWidth;
	private View mUnderLine;
	// �ײ������ƶ���ʼλ��
	private int mUnderLineFromX = 0;

	public TopIndicator(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TopIndicator(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TopIndicator(Context context) {
		super(context);
		init(context);
	}

	private void init(final Context context) {
		setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundColor(Color.rgb(250, 250, 250));

		mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
		mUnderLineWidth = mScreenWidth / mLabels.length;

		mUnderLine = new View(context);
		mUnderLine.setBackgroundColor(Color.rgb(247, 88, 123));
		LinearLayout.LayoutParams underLineParams = new LinearLayout.LayoutParams(
				mUnderLineWidth, 4);

		// ����layout
		LinearLayout topLayout = new LinearLayout(context);
		LinearLayout.LayoutParams topLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		topLayout.setOrientation(LinearLayout.HORIZONTAL);

		LayoutInflater inflater = LayoutInflater.from(context);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;

		int size = mLabels.length;
		for (int i = 0; i < size; i++) {

			final int index = i;

			final View view = inflater.inflate(R.layout.top_indicator_item,
					null);

			final CheckedTextView itemName = (CheckedTextView) view
					.findViewById(R.id.item_name);
			itemName.setCompoundDrawablesWithIntrinsicBounds(context
					.getResources().getDrawable(mDrawableIds[i]), null, null,
					null);
			itemName.setCompoundDrawablePadding(10);
			itemName.setText(mLabels[i]);

			topLayout.addView(view, params);

			itemName.setTag(index);

			mCheckedList.add(itemName);
			mViewList.add(view);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (null != mTabListener) {
						mTabListener.onIndicatorSelected(index);
					}
				}
			});

			// ��ʼ�� �ײ��˵�ѡ��״̬,Ĭ�ϵ�һ��ѡ��
			if (i == 0) {
				itemName.setChecked(true);
				itemName.setTextColor(Color.rgb(247, 88, 123));
			} else {
				itemName.setChecked(false);
				itemName.setTextColor(Color.rgb(19, 12, 14));
			}

		}
		this.addView(topLayout, topLayoutParams);
		this.addView(mUnderLine, underLineParams);
	}

	/**
	 * ���õײ�������ͼƬ��ʾ״̬��������ɫ
	 */
	public void setTabsDisplay(Context context, int index) {
		int size = mCheckedList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				LogUtils.i(TAG, mLabels[index] + " is selected...");
				checkedTextView.setChecked(true);
				checkedTextView.setTextColor(Color.rgb(247, 88, 123));
			} else {
				checkedTextView.setChecked(false);
				checkedTextView.setTextColor(Color.rgb(19, 12, 14));
			}
		}
		// �»��߶���
		doUnderLineAnimation(index);
	}

	private void doUnderLineAnimation(int index) {
		TranslateAnimation animation = new TranslateAnimation(mUnderLineFromX,
				index * mUnderLineWidth, 0, 0);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setFillAfter(true);
		animation.setDuration(150);
		mUnderLine.startAnimation(animation);
		mUnderLineFromX = index * mUnderLineWidth;
	}

	// �ص��ӿ�
	private OnTopIndicatorListener mTabListener;

	public interface OnTopIndicatorListener {
		void onIndicatorSelected(int index);
	}

	public void setOnTopIndicatorListener(OnTopIndicatorListener listener) {
		this.mTabListener = listener;
	}

}
