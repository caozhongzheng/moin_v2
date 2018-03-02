package com.moinapp.wuliao.commons.net;


import android.os.Bundle;

import com.moinapp.wuliao.modules.update.model.App;

public interface UpdateListener extends Listener{

	public void getUpdateSucc(App app);
}
