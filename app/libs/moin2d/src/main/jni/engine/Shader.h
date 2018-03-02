//
//  Shader.h
//  DakaXiu
//
//  Created by wufan on 15/7/16.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Shader__
#define __DakaXiu__Shader__

#include <stdio.h>
#include"MRMacro.h"


namespace Moin_2d
{
    enum {
        VertexAttrib_Position,
        //        VertexAttrib_Normal
        VertexAttrib_TexCoords
    };
    
    
    typedef enum{
        ShaderColor,
        ShaderTexture
    }EShaderMode;
    class Shader
    {
        public:
            Shader();
            ~Shader();
        public:
            void initShader(EShaderMode shaderMode = ShaderTexture);
            void use();
        
            void setMatrix(float* matrix);
        
        private:
            bool compileShader(GLuint * shader, GLenum type, const GLchar* source);
        protected:
            GLuint            m_uProgram;
        
            GLint             m_mvp;
            GLint             m_sample;
    };
}

#endif /* defined(__DakaXiu__Shader__) */
