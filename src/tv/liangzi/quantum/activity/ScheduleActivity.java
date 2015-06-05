package tv.liangzi.quantum.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import tv.liangzi.quantum.R;
import tv.liangzi.quantum.base.BaseActivity;

public class ScheduleActivity extends BaseActivity implements View.OnClickListener{

    private ImageView upCouver;
    private TextView schedule;
    private TextView now;
    private EditText nameTopic;
    @Override
    public void setContentView() {
       setContentView(R.layout.view_schedule_video);
    }

    @Override
    public void initViews() {

       nameTopic= (EditText) findViewById(R.id.et_name_topic);
       upCouver= (ImageView) findViewById(R.id.im_up_couver);
       schedule=(TextView) findViewById(R.id.tv_schedule);
       now=(TextView)findViewById(R.id.tv_now);

    }

    @Override
    public void initListeners() {
        schedule.setOnClickListener(this);

    }

    @Override
    public void initData() {


    }

    @Override
    public void onClick(View v) {
       switch (v.getId()){
           case R.id.tv_schedule:
               break;
       }
    }
}
