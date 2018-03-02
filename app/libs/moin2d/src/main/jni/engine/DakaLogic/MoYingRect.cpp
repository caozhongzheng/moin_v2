//
//  moYingRect.cpp
//  MoYing
//
//  Created by wufan on 15-5-13.
//
//

#include "MoYingRect.h"
//#include "gif.h"

MoYingRect::MoYingRect()
{
    currentNode = 0;
    scale = 1.f;
    rotation =0.f;
    helpScale =1.f;
    currentTouchIndex = 0;
    currentTouchMode = MoYingRect_None;
    
//    limitRect.setRect(0, 0, Director::getInstance()->getWinSize().width, Director::getInstance()->getWinSize().height);
}

MoYingRect::~MoYingRect()
{
    
}

void MoYingRect::setNode(Node* node)
{
    currentNode = node;
    size =  node->getSize();
    
    rotation = node ->getRotationZ();
    scale = node->getScale();
    pos.x = node->getPosition().x;
    pos.y = node->getPosition().y;
    

    
    v0.x = pos.x - size.x*scale/2;
    v0.y = pos.y - size.y*scale/2;
    
    v1.x = pos.x - size.x*scale/2;
    v1.y = pos.y + size.y*scale/2;
    
    v2.x = pos.x + size.x*scale/2;
    v2.y = pos.y + size.y*scale/2;
    
    v3.x = pos.x + size.x*scale/2;
    v3.y = pos.y - size.y*scale/2;
    
    Vector2 pos2;
    pos2.x = pos.x;
    pos2.y = pos.y;
    
    v0.rotate(pos2, -rotation);
    v1.rotate(pos2, -rotation);
    v2.rotate(pos2, -rotation);
    v3.rotate(pos2, -rotation);
}
//#define testRange 20
#define edgeRange 35
#define edgeRangeScaleMin 1.5
MoYingRect::MoYingTouchMode MoYingRect::touchInRect(const Vector2& inPoint,Node* node)
{
  
    currentTouchMode = this->inRect(inPoint, node);
    
    if(currentTouchMode != MoYingRect::MoYingRect_None)
    {
        setNode(node);
    }
    beginPoint = inPoint;
    helpScale = node->getScale();
    return currentTouchMode;
}

