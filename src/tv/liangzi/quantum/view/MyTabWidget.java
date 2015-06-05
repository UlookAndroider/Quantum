package tv.liangzi.quantum.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.exception.CustomException;
import tv.liangzi.quantum.utils.LogUtils;

/**
 * �ײ�����
 * 
 * @author dewyze
 * 
 */
public class MyTabWidget extends LinearLayout {

	private static final String TAG = "MyTabWidget";
	private int[] mDrawableIds = new int[] { R.drawable.bg_explore,
			R.drawable.bg_live, R.drawable.bg_sned,
			R.drawable.bg_people };
	// ��ŵײ��˵��ĸ�������CheckedTextView
	private List<CheckedTextView> mCheckedList = new ArrayList<CheckedTextView>();
	// ��ŵײ��˵�ÿ��View
	private List<View> mViewList = new ArrayList<View>();
	// ���ָʾ��
	private List<ImageView> mIndicateImgs = new ArrayList<ImageView>();

	// �ײ��˵�����������
	private CharSequence[] mLabels;

	public MyTabWidget(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.TabWidget, defStyle, 0);

		// ��ȡxml�У�����tabʹ�õ�����
		mLabels = a.getTextArray(R.styleable.TabWidget_bottom_labels);

		if (null == mLabels || mLabels.length <= 0) {
			try {
				throw new CustomException("....");
			} catch (CustomException e) {
				e.printStackTrace();
			} finally {
				LogUtils.i(TAG, MyTabWidget.class.getSimpleName() + " ..");
			}
			a.recycle();
			return;
		}

		a.recycle();

		// ��ʼ���ؼ�
		init(context);
	}

	public MyTabWidget(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyTabWidget(Context context) {
		super(context);
		init(context);
	}

	/**
	 *
	 */
	private void init(final Context context) {
		this.setOrientation(LinearLayout.HORIZONTAL);
//		this.setBackgroundResource(R.drawable.index_bottom_bar);
        this.setBackgroundColor(getResources().getColor(R.color.white));
		LayoutInflater inflater = LayoutInflater.from(context);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.weight = 1.0f;
		params.gravity = Gravity.CENTER;

		int size = mLabels.length;
		for (int i = 0; i < size; i++) {

			final int index = i;

			// ÿ��tab��Ӧ��layout
			final View view = inflater.inflate(R.layout.tab_item, null);

			final CheckedTextView itemName = (CheckedTextView) view
					.findViewById(R.id.item_name);
			itemName.setCompoundDrawablesWithIntrinsicBounds(null, context
					.getResources().getDrawable(mDrawableIds[i]), null, null);
			itemName.setText(mLabels[i]);

			/*final ImageView indicateImg = (ImageView) view
					.findViewById(R.id.indicate_img);*/

			this.addView(view, params);

			// CheckedTextView
			itemName.setTag(index);

			mCheckedList.add(itemName);
//			mIndicateImgs.add(indicateImg);
			mViewList.add(view);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					setTabsDisplay(context, index);

					if (null != mTabListener) {
						mTabListener.onTabSelected(index);
					}
				}
			});

			//
			if (i == 0) {
				itemName.setChecked(true);
				itemName.setTextColor(Color.rgb(0, 0, 0));
//				view.setBackgroundColor(Color.rgb(240, 241, 242));
			} else {
				itemName.setChecked(false);
				itemName.setTextColor(Color.rgb(200, 5, 20));
//				view.setBackgroundColor(Color.rgb(250, 250, 250));
			}

		}
	}

	/**
	 * ����ָʾ�����ʾ
	 * 
	 * @param context
	 * @param position
	 *            ��ʾλ��
	 * @param visible
	 *
	 */
	public void setIndicateDisplay(Context context, int position,
			boolean visible) {
		int size = mIndicateImgs.size();
		if (size <= position) {

			return;
		}
		ImageView indicateImg = mIndicateImgs.get(position);
		indicateImg.setVisibility(visible ? View.VISIBLE : View.GONE);
	}


	/**
	 *
	 */
	public void setTabsDisplay(Context context, int index) {
		int size = mCheckedList.size();
		for (int i = 0; i < size; i++) {
			CheckedTextView checkedTextView = mCheckedList.get(i);
			if ((Integer) (checkedTextView.getTag()) == index) {
				LogUtils.i(TAG, mLabels[index] + " is selected...");
				checkedTextView.setChecked(true);
				checkedTextView.setTextColor(Color.rgb(200, 5, 20));
//				mViewList.get(i).setBackgroundColor(Color.rgb(240, 241, 242));
			} else {
				checkedTextView.setChecked(false);
				checkedTextView.setTextColor(Color.rgb(181, 186, 175));
//				mViewList.get(i).setBackgroundColor(Color.rgb(250, 250, 250));
			}
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

		if (widthSpecMode != MeasureSpec.EXACTLY) {
			widthSpecSize = 0;
		}

		if (heightSpecMode != MeasureSpec.EXACTLY) {
			heightSpecSize = 0;
		}

		if (widthSpecMode == MeasureSpec.UNSPECIFIED
				|| heightSpecMode == MeasureSpec.UNSPECIFIED) {
		}

		// �ؼ������߶ȣ������±�tab�ı������߶�
		int width;
		int height;
		width = Math.max(getMeasuredWidth(), widthSpecSize);
		height = Math.max(this.getBackground().getIntrinsicHeight(),
				heightSpecSize);
		setMeasuredDimension(width, height);
	}

	private OnTabSelectedListener mTabListener;

	public interface OnTabSelectedListener {
		void onTabSelected(int index);
	}

	public void setOnTabSelectedListener(OnTabSelectedListener listener) {
		this.mTabListener = listener;
	}

}
