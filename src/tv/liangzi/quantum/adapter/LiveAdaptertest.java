package tv.liangzi.quantum.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import tv.liangzi.quantum.R;
import tv.liangzi.quantum.bean.Live;
import tv.liangzi.quantum.utils.DateUtil;


public class LiveAdaptertest extends BaseAdapter implements SectionIndexer,StickyListHeadersAdapter
		 {
	private LayoutInflater inflater;
	private static final int TYPE_LIVE=1;
	private static final int TYPE_WAIT=0;
	private static final int TYPE_LIVE_TITLE=2;
	private static final int TYPE_WAIT_TITLE=3;
	private int mUserId=0;
	private Context mContext;
	private View livingView;
	private View titleView;
	private List<Live> mList;
	private int[] sectionIndices;
	private Integer[] sectionHeaders;
			 private int mScreennWidth;
	OnItemButtonClickListener mListener;

	public LiveAdaptertest(Context context,List<Live> list,int userId,int width){
		mUserId=userId;
		mScreennWidth=width;
		inflater=LayoutInflater.from(context);
		mContext=context;
		mList=list;
		if(mList.size()>0)
		{
			sectionIndices = getSectionIndices();
			sectionHeaders=getSectionHeaders();
		}

	}
	public void setLives(List<Live> lives){
		mList=lives;
		if (mList.size()>0){
			sectionIndices = getSectionIndices();
			sectionHeaders=getSectionHeaders();
		}
	}
	public void setButtonOnClickListener(OnItemButtonClickListener listener){
		this.mListener=listener;
	}
	// 资源id
	private static final Integer[] ALL_PIC_RESOURCES = new Integer[] {
			R.drawable.section_my,
			R.drawable.section_living,
			R.drawable.section_schedule
	};
	private int[] getSectionIndices() {
		List<Integer> sectionIndices = new ArrayList<Integer>();
//		int state=mList.get(0).getUserId();
		int state=mList.get(0).getState();
		sectionIndices.add(0);
		for (int i = 1; i < mList.size(); i++) {
			int createState = mList.get(i).getState();
			if (createState!=state) {
				state = createState;
				sectionIndices.add(i);
			}
		}
		int[] sections = new int[sectionIndices.size()];
		for (int i = 0; i < sectionIndices.size(); i++) {
			sections[i] = sectionIndices.get(i);
		}
		return sections;
	}

	private Integer[] getSectionHeaders() {
		Integer[] sectionHeaders = new Integer[sectionIndices.length];
		for (int i = 0; i < sectionIndices.length; i++) {
			if (mList.get(sectionIndices[0]).getUserId()==mUserId){
				sectionHeaders[0] =ALL_PIC_RESOURCES[0];
			}else if(mList.get(sectionIndices[i]).getState()==1){
				sectionHeaders[i] =ALL_PIC_RESOURCES[1];
			}else if(mList.get(sectionIndices[i]).getState()==0){
				sectionHeaders[i] =ALL_PIC_RESOURCES[2];
			}

			    }

		return sectionHeaders;
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return sectionHeaders;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex >= sectionIndices.length) {
			sectionIndex = sectionIndices.length - 1;
		} else if (sectionIndex < 0) {
			sectionIndex = 0;
		}
		return sectionIndices[sectionIndex];
	}

	@Override
	public int getSectionForPosition(int position) {
		for (int i = 0; i < sectionIndices.length; i++) {
			if (position < sectionIndices[i]) {
				return i - 1;
			}
		}
		return sectionIndices.length - 1;
	}


	@Override
	public View getHeaderView(int position, View convertView, ViewGroup parent) {
		TitleHolder hvh;
		if (convertView == null) {
			hvh = new TitleHolder();
			convertView = inflater.inflate(R.layout.fragment_living_title_item, null);
			hvh.titleImage = (ImageView) convertView.findViewById(R.id.title_image);
			convertView.setTag(hvh);
		} else {
			hvh = (TitleHolder) convertView.getTag();
		}
		int imgId = 0;
		if (mList.get(position).getState()==3){
			imgId=ALL_PIC_RESOURCES[0];
	    }else if(mList.get(position).getState()==1){
			 imgId=ALL_PIC_RESOURCES[1];
		}else if (mList.get(position).getState()==0){
			 imgId=ALL_PIC_RESOURCES[2];
		}
		hvh.titleImage.setImageResource(imgId);
		return convertView;
	}

	@Override
	public long getHeaderId(int position) {
		return mList.get(position).getState();
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup viewGroup) {

		final ScheduleHolder scheduleHolder;
			if (convertView==null){
				scheduleHolder=new ScheduleHolder();
				convertView = inflater.inflate(
						R.layout.fragment_living_schedule_item, null);
				scheduleHolder.lv_linearLayout= (LinearLayout) convertView.findViewById(R.id.lv_linearLayout);
				scheduleHolder.theme= (TextView) convertView.findViewById(R.id.video_schedule_theme);
				scheduleHolder.userName= (TextView) convertView.findViewById(R.id.video_schedule_user);
				scheduleHolder.year= (TextView) convertView.findViewById(R.id.tv_living_year);
				scheduleHolder.hour= (TextView) convertView.findViewById(R.id.tv_living_time);
				scheduleHolder.rl_living_sign= (RelativeLayout) convertView.findViewById(R.id.rl_living_sign);
				scheduleHolder.rl_mlive_sign= (RelativeLayout) convertView.findViewById(R.id.rl_mlive_sign);
				scheduleHolder.relativeLayout=(RelativeLayout)convertView.findViewById(R.id.rl_sunscribe);
				scheduleHolder.concernedCountLive= (TextView) convertView.findViewById(R.id.tv_concerned_count_live);
				scheduleHolder.concernedCountLiving= (TextView) convertView.findViewById(R.id.tv_concerned_count_live);
				scheduleHolder.concernedCountUlook= (TextView) convertView.findViewById(R.id.tv_concerned_count_ulook);
				scheduleHolder.picBackground= (ImageView) convertView.findViewById(R.id.video_pic);
				scheduleHolder.ulooked= (ImageView) convertView.findViewById(R.id.icon_ulooked);
				scheduleHolder.userHead= (ImageView) convertView.findViewById(R.id.living_schedule_head);
				scheduleHolder.imgLiving=(ImageView)convertView.findViewById(R.id.icon_living);
				scheduleHolder.imgResumLiving=(ImageView)convertView.findViewById(R.id.icon_circle);
				scheduleHolder.attention= (TextView) convertView.findViewById(R.id.tv_attention);
				convertView.setTag(scheduleHolder);
			}else{
				scheduleHolder= (ScheduleHolder) convertView.getTag();
				scheduleHolder.ulooked.setImageDrawable(null);
			}
		if(mList.get(position).getState()==1){
			scheduleHolder.rl_living_sign.setVisibility(View.VISIBLE);
			scheduleHolder.relativeLayout.setVisibility(View.GONE);
			scheduleHolder.year.setVisibility(View.GONE);
			scheduleHolder.hour.setVisibility(View.GONE);
			scheduleHolder.rl_mlive_sign.setVisibility(View.GONE);
			scheduleHolder.attention.setVisibility(View.GONE);
			scheduleHolder.concernedCountLiving.setText(mList.get(position).getSubscibes() + "");


		}else if(mList.get(position).getState()==0){
			scheduleHolder.rl_mlive_sign.setVisibility(View.GONE);
			scheduleHolder.rl_living_sign.setVisibility(View.GONE);
			scheduleHolder.relativeLayout.setVisibility(View.VISIBLE);
			scheduleHolder.attention.setVisibility(View.GONE);
			 if (mList.get(position).getSubscibeId()==0){
				scheduleHolder.concernedCountUlook.setTextColor(Color.WHITE);
				scheduleHolder.ulooked.setImageResource(R.drawable.unsubscribe_middle);
			}else {
				scheduleHolder.concernedCountUlook.setTextColor(Color.parseColor("#B90B0E"));
				scheduleHolder.ulooked.setImageResource(R.drawable.subscribe_middle);
			}
			scheduleHolder.concernedCountUlook.setText(mList.get(position).getSubscibes() + "");

			scheduleHolder.ulooked.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					mListener.onItemClick(scheduleHolder.relativeLayout, position, mList.get(position).getLiveId(), mList.get(position).getSubscibeId());
				}
			});
		}else if(mList.get(position).getState()==3) {
			if (mList.get(position).getGroupid().equals("8")) {
				scheduleHolder.rl_mlive_sign.setVisibility(View.VISIBLE);
				scheduleHolder.relativeLayout.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.GONE);
				scheduleHolder.hour.setVisibility(View.GONE);
				scheduleHolder.rl_living_sign.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.GONE);
				scheduleHolder.hour.setVisibility(View.GONE);
				scheduleHolder.concernedCountLive.setTextColor(Color.WHITE);
				scheduleHolder.concernedCountLive.setText(String.valueOf(mList.get(position).getSubscibes()));
				scheduleHolder.imgResumLiving.setImageResource(R.drawable.state_eight);
				scheduleHolder.attention.setVisibility(View.GONE);
			} else if (mList.get(position).getGroupid().equals("9") ){
				scheduleHolder.rl_mlive_sign.setVisibility(View.VISIBLE);
				scheduleHolder.relativeLayout.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.GONE);
				scheduleHolder.hour.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.VISIBLE);
				scheduleHolder.hour.setVisibility(View.VISIBLE);
				scheduleHolder.concernedCountLive.setTextColor(Color.parseColor("#B90B0E"));
				scheduleHolder.concernedCountLive.setText(String.valueOf(mList.get(position).getSubscibes()));
				scheduleHolder.rl_living_sign.setVisibility(View.GONE);
				scheduleHolder.attention.setVisibility(View.VISIBLE);
				scheduleHolder.imgResumLiving.setImageResource(R.drawable.state_ten);
			} else if (mList.get(position).getGroupid().equals("10") ){
				scheduleHolder.rl_mlive_sign.setVisibility(View.VISIBLE);
				scheduleHolder.relativeLayout.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.GONE);
				scheduleHolder.hour.setVisibility(View.GONE);
				scheduleHolder.year.setVisibility(View.VISIBLE);
				scheduleHolder.hour.setVisibility(View.VISIBLE);
				scheduleHolder.concernedCountLive.setTextColor(Color.WHITE);
				scheduleHolder.concernedCountLive.setText(String.valueOf(mList.get(position).getSubscibes()));
				scheduleHolder.rl_living_sign.setVisibility(View.GONE);
				scheduleHolder.attention.setVisibility(View.GONE);
				scheduleHolder.imgResumLiving.setImageResource(R.drawable.state_nine);
			}
		}
		String photo=mList.get(position).getPhoto();
		String img=mList.get(position).getImg();
		if (photo!=null&&!photo.equals("")){
			Picasso.with(mContext).load(photo).placeholder(R.drawable.default_head)
					.error(R.drawable.defult_head).into(scheduleHolder.userHead);

		}
		if(img!=null&&!img.equals("")){
			Picasso.with(mContext).load(img).placeholder(R.drawable.ic_loading).error(R.drawable.ic_loading).into(scheduleHolder.picBackground);
		}
