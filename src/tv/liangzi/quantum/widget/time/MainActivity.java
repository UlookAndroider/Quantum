package tv.liangzi.quantum.widget.time;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import tv.liangzi.quantum.R;

public class MainActivity extends Activity {
	WheelMain wheelMain;
	EditText txttime;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        txttime = (EditText)findViewById(R.id.txttime);
	  	Calendar calendar = Calendar.getInstance();
	  	txttime.setText(calendar.get(Calendar.YEAR) + "-" +
	  				    (calendar.get(Calendar.MONTH) + 1 )+ "-" +
	  				    calendar.get(Calendar.DAY_OF_MONTH) + "");
        Button btnselecttime = (Button)findViewById(R.id.button1);
		btnselecttime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LayoutInflater inflater=LayoutInflater.from(MainActivity.this);
				final View timepickerview=inflater.inflate(R.layout.timepicker, null);
				ScreenInfo screenInfo = new ScreenInfo(MainActivity.this);
				wheelMain = new WheelMain(timepickerview);
				wheelMain.screenheight = screenInfo.getHeight();
				String time = txttime.getText().toString();
				Calendar calendar = Calendar.getInstance();
				if(JudgeDate.isDate(time, "yyyy-MM-dd")){
					try {
						calendar.setTime(dateFormat.parse(time));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				wheelMain.initDateTimePicker(year,month,day);
				new AlertDialog.Builder(MainActivity.this)
				.setTitle("ѡ��ʱ��")
				.setView(timepickerview)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						txttime.setText(wheelMain.getTime());
					}
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();
			}
		});
    }
}