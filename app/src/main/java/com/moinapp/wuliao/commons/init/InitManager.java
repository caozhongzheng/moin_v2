package com.moinapp.wuliao.commons.init;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;

import com.moinapp.wuliao.commons.ApplicationContext;
import com.moinapp.wuliao.commons.db.IDataTable;
import com.moinapp.wuliao.commons.info.ClientInfo;
import com.moinapp.wuliao.commons.log.ILogger;
import com.moinapp.wuliao.commons.log.LoggerFactory;
import com.moinapp.wuliao.commons.moduleframework.IModule;
import com.moinapp.wuliao.commons.moduleframework.ModuleContainer;
import com.moinapp.wuliao.commons.mydownloadmanager.MyDownloadModule;
import com.moinapp.wuliao.commons.preference.CommonsPreference;
import com.moinapp.wuliao.commons.receiver.LiveReceiver;
import com.moinapp.wuliao.commons.service.BackgroundService;
import com.moinapp.wuliao.commons.system.SystemFacadeFactory;
import com.moinapp.wuliao.modules.domainupdate.DomainModule;
import com.moinapp.wuliao.modules.feedback.FeedbackModule;
import com.moinapp.wuliao.modules.ipresource.IPResourceModule;
import com.moinapp.wuliao.modules.login.LoginModule;
import com.moinapp.wuliao.modules.push.PushModule;
import com.moinapp.wuliao.modules.regularupdate.RegularUpdateModule;
import com.moinapp.wuliao.modules.stat.StatModule;
import com.moinapp.wuliao.modules.update.UpdateModule;
import com.moinapp.wuliao.modules.wowo.WowoModule;
import com.moinapp.wuliao.utils.CollectionUtils;
import com.moinapp.wuliao.utils.EmoticonsUtils;
import com.moinapp.wuliao.utils.FileUtil;
import com.moinapp.wuliao.utils.PackageUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class InitManager {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final ILogger MyLog = LoggerFactory.getLogger("init");
	
    public static final String TRUSTSTORE_FILE_NAME = "truststore.bks";
    public static final String TABLE_VERSION_PREFIX = "table_version_";
	// ===========================================================
	// Fields
	// ===========================================================
	private ModuleContainer mModules;
	private InitPreference mPreference;
    private Map<IModule, Boolean> mModuleDefaults;
	private Context mContext;

	// ===========================================================
	// Constructors
	// ===========================================================
	public InitManager(Context context) {
		mContext = context;
		ApplicationContext.setContext(context);

		EmoticonsUtils.initEmoticonsDB(ApplicationContext.getContext());
		mPreference = InitPreference.getInstance();
		mModules = ModuleContainer.getInstance();	
		mModuleDefaults = new LinkedHashMap<IModule, Boolean>();
	}
	
	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================
    public void registerModules() {
        // 前几个为StoreMainActivity 下的 Tab，顺序固定为应用 主题 壁纸
		addModule(new DomainModule(),true); //必须在所有联网之前初始化
		addModule(new UpdateModule(), true);
		addModule(new RegularUpdateModule(), true);
		addModule(new FeedbackModule(), true);
		addModule(new StatModule(), true);

		addModule(new PushModule());

		addModule(new MyDownloadModule(), true);
		addModule(new LoginModule(), true);

		addModule(new IPResourceModule(), true);
		addModule(new WowoModule(), true);
        overrideModuleDefaults();
        
        dump();
    }

	private void overrideModuleDefaults() {
		Map<String, Boolean> overrides = ClientInfo.overrideModuleDefaults();
        if (overrides != null){
        	for (Entry<String, Boolean> entry : overrides.entrySet()){
        		String moduleName = entry.getKey();
        		IModule module = mModules.getModuleByName(moduleName);
        		boolean enabled = entry.getValue();
        		mModuleDefaults.put(module, enabled);
        	}
        }
	}
    
    private void addModule(IModule module) {
    	addModule(module, false);
    }
    private void addModule(IModule module, boolean enabled){
        mModuleDefaults.put(module, enabled);
        mModules.addModule(module);
    }
	
    public void init() {
//			checkVersionUpgrade();
//			registerTables();
			initModules();
			
			registerReceiver();
			startBackgroundService();
//		}
    }
    
    private void registerTables() {
		List<IDataTable> tables = getAllTables();

		for (IDataTable table : tables) {
			String tableName = table.getName();
			mPreference.setInt(TABLE_VERSION_PREFIX + tableName,
					table.getVersion());
		}
	}

	private List<IDataTable> getAllTables() {
		Collection<IModule> modules = ModuleContainer.getInstance().getModules();
		List<IDataTable> tables = new ArrayList<IDataTable>();
		for (IModule module : modules) {
			List<IDataTable> list = module.getTables();
			if (list != null && !list.isEmpty()){	
				tables.addAll(list);
			}
		}
		return tables;
	}

	private void initModules() {
		{
//			copyTrustStoreFile();
			for (Entry<IModule, Boolean> entry : mModuleDefaults.entrySet()) {
				IModule module = entry.getKey();
				boolean enabled = entry.getValue();
//				module.onAppFirstInit(enabled);
				initModule(module, enabled);
			}
		}

	}

	// 覆盖安装这个需要继续引用么
	private void checkVersionUpgrade() {
		int ver = mPreference.getCurrentVersion();
		long lastInstallTime = mPreference.getLastInstallTime();
		long currentInstallTime = PackageUtils.getLastUpdateTime(mContext);
		if (ver != ClientInfo.getEditionId()
				|| (currentInstallTime > 0 && lastInstallTime != currentInstallTime)) {
//        	ActivePreference.getInstance().setOverrideInstall(true);//设置覆盖安装 标志
        	mPreference.setLastVersion(ver);
        	mPreference.setCurrentVersion(ClientInfo.getEditionId());
        	mPreference.setLastInstallTime(currentInstallTime);
        	
        	//升级后，重置渠道号，新渠道号从新包里获取
        	CommonsPreference pref = CommonsPreference.getInstance();
        	pref.setChannelId("");
        	
        	//升级后，需要重新走一下激活
        	pref.setForegroundActiveSuccess(false);
        	pref.setNeedForegroundActive(true);

			ClientInfo.onUpgrade(ver);
        }else{
//        	ActivePreference.getInstance().setOverrideInstall(false);//设置覆盖安装 标志
        }
	}
	
	private void startBackgroundService() {
		Context context = ApplicationContext.getContext();
        Intent i = new Intent(context,BackgroundService.class);
        i.setAction(BackgroundService.IMMEDIATE_PERIOD_CHECK_ACTION);
        context.startService(i);
	}

	private void initModule(IModule module, boolean enabled) {
		try {
			long start = SystemFacadeFactory.getSystem().currentTimeMillis();
			module.setEnabled(enabled);
			long end = SystemFacadeFactory.getSystem().currentTimeMillis();
			MyLog.i("Init: module=" + module.getName() + ", enabled=" + enabled
					+ ", time=" + (end - start));
		} catch (Exception e) {
			MyLog.e(e);
		}
    }


    private void copyTrustStoreFile() {
    	InputStream input = null;
        try {
            Context context = ApplicationContext.getContext();
            input = context.getAssets().open(TRUSTSTORE_FILE_NAME);
        	String outFileName = context.getFilesDir() + "/" + TRUSTSTORE_FILE_NAME;
        	FileUtil.writeStreamToFile(outFileName, input);
        } catch (Exception e) {
            MyLog.e(e);
        } finally {
        	FileUtil.closeStream(input);
        }
    }
    
    private void dump(){
    	int size = mModules.getModules().size();
		MyLog.d("InitManagerNew size:" + size + " mModules:" + mModules);
		int i = 0;
		for (IModule module : mModules.getModules()) {
			List<IDataTable> tables = module.getTables();
			MyLog.d("InitManagerNew i:" + i + " module.name:"
					+ module.getName() + " tables:" + tables);
			if (CollectionUtils.isNotEmpty(tables)) {
				for (IDataTable iDataTable : tables) {
					MyLog.d("    InitManagerNew  iDataTable.name:"
							+ iDataTable.getName());
				}
			}
			i++;
		}
    }

	public void upgradeDb(SQLiteDatabase db, int oldVersion, int newVersion) {
		List<IDataTable> tables = getAllTables();
		
		for (IDataTable table : tables) {
			String tableName = table.getName();
			int oldTableVersion = 2;//mPreference.getInt(TABLE_VERSION_PREFIX+tableName);
			table.upgrade(db, oldTableVersion, table.getVersion());
		}
	}
	
	private void registerReceiver(){
		LiveReceiver receiver = new LiveReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(LiveReceiver.ACTION_REGULAR_UPDATE);
		filter.addAction(LiveReceiver.ACTION_SILENT_INSTALL);

		mContext.registerReceiver(receiver, filter);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
