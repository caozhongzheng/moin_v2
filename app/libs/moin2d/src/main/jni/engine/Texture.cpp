//
//  Texture.cpp
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Texture.h"
#include "MRMacro.h"
#include "Image.h"
namespace Moin_2d
{
    Texture::Texture()
    {
        _textureFormat = Texture2DPixelFormat_RGBA8888;
        width = 0.f;
        height = 0.f;
        uName = 0;
        m_bHasPremultipliedAlpha = false;
        
    }

    
    Texture::Texture(unsigned char* byte,int length)
    {
        _textureFormat = Texture2DPixelFormat_RGBA8888;
        width = 0.f;
        height = 0.f;
        uName = 0;
        m_bHasPremultipliedAlpha = false;
        
        Image* tImage = new Image();
        tImage->initWithData(byte, length);
        initWithImage(tImage);
        delete tImage;
    }
    
    Texture::Texture(const char* path)
    {
        _textureFormat = Texture2DPixelFormat_RGBA8888;
        width = 0.f;
        height = 0.f;
        uName = 0;
        m_bHasPremultipliedAlpha = false;
        
    }
    
    
    Texture::~Texture()
    {
        if(uName)
        {
            
            glDeleteTextures(1, &uName);
            uName = 0;
        }
    }
    
    void  Texture::bind()
    {
        glBindTexture(GL_TEXTURE_2D, uName);
    }
    
    bool Texture::initWithData(const void* data, Texture2DPixelFormat pixelFormat, unsigned int pixelsWide, unsigned int pixelsHigh)
    {

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        
        //纹理存在则生成//
        if(uName == 0)
        {
           
            glGenTextures(1, &uName);
        }
        
        bind();
        
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE );
        glTexParameteri( GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE );
        
    
        switch(pixelFormat)
        {
            case Texture2DPixelFormat_RGBA8888:
                
                   printf("生成texPng\n");
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, (GLsizei)pixelsWide, (GLsizei)pixelsHigh, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
                
                break;
              
            default:
                break;
                
        }
        
        
        width = pixelsWide;
        height = pixelsHigh;
        _textureFormat = pixelFormat;
        
        m_bHasPremultipliedAlpha = false;
        return true;
    }
    
    
    bool Texture::initWithImage(Image * uiImage)
    {
        if (uiImage == 0)
        {
            return false;
        }
        
        unsigned int imageWidth = uiImage->width;
        unsigned int imageHeight = uiImage->height;
        
        // always load premultiplied images
        return initWithData(uiImage->getData(),Texture2DPixelFormat_RGBA8888, imageWidth, imageHeight);
    }
    

}