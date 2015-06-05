package tv.liangzi.quantum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import tv.liangzi.quantum.R;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>{

	private List<String> mList;
	
	public RecycleAdapter(Context context,List<String> list) {
		// 绑定数据集合
		mList=list;
	}
	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return 15;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int i) {
		// 绑定数据到viewholder上
//		viewHolder.mImageView.setImageResource(R.drawable.headtest);
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		// TODO Auto-generated method stub
		View view = View.inflate(viewGroup.getContext(), R.layout.activity_recycle_item, null); 
                
		 // 创建一个ViewHolder 

		 ViewHolder holder = new ViewHolder(view); 

		 return holder; 

	}
	public static class ViewHolder extends RecyclerView.ViewHolder{ 

		 public ImageView mImageView;

		 public ViewHolder(View itemView) { 
			 
		 super(itemView); 

		 mImageView= (ImageView) itemView.findViewById(R.id.im_recyle_head);

		 } 

		 }

}

