//
//  Shader.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/16.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "Shader.h"

namespace Moin_2d
{
//    gl_Position = u_MVPMatrix*a_position;
    const GLchar * general_vert =
    "                                                    \n\
    attribute vec4 a_position;                            \n\
    attribute vec2 a_texCoord;                              \n\
    uniform     mat4 u_MVPMatrix;                        \n\
    varying vec2 v_texCoord;                                \n\
    void main()                                            \n\
    {\n\
    gl_Position = u_MVPMatrix*a_position;\n\
    v_texCoord = vec2(a_texCoord.r,a_texCoord.g);\n\
    }                                                    \n\
    ";
    
    const GLchar * general_frag = "                                            \n\
    precision highp float;                        \n\
    varying vec2 v_texCoord;                    \n\
    uniform sampler2D u_texture;                \n\
    \n\
    void main()                                  \n\
    {                                            \n\
    gl_FragColor = texture2D(u_texture, v_texCoord);\n\
    }                                            \n\
    ";
    
    //固定颜色//
    const GLchar * general_Colorfrag ="precision mediump float;\n"
    "void main() {\n"
    "  gl_FragColor = vec4(1.0, 1.0, 0.0, 1.0);\n"
    "}\n";
    
#define M5DGLASSERT( gl_code ) do \
{ \
gl_code; \
GLenum  __gl_error_code = glGetError(); \
M5DASSERT(__gl_error_code == GL_NO_ERROR); \
} while(0)


    
    Shader::Shader():m_uProgram(0)
    {
        
    }
    
    Shader::~Shader()
    {
        if(m_uProgram)
        {
            glDeleteProgram(m_uProgram);
            m_uProgram = 0;
        }

    }
    



    void Shader::initShader(EShaderMode shaderMode)
    {
        m_uProgram = glCreateProgram();
        
        GLuint            uVertShader;
        GLuint            uFragShader;

        
        uVertShader = uFragShader = 0;
        
        compileShader(&uVertShader, GL_VERTEX_SHADER, general_vert);
        
        if(shaderMode == ShaderTexture)
        {
            compileShader(&uFragShader, GL_FRAGMENT_SHADER, general_frag);
        }
        else if(shaderMode == ShaderColor)
        {
            compileShader(&uFragShader, GL_FRAGMENT_SHADER, general_Colorfrag);
        }
        
        if( uVertShader ) {
            glAttachShader(m_uProgram, uVertShader);
        }
        if( uFragShader ) {
            glAttachShader(m_uProgram, uFragShader);
        }
        
        
        glBindAttribLocation(m_uProgram,
                             VertexAttrib_Position,
                             "a_position");
        glBindAttribLocation(m_uProgram,
                             VertexAttrib_TexCoords,
                             "a_texCoord");
        
        //link
        glLinkProgram(m_uProgram);
        
        if( uVertShader )
        {
             glDeleteShader(uVertShader);
        }
        if( uFragShader )
        {
            glDeleteShader(uFragShader);
        }

        m_mvp =  glGetUniformLocation(m_uProgram,"u_MVPMatrix");
        if(shaderMode == ShaderTexture)
        {
            m_sample =  glGetUniformLocation(m_uProgram,"u_texture");
        }

        //第一张纹理，后期可以使用多重纹理//
        glUniform1i( m_sample, 0);
        
        glUseProgram(m_uProgram);
        
        return;
    }
    
    
    
    bool Shader::compileShader(GLuint * shader, GLenum type, const GLchar* source)
    {
        //  M5dLog("%s\n",source);
        GLint status;
        
        if (!source)
            return false;
        
        *shader = glCreateShader(type);
        glShaderSource(*shader, 1, &source, 0);
        glCompileShader(*shader);
        glGetShaderiv(*shader, GL_COMPILE_STATUS, &status);
        if(status == GL_FALSE)
        {
            return false;
        }
        
        return true;
    }
    
    void Shader::setMatrix(float* matrix)
    {
        glUniformMatrix4fv(m_mvp, 1, GL_FALSE, (GLfloat*)matrix);
     
    }
    
    

    void Shader::use()
    {
        glUseProgram(m_uProgram);
    }
    
}