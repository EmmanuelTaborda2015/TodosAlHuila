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
		final Integer[] resArray = new Integer[]{R.drawable.imagen1, R.drawable.imagen2, R.drawable.imagen3, R.drawable.imagen4, R.drawable.imagen5, R.drawable.imagen6};
		this.imageIndicatorView.setupLayoutByDrawable(resArray);
		this.imageIndicatorView.show();
	}
}
