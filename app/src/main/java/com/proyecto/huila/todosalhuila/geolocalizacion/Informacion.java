package com.proyecto.huila.todosalhuila.geolocalizacion;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.proyecto.huila.indicador.ImageIndicatorView;
import com.proyecto.huila.todosalhuila.R;


public class Informacion extends AppCompatActivity {

    private ImageIndicatorView imageIndicatorView;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_informacion);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            this.setTitle("Hotel GHL");

            this.imageIndicatorView = (ImageIndicatorView) findViewById(R.id.indicate_view);
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

            TabHost tabs=(TabHost)findViewById(android.R.id.tabhost);
            tabs.setup();

            TabHost.TabSpec spec=tabs.newTabSpec("tab1");
            spec.setContent(R.id.tab1);
            spec.setIndicator(getString(R.string.text_tab1));
            tabs.addTab(spec);

            spec=tabs.newTabSpec("tab2");
            spec.setContent(R.id.tab2);
            spec.setIndicator(getString(R.string.text_tab2));
            tabs.addTab(spec);

            spec=tabs.newTabSpec("tab3");
            spec.setContent(R.id.tab3);
            spec.setIndicator(getString(R.string.text_tab3));
            tabs.addTab(spec);

            tabs.setCurrentTab(0);

            TabWidget widget = tabs.getTabWidget();
            for(int i = 0; i < widget.getChildCount(); i++) {
                View v = widget.getChildAt(i);

                // Look for the title view to ensure this is an indicator and not a divider.
                TextView tv = (TextView)v.findViewById(android.R.id.title);
                if(tv == null) {
                    continue;
                }
                v.setBackgroundResource(R.drawable.tab_selector);
            }

            TextView x = (TextView) tabs.getTabWidget().getChildAt(0).findViewById(android.R.id.title);
            x.setTextSize(10);
            x = (TextView) tabs.getTabWidget().getChildAt(1).findViewById(android.R.id.title);
            x.setTextSize(10);
            x = (TextView) tabs.getTabWidget().getChildAt(2).findViewById(android.R.id.title);
            x.setTextSize(10);

        }

        private void initView() {
            final Integer[] resArray = new Integer[] { R.drawable.hotel1, R.drawable.hotel2, R.drawable.hotel3, R.drawable.hotel4 };
            this.imageIndicatorView.setupLayoutByDrawable(resArray);
            this.imageIndicatorView.show();
        }
 }


