package jp.co.taosoftware.android.riskfinder.util;

import java.util.List;

import jp.co.taosoftware.android.riskfinder.provider.RiskFinderDBAccessor;
import jp.co.taosoftware.android.riskfinder.provider.RiskFinderData;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class PackageInfosInsertUtil {

	/**
	 * インストール済みアプリ情報をDBにインサートするメソッド
	 */
	public static void dbInsert(Context context) {

		// PackageManagerの取得
		PackageManager packageManager = context.getPackageManager();
		List<ApplicationInfo> apkList = packageManager
				.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);

		insertPackageInfo(context, apkList);
	}

	/**
	 * APKの情報を取得し、DBに追加する
	 * 
	 * @param apkList
	 *            追加対象のデータのリスト
	 */
	private static void insertPackageInfo(Context context, List<ApplicationInfo> apkList) {

		//アプリ数分ループする
		for (ApplicationInfo appInfo : apkList) {

			RiskFinderData data = new RiskFinderData();

			// DBに格納する初期値を取得
			data = Utils.setAppInfo(context, appInfo);

			// DBインサート
			RiskFinderDBAccessor.insert(context, data);

		}
	}

}
