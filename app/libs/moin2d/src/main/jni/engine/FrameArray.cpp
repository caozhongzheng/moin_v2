//
//  FrameArray.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/24.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "FrameArray.h"
//帧读取//
namespace Moin_2d
{
    

    #define SWAP(x, y, type)    \
    {       type temp = (x);        \
        x = y; y = temp;        \
    }
    
    
    SpriteFrame::SpriteFrame():isFlip(false),isRotated(false)
    {
        atlasSize.x = atlasSize.y = 0;
    }
    
    SpriteFrame::~SpriteFrame()
    {
        
    }
    
    
    void SpriteFrame::generatePointData()
    {
        
        float relativeOffsetX = offset.x;
        float relativeOffsetY = offset.y;
        
        if(isFlip)
        {
            relativeOffsetX = -relativeOffsetX;
        }
        
        
        float  toffsetX = relativeOffsetX  - frame.width/2;
        float  toffsetY = relativeOffsetY  - frame.height/2;

        float x0 = toffsetX;
        float y0 = toffsetY;
        float x1 = x0 + frame.width;
        float y1 = y0 + frame.height;
        
        
        //左右//
        float left, right, top, bottom;
        
        
        if (isRotated)
        {

            left    = frame.x/atlasSize.x;
            right    = (frame.x+frame.height) / atlasSize.x;
            top        = frame.y/atlasSize.y;
            bottom    =   (frame.y+frame.width) / atlasSize.y;
            
            
            if (isFlip)
            {
                SWAP(top, bottom, float);
            }
            
            pointFormat[0].u = left;
            pointFormat[0].v = top;
            
            pointFormat[1].u = left;
            pointFormat[1].v = bottom;
            
            pointFormat[2].u = right;
            pointFormat[2].v = top;
            
            pointFormat[3].u = right;
            pointFormat[3].v = bottom;


        }
        else
        {

            
            left    = frame.x/atlasSize.x;
            right    = (frame.x + frame.width) / atlasSize.x;
            top        = frame.y/atlasSize.y;
            bottom    = (frame.y + frame.height) / atlasSize.y;
            
            if(isFlip)
            {
                SWAP(left,right,float);
            }
            
            
            pointFormat[0].u = left;
            pointFormat[0].v = bottom;
            
            pointFormat[1].u = right;
            pointFormat[1].v = bottom;
            
            pointFormat[2].u = left;
            pointFormat[2].v = top;
            
            pointFormat[3].u = right;
            pointFormat[3].v = top;
            
        }
        
        
        
        
        pointFormat[0].x = x0;
        pointFormat[0].y = y0;
        pointFormat[0].z = 0;

        
        pointFormat[1].x = x1;
        pointFormat[1].y = y0;
        pointFormat[1].z = 0;

        
        pointFormat[2].x = x0;
        pointFormat[2].y = y1;
        pointFormat[2].z = 0;

        
        pointFormat[3].x = x1;
        pointFormat[3].y = y1;
        pointFormat[3].z = 0;
        
    }
    
    
    
    
    
    int isNum(char tN)
    {
        if((tN <= '9' && tN>='0') || tN=='.')
        {
            return 1;
        }
        return 0;
    }
    
    void getVectorFromString(char* type,Vector2& outRect)
    {
        char* t = type;
        int beginNum = 0;
        char* beginP = 0;
        while (*t != '\0') {
            if(isNum(*t))
            {
                if(beginP == 0)
                {
                    beginNum++;
                    beginP = t;
                }
            }
            else
            {
                if(beginP!=0 )
                {
                    char lastT = *t;
                    *t = '\0';
                    switch (beginNum) {
                        case 1:
                            outRect.x = atoi(beginP);
                            break;
                            
                        case 2:
                            outRect.y = atoi(beginP);
                            break;
                            
                        default:
                            break;
                    }
                    *t = lastT;
                    beginP = 0;
                    if(beginNum>2)
                    {
                        break;
                    }
                }
            }
            t++;
        }
    }

    
    
