    //
//  DakaLogic.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__DakaLogic__
#define __MoYing__DakaLogic__

#include <stdio.h>
#include "../MoinHeader.h"
#include "MoYingRect.h"
#include "CosplayManager.h"

class DakaXiu :public Moin_2d::Moin2dApp,public Moin_2d::StageDelegate
{
    public:
        DakaXiu();
        ~DakaXiu();
    
    public:
        virtual bool touchBegin(const Vector2& p0,const Vector2& p1);
        virtual bool touchMove(const Vector2& p0,const Vector2& p0Last,const Vector2& p1,const Vector2& p1last);
        virtual bool touchEnd(const Vector2& p0,const Vector2& p1);
    
        void createTexture(std::string texture);
        void init(Moin2d* moin2d);
        #ifdef __ANDROID__
          void createImage(Image* image,const char* name);

        #endif

    
    
    public:
    
    
      //  void setPart(std::string type, std::string commonZip);
        void setPart(std::string type,std::string tag,std::string zipName,float scale = 1.f);
    private:
        void focusNode(Node* node);
    private:
        
        Sprite* _bg;
        Moin2d* _moin2d;
    
        Node *_spriteContainer;
        vector<Node*> _editCell;
        Node* _currentActiveSprite;
        Scale9Sprite* _touchFrame;
        MoYingRect _moYingRect;

        float _baseScale;

        std::map<std::string,Sprite*> _map ;
    
        CosplayManager* _cosplayManager;
    
};


#endif /* defined(__MoYing__DakaLogic__) */
