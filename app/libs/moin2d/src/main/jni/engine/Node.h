//
//  Node.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__Node__
#define __MoYing__Node__

#include "MRMacro.h"
#include "math.h"

namespace Moin_2d
{
    class Stage;
    class Node
    {
    public:
        Node();
        ~Node();
    public:
        virtual bool touchBegin(const Vector2& p0,const Vector2& p1);

        virtual bool touchMove(const Vector2& p0,const Vector2& p0Last, const Vector2& p1,const Vector2& p1Last);

        virtual bool touchEnd(const Vector2& p0,const Vector2& p1);
        
        
    public:
        void setPosition(float x,float y,float z = 0.f);
        void setRotationZ(float radius);
        void setScale(float scale);
        void setScaleX(float scaleX);
        void setScaleY(float scaleY);
        void setScaleZ(float scaleZ);
        
        
        
        
        const Vector3& getPosition()
        {
            return _position;
        }
        
        
        float getRotationZ()
        {
            return _rotationZ;
        }
        float getScale()
        {
            return _scale.x;
        }
        virtual void render();
        virtual void visit();
        virtual void update(int nowFrame);
        
        void addChild(Node*node);
        void toFront();
        void toBack();
        void removeChild(Node* node);
        
        Node* getParent()
        {
            return _parentNode;
        }
        
        void setParent(Node* parentNode)
        {
            _parentNode = parentNode;
        }

   
        Stage* getStage()
        {
            return _stage;
        }
        void setStage(Stage* stage);
        
        void invalidate();
 
        //强行设置大小//
        void setSize(float sizeX,float sizeY);
        
        int getChildrenCount()
        {
            return (int)_chirdren.size();
        }
        
        void setVisible(bool isVisible);
        bool getVisible();
        
        const Vector2& getSize()
        {
            return _size;
        }
        
        std::list<Node*>& getChildren()
        {
            return _chirdren;
        }
    protected:
        virtual void generateMatrix();
    public:

        void setName(std::string name)
        {
            _name = name;
        }
        std::string getName()
        {
            return _name;
        }
        static Matrix helpMatrix;
        static Matrix helpMatrix0;
        
        Matrix mvpMatrix;
        Matrix localMatrix;
    protected:
        bool _isVisible;
        bool _isDirty;
        Vector3 _position;
        float _rotationZ;
        Vector3 _scale;
        
        //mvpMatrix//
    
        Node* _parentNode;
        
        Stage* _stage;
        
        Vector2 _size;

        std::string _name;
    public:
        std::list<Node*> _chirdren;

        
    };
}

#endif /* defined(__MoYing__Node__) */
