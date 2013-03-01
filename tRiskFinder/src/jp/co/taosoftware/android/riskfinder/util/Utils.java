package jp.co.taosoftware.android.riskfinder.util;

import java.io.IOException;
import jp.co.taosoftware.android.riskfinder.provider.RiskFinderData;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.Pair;

public class Utils {
    // ログ出力用タグ
    public static final String TAG = "tRiskFinder";
	public static final String EXTRA_SHOW_FRAGMENT = "extra_show_fragment";
	
	/*暗黙的Intentを受け付けるブラウザブルアプリの場合*/
	public static final int STATUS_BRAWSABLE = 1;
	/*暗黙的Intentを受け付けるブラウザブルアプリ、かつJavaScriptの場合*/
	public static final int STATUS_JAVASCRIPT = 2;

	/*ContentProviderを公開しているアプリの場合*/
	public static final int STATUS_EXPORT = 1;
	
	private static final String MANIFEST_NAME = "AndroidManifest.xml";
    
	public static final String EXTRA_PACKAGE_MANIFEST       = "manifest";
    
	public static final String LOADER_ARGS_KEY_STRING_SELECTION = "loader_args_key_string_selection";
	public static final String LOADER_ARGS_KEY_STRING_SELECTION_ARGS = "loader_args_key_string_selection_args";
	
	public static RiskFinderData setAppInfo(Context context, ApplicationInfo appInfo) {
		RiskFinderData data = new RiskFinderData();
		// PackageManagerの取得
		PackageManager pkgManager = context.getPackageManager();

		data.uid = appInfo.uid;
		// アプリ名
		data.appName = appInfo.loadLabel(pkgManager).toString();
		// パッケージ名
		data.packageName = appInfo.packageName.toString();

		Pair<Integer, Integer> statusPair = checkManifestXmlResource(context, data.packageName);
		data.browsableStatus = statusPair.first;
		data.contentProviderStatus = statusPair.second;
		
		return data;
	}
	

	/**
	 * 
	 * @param context
	 * @param packageName
	 * @return statusPair first:webViewStatus　 second:databaseStatus
	 */
	private static Pair<Integer, Integer> checkManifestXmlResource(Context context, String packageName) {
        XmlResourceParser xml = null;
        // 特定パッケージのContextを取得
        Context targetContext = null;
        Resources targetResources = null;
		try {
			targetContext = context.createPackageContext(packageName,
			                                                    Context.CONTEXT_RESTRICTED);
			// 特定パッケージのResourcesを取得
			targetResources = targetContext.getResources();
			// 特定パッケージのAndroidManifest.xmlデータ取得
			xml = targetContext.getAssets().openXmlResourceParser(MANIFEST_NAME);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        XmlPerserUtil xmlPerser = new XmlPerserUtil();
        int first = xmlPerser.getBrawsableStatus(xml, targetResources);
        int second = xmlPerser.getDatabaseStatus(targetContext, targetResources);
        Pair<Integer, Integer> statusPair = new Pair<Integer, Integer>(first, second);
        
		return statusPair;
	}

	/**
	 * パッケージ名でアプリのアイコンを取得
	 * 
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static Drawable getDrawableIcon(Context context, String packageName) {
		ApplicationInfo appinfo = null;
		PackageManager pm = context.getPackageManager();
		Drawable drawableIcon = null;
		try {
			appinfo = pm.getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
			drawableIcon = appinfo.loadIcon(pm);
		} catch (NameNotFoundException e) {
			return null;
		}

		return drawableIcon;

	}
}
