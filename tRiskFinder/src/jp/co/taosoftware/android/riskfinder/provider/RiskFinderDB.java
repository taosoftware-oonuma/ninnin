package jp.co.taosoftware.android.riskfinder.provider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * コンテントプロバイダ用定数クラス
 */
public final class RiskFinderDB {

    // パッケージ
    public static final String AUTHORITY    = "jp.co.taosoftware.android.riskfinder.provider.riskfindercontentprovider";

    /**
     * データベーステーブルの定義（アプリ情報）
     */
    public static final class PackageInfos implements BaseColumns {

        // URIに利用する文字列を定義
        public static final String PATH_ITEM    = "packageinfos";
        public static final String PATH_ITEM_ID = "packageinfos/#";

        // コンテントプロバイダURI
        public static final Uri    CONTENT_URI             = Uri.parse("content://" + AUTHORITY
                                                                   + "/" + PATH_ITEM);

        // ディレクトリのMIMEタイプ
        public static final String CONTENT_TYPE            = "vnd.android.cursor.dir/vnd.jp.co.taosoftware.android.riskfinder.provider/packageinfos";
        // 単一のMIMEタイプ
        public static final String CONTENT_ITEM_TYPE       = "vnd.android.cursor.item/vnd.jp.co.taosoftware.android.riskfinder.provider/packageinfos";

        // テーブル名
        public static final String TABLE_NAME              = "packageinfos";

        // カラム名
        public static final String KEY_UID                    = "uid";
        public static final String KEY_APP_NAME               = "app_name";               // アプリ名
        public static final String KEY_PACKAGE_NAME           = "package_name";           // パッケージ名
        public static final String KEY_DATABASE_STATUS        = "database_status";        // ステータス
        public static final String KEY_BRAWSABLE_STATUS       = "brawsable_status";       // ステータス

    }
}
