//
//  Ref.h
//  DakaXiu
//
//  Created by wufan on 15/7/14.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __DakaXiu__Ref__
#define __DakaXiu__Ref__

#include <stdio.h>
namespace Moin_2d {
    
    class Ref
    {
        public:
            Ref()
            {
                nRef = 1;
            }
            ~Ref()
            {
                
            }
        
            void retain()
            {
                nRef ++;
            }
            void release()
            {
                nRef--;
            }
        
            int nRef;
        
        
    };
    
    
}


#endif /* defined(__DakaXiu__Ref__) */
