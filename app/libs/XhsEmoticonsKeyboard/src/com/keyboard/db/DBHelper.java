package com.keyboard.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.keyboard.bean.EmoticonBean;
import com.keyboard.bean.EmoticonSetBean;
import com.keyboard.utils.DefEmoticons;

import java.util.ArrayList;

public class DBHelper {

    /**
     * 0-->1 add emoticons,emoticonset
     */
    private static final int VERSION = 1;
    private static final String TAG = "dbh";

    private static final String DATABASE_NAME = "moinemoticons.db";
    private static final String TABLE_NAME_EMOTICONS = "emoticons";
    private static final String TABLE_NAME_EMOTICONSET = "emoticonset";

    private SQLiteDatabase db;
    private DBOpenHelper dbOpenHelper;

    public DBHelper(Context context) {
        this.dbOpenHelper = new DBOpenHelper(context);
        establishDb();
    }

    private void establishDb() {
        if (this.db == null) {
            this.db = this.dbOpenHelper.getWritableDatabase();
        }
    }

    public ContentValues createEmoticonSetContentValues(EmoticonBean bean, String beanSetName) {
        if (bean == null) {
            return null;
        }
        return buildEmoticonValues(bean, beanSetName);
    }

    public ContentValues buildEmoticonValues(EmoticonBean bean, String beanSetName) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonColumns.ID, bean.getId());
        values.put(TableColumns.EmoticonColumns.PARENT_ID, nullToEmpty(bean.getParentId()));
        values.put(TableColumns.EmoticonColumns.TAG, nullToEmpty(bean.getTags()));
        values.put(TableColumns.EmoticonColumns.USESTAT, bean.getUseStat());
        values.put(TableColumns.EmoticonColumns.GIFURI, nullToEmpty(bean.getGifUri()));
        values.put(TableColumns.EmoticonColumns.GIFURL, nullToEmpty(bean.getGifUrl()));
        values.put(TableColumns.EmoticonColumns.ICONURL, nullToEmpty(bean.getIconUrl()));
        values.put(TableColumns.EmoticonColumns.EVENTTYPE, bean.getEventType());
        values.put(TableColumns.EmoticonColumns.CONTENT, nullToEmpty(bean.getContent()));
        values.put(TableColumns.EmoticonColumns.ICONURI, nullToEmpty(bean.getIconUri()));
        values.put(TableColumns.EmoticonColumns.EMOTICONSET_NAME, nullToEmpty(beanSetName));
        return values;
    }

    private String nullToEmpty(String str) {
        if (str == null) {
            return "";
        } else {
            return str;
        }
    }

    public long insertEmoticonBean(EmoticonBean bean, String beanSetName) {

        long result = -1;
        if (bean == null || db == null) {
            return result;
        }
        ContentValues values = createEmoticonSetContentValues(bean, beanSetName);
        try {
            result = db.insert(TABLE_NAME_EMOTICONS, null, values);
        } catch (SQLiteConstraintException e) {
        }
        return result;
    }

    public long insertEmoticonBeans(ContentValues[] values) {
        db.beginTransaction();
        int insertSuccessCount = values.length;
        try {
            for (ContentValues cv : values) {
                if (db.insert(TABLE_NAME_EMOTICONS, null, cv) < 0) {
                    insertSuccessCount--;
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteConstraintException e) {
        } catch (Exception e) {
        } finally {
            db.endTransaction();
        }
        return insertSuccessCount;
    }

    public long insertEmoticonSet(EmoticonSetBean bean) {
        long result = -1;
        if (bean == null || db == null || TextUtils.isEmpty(bean.getName())) {
            return result;
        }

        bean.setItemPadding(15);
        bean.setVerticalSpacing(5);
        ContentValues values = buildEmoticonSetValues(bean);

        result = db.insert(TABLE_NAME_EMOTICONSET, null, values);

        ArrayList<EmoticonBean> emoticonList = bean.getEmoticonList();
        long count = 0;
        if (emoticonList != null) {
            String emoticonSetname = bean.getName();
            ContentValues[] contentValues = new ContentValues[emoticonList.size()];
            for (int i = 0; i < emoticonList.size(); i++) {
                contentValues[i] = createEmoticonSetContentValues(emoticonList.get(i), emoticonSetname);
            }
            count = insertEmoticonBeans(contentValues);
        }
        return result * count;
    }

    private ContentValues buildEmoticonSetValues(EmoticonSetBean bean) {
        ContentValues values = new ContentValues();
        values.put(TableColumns.EmoticonSetColumns.ID, nullToEmpty(bean.getId()));
        values.put(TableColumns.EmoticonSetColumns.UID, nullToEmpty(bean.getUid()));
        values.put(TableColumns.EmoticonSetColumns.ORDER, bean.getOrder());
        values.put(TableColumns.EmoticonSetColumns.NAME, nullToEmpty(bean.getName()));
        values.put(TableColumns.EmoticonSetColumns.LINE, bean.getLine());
        values.put(TableColumns.EmoticonSetColumns.ROW, bean.getRow());
        values.put(TableColumns.EmoticonSetColumns.ICONURI, bean.getIconUri());
        values.put(TableColumns.EmoticonSetColumns.ICONURL, bean.getIconUrl());
        values.put(TableColumns.EmoticonSetColumns.ICONNAME, bean.getIconName());
        values.put(TableColumns.EmoticonSetColumns.ISSHOWDELBTN, bean.isShowDelBtn() ? 1 : 0);
        values.put(TableColumns.EmoticonSetColumns.ITEMPADDING, bean.getItemPadding());
        values.put(TableColumns.EmoticonSetColumns.HORIZONTALSPACING, bean.getHorizontalSpacing());
        values.put(TableColumns.EmoticonSetColumns.VERTICALSPACING, bean.getVerticalSpacing());
        values.put(TableColumns.EmoticonSetColumns.UPDATETIME, bean.getUpdateTime());
        return values;
    }

    public int deleteEmoticonSet(String EmoticonSetId) {
        int result = db.delete(TABLE_NAME_EMOTICONSET, TableColumns.EmoticonSetColumns.ID+"=?", new String[]{EmoticonSetId});
        int result2 = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.PARENT_ID + "=?", new String[]{EmoticonSetId});

        return result * result2;
    }

    public int deleteEmoticon(String EmoticonId) {
        int result = db.delete(TABLE_NAME_EMOTICONS, TableColumns.EmoticonColumns.ID + "=?", new String[]{EmoticonId});

        return result;
    }

    public EmoticonBean queryEmoticonBean(String contentStr) {
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.CONTENT + " = '" + contentStr + "'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                return buildEmoticonBeanFromCursor(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public EmoticonBean queryEmoticonBeanByID(String id) {
        String sql = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.ID + " = '" + id + "'";
        Cursor cursor = db.rawQuery(sql, null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                return buildEmoticonBeanFromCursor(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    private EmoticonBean buildEmoticonBeanFromCursor(Cursor cursor) {
        if(cursor == null)
            return null;
        String id = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ID));
        int useStat = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonColumns.USESTAT));
        String pid = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.PARENT_ID));
        long eventType = cursor.getLong(cursor.getColumnIndex(TableColumns.EmoticonColumns.EVENTTYPE));
        String content = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.CONTENT));
        String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICONURI));
        String iconUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.ICONURL));
        String gifUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.GIFURI));
        String gifUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.GIFURL));
        String tag = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonColumns.TAG));
        return new EmoticonBean(id, pid, iconUri, iconUrl, gifUri, gifUrl, tag, useStat, eventType, content);
    }

    public ArrayList<EmoticonBean> queryEmoticonBeanList(String sql) {
        Cursor cursor = db.rawQuery(sql, null);
        try {
            int count = cursor.getCount();
            android.util.Log.i(TAG, "EmoticonBean count="+count);
            ArrayList<EmoticonBean> beanList = new ArrayList<EmoticonBean>();
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    EmoticonBean emoticonBeanTmp = buildEmoticonBeanFromCursor(cursor);
                    beanList.add(emoticonBeanTmp);
                    android.util.Log.i(TAG, i+":="+emoticonBeanTmp.toString());
                    cursor.moveToNext();
                }
                return beanList;
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public ArrayList<EmoticonBean> queryAllEmoticonBeans() {
        String sql = "select * from " + TABLE_NAME_EMOTICONS;
        return queryEmoticonBeanList(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSetByID(String uid, String ... setIDs) {
        if(setIDs == null || setIDs.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and ";
        for(int i = 0 ;i < setIDs.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.ID + " = '" + setIDs[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(String uid, String ... setNames) {
        if(setNames == null || setNames.length == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' and ";
        for(int i = 0 ;i < setNames.length ; i++){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + setNames[i] + "' ";
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(ArrayList<String> setNameList) {
        if(setNameList == null || setNameList.size() == 0){
            return null;
        }
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " ;
        int i = 0 ;
        for(String name : setNameList){
            if(i != 0){
                sql = sql + " or ";
            }
            sql = sql + TableColumns.EmoticonSetColumns.NAME + " = '" + name + "' ";
            i++;
        }
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryAllEmoticonSet(String uid) {
        String sql = "select * from " + TABLE_NAME_EMOTICONSET + " where " +
                TableColumns.EmoticonSetColumns.UID + " = '" + uid + "' " +
                "or " + TableColumns.EmoticonSetColumns.UID + " = '" + DefEmoticons.DEFAULT_EMOJISET_UID + "' ";
        return queryEmoticonSet(sql);
    }

    public ArrayList<EmoticonSetBean> queryEmoticonSet(String sql) {
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(sql, null);
            int count = cursor.getCount();
            android.util.Log.i(TAG, "EmoticonSetBean count="+count);
            ArrayList<EmoticonSetBean> beanList = new ArrayList<EmoticonSetBean>();
            if (count > 0) {
                cursor.moveToFirst();
                for (int i = 0; i < count; i++) {
                    String id = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ID));
                    String name = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.NAME));
                    int line = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.LINE));
                    int order = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ORDER));
                    int row = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ROW));
                    String iconUri = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONURI));
                    String iconUrl = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONURL));
                    String iconName = cursor.getString(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ICONNAME));
                    boolean isshowdelbtn = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ISSHOWDELBTN)) == 1 ? true : false;
                    int itempadding = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.ITEMPADDING));
                    int horizontalspacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.HORIZONTALSPACING));
                    int verticalSpacing = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.VERTICALSPACING));
                    int updatetime = cursor.getInt(cursor.getColumnIndex(TableColumns.EmoticonSetColumns.UPDATETIME));
                    ArrayList<EmoticonBean> emoticonList = null;
                    android.util.Log.i(TAG, "EmoticonSetBean id= "+id);
                    if (!TextUtils.isEmpty(id)) {
                        String sqlGetEmoticonBean = "select * from " + TABLE_NAME_EMOTICONS + " where " + TableColumns.EmoticonColumns.PARENT_ID + " = '" + id + "'";
                        emoticonList = queryEmoticonBeanList(sqlGetEmoticonBean);
                    }

                    int pageCount = 0;
                    if (emoticonList != null) {
                        int del = isshowdelbtn ? 1 : 0;
                        int everyPageMaxSum = row * line - del;
                        pageCount = (int) Math.ceil((double) emoticonList.size() / everyPageMaxSum);
                    }

                    EmoticonSetBean bean = new EmoticonSetBean(id, order, name, line, row, iconUri, iconUrl, iconName, isshowdelbtn, itempadding, horizontalspacing, verticalSpacing, updatetime, emoticonList);
                    beanList.add(bean);
                    cursor.moveToNext();
                }
                return beanList;
            }
        }
        catch (SQLiteException e){

        }
        finally {
            cursor.close();
        }
        return null;
    }


    public void cleanup() {
        if (this.db != null) {
            this.db.close();
            this.db = null;
        }
    }

    private static class DBOpenHelper extends SQLiteOpenHelper {

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, VERSION);
        }

        private static void createEmoticonsTable(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONS + " ( " +
                    TableColumns.EmoticonColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonColumns.ID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.PARENT_ID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.EVENTTYPE + " INTEGER, " +
                    TableColumns.EmoticonColumns.CONTENT + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.ICONURI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.ICONURL + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.GIFURI + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.GIFURL + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.TAG + " TEXT NOT NULL, " +
                    TableColumns.EmoticonColumns.USESTAT + " INTEGER, " +
                    TableColumns.EmoticonColumns.EMOTICONSET_NAME + " TEXT NOT NULL);");


            db.execSQL("CREATE TABLE " + TABLE_NAME_EMOTICONSET + " ( " +
                    TableColumns.EmoticonSetColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TableColumns.EmoticonSetColumns.ID + " TEXT NOT NULL UNIQUE, " +
                    TableColumns.EmoticonSetColumns.UID + " TEXT NOT NULL, " +
                    TableColumns.EmoticonSetColumns.ORDER + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.NAME + " TEXT NOT NULL, " +
                    TableColumns.EmoticonSetColumns.LINE + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ROW + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.ICONURI + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ICONURL + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ICONNAME + " TEXT, " +
                    TableColumns.EmoticonSetColumns.ISSHOWDELBTN + " BOOLEAN, " +
                    TableColumns.EmoticonSetColumns.ITEMPADDING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.HORIZONTALSPACING + " INTEGER, " +
                    TableColumns.EmoticonSetColumns.VERTICALSPACING + " TEXT, " +
                    TableColumns.EmoticonSetColumns.UPDATETIME + " INTEGER);");
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            onUpgrade(sqLiteDatabase, 0, VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int currentVersion) {
            for (int version = oldVersion + 1; version <= currentVersion; version++) {
                upgradeTo(sqLiteDatabase, version);
            }
        }

        private void upgradeTo(SQLiteDatabase db, int version) {
            switch (version) {
                case 1:
                    createEmoticonsTable(db);
                    break;
                default:
                    throw new IllegalStateException("Don't know how to upgrade to " + version);
            }
        }
    }
}