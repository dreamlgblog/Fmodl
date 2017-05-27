#include <jni.h>
#include <stdlib.h>
#include <unistd.h>
#include <android/log.h>
#include "include/fmod.hpp"

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"jason",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"jason",FORMAT,##__VA_ARGS__);

#define MODE_NORMAL 0
#define MODE_LUOLI 1
#define MODE_DASHU 2
#define MODE_JINGSONG 3
#define MODE_GAOGUAI 4
#define MODE_KONGLING 5
#define DEVICE_INDEX    (0)
#define LATENCY_MS      (50) /* Some devices will require higher latency to avoid glitches */
#define DRIFT_MS        (1)
using namespace FMOD;
extern "C"{
JNIEXPORT void JNICALL
    Java_com_dream_fmodl_FmodUtils_record(JNIEnv *env, jclass type, jstring output_) {
        const char *output = env->GetStringUTFChars(output_, 0);
        System *system;
        Channel *channel;
        FMOD_RESULT result = System_Create(&system);

        unsigned int version = 0;
        result = system->getVersion(&version);
        if(version < FMOD_VERSION){
            LOGI("%s","FMOD lib version  doesn't match header version ");
        }

        result = system->init(100,FMOD_INIT_NORMAL,NULL);
        int numDrivers = 0;
        result = system->getRecordNumDrivers(NULL, &numDrivers);
        if (numDrivers == 0)
        {
            LOGI("%s","No recording devices found/plugged in!  Aborting.");
        }

    int nativeRate = 0;
    int nativeChannels = 0;
    result = system->getRecordDriverInfo(DEVICE_INDEX, NULL, 0, NULL, &nativeRate, NULL, &nativeChannels, NULL);


    unsigned int driftThreshold = (nativeRate * DRIFT_MS) / 1000;       /* The point where we start compensating for drift */
    unsigned int desiredLatency = (nativeRate * LATENCY_MS) / 1000;     /* User specified latency */
    unsigned int adjustedLatency = desiredLatency;                      /* User specified latency adjusted for driver update granularity */
    int actualLatency = desiredLatency;

    /*
   Create user sound to record into, then start recording.
*/
    FMOD_CREATESOUNDEXINFO exinfo = {0};
    exinfo.cbsize           = sizeof(FMOD_CREATESOUNDEXINFO);
    exinfo.numchannels      = nativeChannels;
    exinfo.format           = FMOD_SOUND_FORMAT_PCM16;
    exinfo.defaultfrequency = nativeRate;
    exinfo.length           = nativeRate * sizeof(short) * nativeChannels; /* 1 second buffer, size here doesn't change latency */

    FMOD::Sound *sound = NULL;
    result = system->createSound(0, FMOD_LOOP_NORMAL | FMOD_OPENUSER, &exinfo, &sound);


    result = system->recordStart(DEVICE_INDEX, sound, true);


    unsigned int soundLength = 0;
    result = sound->getLength(&soundLength, FMOD_TIMEUNIT_PCM);




        env->ReleaseStringUTFChars(output_, output);
    }
    JNIEXPORT void JNICALL Java_com_dream_fmodl_FmodUtils_fix(JNIEnv *env, jclass type_, jstring path_,jint type) {
        System *system;
        Sound *sound, *sound_to_play;
        Channel *channel;
        DSP *dsp;
        bool playing = true;
        float frequency = 0;
        unsigned int      version;
        void             *extradriverdata = 0;
        int               numsubsounds;

        const char* path_cstr = env->GetStringUTFChars(path_,NULL);
        LOGI("%s",path_cstr);
        try {
            System_Create(&system);//初始化
            system->init(100, FMOD_INIT_NORMAL, NULL);
            //system->createSound(path_cstr,FMOD_INIT_NORMAL,NULL,&sound);
            system->createStream(path_cstr,FMOD_LOOP_NORMAL | FMOD_2D, 0, &sound);
            sound->getNumSubSounds(&numsubsounds);
            if (numsubsounds)
            {
                sound->getSubSound(0, &sound_to_play);
            }
            else
            {
                sound_to_play = sound;
            }
            switch (type){
                case MODE_NORMAL:
                    system->playSound(sound,0,false,&channel);
                    break;
                case MODE_LUOLI:
                    //萝莉
                    //DSP digital signal process
                    //dsp -> 音效 创建fmod中预定义好的音效
                    //FMOD_DSP_TYPE_PITCHSHIFT dsp，提升或者降低音调用的一种音效
                    system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT,&dsp);
                    //设置音调的参数
                    dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,2.5);

                    system->playSound(sound_to_play, 0, false, &channel);
                    //添加到channel
                    channel->addDSP(0,dsp);
                    LOGI("%s","fix luoli");
                    break;

                case MODE_JINGSONG:
                    //惊悚
                    system->createDSPByType(FMOD_DSP_TYPE_TREMOLO,&dsp);
                    dsp->setParameterFloat(FMOD_DSP_TREMOLO_SKEW, 0.5);
                    system->playSound(sound_to_play, 0, false, &channel);
                    channel->addDSP(0,dsp);

                    break;
                case MODE_DASHU:
                    //大叔
                    system->createDSPByType(FMOD_DSP_TYPE_PITCHSHIFT,&dsp);
                    dsp->setParameterFloat(FMOD_DSP_PITCHSHIFT_PITCH,0.8);

                    system->playSound(sound_to_play, 0, false, &channel);
                    //添加到channel
                    channel->addDSP(0,dsp);
                    LOGI("%s","fix dashu");
                    break;
                case MODE_GAOGUAI:
                    //搞怪
                    //提高说话的速度
                    system->playSound(sound_to_play, 0, false, &channel);
                    channel->getFrequency(&frequency);
                    frequency = frequency * 1.6;
                    channel->setFrequency(frequency);
                    LOGI("%s","fix gaoguai");
                    break;
                case MODE_KONGLING:
                    //空灵
                    system->createDSPByType(FMOD_DSP_TYPE_ECHO,&dsp);
                    dsp->setParameterFloat(FMOD_DSP_ECHO_DELAY,300);
                    dsp->setParameterFloat(FMOD_DSP_ECHO_FEEDBACK,20);
                    system->playSound(sound_to_play, 0, false, &channel);
                    channel->addDSP(0,dsp);
                    LOGI("%s","fix kongling");
                    break;

                default:
                    break;
            }

        }catch (...){
            LOGE("%s","发生异常");
            goto end;
        }
        system->update();
        //释放资源
        //单位是微秒
        //每秒钟判断下是否在播放
        while(playing){
            channel->isPlaying(&playing);
            usleep(1000 * 1000);
        }
        goto end;
    end:
        env->ReleaseStringUTFChars(path_,path_cstr);
        sound->release();
        system->close();
        system->release();
    }
}