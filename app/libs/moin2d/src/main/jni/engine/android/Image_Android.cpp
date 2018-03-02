//
//  Image.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/25.
//  Copyright (c) 2015年 wufan. All rights reserved.
//


#include "../Image.h"
#include"../Path.h"
#include"../Texture.h"
#include"JniHelper.h"

namespace Moin_2d
{
    
    Image::Image():width(0),height(0),m_nBitsPerComponent(0),m_bHasAlpha(false),m_bPreMulti(false),_pData(0)
    {
        
    }
    
    void Image::initWithName(const char* name)
    {
        //默认只能读asset目录//

        //JniHelper::createBitmap(name,);
//        FILE *fp = fopen(Path::shareInstance()->getBundleRes(name).c_str(), "rb");
//        char * pBuffer = NULL;
//        fseek(fp,0,SEEK_END);
//        int pSize = ftell(fp);
//        fseek(fp,0,SEEK_SET);
//        pBuffer = new char[pSize];
//        //文本的长度//
//        fread(pBuffer,sizeof(char), pSize,fp);
//        fclose(fp);
//        if(_pData)
//        {
//            delete []_pData;
//            _pData = NULL;
//        }
//
//        initWithPngData((unsigned char *)pBuffer, pSize);
        
       // delete []pBuffer;
        
        
    }

    Texture* Image::createByAssetPath(const char* path)
    {
           return JniHelper::createBitmap(path,AssetPath);
    }

    Texture* Image::createByData(void* pDatas,int nDataLen)
    {
        return JniHelper::createBitmapBydata((unsigned char*)pDatas,nDataLen);
    }

    
    
    Image::~Image()
    {
        if(_pData)
        {
            delete []_pData;
            _pData = 0;
        }
    }
    
    
    void Image::initWithData(void * pData,
                      int nDataLen
                      )
    {
        initWithPngData((unsigned char *)pData,nDataLen);
    }

    
    bool Image::initWithPngData(unsigned char * data, ssize_t dataLen)
    {
        //JniMethodInfo tBitmapDecode;
        //if(JniHelper::getStaticMethodInfo(tBitmapDecode,)
        return true;
    }


}


//#endif /* defined(__DakaXiu__MRImage__) */