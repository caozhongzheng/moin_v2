//
//  DakaLogic.cpp
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "DakaXiu.h"

DakaXiu::DakaXiu():_baseScale(1.f),_moin2d(0),_spriteContainer(0),_bg(0),_currentActiveSprite(0),_touchFrame(0)
{

}

DakaXiu::~DakaXiu()
{
    
}
#define FrameWidthOffset 30
#define FrameHeightOffset 30


void DakaXiu::focusNode(Node* node)
{
       if(node == 0)
       {
       return;
       }

    
    _moYingRect.setNode(node);
    _touchFrame->setVisible(true);
    
    
//    printf("_moYingRectX:%f,_moYingRectY:%f\n",_moYingRect.pos.x,_moYingRect.pos.y);//
    _touchFrame->setPosition(_moYingRect.pos.x,_moYingRect.pos.y,0);
    _touchFrame->setRotationZ(_moYingRect.rotation);
    
    Vector2 tSize(_moYingRect.size.x*_moYingRect.scale,_moYingRect.size.y*_moYingRect.scale) ;
    tSize.x += FrameWidthOffset*2;
    tSize.y += FrameHeightOffset*2;

    Vector2 tSize1 = _touchFrame->getSize();
    float tSx = tSize.x/tSize1.x;
    float tSy = tSize.y/tSize1.y;
    
    
    _touchFrame->setScaleX(tSx);
    _touchFrame->setScaleY(tSy);
//_touchFrame->setScaleY(tSZ);
    
    if(_currentActiveSprite)
    {
        _currentActiveSprite->toFront();
    }

    
    _currentActiveSprite = node;
    
}

bool DakaXiu::touchBegin(const Vector2& p0,const Vector2& p1)
{
    
    bool isInRect  =false;
    std::list<Node*>& ch =_spriteContainer->getChildren();
    std::list<Node*>::reverse_iterator it = ch.rbegin();
    for(;it != ch.rend();it++)
    {
        Sprite* tSprite = dynamic_cast<Sprite*>(*it);
        if(tSprite)
        {
            MoYingRect::MoYingTouchMode tInRectMode = _moYingRect.touchInRect(p0, tSprite);
            
            if(tInRectMode != MoYingRect::MoYingRect_None)
            {
                if(tSprite == _currentActiveSprite)
                {
                    
                    //                    //如果当前击中//
                    isInRect = true;
                    if(tInRectMode == MoYingRect::MoYingRect_Corner && _moYingRect.currentTouchIndex == 1)
                    {
                        _moYingRect.flip();
                        return false;
                    }
                    
                    return true;
                }
            }

        }
    }
    
    for(it = ch.rbegin(); it != ch.rend(); it++)
    {
        Sprite* tSprite = dynamic_cast<Sprite*>(*it);

        if(tSprite)
        {
            
            MoYingRect::MoYingTouchMode tInRectMode = _moYingRect.touchInRect(p0, tSprite);
            
            if(tInRectMode != MoYingRect::MoYingRect_None)
            {
                focusNode(tSprite);
                return false;
                
            }
            
        }
    }
    return false;
}


bool DakaXiu::touchMove(const Vector2& p0,const Vector2& p0Last,const Vector2& p1,const Vector2& p1last)
{
    if(_moYingRect.touchMove(p0Last, p0))
    {
        focusNode(_currentActiveSprite);
    }
    
    return true;
}
bool DakaXiu::touchEnd(const Vector2& p0,const Vector2& p1)
{
    return true;
}



void DakaXiu::init(Moin2d* moin2d)
{

    _moin2d = moin2d;
    moin2d->stage->setStageDelegate(this);
    _spriteContainer = new Node();
    moin2d->stage->addChild(_spriteContainer);
    focusNode(_currentActiveSprite);
    
      _touchFrame = new Scale9Sprite("cosplay_bianjikuang.png");
             Vector2 size = _moin2d->stage->getSize();
                            Rect tRect(-0.1,-0.1,0.2,0.2);
                            _touchFrame->setScale9Rect(tRect);
                            _touchFrame->setPosition(size.x/2, size.y/2);
                            _touchFrame->setScale(1.0);
    _moin2d->stage->addChild(_touchFrame);
    
    _touchFrame->toFront();



    //    if(strcmp(name,"bg") == 0)
    //    {
    //        _bg = createWithImage(image);
    //        Vector2 size = _moin2d->stage->getSize();
    //        _bg->setPosition(size.x/2, size.y/2);
    //        _moin2d->stage->addChild(_bg);
    //        _bg->toBack();
    //    }
    return;

}




#define MaxBufferSize 1024*10
//换资源//
 //void setPart(std::string type, std::string commonZip);
void DakaXiu::setPart(std::string type,std::string tag,std::string zipName,float scale)
{

    Sprite *tSprite = 0;
    std::map<std::string,Sprite*>::iterator it = _map.find(type);
    if(it == _map.end())
    {
        tSprite = new Sprite(zipName);
        _spriteContainer->addChild(tSprite);

        _map[type] = tSprite;
        Vector2 size = _moin2d->stage->getSize();
        tSprite->setPosition(size.x/2, size.y/2);
    }
    else
    {

       tSprite = it->second;
       if(tSprite->getName()  != tag)
       {
           tSprite->setName(tag);
           tSprite->setfileName(zipName);

       }
    }

    tSprite->setScale(scale);
    focusNode(tSprite);
}

