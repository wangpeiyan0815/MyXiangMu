package com.qibaike.bike.myqqlogin_num2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * qq登录获取数据信息
 */
public class MainActivity extends AppCompatActivity {
    private Tencent mTencent;
    private String appId = "1105851753";
    private Button btn;
    private Button userBtn;
    private IUiListener loginListener; //授权登录监听器
    private IUiListener userInfoListener; //获取用户信息监听器
    private String scope; //获取信息的范围参数
    private UserInfo userInfo; //qq用户信息
    private ImageView imagee;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String url = (String) msg.obj;
            BitmapUtils bitmapUtils = new BitmapUtils(MainActivity.this);
            bitmapUtils.display(imagee, url);
        }
    };
    private Button fenBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        userBtn = (Button) findViewById(R.id.userBtn);
        imagee = (ImageView) findViewById(R.id.imagee);
        fenBtn = (Button) findViewById(R.id.fenBtn);
        mTencent = mTencent.createInstance(appId, this);
        setupViews();
        //initData();
    }

    private void setupViews() {
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                System.out.println("你点击了使用qq登录按钮");
                login();

            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               /* System.out.println("开始获取用户信息");
                userInfo = new UserInfo(MainActivity.this, mTencent.getQQToken());
                userInfo.getUserInfo(userInfoListener);*/
                onClickShare();
            }
        });
        fenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareToQzone();
            }
        });
    }

    private void login() {
        //如果session无效，就开始登录
        if (!mTencent.isSessionValid()) {
            //开始qq授权登录
            mTencent.login(MainActivity.this, scope, loginListener);
            initData();
        }
    }

    private void initData() {
        //要所有权限，不然会再次申请增量权限，这里不要设置成get_user_info,add_t
        scope = "all";
        loginListener = new IUiListener() {
            @Override
            public void onError(UiError arg0) {

            }

            @Override
            public void onComplete(Object value) {

                System.out.println("有数据返回..");
                if (value == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) value;

                    int ret = jo.getInt("ret");

                    System.out.println("json=" + String.valueOf(jo));

                    if (ret == 0) {
                        Toast.makeText(MainActivity.this, "登录成功",
                                Toast.LENGTH_LONG).show();
                        String openID = jo.getString("openid");
                        String accessToken = jo.getString("access_token");
                        String expires = jo.getString("expires_in");
                        mTencent.setOpenId(openID);
                        mTencent.setAccessToken(accessToken, expires);
                    }

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancel() {
            }
        };

        /*userInfoListener = new IUiListener() {

            @Override
            public void onError(UiError arg0) {
            }

            @Override
            public void onComplete(Object arg0) {
                if (arg0 == null) {
                    return;
                }
                try {
                    JSONObject jo = (JSONObject) arg0;
                    int ret = jo.getInt("ret");
                    System.out.println("json=" + String.valueOf(jo));
                    String nickName = jo.getString("nickname");
                    String gender = jo.getString("gender");
                    String figureurl_1 = jo.getString("figureurl_1");
                    Message message = new Message();
                    message.obj = figureurl_1;
                    handler.sendMessage(message);
                    Toast.makeText(MainActivity.this, "你好，" + nickName,
                            Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                }
            }

            @Override
            public void onCancel() {

            }
        };*/
    }

    /**
     * QQ分享
     */
    private void onClickShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "要分享的标题");
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY,  "要分享的摘要");
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  "http://www.qq.com/news/1.html");
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://imgcache.qq.com/qzone/space_item/pre/0/66768.gif");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME,  "测试应用222222");
        //params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,  "其他附加功能");
        mTencent.shareToQQ(MainActivity.this, params,loginListener);
    }

    /**
     * 分享到qq空间
     */
    private void shareToQzone () {
        Bundle params = new Bundle();
        //分享类型
        params.putInt(QQShare.SHARE_TO_QQ_EXT_INT,QQShare.SHARE_TO_QQ_FLAG_QZONE_AUTO_OPEN);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, "你好");//必填
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, "今天天气很好");//选填
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3289431202,3394160365&fm=58");//必填
       // params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, "图片链接ArrayList");
        mTencent.shareToQQ(MainActivity.this, params, loginListener);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_LOGIN) {
            if (resultCode == -1) {
                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
                Tencent.handleResultData(data, loginListener);
                UserInfo info = new UserInfo(this, mTencent.getQQToken());
                info.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object o) {
                        try {
                            JSONObject info = (JSONObject) o;
                            String nickName = info.getString("nickname");//获取用户昵称
                            String iconUrl = info.getString("figureurl_qq_2");//获取用户头像的url
                            Toast.makeText(MainActivity.this, "昵称：" + nickName, Toast.LENGTH_SHORT).show();
                            Message message = new Message();
                            message.obj = iconUrl;
                            handler.sendMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(UiError uiError) {
                    }

                    @Override
                    public void onCancel() {
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mTencent != null) {
            //注销登录
            mTencent.logout(MainActivity.this);
        }
        super.onDestroy();
    }
}
