package com.proyecto.huila.todosalhuila.galeria;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;

public class GuideImageIndicatorActivity extends Activity implements View.OnClickListener,
		ImageIndicatorView.OnItemChangeListener {
	private ImageIndicatorView imageIndicatorView;
	private Button goButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_indicator_guide);

		//this.imageIndicatorView = (ImageIndicatorView) findViewById(R.id.guide_indicate_view);
		//this.goButton = (Button) findViewById(R.id.button1);
		this.goButton.setVisibility(View.GONE);

		this.imageIndicatorView.setOnItemChangeListener(this);
		this.goButton.setOnClickListener(this);

		this.initView();
	}

	@Override
	public void onPosition(int position, int totalCount) {
		if (position == totalCount - 1) {
			goButton.setVisibility(View.VISIBLE);
		} else {
			goButton.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View view) {
		Toast.makeText(GuideImageIndicatorActivity.this, "let's roll...", Toast.LENGTH_SHORT).show();
		GuideImageIndicatorActivity.this.finish();
	}

	private void initView() {
		final Integer[] resArray = new Integer[]{R.drawable.imagen1, R.drawable.imagen2, R.drawable.imagen3, R.drawable.imagen4, R.drawable.imagen5, R.drawable.imagen6};
		this.imageIndicatorView.setupLayoutByDrawable(resArray);
		this.imageIndicatorView.setIndicateStyle(ImageIndicatorView.INDICATE_USERGUIDE_STYLE);
		this.imageIndicatorView.show();
	}

}
