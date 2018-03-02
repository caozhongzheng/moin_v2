//
//  Node.cpp
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Node.h"
#include "Stage.h"
namespace Moin_2d
{
    Node::Node()
    {
        _size.x = _size.y = 1.f;
        _stage = 0;
        _parentNode = 0;
        _position.x = _position.y  = _position.z = 0;
        _rotationZ = 0;
        _scale.x = _scale.y = _scale.z = 1.0;
        _isDirty = true;
        _isVisible = true;
    }
    
    Node::~Node()
    {
        
    }

    
 
    
    void Node::setPosition(float x,float y,float z)
    {
        if(_position.x != x)
        {
            _position.x = x;
            invalidate();
        }
        
        if(_position.y != y)
        {
            _position.y = y;
            invalidate();
        }
        
        if(_position.z != z)
        {
            _position.z = z;
            invalidate();
        }
    }
    
    void Node::setRotationZ(float radius)
    {
        if(_rotationZ != radius)
        {
            _rotationZ = radius;
            invalidate();
        }
    }
    void Node::setScale(float scale)
    {
        if(_scale.x == scale && _scale.y == scale && _scale.z == scale)
        {
            
            
        }
        else
        {
            _scale.x = _scale.y = _scale.z = scale;
            invalidate();
        }
    }
    
    void Node::setScaleX(float scaleX)
    {
        if(_scale.x != scaleX)
        {
            _scale.x = scaleX;
            invalidate();
        }

    }
    void Node::setScaleY(float scaleY)
    {
        
        if(_scale.y != scaleY)
        {
            _scale.y = scaleY;
            invalidate();
        }
    }
    void Node::setScaleZ(float scaleZ)
    {
        if(_scale.z != scaleZ)
        {
            _scale.z = scaleZ;
            invalidate();
        }
    }

    
    void Node::setSize(float sizeX,float sizeY)
    {
        _size.x = sizeX;
        _size.y = sizeY;
    }
    
    void Node::setVisible(bool isVisible)
    {
        _isVisible = isVisible;
    }
    
    bool Node::getVisible()
    {
        return _isVisible;
    }

    
    void Node::invalidate()
    {
        _isDirty = true;
        std::list<Node*>::iterator it = _chirdren.begin();
        for (; it != _chirdren.end(); it++) {
            (*it)->invalidate();
        }
    }
    
    //渲染//
    void Node::render()
    {
        //生成本地矩阵
        if(_isVisible)
        {

            generateMatrix();

            visit();

            std::list<Node*>::iterator it = _chirdren.begin();
            for (; it != _chirdren.end(); it++) {
                (*it)->render();
            }
        }
        
    }
    
    void Node::visit()
    {
        
    }
    
    void Node::setStage(Stage* stage)
    {
        _stage = stage;
        std::list<Node*>::iterator it = _chirdren.begin();
        for (; it != _chirdren.end(); it++) {
            (*it)->setStage(stage);
        }

        
    }
    
    void Node::addChild(Node*node)
    {
        assert(node);

        if(node->getParent())
        {
            return;
        }

        _chirdren.push_back(node);
        node->invalidate();
        node->setParent(this);
        node->setStage(_stage);
        
    }
    void Node::toFront()
    {
        if(_parentNode)
        {
            _parentNode->_chirdren.remove(this);
            _parentNode->_chirdren.push_back(this);
        }
    }
    void Node::toBack()
    {
        if(_parentNode)
        {
            _parentNode->_chirdren.remove(this);
            _parentNode->_chirdren.push_front(this);
        }
    }

    void Node::removeChild(Node* node)
    {
        std::list<Node*>::iterator iter = std::find(_chirdren.begin(), _chirdren.end(), node);
        if(iter != _chirdren.end())
        {
            _chirdren.erase(iter);
            node->setParent(0);
            node->setStage(0);
            node->invalidate();
        }
    }

    Matrix    Node::helpMatrix = Matrix();
    Matrix    Node::helpMatrix0 = Matrix();
    
    
//    void printMatrix(const Matrix &matrix)
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
    
    void Node::update(int nowFrame)
    {
        std::list<Node*>::iterator it  =   _chirdren.begin();
        for(;it != _chirdren.end();it++)
        {
            (*it)->update(nowFrame);
        }
    }

    
    bool Node::touchBegin(const Vector2& p0,const Vector2& p1)
    {
        return true;
    }
    bool Node::touchMove(const Vector2& p0,const Vector2& p0Last, const Vector2& p1,const Vector2& p1Last)
    {
        return true;
    }
    bool Node::touchEnd(const Vector2& p0,const Vector2& p1)
    {
        return true;
    }
    //生成矩阵，父矩阵必须先生成//
   
    void Node::generateMatrix()
    {

        if(_isDirty&&_parentNode&&_stage)
        {
            localMatrix.create2dTranslate(_position, _scale,  _rotationZ);
            
            Matrix::matrixmul(_parentNode->localMatrix,localMatrix, helpMatrix);
            Matrix::matrixmul(_stage->viewPort->matrix,helpMatrix, mvpMatrix);
            
            _isDirty = false;
        }
    }
    
}