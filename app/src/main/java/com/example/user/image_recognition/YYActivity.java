package com.example.user.image_recognition;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.image_recognition.adapter.ChatAdapter;
import com.example.user.image_recognition.adapter.CommonFragmentPagerAdapter;
import com.example.user.image_recognition.enity.FullImageInfo;
import com.example.user.image_recognition.enity.MessageInfo;
import com.example.user.image_recognition.fragment.ChatEmotionFragment;
import com.example.user.image_recognition.fragment.ChatFunctionFragment;
import com.example.user.image_recognition.util.Constants;
import com.example.user.image_recognition.util.GlobalOnItemClickManagerUtils;
import com.example.user.image_recognition.util.MediaManager;
import com.example.user.image_recognition.widget.EmotionInputDetector;
import com.example.user.image_recognition.widget.NoScrollViewPager;
import com.example.user.image_recognition.widget.StateButton;
import com.jude.easyrecyclerview.EasyRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class YYActivity extends AppCompatActivity {  //使用兼容版的actionbar
    /*
        自动生成findViewById无需再实例化控件
     */
    @Bind(R.id.chat_list)
    EasyRecyclerView chatList;
    @Bind(R.id.emotion_voice)
    ImageView emotionVoice;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.voice_text)
    TextView voiceText;
    @Bind(R.id.emotion_button)
    ImageView emotionButton;
    @Bind(R.id.emotion_add)
    ImageView emotionAdd;
    @Bind(R.id.emotion_send)
    StateButton emotionSend;
    @Bind(R.id.viewpager)
    NoScrollViewPager viewpager;
    @Bind(R.id.emotion_layout)//左右滑动时，切换不同的view
    RelativeLayout emotionLayout;

    private EmotionInputDetector mDetector;
    private ArrayList<Fragment> fragments;
    private ChatEmotionFragment chatEmotionFragment;
    private ChatFunctionFragment chatFunctionFragment;
    private CommonFragmentPagerAdapter adapter;

    private ChatAdapter chatAdapter;
    private LinearLayoutManager layoutManager;
    private List<MessageInfo> messageInfos;

    //录音、后台通信相关
    int animationRes = 0;
    int res = 0;
    int send_cnt = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    private static Request.Builder builder = new Request.Builder();
    OkHttpClient client1 = new OkHttpClient();
    OkHttpClient client2 = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS) //连接超时时间
            .readTimeout(200,TimeUnit.SECONDS)  //读取超时时间
            .build();
    FormBody.Builder formBuilder = new FormBody.Builder();
    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private File file;
    Bitmap bitmap;

    private ImageView imageView;
    private Dialog dialog;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //在窗口显示前设置窗口的属性如风格、位置颜色等
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //解决安卓版本兼容问题，例如打不开相机
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yy);
        getSupportActionBar().hide();
        ButterKnife.bind(this);
        EventBus.getDefault().register(this); //注册消息总线
        initWidget();   //加载界面Fragment，增加屏幕滚动和点击响应事件
    }

    private void initWidget() {
        fragments = new ArrayList<>();
        chatEmotionFragment = new ChatEmotionFragment();
        fragments.add(chatEmotionFragment);
        chatFunctionFragment = new ChatFunctionFragment();
        fragments.add(chatFunctionFragment);
        adapter = new CommonFragmentPagerAdapter(getSupportFragmentManager(), fragments);//在Activity中使用Fragment的管理器，对所有Fragment进行管理。
        viewpager.setAdapter(adapter);
        viewpager.setCurrentItem(0);

        mDetector = EmotionInputDetector.with(this)
                .setEmotionView(emotionLayout)
                .setViewPager(viewpager)
                .bindToContent(chatList)
                .bindToEditText(editText)
                .bindToEmotionButton(emotionButton)
                .bindToAddButton(emotionAdd)
                .bindToSendButton(emotionSend)
                .bindToVoiceButton(emotionVoice)
                .bindToVoiceText(voiceText)
                .build();
        GlobalOnItemClickManagerUtils globalOnItemClickListener = GlobalOnItemClickManagerUtils.getInstance(this);
        globalOnItemClickListener.attachToEditText(editText);

        chatAdapter = new ChatAdapter(this);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        chatList.setLayoutManager(layoutManager);
        chatList.setAdapter(chatAdapter);
        chatList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE://判断滚动是否停止.
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        chatAdapter.notifyDataSetChanged();
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        chatAdapter.handler.removeCallbacksAndMessages(null);
                        mDetector.hideEmotionLayout(false);
                        mDetector.hideSoftInput();
                        break;
                    default:
                        break;
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        chatAdapter.addItemClickListener(itemClickListener);
        LoadData();
    }

    /**
     * item点击事件
     */
    private ChatAdapter.onItemClickListener itemClickListener;
    {
        itemClickListener = new ChatAdapter.onItemClickListener() {
            @Override
            public void onHeaderClick(int position) {//点击头像
                Toast.makeText(YYActivity.this, "onHeaderClick", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onImageClick(View view, int position) {  //界面点击图片显示  放大图片
                int location[] = new int[2];
                view.getLocationOnScreen(location);
                FullImageInfo fullImageInfo = new FullImageInfo();
                fullImageInfo.setLocationX(location[0]);
                fullImageInfo.setLocationY(location[1]);
                fullImageInfo.setWidth(view.getWidth());
                fullImageInfo.setHeight(view.getHeight());
                fullImageInfo.setImageUrl(messageInfos.get(position).getImageUrl());
                EventBus.getDefault().postSticky(fullImageInfo);
                startActivity(new Intent(YYActivity.this, FullImageActivity.class));
                overridePendingTransition(0, 0);
            }
            //点击语音消息
            @Override
            public void onVoiceClick(final ImageView imageView, final int position) {
                if (animView != null) {
                    animView.setImageResource(res);
                    animView = null;
                }
                switch (messageInfos.get(position).getType()) {
                    case 1:
                        animationRes = R.drawable.voice_left;
                        res = R.mipmap.icon_voice_left3;
                        break;
                    case 2:
                        animationRes = R.drawable.voice_right;
                        res = R.mipmap.icon_voice_right3;
                        break;
                }
                animView = imageView;
                animView.setImageResource(animationRes);
                animationDrawable = (AnimationDrawable) imageView.getDrawable();
                animationDrawable.start();
                MediaManager.playSound(messageInfos.get(position).getFilepath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        animView.setImageResource(res);
                    }
                });
            }
        };
    }

    /**
     * 构造、初始化聊天数据
     */
    private void LoadData() {
        messageInfos = new ArrayList<>();
        MessageInfo messageInfo = new MessageInfo();
        messageInfo.setContent("欢迎使用视觉问答系统");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_LEFT);
        messageInfo.setHeader("https://img5.duitang.com/uploads/item/201407/12/20140712124217_mk5d2.thumb.224_0.png");
        messageInfos.add(messageInfo);
        chatAdapter.addAll(messageInfos);
    }

    /*
    与后台交互的通信模块，采用OKHTTP框架，以下为三种交互形式
     */
    private String intToIp(int i) {
        return (i & 0xFF ) + "." +
                ((i >> 8 ) & 0xFF) + "." +
                ((i >> 16 ) & 0xFF) + "." +
                ( i >> 24 & 0xFF) ;
    }

    /*
       发送字符串给服务器
     */
    private void sendQuesToServer_5(String ques) {
        //获取wifi服务 和 连接内网的用户IP地址
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = intToIp(ipAddress);
        //接口地址 102839
        String urlAddress = "http://192.168.157.159:8689/question";
        send_cnt = send_cnt + 1;
        String userip = ip.split("\\.")[0] + ip.split("\\.")[1] +ip.split("\\.")[2]+ip.split("\\.")[3];
        Toast.makeText(YYActivity.this, userip, Toast.LENGTH_SHORT).show();

        formBuilder.add("question"+userip+String.valueOf(send_cnt), ques);
//        formBuilder.add("question", ques);
        Request request = new Request.Builder().url(urlAddress).post(formBuilder.build()).build();
        Call call = client1.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(YYActivity.this, "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("0") || res.length() > 20) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(YYActivity.this, "无效", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            MessageInfo message = new MessageInfo();
                                            message.setContent(res);
                                            message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                            message.setHeader("https://img5.duitang.com/uploads/item/201407/12/20140712124217_mk5d2.thumb.224_0.png");
                                            messageInfos.add(message);
                                            chatAdapter.add(message);
                                            chatList.scrollToPosition(chatAdapter.getCount() - 1);
                                        }
                                    }, 3000);
                                }
                            });
                        }
                    }
                });
            }
        });
    }


    /*
        发送图片给服务器，视觉问答项目的路由为register（图片）和question，繁体字模块的路由为recOCR
     */
    private void sendImgToServer_2(String imguri) {
        Log.d("res", imguri);
        String urlAddress = "http://192.168.157.159:8689/register";
        String image_path = imguri;
        file = new File(image_path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "img" + "_" + System.currentTimeMillis() + ".jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, file));
        Request request = new Request.Builder().url(urlAddress).post(builder.build()).build();
        Call call = client1.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)  //在ui线程执行
    public void MessageEventBus(final MessageInfo messageInfo) {  //final变量不能被修改
        messageInfo.setHeader("https://img.52z.com/upload/news/image/20180213/20180213062641_35687.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
        messageInfos.add(messageInfo);
        chatAdapter.add(messageInfo);
//        send_cnt = send_cnt + 1;
        chatList.scrollToPosition(chatAdapter.getCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
//              messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                chatAdapter.notifyDataSetChanged();
            }
        }, 2000);

        // 判断发送给服务器的是否为问题字符串(仅在视觉问答中使用），否则为图片
        if(messageInfo.getContent() instanceof String && messageInfo.getContent().length()<50){
            sendQuesToServer_5(messageInfo.getContent());
        }
        else{
            send_cnt = 0;   //重置一个图片的send_cnt
            sendImgToServer_2(messageInfo.getImageUrl());
        }
    }

    @Override
    public void onBackPressed() {
        if (!mDetector.interceptBackPress()) {
            super.onBackPressed();
        }
    }//Android程序当你按下手机的back键时系统会默认调用程序栈中最上层Activity的Destroy()方法来销毁当前Activity

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }
}