MoYingRect::MoYingTouchMode MoYingRect::inRect(const Vector2& inPoint,Node* node)
{
    Vector3 inpos =  node->getPosition();
    float inScale = node->getScale();
    float inRotation = node->getRotationZ();
    Vector2 inSize = node->getSize();
    Vector2 point(inPoint);
    
    Vector2 pos2;
    pos2.x = inpos.x;
    pos2.y = inpos.y;
    
    point.rotate(pos2, -inRotation);
    
    Vector2 tv0,tv1,tv2,tv3;
    tv0.x = inpos.x - inSize.x*inScale/2;
    tv0.y = inpos.y - inSize.y*inScale/2;
    
    tv1.x = inpos.x - inSize.x*inScale/2;
    tv1.y = inpos.y + inSize.y*inScale/2;
    
    tv2.x = inpos.x + inSize.x*inScale/2;
    tv2.y = inpos.y + inSize.y*inScale/2;
    
    tv3.x = inpos.x + inSize.x*inScale/2;
    tv3.y = inpos.y - inSize.y*inScale/2;
    
    currentTouchIndex = 0;
    

    
//    printf("11point.x:%f,point.y:%f\n",point.x,point.y);

//        CCLOG("inRotation:%f\n",inRotation);
   // printf("pointX:%f,y:%f,v0x:%f,v0y:%f\n",point.x,point.y,tv2.x,tv2.y);
    if(!(point.x < tv0.x-edgeRange||
         point.x > tv2.x+edgeRange||
         point.y < tv0.y-edgeRange||
         point.y > tv2.y+edgeRange))
    {
     //   printf("inPos\n");
      //  float tLength = (point-tv0).length();
        if((point.y < tv0.y+edgeRange)&&
           (point.y > tv0.y-edgeRange)&&
           (point.x > tv0.x-edgeRange)&&
           (point.x < tv0.x+edgeRange))
        {
            currentTouchIndex = 1;
            return MoYingRect_Corner;
        }
//        else
//        if((point.y < tv1.y+edgeRange)&&
//                (point.y > tv1.y-edgeRange)&&
//                (point.x > tv1.x - edgeRange)&&
//                (point.x < tv1.x+edgeRange))
//        {
//            currentTouchIndex = 2;
//            return MoYingRect_Corner;
//        }
        else if((point.y < tv2.y+edgeRange)&&
                (point.y > tv2.y-edgeRange)&&
                (point.x > tv2.x-edgeRange)&&
                (point.x < tv2.x+edgeRange))
        {
            currentTouchIndex = 3;
            return MoYingRect_Corner;
        }
//        else if((point.y < tv3.y+edgeRange)&&
//                (point.y > tv3.y-edgeRange)&&
//                (point.x > tv3.x-edgeRange)&&
//                (point.x < tv3.x+edgeRange))
//        {
//                        currentTouchIndex = 4;
//            return MoYingRect_Corner;
//        }
        //边缘//
        
        //左边缘//
        /*if((point.x < tv0.x+edgeRange)&&
            (point.x > tv0.x-edgeRange)&&
            (point.y < tv1.y-edgeRange)&&
            (point.y > tv0.y+edgeRange)
            )
        {
                        currentTouchIndex = 1;
            return MoYingRect_Edge;
        }
        //上边缘//
        else if((point.y < tv1.y+edgeRange)&&
                (point.y > tv1.y-edgeRange)&&
                (point.x > tv1.x+edgeRange)&&
                (point.x < tv2.x-edgeRange))
        {
                        currentTouchIndex = 2;
            return MoYingRect_Edge;
        }
        //右边缘//
        else if((point.x < tv2.x+edgeRange)&&
                (point.x > tv2.x-edgeRange)&&
                (point.y > tv3.y+edgeRange)&&
                (point.y < tv2.y-edgeRange))
        {
                        currentTouchIndex = 3;
            return MoYingRect_Edge;
        }*/
        //下边缘//
//        else if((point.y < tv3.y + edgeRange)&&
//                (point.y > tv3.y - edgeRange)&&
//                (point.x > tv0.x + edgeRange)&&
//                (point.x < tv3.x - edgeRange)
//                )
//                
//        {
//            currentTouchIndex = 4;
//            return MoYingRect_Edge;
//         
//        }
        

        
        else if(!((point.y > tv1.y)||
                (point.y < tv0.y )||
                (point.x < tv0.x)||
                (point.x > tv2.x)))
        {
            return MoYingRect_Center;
        }
        
    }
    
    return MoYingRect_None;
    
    
}

void MoYingRect::flip()
{

    Sprite* tSprite =  dynamic_cast<Sprite*>(currentNode);
    if(tSprite)
    {
        tSprite->Flip();
    }
}


bool   MoYingRect::touchMove(const Vector2& prePoint,const Vector2& nowPoint)
{

    
    if(currentNode == 0)
    {
        return false;
    }
    
    if(currentTouchMode ==MoYingRect_Corner&&currentTouchIndex == 3)
    {
        Vector2 t1 = prePoint - pos;
        Vector2 t2 = nowPoint - pos;
        
        float tAngle = t1.getAngle(t2);
        LOGI("rotation:%f",tAngle);
        rotation = rotation + tAngle;
        currentNode->setRotationZ(rotation);
        
        
        float BegintScale = (beginPoint - pos).length();
        float NowScale = (nowPoint-pos).length();
        
        float sureScale = helpScale/BegintScale*NowScale;
        
        
            //    printf("BegintScale:%f\n,NowScale:%f\n,sureScale:%f\n",BegintScale,NowScale,sureScale);
        float minScale = edgeRange*edgeRangeScaleMin/size.x;
        

        if(sureScale<minScale)
        {
            sureScale = minScale;
        }

        
        currentNode->setScale(sureScale);

        return true;
    }
    //左上角不做处理，外部镜像//
    else if(currentTouchMode == MoYingRect_Corner&&currentTouchIndex == 1)
    {
    
        return true;
    }
    else if(currentTouchMode == MoYingRect_Center)
    {
        Vector2 tempLastPos(pos);
        pos += (nowPoint-prePoint);
//        printf("nowPoint.x:%f,y:%f\n",nowPoint.x,nowPoint.y);
        
  //              printf("prePoint:%f,y:%f\n",prePoint.x,prePoint.y);
     //   if(limitRect.containsPoint(pos))
        {
            currentNode->setPosition(pos.x,pos.y);
        }
       // else
        {
//            if(pos-tempLastPos)
//            {
//                
//            }
//            limitRect.
        }
     
        
        return true;
    }
    return false;
    
}





