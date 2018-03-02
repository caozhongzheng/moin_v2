//
//  Scale9Sprite.h
//  DakaXiu
//
//  Created by wufan on 15/7/22.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Scale9Sprite__
#define __DakaXiu__Scale9Sprite__

#include "MRMacro.h"
#include "Node.h"
#include "ShareData.h"


namespace Moin_2d
{
    class Texture;
    class Scale9Sprite :public Node
    {
    public:

        Scale9Sprite(const char* fileName);
        Scale9Sprite(Texture* texture = 0);
        ~Scale9Sprite();
        
        virtual void commitRenderState();
        virtual void visit();
        virtual void update(int nowFrame);
        
        //基本的宽和高//
        void setScale9Rect(const Rect& rect);
        void deleteScale9();
        

        void Flip();
        void setTexture(Texture* texture);
        
        bool getIsFlip()
        {
            return _isFlip;
        }
        
    protected:
        
        virtual void generateMatrix();
        void playAnimation();
        bool _isFlip;
        
        Texture* _texture;
        PointFormat* _scalePoint;
        Vector3 *_scaleFlagVec;
        Rect scale9Rect;
        
        float _scaleY;
        
        
    };
}

#endif /* defined(__DakaXiu__Scale9Sprite__) */
