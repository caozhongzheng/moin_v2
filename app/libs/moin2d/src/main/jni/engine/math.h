//
//  math.h
//  DakaXiu
//
//  Created by wufan on 15/7/17.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__math__
#define __DakaXiu__math__

#include <stdio.h>
#include <string.h>
#include <math.h>

namespace Moin_2d
{
    class Vector3;
    class Vector2;
    
    class Rect
    {
    public:
        
        Rect()
        {
            x = y = width = height = 0.0;
        }
        Rect(float x,float y,float width,float height)
        {
            this->x = x;
            this->y = y;
            this->width = width;
            this->height = height;
        }
        float x;
        float y;
        float width;
        float height;
    };
    
    //    M_PI
    class Matrix
    {
    public:
        Matrix();
        ~Matrix();
        
        /**
         * Stores the columns of this 4x4 Matrix.
         * */
        void create2dTranslate(const Vector3& pos,const Vector3& scale,float rotation);
        
        static void matrixmul(const Matrix& m0,const Matrix& m1,Matrix& outMatrix);
        
        void scaleSize(const Vector2& size,Matrix& outMatrix);
        
    public:
        void identity();
        float m[16];
    };
    
    class Vector3
    {
    public:
        Vector3()
        {
            
        }
        ~Vector3()
        {
            
        }
        
        float x;
        float y;
        float z;
    };
    
    class Vector2
    {
    public:
        Vector2(float x,float y)
        {
            this->x = x;
            this->y = y;
        }
        
        Vector2(const Vector2& vec)
        {
            x = vec.x;
            y = vec.y;
        }
    
        Vector2()
        {
            x = y = 0.0;
        }
        ~Vector2()
        {
            
        }
        
        void normalize();
    
        
        void rotate(const Vector2& point, float angle);
        float getAngle(const Vector2& other) const;
        
        float x;
        float y;
        
        
        inline float length() const
        {
            return sqrt(x * x + y * y);
        }
        
        //inline//
        inline const Vector2 operator-(const Vector2& v) const
        {
            Vector2 result;
            result.x = this->x - v.x;
            result.y = this->y - v.y;
            return result;
        }
        
        inline const Vector2 operator+(const Vector2& v) const
        {
            Vector2 result;
            result.x = this->x + v.x;
            result.y = this->y + v.y;
            return result;
        }


        inline Vector2& operator+=(const Vector2& v)
        {
           
            x += v.x;
            y += v.y;
            return *this;
        }
        
        inline float cross(const Vector2& other) const {
            return x*other.y - y*other.x;
        };
        
        inline float dot(const Vector2& v) const
        {
            return (x * v.x + y * v.y);
        }
        
        inline Vector2 getNormalized() const
        {
            Vector2 v(*this);
            v.normalize();
            return v;
        }
    };
}

#endif /* defined(__DakaXiu__math__) */
