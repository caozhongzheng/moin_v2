//
//  Stage.h
//  DakaXiu
//
//  Created by wufan on 15/7/18.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Stage__
#define __DakaXiu__Stage__

#include <stdio.h>
#include "Node.h"
#include "ViewPort.h"
//一个舞台对应一个摄像机组，目前只支持一个stage//
namespace Moin_2d
{
    class Stage;
    
    class StageDelegate
    {
        public:
            virtual bool touchBegin(const Vector2& p0,const Vector2& p1) = 0;
            virtual bool touchMove(const Vector2& p0,const Vector2& p0Last,const Vector2& p1,const Vector2& p1last) = 0;
            virtual bool touchEnd(const Vector2& p0,const Vector2& p1) = 0;
    };
    
    
    
    class Stage :public Node
    {
        public:
            Stage(float x,float y,float width,float height);
            ~Stage();
        
            void setStage(float x,float y,float width,float height);
            void render();
        
            void setStageDelegate(StageDelegate* delegate)
            {
                _delegate = delegate;
            }
        
            virtual bool touchBegin(const Vector2& p0,const Vector2& p1);

            virtual bool touchMove(const Vector2& p0,const Vector2& p0Last, const Vector2& p1,const Vector2& p1Last);
        
            virtual bool touchEnd(const Vector2& p0,const Vector2& p1);
        
        private:
            StageDelegate* _delegate;

        public:
            ViewPort* viewPort;
        
        
          
        
    };
}

#endif /* defined(__DakaXiu__Stage__) */
