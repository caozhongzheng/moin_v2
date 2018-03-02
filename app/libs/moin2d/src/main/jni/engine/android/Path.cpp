

#include "../Path.h"
#include "../MRMacro.h"

namespace Moin_2d
{

    Path* Path::_path = 0;
    std::string Path::getBundleRes(std::string name)
    {
        //NSString *string = [[NSBundle mainBundle] pathForResource:[NSString stringWithUTF8String:name.c_str()] ofType:nil];
        return name;//[string UTF8String];
    }

}

