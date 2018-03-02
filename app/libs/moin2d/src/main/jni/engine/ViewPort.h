//
//  ViewPort.h
//  DakaXiu
//
//  Created by wufan on 15/7/17.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__ViewPort__
#define __DakaXiu__ViewPort__

#include <stdio.h>
#include "math.h"
namespace Moin_2d
{
    
    //目前Viewport
    class ViewPort
    {
        public:
            ViewPort();
            ~ViewPort();
        
        //目前不考虑3d的情况//
        void setOrtho(float left, float right, float bottom, float top, float fnear, float ffar);
        
        void setViewPort();
        
    protected:
        
        void setPerPerspective(float perspective);

        virtual void generateMatrix();
        
    private:
        float _left;
        float _right;
        float _bottom;
        float _top;
        float _fnear;
        float _ffar;
        
        float _perspective;
        
    public:
        bool isDirty;;
        
    public:
        Matrix matrix;
        
        
    };
}

#endif /* defined(__DakaXiu__ViewPort__) */
