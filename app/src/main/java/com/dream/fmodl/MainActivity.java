package com.dream.fmodl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.fmod.FMOD;


public class MainActivity extends Activity {

    public  String path;
    public Button button;
    private ArrayList<HashMap<String, String>> scanAllAudioFiles = new ArrayList<HashMap<String,String>>();
    public ListView listView;
    private MyAdapter myAdapter;

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

        listView = (ListView) findViewById(R.id.list);



        getMusicList();
    }

    public void getMusicList(){
        myAdapter = new MyAdapter();
        listView.setAdapter(myAdapter);
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                scanAllAudioFiles.clear();
                scanAllAudioFiles.addAll( Utils.scanAllAudioFiles(getApplicationContext()));
                System.out.println("----------------------"+scanAllAudioFiles.get(1).get("musicTitle"));
                myAdapter.notifyDataSetChanged();
            }
        }).start();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                System.out.println("------------------------"+scanAllAudioFiles.get(position).get("musicFileUrl"));
                path = scanAllAudioFiles.get(position).get("musicFileUrl");
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
    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return scanAllAudioFiles.size();
        }

        @Override
        public HashMap<String, String> getItem(int position) {
            // TODO Auto-generated method stub
            return scanAllAudioFiles.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item, null);
                holder = new ViewHolder();
                holder.textView = (TextView)convertView.findViewById(R.id.item);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.textView.setText("歌曲名："+scanAllAudioFiles.get(position).get("musicTitle")+"--歌手"+scanAllAudioFiles.get(position).get("music_author"));
            return convertView;
        }


        public  class ViewHolder {
            public TextView textView;
        }
    }
}
