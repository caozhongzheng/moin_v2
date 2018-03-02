//
//  Path.h
//  DakaXiu
//
//  Created by wufan on 15/7/27.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Path__
#define __DakaXiu__Path__

#include <stdio.h>


#include "MRMacro.h"

namespace Moin_2d
{
    
    class Path
    {
    public:
        static Path* shareInstance()
        {
            return _path = _path?_path:new Path();
        }
        
        std::string getBundleRes(std::string name);
        char* getBundleType(std::string name,int &length);
        static Path *_path;
    };
}

#endif /* defined(__DakaXiu__Path__) */
