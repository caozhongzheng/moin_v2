
/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
//#include <string.h>
#include    <jni.h>
#include    <android/log.h>
#include    <android/asset_manager.h>
#include    <android/asset_manager_jni.h>
#include    <stdio.h>
#include    <math.h>
#include    <android/bitmap.h>
#include    "engine/DakaLogic/DakaXiu.h"
#include    "engine/android/JniHelper.h"

//魔影2D//
#define  LOG_TAG    "moin2d"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"glView:",__VA_ARGS__)


#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, "ANDROID_LAB", __VA_ARGS__)
/* This is a trivial JNI example where we use a native method
 * to return a new VM String. See the corresponding Java source
 * file located at:
 *
 *   apps/samples/hello-jni/project/src/com/example/hellojni/HelloJni.java
 */


//JNIEXPORT jstring sda()
//{
//
//}


extern "C"
{

    static AAssetManager * gAssetMgr = NULL;
    DakaXiu* _dakaxxiu = 0;
    Moin2d* m2d = 0;

    jint JNI_OnLoad(JavaVM *vm, void *reserved)
    {
        JniHelper::setJavaVM(vm);

        return JNI_VERSION_1_4;
    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_nativeInit(JNIEnv * env,jobject obj,jint width, jint height,jobject assetManager)
    {

         gAssetMgr = AAssetManager_fromJava(env, assetManager);

        _dakaxxiu = new DakaXiu();

        m2d = new Moin2d();


        m2d->init(0,0,width,height,1.0,_dakaxxiu);




    }


    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_nativeDraw(JNIEnv * env, jobject obj)
    {

        if(m2d)
        {

            m2d->update(10);
            m2d->render();
        }
      //  LOGI("jniDraw");
    }


    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_touchBegin(JNIEnv * env, jobject obj,jfloat x0, jfloat y0,jfloat x0Last, jfloat y0Last, jfloat x1, jfloat y1,jfloat x1Last, jfloat y1Last)
    {

        if(m2d)
        {
            m2d->touch(x0,y0,-1,-1,-1,-1,-1,-1,TOUCHBEGIN);
        }

        LOGI("touch Begin....");
    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_touchEnd(JNIEnv * env, jobject obj,jfloat x0, jfloat y0,jfloat x0Last, jfloat y0Last, jfloat x1, jfloat y1,jfloat x1Last, jfloat y1Last)
    {

       if(m2d)
        {
                       m2d->touch(x0,y0,-1,-1,-1,-1,-1,-1,TOUCHBEGIN);
        }
        LOGI("touch End....");
    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_touchMove(JNIEnv * env, jobject obj,jfloat x0, jfloat y0,jfloat x0Last, jfloat y0Last, jfloat x1, jfloat y1,jfloat x1Last, jfloat y1Last)
    {
      if(m2d)
            {
                 m2d->touch(x0,y0,x0Last,y0Last,-1,-1,-1,-1,TOUCHMOVE);
            }
         LOGI("touch Move....");
    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_stringFromJni(JNIEnv* env) {

        return;
//            cocos2d::CCDirector::sharedDirector()->mainLoop();
    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_exit(JNIEnv* env,jclass tclass)
    {

        //_dakaxxiu自动释放//
        if(m2d)
        {
            delete m2d;
            m2d = 0;
        }
        m2d = 0;

    }

    JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_deleteSprite(JNIEnv* env,jclass tclass,jstring type)
    {

    }

     JNIEXPORT void JNICALL Java_com_moinapp_moin2d_Moin2dJni_addSprite(JNIEnv* env,jclass tclass,jstring jtype,jstring jtag,jstring jzipFile,jfloat initScale)
     {

            if(_dakaxxiu == 0)
            {
                 return;
            }

             const char *type = env->GetStringUTFChars(jtype, 0);
             const char *tag = env->GetStringUTFChars(jtag, 0);
             const char *zipFile = env->GetStringUTFChars(jzipFile,0);

            _dakaxxiu->setPart(type,tag,zipFile,initScale);

            env->ReleaseStringUTFChars(jtype, type);
            env->ReleaseStringUTFChars(jtag, tag);
            env->ReleaseStringUTFChars(jzipFile, zipFile);

        return;
     }



 }



