//
//  FrameArray.h
//  DakaXiu
//
//  Created by wufan on 15/7/24.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __FrameArray__
#define __FrameArray__

#include "MRMacro.h"
#include "math.h"
#include "Texture.h"
#include "PlistReader.h"
#include "ShareData.h"
namespace Moin_2d
{
    
    class SpriteFrame
    {
        public:
            SpriteFrame();
  
            ~SpriteFrame();
        
        
            Rect frame;
            Vector2 offset;

        
            Vector2 size;
            bool isRotated;
            std::string name;
        
            bool isFlip;
        
            void generatePointData();
        
        //    void setDataPointDataCell(PointFormat* pointFormat,float x,float y,bool isFlip);
        
            Vector2 atlasSize;
        public:
            PointFormat pointFormat[4];
        
        
    };
    class FrameArray
    {
        public:
            FrameArray(PlistReader* reader);
            ~FrameArray();
        
            void update();

            void setIsFlip(bool isFlip);
            void generatePointData();
            std::vector<SpriteFrame*> vecFrame;
            Texture* texture;
            int currentIndex;
            int width;
            int height;
        
            float _nowTime;
            float timeTick;
    };
    

}

#endif /* defined(__DakaXiu__FrameArray__) */
