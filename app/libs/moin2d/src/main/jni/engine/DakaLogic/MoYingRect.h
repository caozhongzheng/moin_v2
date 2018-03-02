//
//  moYingRect.h
//  MoYing
//
//  Created by wufan on 15-5-13.
//
//

#ifndef __MoYing__moYingRect__
#define __MoYing__moYingRect__



#include "../MoinHeader.h"


class MoYingRect
{
public:
    MoYingRect();
    ~MoYingRect();
    typedef enum
    {
        MoYingRect_None,
        MoYingRect_Corner,
        MoYingRect_Edge,
        MoYingRect_Center
    }MoYingTouchMode;
    
    void setNode(Node* node) ;
    
    void flip();
    //测试是否在矩形里面,如果是则自动激活当前矩形//
    MoYingTouchMode touchInRect(const Vector2& inPoint,Node* node);
    
    //测试是不是在矩形//
    MoYingTouchMode inRect(const Vector2& inPoint,Node* node);
    
    
    bool   touchMove(const Vector2& prevPoint,const Vector2& nowPoint);
    Vector2 size;
    Vector2 pos;
    float scale;
    float rotation;
    Vector2 v0;
    Vector2 v1;
    Vector2 v2;
    Vector2 v3;
    
    Node* currentNode;
    
    //   Rect limitRect;
public:
    
    float helpScale;
    
    Vector2 beginPoint;
    MoYingTouchMode currentTouchMode;
    int currentTouchIndex;
    
    
};

#endif /* defined(__MoYing__moYingRect__) */
