//
//  Sprite.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__Sprite__
#define __MoYing__Sprite__

#include <stdio.h>
#include <vector>
#include "Node.h"
#include "ShareData.h"
#include "FrameArray.h"

namespace Moin_2d
{
    class Texture;
    class Sprite :public Node
    {
        public:
        
            Sprite(std::string fileName);
            Sprite(Image* image);
            ~Sprite();
        
            virtual void commitRenderState();
            virtual void visit();
            virtual void update(int nowFrame);
    
            void Flip();
        
            bool getIsFlip()
            {
                return _isFlip;
            }
        
            void setFrameArray(FrameArray* array);
        
            void invalidateSize();
            void updateSize();
        
            void setfileName(std::string fileName);

        protected:
            void setTexture(Texture* texture);
            void playAnimation();
            bool _isFlip;
        
            PointFormat* _point;
        
            Texture* _texture;
            FrameArray* _frameArray;
        
            int _nowIndex;
            //间隔时间//
            int _spaceTime;
    
            bool isAnimation;
        
            bool _isDirtySize;

    };
}
#endif /* defined(__MoYing__Sprite__) */
