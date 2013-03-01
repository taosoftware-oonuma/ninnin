package jp.co.taosoftware.android.riskfinder.activity.base;

import jp.co.taosoftware.android.riskfinder.R;
import jp.co.taosoftware.android.riskfinder.util.Utils;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int extra = getIntent().getIntExtra(Utils.EXTRA_SHOW_FRAGMENT, 0);
		if (extra == R.layout.database_export_app){
			setContentView(R.layout.database_export_app);
		} else if (extra == R.layout.brawsable_app){
			setContentView(R.layout.brawsable_app);
		}
	}
}
