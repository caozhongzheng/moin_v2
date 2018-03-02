//
//  CosplayManager.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/25.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "CosplayManager.h"



FrameArray* CosplayManager::getFrameArray(std::string path)
{
    std::map<std::string,FrameArray*>::iterator it =  _mapFrameArray.find(path);
    if(it != _mapFrameArray.end())
    {
        return it->second;
    }
    
    ZipUpzip zipUpZip;
    zipUpZip.unzip(path.c_str());
    std::list<ZipEntry*>::iterator itZip = zipUpZip._zipList.begin();
    
    
    
    bool havePic = false;
    bool havePlist =false;
    Texture* tTexture = 0;
    FrameArray* tFrameArray = 0;
    for(;itZip != zipUpZip._zipList.end();itZip++)
    {
        ZipEntry* ze = *itZip;
        //是PNG图片//
        if (std::string::npos != ze->name.find(".png"))
        {
            tTexture = new Texture((unsigned char* )ze->byte,ze->length);

            havePic = true;
        }
        else if(std::string::npos != ze->name.find(".plist"))
        {
            PlistReader *lR = new PlistReader(ze->byte,ze->length);
            tFrameArray = new FrameArray(lR);
            delete lR;
            havePlist = true;
        }
    }
    
    if(havePlist&& havePic)
    {
        tFrameArray->texture = tTexture;
        _mapFrameArray[path] = tFrameArray;
        return tFrameArray;

    }
    
    return 0;
}
// void DakaXiu::createImage,const char* name)
//{
//    
//}