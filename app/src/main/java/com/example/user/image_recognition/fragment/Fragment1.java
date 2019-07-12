package com.example.user.image_recognition.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.image_recognition.MainActivity;
import com.example.user.image_recognition.R;
import com.example.user.image_recognition.enity.MessageInfo;
import com.example.user.image_recognition.util.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;




public class Fragment1 extends android.support.v4.app.Fragment {

    private static final int CROP_PHOTO = 2;
    private static final int REQUEST_CODE_PICK_IMAGE = 3;
    private static final int REQUEST_CODE_PICK_IMAGE2 = 4;
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 6;  //拍照
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE2 = 7;  //手机相册1

    private File output;
    private File file;
    private Uri imageUri;
    //private String image_path;
    private Bitmap bitmap1;
    private Bitmap bitmap2;


    //录音、后台通信相关
    int animationRes = 0;
    int res = 0;
    int send_cnt = 0;
    AnimationDrawable animationDrawable = null;
    private ImageView animView;

    private static Request.Builder builder = new Request.Builder();
    OkHttpClient client1 = new OkHttpClient();
    OkHttpClient client2 = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) //连接超时时间
            .readTimeout(200, TimeUnit.SECONDS)  //读取超时时间
            .build();
    FormBody.Builder formBuilder = new FormBody.Builder();


    private final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    @Bind(R.id.fragment1_photo1)
    TextView photo1;
    @Bind(R.id.fragment1_photo2)
    TextView phpto2;
    @Bind(R.id.gen_image1)
    ImageView gen1;
    @Bind(R.id.gen_image2)
    ImageView gen2;
    @Bind(R.id.gen_image3)
    ImageView gen3;

    private View Fragment1_Layout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment1_Layout = inflater.inflate(R.layout.fragment1, container, false);
        ButterKnife.bind(this, Fragment1_Layout);
        EventBus.getDefault().register(this); //注册消息总线
        return Fragment1_Layout;

    }
    @OnClick({R.id.fragment1_photo1, R.id.fragment1_photo2})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fragment1_photo1:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    choosePhoto();
                     //bitmap1 = BitmapFactory.decodeFile(image_path);
                     //gen1.setImageBitmap(bitmap1);
                }
                break;
            case R.id.fragment1_photo2:
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_PHONE2);

                } else {
                    choosePhoto2();
                    //bitmap2 = BitmapFactory.decodeFile(image_path);
                    //gen2.setImageBitmap(bitmap2);
                }
                break;
        }


    }


    /**
     * 从相册选取图片
     */
    private void choosePhoto() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }
    private void choosePhoto2() {
        /**
         * 打开选择图片的界面
         */
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE2);

    }

    public void onActivityResult(int req, int res, Intent data) {
        switch (req) {
            case CROP_PHOTO:   // 相机拍照
                if (res == Activity.RESULT_OK) {
                    try {
                        MessageInfo messageInfo = new MessageInfo();
                        messageInfo.setImageUrl(imageUri.getPath());
                        //image_path = imageUri.getPath();
                        //Bitmap bitmap = BitmapFactory.decodeFile(image_path);
                        //gen1.setImageBitmap(bitmap);
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;
            /*
            相册选取图片
             */
            case REQUEST_CODE_PICK_IMAGE:
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageInfo messageInfo = new MessageInfo();

                        String image_path1 = getRealPathFromURI(uri);
                        Bitmap bitmap = BitmapFactory.decodeFile(image_path1);
                        gen1.setImageBitmap(bitmap);
                        messageInfo.setImageUrl(getRealPathFromURI(uri));
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;
            case REQUEST_CODE_PICK_IMAGE2:
                if (res == Activity.RESULT_OK) {
                    try {
                        Uri uri = data.getData();
                        MessageInfo messageInfo = new MessageInfo();

                        String image_path1 = getRealPathFromURI(uri);
                        Bitmap bitmap = BitmapFactory.decodeFile(image_path1);
                        gen2.setImageBitmap(bitmap);
                        messageInfo.setImageUrl(getRealPathFromURI(uri));
                        EventBus.getDefault().post(messageInfo);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(Constants.TAG, e.getMessage());
                    }
                } else {
                    Log.d(Constants.TAG, "失败");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {



        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                choosePhoto();
            } else {
                Toast.makeText(getContext(), "请同意系统权限后继续", Toast.LENGTH_SHORT).show();
                //toastShow("请同意系统权限后继续");
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private String intToIp(int i) {
        return (i & 0xFF) + "." +
                ((i >> 8) & 0xFF) + "." +
                ((i >> 16) & 0xFF) + "." +
                (i >> 24 & 0xFF);
    }
    /*
          发送图片给服务器，视觉问答项目的路由为register（图片）和question，繁体字模块的路由为recOCR
       */
    private void sendImgToServer_1(String imguri) {
        String urlAddress_1 = "http://192.168.157.159:8689/picGAN";
        String image_path = imguri;
        file = new File(image_path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "img" + "_" + System.currentTimeMillis() + ".jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, file));
        Request request = new Request.Builder().url(urlAddress_1).post(builder.build()).build();
        Call call = client1.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                    Toast.makeText(MainActivity.this, "读取成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendImgToServer_2(String imguri) {
        String urlAddress_2 = "http://192.168.157.159:8689/zcyGAN";
        String image_path = imguri;
        file = new File(image_path);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image", "img" + "_" + System.currentTimeMillis() + ".jpg",
                        RequestBody.create(MEDIA_TYPE_PNG, file));

        Request request = new Request.Builder().url(urlAddress_2).post(builder.build()).build();
        Call call = client2.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(), "服务器错误", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                final String res = response.body().string();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (res.equals("0")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(), "无效", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    new Handler().postDelayed(new Runnable() {
                                        public void run() {
                                            MessageInfo message = new MessageInfo();

                                            byte[] decodedString = Base64.decode(res, Base64.DEFAULT);
                                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                            gen3.setImageBitmap(decodedByte);

                                            Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), decodedByte, null,null));
                                            message.setImageUrl(getRealPathFromURI(uri));
//                                            message.setContent(res);

                                            message.setType(Constants.CHAT_ITEM_TYPE_LEFT);
                                            message.setHeader("https://fimg5.duitang.com/uploads/item/201407/12/20140712124217_mk5d2.thumb.224_0.png");
                                           // messageInfos.add(message);
                                            //chatAdapter.add(message);
                                            //chatList.scrollToPosition(chatAdapter.getCount() - 1);
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

    @Subscribe(threadMode = ThreadMode.MAIN)  //在ui线程执行
    public void MessageEventBus(final MessageInfo messageInfo) {  //final变量不能被修改
        messageInfo.setHeader("https://img.52z.com/upload/news/image/20180213/20180213062641_35687.jpg");
        messageInfo.setType(Constants.CHAT_ITEM_TYPE_RIGHT);
//        messageInfo.setSendState(Constants.CHAT_ITEM_SENDING);
       // messageInfos.add(messageInfo);
       // chatAdapter.add(messageInfo);

        //chatList.scrollToPosition(chatAdapter.getCount() - 1);
        new Handler().postDelayed(new Runnable() {
            public void run() {
//              messageInfo.setSendState(Constants.CHAT_ITEM_SEND_SUCCESS);
                //chatAdapter.notifyDataSetChanged();
            }
        }, 2000);
        // 判断发送给服务器的是否为问题字符串(仅在视觉问答中使用），否则为图片
        if(messageInfo.getContent() instanceof String && messageInfo.getContent().length()<50){
           // sendQuesToServer(messageInfo.getContent());
        }
        else{
//            send_cnt = 0;
            //

            send_cnt = send_cnt + 1;
           if(send_cnt % 2 == 1)
               sendImgToServer_1(messageInfo.getImageUrl());
            else
                sendImgToServer_2(messageInfo.getImageUrl());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        EventBus.getDefault().removeStickyEvent(this);
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            EventBus.getDefault().unregister(this);
        }
        else {
            EventBus.getDefault().register(this); //注册消息总线
        }
    }

}
