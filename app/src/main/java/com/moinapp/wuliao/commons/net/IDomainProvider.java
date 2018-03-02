package com.moinapp.wuliao.commons.net;

public interface IDomainProvider {

    public DomainInfo getDomainInfo(int type);

    public void switchToNext(int type);
    
}
