package com.wedo.bandwriter.uitl;

import java.util.HashMap;
import java.util.Map;

import com.wedo.bandwriter.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class BandScannerUtil {
	public static SoundPool sp ;
	public static Map<Integer, Integer> soundMap;
	public static Context context;
	
	//初始化声音池
	public static void initSoundPool(Context context){
		BandScannerUtil.context = context;
		sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
		soundMap = new HashMap<Integer, Integer>();
		soundMap.put(1, sp.load(context, R.raw.msg, 1));
		soundMap.put(2, sp.load(context, R.raw.awp,1));
	}
	
	//播放声音池声音
	public static  void play(int sound, int number){
		AudioManager am = (AudioManager)BandScannerUtil.context.getSystemService(BandScannerUtil.context.AUDIO_SERVICE);
	   //返回当前AlarmManager最大音量
	    float audioMaxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	        
	        //返回当前AudioManager对象的音量值
	        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
	        float volumnRatio = audioCurrentVolume/audioMaxVolume;
	        sp.play(
	        		soundMap.get(sound), //播放的音乐Id 
	        		audioCurrentVolume, //左声道音量
	        		audioCurrentVolume, //右声道音量
	                1, //优先级，0为最低
	                number, //循环次数，0无不循环，-1无永远循环
	                1);//回放速度，值在0.5-2.0之间，1为正常速度
	    }
}
