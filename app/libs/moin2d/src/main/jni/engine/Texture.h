//
//  Texture.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__Texture__
#define __MoYing__Texture__

#include <stdio.h>
//#include "Geometry.h"
#include "math.h"
#include "Image.h"
#include "MRMacro.h"

namespace Moin_2d
{
    
    typedef enum {
        
        //! 32-bit texture: RGBA8888
        Texture2DPixelFormat_RGBA8888,
        //! 24-bit texture: RGBA888
        //    Texture2DPixelFormat_RGB888,
        //! 16-bit texture without Alpha channel
        Texture2DPixelFormat_RGB565,
        //! 8-bit textures used as masks
        //    Texture2DPixelFormat_A8,
        //! 8-bit intensity texture
        //    Texture2DPixelFormat_I8,
        //    //! 16-bit textures used as masks
        //    Texture2DPixelFormat_AI88,
        //    //! 16-bit textures: RGBA4444
        //    Texture2DPixelFormat_RGBA4444,
        //    //! 16-bit textures: RGB5A1
        //    Texture2DPixelFormat_RGB5A1,
    } Texture2DPixelFormat;
    
    
    class  Texture
    {
    public:
        /**
         * @js ctor
         */
        Texture();
        Texture(unsigned char* byte,int length);
        Texture(const char* path);
        virtual ~Texture();
        
        void  bind();
        
        bool initWithData(const void* data, Texture2DPixelFormat pixelFormat, unsigned int pixelsWide, unsigned int pixelsHigh);

        
//        unsigned int bitsPerPixelForFormat();
        
  //      unsigned int bitsPerPixelForFormat(Texture2DPixelFormat format);
        
        
        //static void setDefaultAlphaPixelFormat(Texture2DPixelFormat format);
        
        /** returns the alpha pixel format
         @since v0.8
         @js getDefaultAlphaPixelFormat
         */
      //  static Texture2DPixelFormat defaultAlphaPixelFormat();
        
        /** content size */
        const Vector2& getContentSizeInPixels();
        
        bool hasPremultipliedAlpha();
        
        bool initWithImage(Image * image);
    protected:
        
        
        /** whether or not the texture has their Alpha premultiplied */
        bool m_bHasPremultipliedAlpha;
        
        
        //文件路径//
    public:
        float width;
        float height;
        
        GLuint uName;
        
        Vector2 size;
        Texture2DPixelFormat _textureFormat;
        
    };
    
}

#endif /* defined(__MoYing__Texture__) */
