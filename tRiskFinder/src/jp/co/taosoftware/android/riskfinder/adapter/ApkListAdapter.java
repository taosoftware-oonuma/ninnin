package jp.co.taosoftware.android.riskfinder.adapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import jp.co.taosoftware.android.riskfinder.R;
import jp.co.taosoftware.android.riskfinder.provider.RiskFinderDB;
import jp.co.taosoftware.android.riskfinder.util.Utils;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * メイン画面のList情報をバインドするアダプター
 */
public class ApkListAdapter extends ResourceCursorAdapter {

    // カラムインデックス
	private int mAppNameIdx;
	private int mAppPackageNameIdx;

    /**
     * コンストラクタ
     */
    public ApkListAdapter(Context context){
    	 super(context, R.layout.app_list_item, null, false);
    	 mContext = context;
    }

    @Override
    protected void onContentChanged() {
    	super.onContentChanged();
    }
    
    /**
     * Adapterで使用するCursorが変更されるタイミングで呼ばれるメソッド
     */
    @Override
    public Cursor swapCursor(Cursor newCursor) {
        // カーソルのIndexをカーソルがスワップされたタイミングで初期化する
        initColumnIndex(newCursor);
        return super.swapCursor(newCursor);
    }
    
    /**
     * カーソルのIndexを初期化する
     * @param cur nullを指定した場合は何もしない。
     */
    private void initColumnIndex(Cursor cur){
        if(cur != null){
        	 // 高速化のためにIDを取得しておく
            mAppNameIdx = cur.getColumnIndexOrThrow(RiskFinderDB.PackageInfos.KEY_APP_NAME);
            mAppPackageNameIdx = cur.getColumnIndexOrThrow(RiskFinderDB.PackageInfos.KEY_PACKAGE_NAME);
        }
    }
    
    
    /**
     * 受け取ったカーソルの情報をバインドするメソッド
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        // アプリ名をビューにセット
        holder.appNameView.setText(cursor.getString(mAppNameIdx));

        // パッケージ名
        String packageName = cursor.getString(mAppPackageNameIdx);
        /**********************************
         * アイコン表示
         */
        Drawable drawable = ImageCache.getImage(packageName);
        if(drawable == null){
            drawable = Utils.getDrawableIcon(context, packageName);
            ImageCache.setImage(packageName,drawable);
        }
        holder.iconView.setImageDrawable(drawable);

    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = super.newView(context, cursor, parent);
        
        ViewHolder holder = new ViewHolder();
        holder.appNameView = (TextView) view.findViewById(R.id.toptext);
        holder.appNameView.setTypeface(Typeface.DEFAULT_BOLD);
        holder.iconView = (ImageView) view.findViewById(R.id.icon);
        view.setTag(holder);
        
        return view;
    }

    private final static class ViewHolder {

        public TextView  appNameView;
        public ImageView iconView;
    }
    
    /**
     * アイコンイメージ(Drawable)をcashする
     * SoftReferenceを使う
     * @author taosoftware.okayama
     *
     */
    public static class ImageCache {
        private static HashMap<String, SoftReference<Drawable>> cache = new HashMap<String, SoftReference<Drawable>>();

        /**
         * アプリのパッケージ名をキーに取得
         * @param key
         * @return
         */
        public static Drawable getImage(String key) {
            SoftReference<Drawable> ref = cache.get(key);
            if (ref != null) {
                return ref.get();
            }
            return null;
        }

        /**
         * アプリのパッケージ名をキーに、アイコンイメージをimageに
         * @param key
         * @param image
         */
        public static void setImage(String key, Drawable image) {
        	if( ! cache.containsKey(key) ){
                cache.put(key, new SoftReference<Drawable>(image));
        	}
        }
    }
}

