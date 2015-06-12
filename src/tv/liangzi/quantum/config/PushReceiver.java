package tv.liangzi.quantum.config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.google.gson.Gson;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import tv.liangzi.quantum.bean.HTTPKey;
import tv.liangzi.quantum.bean.PeopleDetails;
import tv.liangzi.quantum.utils.MD5Util;
import tv.liangzi.quantum.utils.OkHttpUtil;

public class PushReceiver extends BroadcastReceiver {

    /**
     * 应用未启动, 个推 service已经被唤醒,保存在该时间段内离线消息(此时 GetuiSdkDemoActivity.tLogView == null)
     */
    public static StringBuilder clientid = new StringBuilder();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.e("receiver", "onReceive() action=" + bundle.getInt("action"));

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {
            case PushConsts.GET_MSG_DATA:
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);
                System.out.println("第三方回执接口调用" + (result ? "成功" : "失败"));

                if (payload != null) {
                    String data = new String(payload);
                    Log.d("GetuiSdkDemo", "receiver payload : " + data);
//                    payloadData.append(data);
//                    payloadData.append("\n");
                }
                break;

            case PushConsts.GET_CLIENTID:
                final HTTPKey httpKey=new HTTPKey();
                // 获取ClientID(CID)
//                FormEncodingBuilder formBody = new FormEncodingBuilder();
//                formBody.add(httpKey.USER_ACCOUNT, account);
//                post(MyAapplication.IP + "session", formBody);
                clientid.append(bundle.getString("clientid"));
                // 第三方应用需要将CID上传到第三方服务器，并且将当前用户帐号和CID进行关联，以便日后通过用户帐号查找CID进行消息推送
                String cid = bundle.getString("clientid");
                Log.e("clientid","+++++++++clientid="+cid);
                break;

            case PushConsts.THIRDPART_FEEDBACK:
                /*
                 * String appid = bundle.getString("appid"); String taskid =
                 * bundle.getString("taskid"); String actionid = bundle.getString("actionid");
                 * String result = bundle.getString("result"); long timestamp =
                 * bundle.getLong("timestamp");
                 * 
                 * Log.d("GetuiSdkDemo", "appid = " + appid); Log.d("GetuiSdkDemo", "taskid = " +
                 * taskid); Log.d("GetuiSdkDemo", "actionid = " + actionid); Log.d("GetuiSdkDemo",
                 * "result = " + result); Log.d("GetuiSdkDemo", "timestamp = " + timestamp);
                 */
                break;

            default:
                break;
        }
    }
    void post(String url,FormEncodingBuilder formBody ) throws IOException {
          final Gson gson = new Gson();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody.build())
                .build();

        OkHttpUtil.enqueue(request, new Callback() {

            @Override
            public void onResponse(Response response) throws IOException {

                PeopleDetails user = gson.fromJson(response.body().charStream(), PeopleDetails.class);
                if (user.getResponseCode().equals("201")) {
                    String EmAccount = MD5Util.stringToMD5(user.getUserId() + "");
//                    mHandler.sendEmptyMessage(MESSAGE_SUCCEED);
//                    SharedPreferences.Editor editor = UserSP.edit();//获取编辑器
//                    editor.putString("nickName", user.getNickName());
//                    editor.putString("sinaNickName", user.getSinaNickName());
//                    editor.putString("wechatNickName", user.getWechatNickName());
//                    editor.putString("especialUploadToken", user.getEspecialUploadToken());
//                    editor.putString("photo", user.getPhoto());
//                    editor.putString("commonUploadToken", user.getCommonUploadToken());
//                    editor.putString("accessToken", user.getAccessToken());
//                    editor.putString("userId", user.getUserId() + "");
//                    editor.putString("addr", user.getAddr());
//                    editor.putString("focusNum", user.getFocusNum() + "");
//                    editor.putString("fansNum", user.getFansNum() + "");
//                    editor.putString("sign", user.getSign());
//                    editor.putString("lng", user.getLng() + "");
//                    editor.putString("lat", user.getLat() + "");
//                    editor.commit();//提交修改
                } else {
//                    mHandler.sendEmptyMessage(MESSAGE_FAILED);
                }


            }

            @Override
            public void onFailure(Request arg0, IOException arg1) {
                Message msg = new Message();
                msg.what = 3;
                msg.obj = arg1.toString();
//                mHandler.sendMessage(msg);
            }
        });
    }
}
