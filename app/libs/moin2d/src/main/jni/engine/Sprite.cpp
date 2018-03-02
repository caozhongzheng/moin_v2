//
//  Sprite.cpp
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Sprite.h"
#include "Shader.h"
#include "ShareData.h"
#include "Texture.h"
#include "Stage.h"
#include "ZipUpzip.h"

namespace Moin_2d
{
    

    
    
    Sprite::Sprite(std::string fileName):isAnimation(false),_point(0),_isDirtySize(true),_frameArray(0),_texture(0)
    {

        setfileName(fileName);
       
    }
    
    void Sprite::setfileName(std::string fileName)
    {
        _texture = 0;
        _isFlip = false;
        _point = NULL;
        _nowIndex = 0;
        if(_frameArray)
        {
            delete _frameArray;
            _frameArray = 0;
        }
        if(_texture)
        {
            delete _texture;
            _texture = 0;
        }
        
        if(std::string::npos != fileName.find(".zip"))
        {

            ZipUpzip zipUpZip;
            zipUpZip.unzip(fileName.c_str());
            std::list<ZipEntry*>::iterator it = zipUpZip._zipList.begin();

           FrameArray* tframeArray = 0 ;
            Texture* tTexture = 0;
            for(;it != zipUpZip._zipList.end();it++)
            {
                ZipEntry* ze = *it;
                //是PNG图片//
                if (std::string::npos != ze->name.find(".png"))
                {

                    tTexture = Image::createByData(ze->byte,ze->length);

                }
                else if(std::string::npos != ze->name.find(".plist"))
                {

                    PlistReader *lR = new PlistReader(ze->byte,ze->length);
                    tframeArray = new FrameArray(lR);
                    delete lR;
                }
            }
            
            if(tTexture&&tframeArray)
            {
                tframeArray->texture = tTexture;
                tframeArray->generatePointData();
            }
            setFrameArray(tframeArray);
        }
        else if(std::string::npos != fileName.find(".png"))
        {
            
            Image* tImage = new Image();
            tImage->initWithName(fileName.c_str());
            _texture = new Texture();
            _texture->initWithImage(tImage);
            delete tImage;
            
        }
        
        
    }
    
    Sprite::Sprite(Image* image)
    {
        _texture = new Texture();
        _texture->initWithImage(image);

    }
    
    
    
    Sprite::~Sprite()
    {
        if(_point)
        {
            delete []_point;
            _point = 0;
        }
    }
    
    //提交渲染状态//
    void Sprite::commitRenderState()
    {
        
    }
    
    
    void Sprite::update(int nowFrame)
    {
        
        if(_frameArray)
        {
            _frameArray->update();
        }
        
        Node::update(nowFrame);
    }
    
    
    
    void Sprite::setTexture(Texture* texture)
    {
        if(_texture == texture)
        {
            return;
        }
        _texture = texture;
        if(texture)
        {
            _size.x = texture->width;
            _size.y = texture->height;
        }
        
        invalidateSize();
        
    }
    
    void Sprite::setFrameArray(FrameArray* array)
    {
        if(array)
        {
            _texture = 0 ;
        }
        if(_frameArray  == array)
        {
            return;
        }
        
        _frameArray  = array;
        
        std::vector<SpriteFrame*>::iterator it =  _frameArray->vecFrame.begin();
        
        if(it !=  _frameArray->vecFrame.end())
        {
            _size = (*it)->size;
        }
        invalidateSize();
    }
    
    
    void Sprite::invalidateSize()
    {
        _isDirtySize = true;
    }
    
    void Sprite::updateSize()
    {
        if(_point == 0)
        {
            _point = new PointFormat[4];
            
            
            
        }


        if(_isDirtySize)
        {
            if(_texture)
            {
                ShareData::Instance()->getPointData(_point,_isFlip,_size);
            }
            else if(_frameArray)
            {
                _frameArray->setIsFlip(_isFlip);
                _frameArray->generatePointData();
            }
            _isDirtySize = false;
        }
    }
    
    
    //    void printMatrix1(const Matrix &matrix)
    //    {
    //        const float *f = matrix.m;
    //
    //        for(int i = 0;i<4;i++)
    //        {
    //            for(int j = 0 ;j<4;j++)
    //            {
    //                printf("[%d]:%f",i*4+j,f[i*4+j]);
    //            }
    //            printf("\n");
    //        }
    //        printf("\n");
    //    }
    void Sprite::visit()
    {

        //提交渲染状态暂不需要实现//
        commitRenderState();
        //设置shader//
        Shader* cShader = 0;
        
        
        updateSize();

        if(_texture)
        {

            cShader = ShareData::Instance()->getTextureShader();
            cShader->use();

            _texture->bind();

            cShader->setMatrix(mvpMatrix.m);
            
            //可以有更多优化空间,目前没有必要//

            
            
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            
            glEnableVertexAttribArray(VertexAttrib_Position);
            glVertexAttribPointer(VertexAttrib_Position, 3, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),_point);
            glEnableVertexAttribArray(VertexAttrib_TexCoords);
            glVertexAttribPointer(VertexAttrib_TexCoords, 2, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),(GLvoid*)((float*)_point +  3));
            
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            
            glDisableVertexAttribArray(VertexAttrib_Position);
            glDisableVertexAttribArray(VertexAttrib_TexCoords);
        }
        else if(_frameArray)
        {

            cShader = ShareData::Instance()->getTextureShader();
            cShader->use();
            _frameArray->texture->bind();

            cShader->setMatrix(mvpMatrix.m);
            
            //可以有更多优化空间,目前没有必要//
            
            
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            
            glEnableVertexAttribArray(VertexAttrib_Position);
            glVertexAttribPointer(VertexAttrib_Position, 3, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),_frameArray->vecFrame[_frameArray->currentIndex]->pointFormat);
            glEnableVertexAttribArray(VertexAttrib_TexCoords);
            glVertexAttribPointer(VertexAttrib_TexCoords, 2, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),(GLvoid*)((float*)(_frameArray->vecFrame[_frameArray->currentIndex]->pointFormat) +  3));
            
           // printf("frame:%d\n",_frameArray->currentIndex);
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            
            glDisableVertexAttribArray(VertexAttrib_Position);
            glDisableVertexAttribArray(VertexAttrib_TexCoords);
            
        }
        else
        {

            cShader = ShareData::Instance()->getColorShader();
            cShader->use();
            cShader->setMatrix(mvpMatrix.m);
            
            
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            
            glEnableVertexAttribArray(VertexAttrib_Position);
            glVertexAttribPointer(VertexAttrib_Position, 3, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),_point);
            glEnableVertexAttribArray(VertexAttrib_TexCoords);
            glVertexAttribPointer(VertexAttrib_TexCoords, 2, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),(GLvoid*)((float*)_point +  3));
            
            glDrawArrays(GL_TRIANGLE_STRIP, 0, 4);
            
            glDisableVertexAttribArray(VertexAttrib_Position);
            glDisableVertexAttribArray(VertexAttrib_TexCoords);
        }
        
        
     
        
        
        
    }
    
    void Sprite::Flip()
    {
        LOGI("Flip");
        _isFlip = !_isFlip;
        invalidateSize();
        
    }
    
    void Sprite::playAnimation()
    {
        
    }
    
}
