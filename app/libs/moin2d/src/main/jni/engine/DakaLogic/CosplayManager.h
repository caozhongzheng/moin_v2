//
//  CosplayManager.h
//  DakaXiu
//
//  Created by wufan on 15/7/25.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__CosplayManager__
#define __DakaXiu__CosplayManager__
#include "../MoinHeader.h"

class CosplayManager
{
    public:
        CosplayManager()
        {
        
        }
        ~CosplayManager()
        {
        
        }
        FrameArray* getFrameArray(std::string path);
    protected:
        std::map<std::string,FrameArray*> _mapFrameArray;
    
};

#endif /* defined(__DakaXiu__CosplayManager__) */
