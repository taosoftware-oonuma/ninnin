package jp.co.taosoftware.android.riskfinder.activity;

import jp.co.taosoftware.android.riskfinder.adapter.ApkListAdapter;
import jp.co.taosoftware.android.riskfinder.preference.Setting;
import jp.co.taosoftware.android.riskfinder.provider.RiskFinderDB;
import jp.co.taosoftware.android.riskfinder.util.PackageInfosInsertUtil;
import jp.co.taosoftware.android.riskfinder.util.Utils;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DatabaseExportAppListFragment extends ListFragment implements LoaderCallbacks<Cursor> {

	// リスト表示に使用するAdapter
	protected CursorAdapter mAdapter;

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// ローダーを破棄する。ローダーひとつしか生成されていないので0でいいはず。
		getLoaderManager().destroyLoader(0);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		// Progressバーの含まれるViewをSupportV4のListFragment.class内で定義されているIDを直接指定して取得する。
		// 【注意】このIDが今後変更される可能性があるので注意。
		LinearLayout pframe = (LinearLayout) v.findViewById(0x00ff0002);
		TextView loading = new TextView(getActivity());
		loading.setText("loading");
		pframe.addView(loading, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT));

		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// ListFragmentの機能を利用してListが空の際に表示するメッセージを設定する。
		setEmptyText("No data");
		getListView().setScrollingCacheEnabled(false);
		// ListAdapterを生成
		mAdapter = new ApkListAdapter(getActivity());
		setListAdapter(mAdapter);
		// 長押しのメニューを設定
		registerForContextMenu(getListView());

		// リストデータが生成されるまでListFragmentが提供するプログレスバーを表示する
		setListShown(false);

		// Loaderに渡すパラメータを生成。
		Bundle loaderArgs = createLoaderArgs();

		// Loaderを初期化してデータをバックグラウンドで読み込む。
		// ローダーはインスタン化せずに使用し、管理はLoaderManagerに任せる。
		// このメソッドは内部でonCreateLoaderを呼び出し、第一、第二引数がそのまま渡される。
		// 引数：Loaderを識別するID（1つのLoaderしか使わないので0を指定）、Loaderに渡すパラメータ、Loaderのコールバック
		getLoaderManager().initLoader(0, loaderArgs, this);
	}


	/**
	 * リストを長押ししたときの処理
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/**
	 * リストを長押ししたときのメニューを選択したときの処理
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return false;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
	}

	/********************************
	 * Loader
	 ********************************/
	/**
	 * ローダーを生成するタイミングで呼び出されるメソッド idで指定したLoaderを生成して返す
	 */
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// CursorLoaderではコンストラクタでQueryを指定して取得したデータのLoaderを生成する。
		return new CursorLoader(getActivity(), RiskFinderDB.PackageInfos.CONTENT_URI, // URI
				null, // projection
				args.getString(Utils.LOADER_ARGS_KEY_STRING_SELECTION), // selection
				args.getStringArray(Utils.LOADER_ARGS_KEY_STRING_SELECTION_ARGS), // selection args
				null) { // order

			/**
			 * バックグランドでクエリを投げてDBからデータを取得する処理 初回起動時のみDBが空なのでInsertする
			 */
			@Override
			public Cursor loadInBackground() {
				// アプリリストをすべて読み込む。
				// それ以降は、Receiverでアプリが追加や削除されたタイミングでDBを更新する
				Setting setting = new Setting();
				if (setting.getFirstLaunch(getActivity())) {
					PackageInfosInsertUtil.dbInsert(getActivity());
					setting.setFirstLaunch(getActivity());
				}
				// コンストラクタで指定したQueryが実行されてCursorが返される。
				return super.loadInBackground();
			}
		};
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		// アダプターにCursorを渡してListに反映させる。
		mAdapter.swapCursor(cursor);

		if (isResumed()) {
			// onResumeとonPauseの（ユーザに画面が表示されている）間にLoadが完了した場合は
			// Progressを消す際にフワッと消えるアニメーションを表示する。
			setListShown(true);
		} else {
			// onResume前（ユーザにまだ画面が表示されていない段階）にLoadが完了した場合は
			// どうせ画面が表示されていないのでProgressを消す際にアニメーションを使用しない。
			setListShownNoAnimation(true);
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// Adapterのデータを無効化する->ListがNo Dataになる。
		mAdapter.swapCursor(null);
	}

	/**
	 * Loaderの初期化に必要なパラメータを生成するメソッド
	 * 
	 * @return Loaderのパラメータとして渡すQueryのSelect句を格納したBundle
	 */
	protected Bundle createLoaderArgs() {
		Bundle args = new Bundle();
		String selection = RiskFinderDB.PackageInfos.KEY_DATABASE_STATUS + "=?";
		String[] selectionArgs = new String[] { String.valueOf(Utils.STATUS_EXPORT) };
		args.putString(Utils.LOADER_ARGS_KEY_STRING_SELECTION, selection);
		args.putStringArray(Utils.LOADER_ARGS_KEY_STRING_SELECTION_ARGS, selectionArgs);
		return args;
	}

	/**
	 * リストをクリックした時
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
	}

}
