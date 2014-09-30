package com.estimote.examples.demos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AllDemosActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_demos);

		findViewById(R.id.notify_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllDemosActivity.this, NotifyDemosActivity.class);
				startActivity(intent);
			}
		});
		findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
				intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, RegisterActivity.class.getName());
				startActivity(intent);
			}
		});
	}
}
