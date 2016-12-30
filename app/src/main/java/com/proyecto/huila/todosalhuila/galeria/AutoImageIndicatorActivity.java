package com.proyecto.huila.todosalhuila.galeria;

import android.app.Activity;
import android.os.Bundle;

import com.proyecto.huila.indicador.AutoPlayManager;
import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;


public class AutoImageIndicatorActivity extends Activity {
	private ImageIndicatorView autoImageIndicatorView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_indicator);

		//this.autoImageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);
		autoImageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
			@Override
			public void onPosition(int position, int totalCount) {

			}
		});
		this.autoImageIndicatorView.setOnItemChangeListener(new ImageIndicatorView.OnItemChangeListener() {
			@Override
			public void onPosition(int position, int totalCount) {

			}
		});
		
		initView();
	}

	private void initView() {
		final Integer[] resArray = new Integer[]{R.drawable.imagen1, R.drawable.imagen2, R.drawable.imagen3, R.drawable.imagen4, R.drawable.imagen5, R.drawable.imagen6};
		
		this.autoImageIndicatorView.setupLayoutByDrawable(resArray);
		this.autoImageIndicatorView.show();
		
		AutoPlayManager autoBrocastManager =  new AutoPlayManager(this.autoImageIndicatorView);
		autoBrocastManager.setBroadcastEnable(true);
		autoBrocastManager.setBroadCastTimes(5);//循环次数
		autoBrocastManager.setBroadcastTimeIntevel(3 * 1000, 3 * 1000);//首次启动时间及间隔
		autoBrocastManager.loop();
	}

}