    void getRectFromString(char* type,Rect& outRect)
    {
        char* t = type;
        int beginNum = 0;
        char* beginP = 0;
        while (*t != '\0') {
            if(isNum(*t))
            {
                if(beginP == 0)
                {
                    beginNum++;
                    beginP = t;
                }
                
            }
            else
            {
                if(beginP!=0 )
                {
                    char lastT = *t;
                    *t = '\0';
                    switch (beginNum) {
                        case 1:
                            outRect.x = atoi(beginP);
                            break;
                            
                        case 2:
                            outRect.y = atoi(beginP);
                            break;
                            
                        case 3:
                            outRect.width = atoi(beginP);
                            break;
                            
                        case 4:
                            outRect.height = atoi(beginP);
                            break;
                            
                        default:
                            break;
                    }
                    *t = lastT;
                    beginP = 0;
                    if(beginNum>4)
                    {
                        break;
                    }
                }
            }
            t++;
        }
    }
    
    //默认的时间间隔是0.2//
    FrameArray::FrameArray(PlistReader* reader):texture(0),timeTick(0.03f),_nowTime(0),currentIndex(0)
    {
        
        char tValueHelp[1024];
        std::map<std::string,Dictionary*>::iterator it=   reader->rootDic->dicArray.find("frames");
        if(it != reader->rootDic->dicArray.end())
        {
            Dictionary* tdic = it->second;
            it  = tdic->dicArray.begin();
            for(;it != tdic->dicArray.end();it++)
            {
                SpriteFrame *spriteFrame = new SpriteFrame();
                vecFrame.push_back(spriteFrame);
                
                spriteFrame->name = it->first;
                std::map<std::string,Dictionary*>::iterator it1 = it->second->dicArray.begin();

                for(;it1 != it->second->dicArray.end();it1++)
                {
                    if(it1->first == "frame")
                    {
                        strcpy(tValueHelp, it1->second->value.c_str());
                        getRectFromString(tValueHelp,spriteFrame->frame);
                    }
                    else if(it1->first == "offset")
                    {
                        strcpy(tValueHelp, it1->second->value.c_str());
                        getVectorFromString(tValueHelp,spriteFrame->offset);
                    }
                    else if(it1->first == "rotated")
                    {
                        if(it1->second->value == "true")
                        {
                            spriteFrame->isRotated = true;
                        }
                        else
                        {
                            spriteFrame->isRotated = false;
                        }
                    }
                    else if(it1->first == "sourceSize")
                    {
                        strcpy(tValueHelp, it1->second->value.c_str());
                        getVectorFromString(tValueHelp,spriteFrame->size);
                    }
                }
            }
        }

    }

    FrameArray::~FrameArray()
    {
        
        vecFrame.clear();
        if(texture)
        {
            delete texture;
            texture = NULL;
        }
        
    }

    void FrameArray::setIsFlip(bool isFlip)
    {
        std::vector<SpriteFrame*>::iterator it =  vecFrame.begin();
        for(;it != vecFrame.end();it++)
        {
            (*it)->isFlip = isFlip;
        }
    }

    void FrameArray::generatePointData()
    {
        std::vector<SpriteFrame*>::iterator it =  vecFrame.begin();
        for(;it != vecFrame.end();it++)
        {
            if(texture)
            {
                (*it)->atlasSize.x = texture->width;
                (*it)->atlasSize.y = texture->height;
                (*it)->generatePointData();
            }
        }
    }
    
    void FrameArray::update()
    {
        _nowTime += 1.0/LOGIC_FRAME;
        if(_nowTime>= timeTick)
        {
            _nowTime-= timeTick;
            currentIndex ++;
            if(currentIndex==vecFrame.size())
            {
                currentIndex = 0;
            }
        }
    }
    
    
    
}