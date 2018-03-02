//
//  MRImage.h
//  DakaXiu
//
//  Created by wufan on 15/7/14.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Image__
#define __DakaXiu__Image__

#include <stdio.h>

namespace Moin_2d
{

   class Texture;
//#define CC_RGB_PREMULTIPLY_ALPHA(vr, vg, vb, va) \
//(unsigned)(((unsigned)((unsigned char)(vr) * ((unsigned char)(va) + 1)) >> 8) | \
//((unsigned)((unsigned char)(vg) * ((unsigned char)(va) + 1) >> 8) << 8) | \
//((unsigned)((unsigned char)(vb) * ((unsigned char)(va) + 1) >> 8) << 16) | \
//((unsigned)(unsigned char)(va) << 24))
    class Image
    {
    public:
        Image();
        ~Image();
    
        //相对路径//
        void initWithName(const char* name);
        
        void initWithData(void * pData,
                          int nDataLen
                          );

        static Texture* createByAssetPath(const char* path);
        static Texture* createByData(void* pDatas,int nDataLen);
        
        bool initWithPngData(unsigned char * data, ssize_t dataLen);

        unsigned char* getData()
        {
            return _pData;
        }
//        bool _initWithPngData(void * pData, int nDatalen);
    private:
        

        
        unsigned char *_pData;
    public:
        int width;
        int height;
        int m_nBitsPerComponent;
        bool m_bHasAlpha;
        bool m_bPreMulti;
    };
}

#endif /* defined(__DakaXiu__MRImage__) */
