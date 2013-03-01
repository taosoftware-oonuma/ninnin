package jp.co.taosoftware.android.riskfinder.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * コンテントプロバイダー
 */
public class RiskFinderContentProvider extends ContentProvider {

	// SQLiteOpenHelper
	private SQLiteOpenHelper mOpenHelper;

	// Mapping
	private static final int PACKAGE_INFOS = 1;
	private static final int PACKAGE_INFOS_ID = 2;

	// UriMatcher
	private static final UriMatcher sUriMatcher;
	// UriMatcherに情報をadd
	static {
		sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		sUriMatcher.addURI(RiskFinderDB.AUTHORITY, RiskFinderDB.PackageInfos.PATH_ITEM,
				PACKAGE_INFOS);
		sUriMatcher.addURI(RiskFinderDB.AUTHORITY, RiskFinderDB.PackageInfos.PATH_ITEM_ID,
				PACKAGE_INFOS_ID);
	}

	/**
	 * MIMEタイプの取得
	 */
	@Override
	public String getType(Uri uri) {
		switch (sUriMatcher.match(uri)) {
		case PACKAGE_INFOS:
			return RiskFinderDB.PackageInfos.CONTENT_TYPE;
		case PACKAGE_INFOS_ID:
			return RiskFinderDB.PackageInfos.CONTENT_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("Unknown URI : " + uri);
		}
	}

	/**
	 * SQLiteOpenHelperをインスタンス化
	 */
	@Override
	public boolean onCreate() {
		mOpenHelper = new RiskFinderDBHelper(getContext());
		return true;
	}

	/*******************************************
	 * Query
	 *******************************************/
	@Override
	public Cursor query(Uri uri, String[] projection, String where,
			String[] whereArgs, String sortOrder) {

		String selectId = null;
		Cursor cursor = null;

		try {

			switch (sUriMatcher.match(uri)) {
			case PACKAGE_INFOS_ID:
				selectId = uri.getPathSegments().get(1);
			case PACKAGE_INFOS:
				cursor = queryInfos(selectId, projection, where, whereArgs,
						sortOrder);
				break;
			default:
				break;
			}

			if (cursor != null) {
				cursor.setNotificationUri(getContext().getContentResolver(),
						uri);
			}

			return cursor;

		} catch (Exception e) {
		}
		return null;

	}

	protected Cursor queryInfos(String selectId, String[] projection,
			String where, String[] whereArgs, String sortOrder) {

		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
		qb.setTables(RiskFinderDB.PackageInfos.TABLE_NAME);

		if (!TextUtils.isEmpty(selectId)) {
			qb.appendWhere(RiskFinderDB.PackageInfos._ID + "=" + selectId);
		}

		SQLiteDatabase db = mOpenHelper.getReadableDatabase();
		Cursor cursor = qb.query(db, projection, where, whereArgs, null, null,
				sortOrder);

		return cursor;
	}

	/*******************************************
	 * Insert
	 *******************************************
	 * レコード作製
	 *
	 * @return インサートされた行のURI
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (sUriMatcher.match(uri)) {
		case PACKAGE_INFOS:
			return insertInfos(uri, values);

		case PACKAGE_INFOS_ID:
		default:
			throw new IllegalArgumentException("Unknown Uri : " + uri);
		}

	}

	protected Uri insertInfos(Uri uri, ContentValues values) {
		SQLiteDatabase db = null;
		try {
			db = mOpenHelper.getWritableDatabase();

			long rowId = 0;
			rowId = db.insertOrThrow(RiskFinderDB.PackageInfos.TABLE_NAME, null, values);
			

			if (rowId > 0) {
				// 変更の通知を行ってから、URIを返す
				Uri noteUri = ContentUris.withAppendedId(
						RiskFinderDB.PackageInfos.CONTENT_URI, rowId);

				return noteUri;
			}

		} catch (SQLiteException e) {
		} catch (Exception e) {
		}
		return uri;

	}

	/*******************************************
	 * Update
	 *******************************************
	 * レコード更新
	 *
	 * @return 更新された行の数を返す
	 */
	@Override
	public int update(Uri uri, ContentValues values, String where,
			String[] whereArgs) {

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;

		String id = null;
		switch (sUriMatcher.match(uri)) {
		case PACKAGE_INFOS:
			count = db.update(RiskFinderDB.PackageInfos.TABLE_NAME, values, where,
					whereArgs);
			break;
		case PACKAGE_INFOS_ID:
			// 特定のアプリ情報を1つ更新する
			id = uri.getPathSegments().get(1);
			count = db.update(
					RiskFinderDB.PackageInfos.TABLE_NAME,
					values,
					RiskFinderDB.PackageInfos._ID
							+ "="
							+ id
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri : " + uri);
		}

		/*****************************************************************/
		// リスト表示を再表示する
		getContext().getContentResolver().notifyChange(uri, null);
		/*****************************************************************/
		return count;
	}

	/*******************************************
	 * Delete
	 *******************************************
	 * レコード削除
	 *
	 * @return 削除された行の数を返す
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {

		SQLiteDatabase db = mOpenHelper.getWritableDatabase();
		int count = 0;
		String id = null;
		switch (sUriMatcher.match(uri)) {
		case PACKAGE_INFOS:
			count = db.delete(RiskFinderDB.PackageInfos.TABLE_NAME, where,
					whereArgs);
			break;
		case PACKAGE_INFOS_ID:
			// 特定のアプリ情報を1つ削除する
			id = uri.getPathSegments().get(1);
			count = db.delete(
					RiskFinderDB.PackageInfos.TABLE_NAME,
					RiskFinderDB.PackageInfos._ID
							+ "="
							+ id
							+ (!TextUtils.isEmpty(where) ? " AND (" + where
									+ ")" : ""), whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unknown Uri : " + uri);
		}

		/*****************************************************************/
		// リスト表示を再表示する
		getContext().getContentResolver().notifyChange(uri, null);
		/*****************************************************************/
		return count;
	}

	
}
