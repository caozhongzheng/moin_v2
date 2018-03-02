package com.keyboard.db;

import android.provider.BaseColumns;

public final class TableColumns {

    private TableColumns() {}

    public interface EmoticonColumns extends BaseColumns {

        public static final String ID = "id";

        public static final String PARENT_ID = "pid";

        public static final String EVENTTYPE = "eventtype";

        public static final String CONTENT = "content";

        public static final String ICONURI = "iconuri";
        public static final String ICONURL = "iconurl";

        public static final String GIFURI = "gifuri";
        public static final String GIFURL = "gifurl";

        public static final String TAG = "tag";
        public static final String USESTAT = "usestat";

        public static final String EMOTICONSET_NAME = "emoticonset_name";

    }

    public interface EmoticonSetColumns extends BaseColumns {

        public static final String ID = "id";

        public static final String UID = "uid";

        public static final String ORDER = "orderNo";

        public static final String NAME = "name";

        public static final String LINE = "line";

        public static final String ROW = "row";

        public static final String ICONURI = "iconuri";

        public static final String ICONURL = "iconurl";

        public static final String ICONNAME = "iconname";

        public static final String ISSHOWDELBTN = "isshowdelbtn";

        public static final String ITEMPADDING = "itempadding";

        public static final String HORIZONTALSPACING = "horizontalspacing";

        public static final String VERTICALSPACING = "verticalspacing";

        public static final String UPDATETIME = "updatetime";

    }

}
