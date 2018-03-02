//
//  Stage.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/18.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Stage.h"


namespace Moin_2d
{
    Stage::Stage(float x,float y,float width,float height)
    {
        
        _delegate = 0;
        viewPort = new ViewPort();
        setStage(x, y, width, height);
        _stage = this;
    }
    
    Stage::~Stage()
    {
        delete viewPort;
        viewPort = 0;
    }
    
    void Stage::setStage(float x,float y,float width,float height)
    {
        _size.x = width;
        _size.y = height;
        viewPort->setOrtho(0,width , 0, height, -1024, 1024);
    }
    
    bool Stage::touchBegin(const Vector2& p0,const Vector2& p1)
    {
        if(_delegate)
        {
            return _delegate->touchBegin(p0,p1);
        }
        return true;
    }
    

    bool Stage::touchMove(const Vector2& p0,const Vector2& p0Last, const Vector2& p1,const Vector2& p1Last)
    {
        if(_delegate)
        {
            return _delegate->touchMove(p0,p0Last,p1,p1Last);
        }
        return true;
    }
    
    bool Stage::touchEnd(const Vector2& p0,const Vector2& p1)
    {
        if(_delegate)
        {
            return _delegate->touchEnd(p0,p1);
        }
        return true;
    }
    
    void Stage::render()
    {
        viewPort->setViewPort();
        Node::render();
    }
    
}