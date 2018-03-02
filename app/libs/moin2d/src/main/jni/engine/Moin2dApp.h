//
//  Moin2dApp.h
//  MoYing
//
//  Created by wufan on 15/6/28.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#ifndef __MoYing__Moin2dApp__
#define __MoYing__Moin2dApp__

#include <stdio.h>
#include "Ref.h"


namespace Moin_2d {
    
    class Moin2d;
    
    class Moin2dApp:public Ref
    {
    public:
        Moin2dApp();
        virtual ~Moin2dApp();
                
       virtual void init(Moin2d* moin2d);
        
    };
}

#endif /* defined(__MoYing__Moin2dApp__) */
