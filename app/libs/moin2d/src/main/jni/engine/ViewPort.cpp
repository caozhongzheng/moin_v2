//
//  ViewPort.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/17.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "ViewPort.h"
#include "MRMacro.h"
namespace Moin_2d
{
    ViewPort::ViewPort()
    {
        _left = _right = _bottom = _top = _fnear = _ffar = 0.f;
        _perspective = 0.f;
        isDirty = true;
        
    }

    ViewPort::~ViewPort()
    {
        
    }
    
    void ViewPort::setViewPort()
    {
        glViewport(_left, _bottom, _right-_left, _top-_bottom);
    }

    
    void ViewPort::setOrtho(float left, float right, float bottom, float top, float fnear, float ffar)
    {
        
        _left = left;
        _right = right;
        _bottom = bottom;
        _top = top;
        _fnear = fnear;
        _ffar = ffar;
        generateMatrix();
    }
    
    void ViewPort::generateMatrix()
    {
        float tx = -((_right + _left) / (_right - _left));
        float ty = -((_top + _bottom) / (_top - _bottom));
        float tz = -((_ffar + _fnear) / (_ffar - _fnear));
        matrix.identity();
        matrix.m[0] = 2 / (_right - _left);
        matrix.m[5] = 2 / (_top - _bottom);
        matrix.m[10] = -2 / (_ffar - _fnear);
        matrix.m[11] = _perspective;
        //正向的//
        matrix.m[12] = tx;
        matrix.m[13] = ty;
        matrix.m[14] = tz;
    }
}
//视口，目前只有视图矩阵//


