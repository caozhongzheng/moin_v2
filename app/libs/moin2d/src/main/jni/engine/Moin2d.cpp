//
//  Moying2D.cpp
//  MoYing
//
//  Created by wufan on 15-6-19.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Moin2d.h"
#include "Moin2dApp.h"
#include "ShareData.h"
#include "zlib.h"
namespace Moin_2d
{
    
 
    //当前帧数//
    Moin2d::Moin2d():currentApp(0),stage(0),nowFrame(0),realTime(0),logicTime(0),gameScale(1.0)
    {

    }
    
    Moin2d::~Moin2d()
    {
        if(stage)
        {
            delete stage;
            stage = 0;
        }
        if(currentApp)
        {

            currentApp->release();
            if(currentApp->nRef <= 0)
            {
                delete  currentApp;
            }
            currentApp= 0;
        }
        ShareData::destroy();
        
        
    }
    
    void Moin2d::init(float x,float y,float width,float height,float scale,Moin2dApp* app)
    {
        
        gameScale = scale;
        currentApp = app;
        glSize.x = width*scale;
        glSize.y = height*scale;
        if(stage == 0)
        {
            stage = new Stage(x,y,glSize.x,glSize.y);
        }
        
        if(currentApp)
        {
            currentApp->init(this);
        }
    }
    
    void Moin2d::render()
    {
        
//        static float grey;
//        grey += 0.01f;
//        if (grey > 1.0f) {
//            grey = 0.0f;
//        }
//        glClearColor(grey, grey, grey, 1.0f);
//        glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
//        
//        glUseProgram(gProgram);
//        
//        glVertexAttribPointer(gvPositionHandle, 2, GL_FLOAT, GL_FALSE, 0, gTriangleVertices);
//        glEnableVertexAttribArray(gvPositionHandle);
//
//        glDrawArrays(GL_TRIANGLES, 0, 3);
//
//        
//
//        return;


        glDisable(GL_DEPTH_TEST);
        glDepthMask(false);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
        glClear( GL_DEPTH_BUFFER_BIT | GL_COLOR_BUFFER_BIT);
        
        //2d无需裁剪//
        glDisable(GL_CULL_FACE);
        glActiveTexture(GL_TEXTURE0);

        if(stage)
        {
            stage->render();
         }

    }
    
    
    void Moin2d::update(float time)
    {

       // printf("time:%f\n",time);
        if (logicTime == 0) {
            realTime = 1000.f/LOGIC_FRAME;
        } else {
           realTime += time;
        }
        while ( logicTime< realTime) {
            if(stage)
            {
                stage->update(nowFrame);
            }
            logicTime += 1000.f/LOGIC_FRAME;
        }
    }
    
    void Moin2d::touch(float x0,float y0,float xlast, float ylast,
                       float x1,float y1,float x1last, float y1last,TOUCHMODE touchMode)
    {
        
        //OPENGL 转换//
        x0*=gameScale;
        y0*=gameScale;
        //if(y0>0)
        {
            y0 = glSize.y -y0;
        }
        xlast*= gameScale;
        ylast*= gameScale;
        //if(ylast>0)
        {
            ylast = glSize.y-ylast;
        }
        x1*= gameScale;
        y1*= gameScale;
        //if(y1>0)
        {
            y1 = glSize.y -y1;
        }
        x1last*=gameScale;
        y1last*=gameScale;
        //if(y1last)
        {
            y1last = glSize.y - y1last;
        }
        
        //printf("x0:%f,y0:%f\n",x0,y0);
        
        if(touchMode == TOUCHBEGIN)
        {
             if(stage)
             {
        
                 
                 stage->touchBegin(Vector2(x0,y0),Vector2(x1,y1));
             }
        }
        else if(touchMode == TOUCHMOVE)
        {
            if(stage)
            {
                stage->touchMove(Vector2(x0,y0),Vector2(xlast,ylast),Vector2(x1,y1),Vector2(x1last,y1last));
            }
        }
        else if(touchMode == TOUCHEND)
        {
            if(stage)
            {
                stage->touchEnd(Vector2(x0,y0),Vector2(x1,y1));
            }
        }
    }
    
    void Moin2d::resume()
    {
        printf("resume\n");
    }
}





