//
//  Scale9Sprite.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/22.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Scale9Sprite.h"

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

#if defined(linux) || defined(__ANDROID) || defined(__linux__)
    #include"android/JniHelper.h"
#endif

namespace Moin_2d
{
    Scale9Sprite::Scale9Sprite(const char* fileName)
    {
            _scaleY = 1.0f;
            _texture = 0;
            _isFlip = false;
            _scalePoint = 0;
            _scaleFlagVec = 0;


            Texture* tTexture = Image::createByAssetPath(fileName);
            setTexture(tTexture);




    }
    
    Scale9Sprite::Scale9Sprite(Texture* texture)
    {
        
        _scaleY = 1.0f;
        _texture = 0;
        _isFlip = false;
        _scalePoint = 0;
        _scaleFlagVec = 0;
        setTexture(texture);
    }
    
    
    Scale9Sprite::~Scale9Sprite()
    {
        deleteScale9();
    }
    
    //提交渲染状态//
    void Scale9Sprite::commitRenderState()
    {
        
    }
    
    
    void Scale9Sprite::generateMatrix()
    {
        //九宫格创建//
        if(_scalePoint)
        {
            if(_isDirty&&_parentNode&&_stage)
            {
                localMatrix.create2dTranslate(_position, _scale,  _rotationZ);
                
                //标准宽高
                float tSpaceX = _size.x*(-0.5-scale9Rect.x);
                float tSpaceXEnd = _size.x*(0.5-(scale9Rect.x+scale9Rect.width));
                
                float tSpaceY = _size.y*(-0.5-scale9Rect.y);
                float tSpaceYEnd = _size.y*(0.5-(scale9Rect.y+scale9Rect.height));
                
                float tTargetSpaceX = -_size.x/2-tSpaceX/_scale.x;
                float tTargetSpaceXEnd = _size.x/2 - tSpaceXEnd/_scale.x;
                
                float tTargetSpaceY = -_size.y/2 - tSpaceY/_scale.y;
                float tTargetSpaceYEnd = _size.y/2 - tSpaceYEnd/_scale.y;
                
                for(int i = 0 ;i<54;i++)
                {
                    if(_scaleFlagVec[i].x==-1)
                    {
                        _scalePoint[i].x = tTargetSpaceX;
                    }
                    else if(_scaleFlagVec[i].x==1)
                    {
                        _scalePoint[i].x = tTargetSpaceXEnd;
                    }
                    
                    if(_scaleFlagVec[i].y ==-1)
                    {
                        
                        _scalePoint[i].y =  tTargetSpaceY;
                    }
                    else if(_scaleFlagVec[i].y ==1)
                    {
                        
                        _scalePoint[i].y = tTargetSpaceYEnd;
                    }
                    
                    
                }
                
                Matrix::matrixmul(_parentNode->localMatrix,localMatrix, helpMatrix);
                Matrix::matrixmul(_stage->viewPort->matrix,helpMatrix, mvpMatrix);
                
                _isDirty = false;
                
                
            }
        }
        else
        {
            Node::generateMatrix();
        }
    }
    
    
    //必须在设置texture之后//
    void Scale9Sprite::setScale9Rect(const Rect& rect)
    {
        LOGI("输出111");
        if(!_scalePoint)
        {
            _scalePoint = new PointFormat[6*9];
            _scaleFlagVec = new Vector3[6*9];
            
        }
        scale9Rect.x = rect.x;
        scale9Rect.y = rect.y;
        scale9Rect.width = rect.width;
        scale9Rect.height = rect.height;
        
        ShareData::Instance()->setDataList(-0.5,-0.5,rect.x,rect.y,&_scalePoint[0]);
        
        ShareData::Instance()->setDataList(rect.x,-0.5,rect.x+rect.width,rect.y,&_scalePoint[6]);
        
        ShareData::Instance()->setDataList(rect.x+rect.width,-0.5,0.5,rect.y,&_scalePoint[12]);
        
        
        ShareData::Instance()->setDataList(-0.5,rect.y,rect.x,rect.y+rect.height,&_scalePoint[18]);
        
        ShareData::Instance()->setDataList(rect.x,rect.y,rect.x+rect.width,rect.y+rect.height,&_scalePoint[24]);
        
        ShareData::Instance()->setDataList(rect.x+rect.width,rect.y,0.5,rect.y+rect.height,&_scalePoint[30]);
        ShareData::Instance()->setDataList(-0.5,rect.y+rect.height,rect.x,0.5,&_scalePoint[36]);
        
        ShareData::Instance()->setDataList(rect.x,rect.y+rect.height,rect.x+rect.width,0.5,&_scalePoint[42]);
        
        ShareData::Instance()->setDataList(rect.x+rect.width,rect.y+rect.height,0.5,0.5,&_scalePoint[48]);
        
        for (int i = 0; i<54; i++) {
            //选择出中间的格子//
            if(_scalePoint[i].x == rect.x)
            {
                _scaleFlagVec[i].x = -1;
            }
            else if(_scalePoint[i].x == rect.x+ rect.width)
            {
                _scaleFlagVec[i].x = 1;
            }
            else
            {
                _scaleFlagVec[i].x = 0;
            }
            
            if(_scalePoint[i].y == rect.y)
            {
                _scaleFlagVec[i].y = -1;
            }
            else if(_scalePoint[i].y == rect.y + rect.height)
            {
                _scaleFlagVec[i].y = 1;
            }
            else
            {
                _scaleFlagVec[i].y = 0;
            }
        }
        
        for(int i = 0;i<54;i++)
        {
            _scalePoint[i].x *= _size.x;
            _scalePoint[i].y *= _size.y;
        }
        
        
        invalidate();
    }
    
