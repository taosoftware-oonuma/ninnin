package jp.co.taosoftware.android.riskfinder.provider;

public class RiskFinderData {

	/**
	 * PackageInfoテーブルのカラム
	 */
	public long rowId; // _idに対応
	public int uid; // userID
	public String appName; // アプリ名
	public String packageName; // パッケージ名
	public int contentProviderStatus;
	public int browsableStatus;

	public RiskFinderData() {
		super();
		// 初期化
		rowId = 0;
		uid = 0;
		appName = null;
		packageName = null;
		contentProviderStatus = 0;
		browsableStatus = 0;
	}

}
