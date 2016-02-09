package com.proyecto.huila.todosalhuila.galeria;

import android.app.Activity;
import android.os.Bundle;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;

public class ImageIndicatorActivity extends Activity {
	private ImageIndicatorView imageIndicatorView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_indicator);

		//this.imageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);
		this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
			@Override
			public void onPosition(int position, int totalCount) {

			}
		});
		this.imageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
			@Override
			public void onPosition(int position, int totalCount) {

			}
		});

		this.initView();
	}

	private void initView() {
		final Integer[] resArray = new Integer[] { R.drawable.image3, R.drawable.image2, R.drawable.image1 };
		this.imageIndicatorView.setupLayoutByDrawable(resArray);
		this.imageIndicatorView.show();
	}
}
