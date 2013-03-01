package jp.co.taosoftware.android.riskfinder.activity;

import jp.co.taosoftware.android.riskfinder.R;
import jp.co.taosoftware.android.riskfinder.activity.base.BaseFragmentActivity;
import jp.co.taosoftware.android.riskfinder.preference.Setting;
import jp.co.taosoftware.android.riskfinder.util.Utils;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//起動の場合、プリファレンスを作成
		Setting.init(getApplicationContext());
		
		setContentView(R.layout.activity_main);
	}

	public void onClickButton(View view) {
		Intent intent = new Intent(this, BaseFragmentActivity.class);
		if (view.getId() == R.id.database_check_button){
			intent.putExtra(Utils.EXTRA_SHOW_FRAGMENT, R.layout.database_export_app);
		} else if(view.getId() == R.id.brawsing_check_button){
			intent.putExtra(Utils.EXTRA_SHOW_FRAGMENT, R.layout.brawsable_app);
		} 
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
