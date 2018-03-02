//
//  ShareData.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/19.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "ShareData.h"
#include "Shader.h"
namespace Moin_2d
{
    
    ShareData::ShareData():_textureShare(0),_colorShader(0)
    {
        initData();
        createShader();

    }
    
    ShareData::~ShareData()
    {
        destroyShader();
    }
    
    ShareData*  ShareData::_instance = 0;
    
    ShareData* ShareData::Instance()
    {
        return _instance = _instance?_instance:new ShareData();
    }
    
    void ShareData::destroy()
    {
        if(_instance)
        {
            delete _instance;
            _instance = 0;
        }
    }
    
    void ShareData::setData(float beginX,float beginY,float endX,float endY,PointFormat* beginPoint,bool isFlip)
    {
        
        setDataPointDateCell(beginPoint,beginX,beginY,isFlip);
        setDataPointDateCell(beginPoint+1,beginX,endY,isFlip);
        setDataPointDateCell(beginPoint+2,endX,beginY,isFlip);
        setDataPointDateCell(beginPoint+3,endX,endY,isFlip);
        
    }
    
//    void scalePoint(PointFormat* point, float width,float height)
//    {
//        point->x*=width;
//        point->y*=height;
//    }
    //三角列//
    void ShareData::setDataList(float beginX,float beginY,float endX,float endY,PointFormat* beginPoint,bool isFlip)
    {
        
        setDataPointDateCell(beginPoint,beginX,beginY,isFlip);
        setDataPointDateCell(beginPoint+1,beginX,endY,isFlip);
        setDataPointDateCell(beginPoint+2,endX,beginY,isFlip);
        setDataPointDateCell(beginPoint+3,beginX,endY,isFlip);
        setDataPointDateCell(beginPoint+4,endX,beginY,isFlip);
        setDataPointDateCell(beginPoint+5,endX,endY,isFlip);
        
    }

    
    void ShareData::setDataPointDateCell(PointFormat* pointFormat,float x,float y,bool isFlip)
    {
        if(!isFlip)
        {
            pointFormat->x = x;
            pointFormat->y = y;
            pointFormat->z = 0;            
            pointFormat->u = pointFormat->x +0.5;
            pointFormat->v = -(pointFormat->y -0.5);
        }
        else
        {
            pointFormat->x = x;
            pointFormat->y = y;
            pointFormat->z = 0;
            pointFormat->u = 1.0-pointFormat->x -0.5;
            pointFormat->v = -(pointFormat->y -0.5);
        }
    }
    
    void ShareData::initData()
    {
        
        setData(-0.5,-0.5,0.5,0.5,_point,false);
        setData(-0.5,-0.5,0.5,0.5,_pointFlip,true);
        
        
    }
    
    void ShareData::createShader()
    {
        if(_textureShare == 0)
        {
            _textureShare = new Shader();
            _textureShare->initShader(ShaderTexture);
        }
        if(_colorShader == 0)
        {
            _colorShader = new Shader();
            _colorShader->initShader(ShaderColor);
        }
    }
    
    void ShareData::destroyShader()
    {
        if(_colorShader)
        {
            delete _colorShader;
            _colorShader = 0;
        }
        if(_textureShare)
        {
            delete _textureShare;
            _textureShare = 0;
        }
    }
    
    void ShareData::getPointData(PointFormat* outPoint,bool isFlip,const Vector2& size)
    {
        PointFormat* tpoint =  getPointData(isFlip);
        memcpy(outPoint, tpoint, sizeof(PointFormat)*4);
        

        outPoint[0].x*=size.x;
        outPoint[0].y*=size.y;
        
        outPoint[1].x*=size.x;
        outPoint[1].y*=size.y;
        
        outPoint[2].x*=size.x;
        outPoint[2].y*=size.y;

        outPoint[3].x*=size.x;
        outPoint[3].y*=size.y;

    }
    
    
}

