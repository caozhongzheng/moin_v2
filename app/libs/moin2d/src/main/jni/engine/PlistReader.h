//
//  PlistReader.h
//  DakaXiu
//
//  Created by wufan on 15/7/24.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__PlistReader__
#define __DakaXiu__PlistReader__

#include "MRMacro.h"
namespace Moin_2d
{
    //Dictionary可能就是一个string//
    class Dictionary
    {
        public:
            Dictionary();
            ~Dictionary();
            enum
            {
                ENUM_Dic,
                ENUM_String
            };
        
            void read(char** p0);
            int type;
            std::map<std::string,Dictionary*> dicArray;
            std::string value;
    };
    
    class PlistReader
    {
        public:
            PlistReader(char* byteArray,int length);
            ~PlistReader();
        public:
            Dictionary* rootDic;

        
    };
  
}


#endif /* defined(__DakaXiu__PlistReader__) */
