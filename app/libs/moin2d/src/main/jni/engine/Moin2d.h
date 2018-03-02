//
//  Moying2D.h
//  MoYing
//
//  Created by wufan on 15-6-19.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__Moying2D__
#define __MoYing__Moying2D__


#include "MRMacro.h"
#include "Node.h"
#include "Sprite.h"
#include "math.h"
#include "Stage.h"

namespace Moin_2d
{
    class Moin2dApp;
    class Moin2d
    {
    public:
        Moin2d();
        ~Moin2d();
    public:
        void init(float x,float y,float width,float height,float scale,Moin2dApp* app = 0);
        
        void render();
        void update(float time);
        void touch(float x0,float y0,float xlast, float ylast,
                   float x1,float y1,float x1last, float y1last,TOUCHMODE touchMode);

        void resume();
        
    public:
        
        Vector2 glSize;
        float gameScale; 
        Stage* stage;
        Moin2dApp* currentApp;
        int nowFrame;

        float realTime;
        float logicTime;
        
    };
}



#endif /* defined(__MoYing__Moying2D__) */
