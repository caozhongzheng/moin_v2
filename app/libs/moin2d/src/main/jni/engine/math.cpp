//
//  math.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/17.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "math.h"


namespace Moin_2d
{
    
    static const float Matrix_IDENTITY[16] =
    {
        1.0f, 0.0f, 0.0f, 0.0f,
        0.0f, 1.0f, 0.0f, 0.0f,
        0.0f, 0.0f, 1.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 1.0f
    };
    
    
    //后续可以汇编优化//
    void Matrix::matrixmul(const Matrix& m1,const Matrix& m2,Matrix& outMatrix)
    {
        
        outMatrix.m[0]  = m1.m[0] * m2.m[0]  + m1.m[4] * m2.m[1] + m1.m[8]   * m2.m[2]  + m1.m[12] * m2.m[3];
        outMatrix.m[1]  = m1.m[1] * m2.m[0]  + m1.m[5] * m2.m[1] + m1.m[9]   * m2.m[2]  + m1.m[13] * m2.m[3];
        outMatrix.m[2]  = m1.m[2] * m2.m[0]  + m1.m[6] * m2.m[1] + m1.m[10]  * m2.m[2]  + m1.m[14] * m2.m[3];
        outMatrix.m[3]  = m1.m[3] * m2.m[0]  + m1.m[7] * m2.m[1] + m1.m[11]  * m2.m[2]  + m1.m[15] * m2.m[3];
        
        outMatrix.m[4]  = m1.m[0] * m2.m[4]  + m1.m[4] * m2.m[5] + m1.m[8]   * m2.m[6]  + m1.m[12] * m2.m[7];
        outMatrix.m[5]  = m1.m[1] * m2.m[4]  + m1.m[5] * m2.m[5] + m1.m[9]   * m2.m[6]  + m1.m[13] * m2.m[7];
        outMatrix.m[6]  = m1.m[2] * m2.m[4]  + m1.m[6] * m2.m[5] + m1.m[10]  * m2.m[6]  + m1.m[14] * m2.m[7];
        outMatrix.m[7]  = m1.m[3] * m2.m[4]  + m1.m[7] * m2.m[5] + m1.m[11]  * m2.m[6]  + m1.m[15] * m2.m[7];
        
        outMatrix.m[8]  = m1.m[0] * m2.m[8]  + m1.m[4] * m2.m[9] + m1.m[8]   * m2.m[10] + m1.m[12] * m2.m[11];
        outMatrix.m[9]  = m1.m[1] * m2.m[8]  + m1.m[5] * m2.m[9] + m1.m[9]   * m2.m[10] + m1.m[13] * m2.m[11];
        outMatrix.m[10] = m1.m[2] * m2.m[8]  + m1.m[6] * m2.m[9] + m1.m[10]  * m2.m[10] + m1.m[14] * m2.m[11];
        outMatrix.m[11] = m1.m[3] * m2.m[8]  + m1.m[7] * m2.m[9] + m1.m[11]  * m2.m[10] + m1.m[15] * m2.m[11];
        
        outMatrix.m[12] = m1.m[0] * m2.m[12] + m1.m[4] * m2.m[13] + m1.m[8]  * m2.m[14] + m1.m[12] * m2.m[15];
        outMatrix.m[13] = m1.m[1] * m2.m[12] + m1.m[5] * m2.m[13] + m1.m[9]  * m2.m[14] + m1.m[13] * m2.m[15];
        outMatrix.m[14] = m1.m[2] * m2.m[12] + m1.m[6] * m2.m[13] + m1.m[10] * m2.m[14] + m1.m[14] * m2.m[15];
        outMatrix.m[15] = m1.m[3] * m2.m[12] + m1.m[7] * m2.m[13] + m1.m[11] * m2.m[14] + m1.m[15] * m2.m[15];
    }
    
    Matrix::Matrix()
    {
        identity();
    }
    
    Matrix::~Matrix()
    {
        
    }
    
    void Matrix::identity()
    {
        memcpy(m, Matrix_IDENTITY, sizeof(float)*16);
    }
    
    //2D矩阵
    void Matrix::create2dTranslate(const Vector3& pos,const Vector3& scale,float rotation)
    {
        
        float c = cos(rotation);
        float s = sin(rotation);
        //        dst->m[0] = c;
        //        dst->m[1] = s;
        //        dst->m[4] = -s;
        //        dst->m[5] = c;
        
        
        m[0] = scale.x*c; m[1] = scale.x*s; m[2] = 0.f;  m[3] = 0.f;
        
        m[4] = scale.y*-s;m[5] = scale.y*c; m[6] = 0.f;  m[7] = 0.f;
        
        m[8] = 0.f;     m[9] = 0.f;     m[10] = scale.z; m[11] = 0.f;
        
        m[12] = pos.x;     m[13] = pos.y;     m[14] = pos.z; m[15] = 1.f;
        
    }
    
    void Matrix::scaleSize(const Vector2& size,Matrix& outMatrix)
    {
        memcpy(outMatrix.m, this->m, sizeof(float)*16);
        outMatrix.m[0]*=size.x;
        outMatrix.m[1]*=size.x;
        outMatrix.m[4]*=size.y;
        outMatrix.m[5]*=size.y;
    }
    
    
    
    //vec2//
    
    void Vector2::rotate(const Vector2& point, float angle)
    {
        double sinAngle = sin(angle);
        double cosAngle = cos(angle);
        
        if (point.x == 0 && point.y == 0)
        {
            float tempX = x * cosAngle - y * sinAngle;
            y = y * cosAngle + x * sinAngle;
            x = tempX;
        }
        else
        {
            float tempX = x - point.x;
            float tempY = y - point.y;
            
            x = tempX * cosAngle - tempY * sinAngle + point.x;
            y = tempY * cosAngle + tempX * sinAngle + point.y;
        }
    }
    
    
    
    float Vector2::getAngle(const Vector2& other) const
    {
        Vector2 a2 = getNormalized();
        Vector2 b2 = other.getNormalized();
        float angle = atan2f(a2.cross(b2), a2.dot(b2));
        if( fabs(angle) < 0.00001 ) return 0.f;
        return angle;
    }
    
    void Vector2::normalize()
    {
        float n = x * x + y * y;
        // Already normalized.
        if (n == 1.0f)
            return;
        
        n = sqrt(n);
        // Too close to zero.
        if (n < 0.00001)
            return;
        
        n = 1.0f / n;
        x *= n;
        y *= n;
    }
    
}