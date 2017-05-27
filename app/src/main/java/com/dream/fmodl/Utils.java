package com.dream.fmodl;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dream on 2017/5/27.
 */

public class Utils {
    public static ArrayList<HashMap<String, String>> scanAllAudioFiles(Context context){
//生成动态集合，用于存储数据
        ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();

//查询媒体数据库
        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
//遍历媒体数据库
        if(cursor.moveToFirst()){

            while (!cursor.isAfterLast()) {

                //歌曲编号
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID));
                //歌曲名
                String tilte = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
                //歌曲的专辑名：MediaStore.Audio.Media.ALBUM
                String album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM));
                //歌曲的歌手名： MediaStore.Audio.Media.ARTIST
                String author = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
                //歌曲文件的路径 ：MediaStore.Audio.Media.DATA
                String url = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
                //歌曲的总播放时长 ：MediaStore.Audio.Media.DURATION
                int duration = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
                //歌曲文件的大小 ：MediaStore.Audio.Media.SIZE
                Long size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE));

                if(size>1024*800){//如果文件大小大于800K，将该文件信息存入到map集合中
                    HashMap<String, String> map = new HashMap<String, String>();
                   // map.put("musicId", id);
                    map.put("musicTitle", tilte);
                    map.put("musicFileUrl", url);
                  /*  map.put("music_file_name", tilte);
                    map.put("music_author",author);
                    map.put("music_url",url);
                    map.put("music_duration",duration);*/
                    mylist.add(map);
                }
                cursor.moveToNext();
            }
        }
        //返回存储数据的集合
        return mylist;
    }
}
