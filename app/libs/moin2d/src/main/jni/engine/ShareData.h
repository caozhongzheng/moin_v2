//
//  ShareData.h
//  DakaXiu
//
//  Created by wufan on 15/7/19.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__ShareData__
#define __DakaXiu__ShareData__

#include <stdio.h>
#include "math.h"
namespace Moin_2d
{
    class Shader;
    typedef struct M5dPointFormat
    {
        float x;
        float y;
        float z;
        float u;
        float v;
    }PointFormat;
    
    class ShareData
    {
    public:
        ShareData();
        ~ShareData();
        static ShareData* Instance();
        
        static void destroy();
        
        //-0.5,0.5区间//
        void setData(float beginX,float beginY,float endX,float endY,PointFormat* beginPoint,bool isFlip =  false);
        
        void setDataList(float beginX,float beginY,float endX,float endY,PointFormat* beginPoint,bool isFlip =  false);
        
        void setDataPointDateCell(PointFormat* pointFormat,float x,float y,bool isFlip);
        
        
        void initData();
        PointFormat* getPointData(bool isFlip = false)
        {
            if(isFlip)
            {
                return _pointFlip;
            }
            return _point;
        }
        

        void getPointData(PointFormat* outPoint,bool isFlip,const Vector2& size);
        
        PointFormat* getPointScaleData(bool isFlip = false)
        {
            if(isFlip)
            {
                return _pointScale9Flip;
            }
            return _pointScale9;
        }
        
        
        void createShader();
        void destroyShader();
        
        Shader* getTextureShader()
        {
            return _textureShare;
        }
        Shader* getColorShader()
        {
            return _colorShader;
        }
    private:
        static ShareData* _instance;
        
        PointFormat _point[4];
        PointFormat _pointFlip[4];
        
        PointFormat _pointScale9[4*9];
        PointFormat _pointScale9Flip[4*9];
        
        
        Shader* _textureShare;
        Shader* _colorShader;
        
        
    };
}


#endif /* defined(__DakaXiu__ShareData__) */
