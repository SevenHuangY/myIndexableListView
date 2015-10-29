package com.nlt.testindexlistview;

import java.util.ArrayList;
import java.util.Random;

import com.nlt.baseObject.IndexableAdapter;
import com.nlt.testindexlistview.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

public class MainActivity extends Activity
{
	private ListView mLv;
	private IndexableAdapter mAdapter;
	private ArrayList<String> items;
	private String[] sections;
	private final String TAG = "test";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		init();

	}

	private void init()
	{
		// TODO Auto-generated method stub

		items = new ArrayList<String>();
		Random m = new Random();
		StringBuilder sb = new StringBuilder();
		int num;
		for (int i = 0; i < IndexableAdapter.s.length(); i++)
		{

			for (int j = 0; j < 10; j++)
			{
				sb = new StringBuilder();
				sb.append(IndexableAdapter.s.charAt(i));
				for (int k = 0; k < 5; k++)
				{
					num = m.nextInt((IndexableAdapter.s.length() - 1));
					sb.append(IndexableAdapter.s.charAt(num));
				}
				items.add(sb.toString());
			}
		}

		mAdapter = new IndexableAdapter(this, items);
		
		mLv = (ListView) findViewById(R.id.myListView);
		mLv.setAdapter(mAdapter);
		mLv.setFastScrollEnabled(true);
		mLv.setFastScrollAlwaysVisible(false);
		
		
	}

}
