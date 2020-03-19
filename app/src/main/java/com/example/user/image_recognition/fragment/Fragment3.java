package com.example.user.image_recognition.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.image_recognition.MainActivity;
import com.example.user.image_recognition.R;
import com.example.user.image_recognition.SettingActivity;
import com.example.user.image_recognition.Views.CircleImageView;
import com.example.user.image_recognition.YYActivity;

public class Fragment3 extends android.support.v4.app.Fragment {

    private View Fragment3_Layout;
    private Button btn;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Fragment3_Layout = inflater.inflate(R.layout.fragment3, container, false);
        findViews();
        return Fragment3_Layout;
    }
    private void findViews(){

        btn= (Button) Fragment3_Layout.findViewById(R.id.button);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getContext(), YYActivity.class);
//                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
        });

    }
}