//		Picasso.with(mContext).load(photo).placeholder(R.drawable.default_head).error(R.drawable.defult_head).into(scheduleHolder.userHead);
//		Picasso.with(mContext).load(img).placeholder(R.drawable.ic_loading).error(R.drawable.ic_loading).into(scheduleHolder.picBackground);
//				imageLoader.displayImage(mList.get(position).getPhoto(), scheduleHolder.userHead, options, animateFirstListener);
//			imageLoader.displayImage(mList.get(position).getImg(), scheduleHolder.picBackground, options, animateFirstListener);
			scheduleHolder.theme.setText(mList.get(position).getTitle());
			String year=DateUtil.getDateToString(mList.get(position).getReserved());
		    String todayWeek=DateUtil.getDateToString(System.currentTimeMillis());
			if (todayWeek.equals(year)){
				year="今天";
			}
			scheduleHolder.year.setText(year);
			scheduleHolder.hour.setText(DateUtil.getDateToHourString(mList.get(position).getReserved()));
			scheduleHolder.userName.setText(mList.get(position).getNickName());

		setVideoScale(scheduleHolder.lv_linearLayout,mScreennWidth,mScreennWidth/16*9);
		return convertView;
	}
	public interface OnItemButtonClickListener {
		void onItemClick(View view, int position, int id, int subid);
	}
			 /**
			  * 预约
			  * @author invinjun
			  *
			  */
			 class ScheduleHolder {
				 TextView theme;
				 TextView userName;
				 TextView concernedCountUlook;
				 TextView concernedCountLiving;
				 TextView concernedCountLive;
				 TextView year;
				 TextView hour;
				 TextView attention;
				 ImageView picBackground;
				 ImageView userHead;
				 ImageView ulooked;
				 ImageView imgResumLiving;
				 ImageView imgLiving;
				 RelativeLayout relativeLayout;
				 RelativeLayout rl_living_sign;
				 RelativeLayout rl_mlive_sign;
				 LinearLayout lv_linearLayout;
			 }




	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0).getLiveId();
	}





	/**
	 * 现场or预约抬头
	 */
	class TitleHolder {
		ImageView titleImage;
	}

			 public void setVideoScale(View view,int width, int height){
				 RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)view.getLayoutParams();
				 lp.width = width;
				 lp.height = height;
				 view.setLayoutParams(lp);
			 }
}

