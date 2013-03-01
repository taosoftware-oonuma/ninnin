package jp.co.taosoftware.android.riskfinder.provider;

import jp.co.taosoftware.android.riskfinder.provider.RiskFinderDB.PackageInfos;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RiskFinderDBHelper extends SQLiteOpenHelper {

	private static String DATABASE_NAME = "riskfinder.db";
	private static int DATABASE_VERSION = 1;
	
	// テーブル作成SQL文
	private static final String CREATE_TABLE_PACKAGE_INFOS =
		"create table " + PackageInfos.TABLE_NAME + " ( "
		+ PackageInfos._ID + " integer primary key autoincrement,"
		+ PackageInfos.KEY_UID + " integer,"
		+ PackageInfos.KEY_APP_NAME + " text,"
		+ PackageInfos.KEY_PACKAGE_NAME + " text unique,"
		+ PackageInfos.KEY_DATABASE_STATUS + " integer,"
		+ PackageInfos.KEY_BRAWSABLE_STATUS + " integer"
		+ " );";

	public RiskFinderDBHelper(Context context) {
		// SQLiteOpenHelperのコンストラクタを呼ぶ
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// テーブル作成
		db.execSQL(CREATE_TABLE_PACKAGE_INFOS);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
