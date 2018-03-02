//
//  PlistReader.cpp
//  DakaXiu
//
//  Created by wufan on 15/7/24.
//  Copyright (c) 2015年 wufan. All rights reserved.
//

#include "PlistReader.h"


namespace Moin_2d
{
  
    
    Dictionary::Dictionary():type(ENUM_Dic)
    {
        
    }
    

    Dictionary::~Dictionary()
    {
        std::map<std::string,Dictionary*>::iterator it = dicArray.begin();
        for(;it!= dicArray.end();it++)
        {
            delete it->second;
        }
        dicArray.clear();
        
    }
    void fitlerSpace(char **p)
    {

        
        while (**p == ' '||**p =='\t' || **p =='\n' || **p =='\r') {
            (*p)++;
        }
    }
    
    bool isEqualCharArray(char** charBegin,const char* compare)
    {
        int length = strlen(compare);
        
        
        for(int i = 0;i<length;i++)
        {
            if(*(*charBegin+i) == *(compare+i))
            {
                
            }
            else
            {
                return false;
            }
        }
        (*charBegin) += length;
        return true;
    }
    
    bool findNextTag(char**p,const char* tag)
    {
        fitlerSpace(p);
        
        while (!isEqualCharArray(p,tag)) {
            (*p)++;
        }
        
        return true;
    }
    
    //返回是否是结束//
    bool findNextTick(char**p,char outStr[])
    {

        int i = 0;
        while (**p != '>') {
            outStr[i] = **p;
            (*p)++;
            i++;
        }
        

        
        if(*((*p)-1) == '/')
        {
            (*p)++;
            outStr[i-1] = '\0';
            return true;
        }
 
        (*p)++;
        outStr[i] = '\0';
        
        return false;
    }
    
    
    void findValueTag(char**p,char outStr[])
    {
        fitlerSpace(p);
        

        char *beginP = *p;
        
        while (**p != ' '&&**p !='\t' && **p !='\n' && **p !='\r' && **p !='<') {
            (*p)++;
        }
        memcpy(outStr, beginP, (*p-beginP));
        outStr[(*p-beginP)]='\0';
        
        return ;
    }
    
    void Dictionary::read(char** p)
    {
        char key[1024];
        char value[2048];
        
        int needKey = 1;
        while (true) {
            
            if(needKey == 1)
            {
                if(findNextTag(p,"<"))
                {
                    //key>
                    bool isEnd = findNextTick(p,value);
                    if(strcmp(value, "key") == 0)
                    {
                        findValueTag(p,key);
                        needKey = 2;
                        
                        findNextTag(p,"</key>");
                        continue;
                    }
                    if(strcmp(value, "/dict") == 0)
                    {
                        return;
                    }

                    
                }
            }
            //需要值,或者dic//
            else if(needKey == 2)
            {

                if(findNextTag(p,"<"))
                {
                    //找value//
                    bool isEnd = findNextTick(p,value);
                    //直接结束的//
                    if(isEnd)
                    {
                        dicArray[key]= new Dictionary();
                        dicArray[key]->value = value;
                       // printf("endvalue:%s\n",value);
                    }
                    else
                    {
                        if(strcmp(value, "dict") ==0)
                        {
                            dicArray[key] = new Dictionary();
                            dicArray[key]->read(p);
                            dicArray[key]->type = ENUM_Dic;
                        }
                        else
                        {
                            findValueTag(p,value);
                            
                            dicArray[key]= new Dictionary();
                            dicArray[key]->value = value;
                            dicArray[key]->type = ENUM_String;
                            
                            findNextTag(p,"<");
                            findNextTick(p,value);
                        }
                    }
                }
                needKey = 1;
            }
        }
    }
    

    void Parse()
    {
        
    }
    
    PlistReader::PlistReader(char* byteArray,int length):rootDic(0)
    {
        //只可能一个根//
        char *t = byteArray;
        findNextTag(&t,"<dict>");
        rootDic = new Dictionary();
        rootDic->read(&t);
        
        //找到结尾
        
    }
    
    
    PlistReader::~PlistReader()
    {
        if(rootDic)
        {
            delete rootDic;
            rootDic = 0;
        }
    }
    
    
}