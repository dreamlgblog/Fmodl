package com.dream.fmodl;

import java.io.File;
import java.io.IOException;


import android.app.Activity;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.fmod.FMOD;


public class MainActivity extends Activity {

    public  String path;
    public Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FMOD.init(this);
        path =new File(Environment.getExternalStorageDirectory(),"ligang.mp3").getAbsolutePath();
        button = (Button) findViewById(R.id.rec_but);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTrue){
                    button.setText("点击结束录音");
                    startRecord();
                }else{
                    button.setText("点击开始录音");
                    stopRecord();
                }
            }
        });
    }

    public void mFix(View  btn){
                switch (btn.getId()) {
                    case R.id.btn_normal:
                        playSound(FmodUtils.MODE_NORMAL);
                        break;

                    case R.id.btn_luoli:
                        playSound(FmodUtils.MODE_LUOLI);
                        break;

                    case R.id.btn_dashu:
                        playSound(FmodUtils.MODE_DASHU);
                        break;

                    case R.id.btn_jingsong:
                        playSound(FmodUtils.MODE_JINGSONG);
                        break;

                    case R.id.btn_gaoguai:
                        playSound(FmodUtils.MODE_GAOGUAI);
                        break;

                    case R.id.btn_kongling:
                        playSound(FmodUtils.MODE_KONGLING);
                        break;

                    default:
                        break;
                }

    }

    public void playSound(final int mode){
        if(!new File(path).exists()){
            Toast.makeText(this, "请先录音", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                FmodUtils.fix(path,mode);
            }
        }).start();

    }
    //停止录制，资源释放
    private void stopRecord(){
        Toast.makeText(this, "停止录制", Toast.LENGTH_SHORT).show();
        if(mr != null){
            mr.stop();
            mr.release();
            mr = null;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        FMOD.close();
    }

    private MediaRecorder mr = null;
    private boolean isTrue = true;

    //开始录制
    private void startRecord(){
        Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
        isTrue = false;
        if(mr == null){
            mr = new MediaRecorder();
            mr.setAudioSource(MediaRecorder.AudioSource.MIC);  //音频输入源
            mr.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);   //设置输出格式
            mr.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);   //设置编码格式
            mr.setOutputFile(path);
            try {
                mr.prepare();
                mr.start();  //开始录制
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
