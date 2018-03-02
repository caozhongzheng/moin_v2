//
//  ZipAchive.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/21.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "ZipUpzip.h"
#include "Path.h"

namespace Moin_2d
{
    ZipUpzip::ZipUpzip()
    {
        
    }
    
    ZipUpzip::~ZipUpzip()
    {
        clear();
    }
    

    void ZipUpzip::unzip(const char* path)
    {
        clear();
        
        unzFile uF = unzOpen(Path::shareInstance()->getBundleRes(path).c_str());

        if(uF)
        {

            unz_global_info globalInfo = {0};
            if( unzGetGlobalInfo(uF, &globalInfo )==UNZ_OK )
            {

                int ret =  unzGoToFirstFile(uF);
                
                ret = unzOpenCurrentFile(uF);
                
                unz_file_info	fileInfo ={0};
                
                for (int i =0; i<globalInfo.number_entry; i++)
                {
                    
                    char filename[1024];
                    ret = unzGetCurrentFileInfo(uF, &fileInfo, 0, 0, 0, 0, NULL, 0);
                    ret = unzGetCurrentFileInfo(uF, &fileInfo, filename, fileInfo.size_filename+1, 0, 0, 0, 0);

            ;       filename[fileInfo.size_filename] = '\0';
                    ZipEntry *zipEntry = new ZipEntry();
                    zipEntry->resize(fileInfo.uncompressed_size);
                    zipEntry->name = filename;
                    
                    ret = unzOpenCurrentFile(uF);
                    
                    unzReadCurrentFile(uF, zipEntry->byte, (unsigned int)fileInfo.uncompressed_size);
            
                    unzCloseCurrentFile(uF);
                    
                    
                    
                    ret = unzGoToNextFile( uF );
                    _zipList.push_back(zipEntry);
                    
                }
                
            }
            unzClose(uF);
            return;
        }
    }
    
    void ZipUpzip::clear()
    {
        std::list<ZipEntry*>::iterator it = _zipList.begin();
        for(;it != _zipList.end();it++)
        {
            delete *it;
        }
        _zipList.clear();
    }
    

}