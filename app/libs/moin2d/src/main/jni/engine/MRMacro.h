//
//  MRMacro.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef MoYing_MRMacro_h
#define MoYing_MRMacro_h


typedef enum{
    TOUCHBEGIN = 0,
    TOUCHMOVE = 1,
    TOUCHEND = 2
}TOUCHMODE;

// C/C++
#include <new>
#include <memory>
#include <cstdio>
#include <cstdlib>
#include <cassert>
#include <cwchar>
#include <cwctype>
#include <cctype>
#include <cmath>
#include <cstdarg>
#include <ctime>
#include <iostream>
#include <string>
#include <vector>
#include <list>
#include <set>
#include <stack>
#include <map>
#include <algorithm>
#include <limits>
#include <functional>
#include <sys/time.h>



//逻辑30帧//
#define LOGIC_FRAME 30
using namespace::std;

#if defined( __APPLE__ ) && !defined(__MACOSX)
#define LOGI(...)
//#define OPENGLES 2
#include <OpenGLES/ES2/gl.h>
#import <OpenGLES/ES2/glext.h>

#elif defined(linux) || defined(__ANDROID) || defined(__linux__)
#include    <jni.h>
#include    <android/log.h>
//魔影2D//
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,"glView:",__VA_ARGS__)
//#define OPENGLES 2
#include <GLES2/gl2.h>
#include <GLES2/gl2ext.h>

#endif





#endif
