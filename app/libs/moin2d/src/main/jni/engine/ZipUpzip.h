//
//  ZipAchive.h
//  DakaXiu
//
//  Created by wufan on 15/7/21.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__ZipAchive__
#define __DakaXiu__ZipAchive__

#include "MRMacro.h"
#include "minizip/unzip.h"


namespace Moin_2d
{
    class ZipEntry;
    class ZipUpzip
    {
        public:
            ZipUpzip();
            ~ZipUpzip();
            void unzip(const char* path);
            void clear();
        
        public:
            std::list<ZipEntry*> _zipList;
        
        
    };
    
    //解压后的文件//
    class ZipEntry
    {
        public:
            ZipEntry():byte(0),readPoint(0),length(0)
            {
            
            }
            ~ZipEntry()
            {
                if(byte != 0)
                {
                    free(byte);
                    byte = 0;
                }
            }
            void resize(long length)
            {
                if(byte != 0)
                {
                    free(byte);
                    byte = 0;
                }
                byte = (char*)malloc(length);
                readPoint = 0;
                this->length = length;
            }
        
        public:
            int readPoint;
            char *byte;
            int length;
            std::string name;
    };
    
}

#endif /* defined(__DakaXiu__ZipAchive__) */