    void Scale9Sprite::deleteScale9()
    {
        if(_scalePoint)
        {
            delete []_scalePoint;
            _scalePoint = 0;
            //删除九宫格//
            invalidate();
        }
        if(_scaleFlagVec)
        {
            delete [] _scaleFlagVec;
            _scaleFlagVec = 0;
        }
    }
    
    void Scale9Sprite::setTexture(Texture* texture)
    {
        _texture = texture;
        if(_texture)
        {
            _size.x = _texture->width;
            _size.y = _texture->height;
        }
        
    }
    
    
    
  
    void Scale9Sprite::visit()
    {

        if(_scalePoint == 0)
        {
            return;
        }
        //提交渲染状态暂不需要实现//
        commitRenderState();
        //设置shader//
        Shader* cShader = 0;
        

        if(_texture)
        {
            cShader = ShareData::Instance()->getTextureShader();
            cShader->use();
            _texture->bind();

        }


        cShader->setMatrix(mvpMatrix.m);
        
        //可以有更多优化空间,目前没有必要//
        
        GLfloat* tpoint =0;
        
        tpoint = (GLfloat*)_scalePoint;
        
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        
        glEnableVertexAttribArray(VertexAttrib_Position);
        glVertexAttribPointer(VertexAttrib_Position, 3, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),tpoint);
        glEnableVertexAttribArray(VertexAttrib_TexCoords);
        glVertexAttribPointer(VertexAttrib_TexCoords, 2, GL_FLOAT, GL_FALSE, 5*sizeof(GLfloat),(GLvoid*)(tpoint +  3));
        

        glDrawArrays(GL_TRIANGLES, 0, 54);
        glDisableVertexAttribArray(VertexAttrib_Position);
        glDisableVertexAttribArray(VertexAttrib_TexCoords);
        
        
        
    }
    
    void Scale9Sprite::Flip()
    {
        _isFlip = !_isFlip;
    }
    
    void Scale9Sprite::update(int nowFrame)
    {
        
        
    }
    
    
    void Scale9Sprite::playAnimation()
    {
        
    }
    
}
