package jp.co.taosoftware.android.riskfinder.provider;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class RiskFinderDBAccessor {
	
	/*******************************************
	 * Insert
	 ******************************************* 
	 * SpyCheckerDataクラスで受け取ったデータをコンテントプロバイダに渡す（insert）
	 * 
	 * @param ctx
	 * @param data
	 * @return Uri
	 */
	public static Uri insert(Context ctx, RiskFinderData data) {
		ContentValues values = createContentValues(data);
		return ctx.getContentResolver().insert(RiskFinderDB.PackageInfos.CONTENT_URI, values);
	}

	private static ContentValues createContentValues(RiskFinderData data) {
		ContentValues values = new ContentValues();

		if (data.rowId != 0)
			values.put(RiskFinderDB.PackageInfos._ID, data.rowId);
		if (data.uid != 0)
			values.put(RiskFinderDB.PackageInfos.KEY_UID, data.uid);
		if (data.appName != null)
			values.put(RiskFinderDB.PackageInfos.KEY_APP_NAME, data.appName);
		if (data.packageName != null)
			values.put(RiskFinderDB.PackageInfos.KEY_PACKAGE_NAME, data.packageName);
		if (data.contentProviderStatus != 0)
			values.put(RiskFinderDB.PackageInfos.KEY_DATABASE_STATUS, data.contentProviderStatus);
		if (data.browsableStatus != 0)
			values.put(RiskFinderDB.PackageInfos.KEY_BRAWSABLE_STATUS, data.browsableStatus);
		
		return values;
	}

	public static RiskFinderData createPackageInfosData(Cursor cur){
		if (cur == null){
			return null;
		}
		
		RiskFinderData data = new RiskFinderData();
		data.rowId = cur.getInt(cur.getColumnIndex(RiskFinderDB.PackageInfos._ID));
		data.uid = cur.getInt(cur.getColumnIndex(RiskFinderDB.PackageInfos.KEY_UID));
		data.appName = cur.getString(cur.getColumnIndex(RiskFinderDB.PackageInfos.KEY_APP_NAME));
		data.packageName = cur.getString(cur
				.getColumnIndex(RiskFinderDB.PackageInfos.KEY_PACKAGE_NAME));
		data.contentProviderStatus = cur.getInt(cur
				.getColumnIndex(RiskFinderDB.PackageInfos.KEY_DATABASE_STATUS));
		data.browsableStatus = cur.getInt(cur
				.getColumnIndex(RiskFinderDB.PackageInfos.KEY_BRAWSABLE_STATUS));
		return data;
	}

	public static RiskFinderData getPackageInfosData(Context ctx, long rowId) {
		Cursor cur = queryPackageInfoData(ctx, rowId);
		if (cur != null) {
			cur.moveToFirst();
		} else {
			return null;
		}
		if (cur.getCount() == 0) {
			return null;
		}
		RiskFinderData data = createPackageInfosData(cur);
		cur.close();
		cur = null;
		return data;
	}

	private static Cursor queryPackageInfoData(Context ctx, long uid) {
		Uri contentUri = ContentUris.withAppendedId(
				RiskFinderDB.PackageInfos.CONTENT_URI, uid);
		return ctx.getContentResolver()
				.query(contentUri, null, null,
						null, null);
	}
}
