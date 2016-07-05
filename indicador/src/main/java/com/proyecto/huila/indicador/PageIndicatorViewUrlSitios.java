package com.proyecto.huila.indicador;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * user guide, image indicator
 *
 * @author savant-pan
 */
public class PageIndicatorViewUrlSitios extends RelativeLayout {

    private ViewPager viewPager;
    /**
     * anchor container
     */

    /**
     * left button
     */
    private Button leftButton;

    /**
     * right button
     */
    private Button rightButton;

    /**
     * page vies list
     */
    private List<View> viewList = new ArrayList<View>();

    private Handler refreshHandler;

    /**
     * item changed listener
     */
    private OnItemChangeListener onItemChangeListener;

    /**
     * item clicked listener
     */
    private OnItemClickListener onItemClickListener;
    /**
     * page total count
     */
    private int totelCount = 0;
    /**
     * current page
     */
    private int currentIndex = 0;

    /**
     * cycle list arrow anchor
     */
    public static final int INDICATE_ARROW_ROUND_STYLE = 0;

    /**
     * user guide anchor
     */
    public static final int INDICATE_USERGUIDE_STYLE = 1;

    /**
     * INDICATOR style
     */
    private int indicatorStyle = INDICATE_ARROW_ROUND_STYLE;

    /**
     * latest scroll time
     */
    private long refreshTime = 0l;

    /**
     * item changed callback
     */
    public interface OnItemChangeListener {
        void onPosition(int position, int totalCount);
    }

    /**
     * item clicked callback
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public PageIndicatorViewUrlSitios(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public PageIndicatorViewUrlSitios(Context context) {
        super(context);
        this.init(context);
    }

    /**
     * @param context
     */
    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.page_indicator_layout, this);
        this.viewPager = (ViewPager) findViewById(R.id.view_pager);
        this.leftButton = (Button) findViewById(R.id.left_button);
        this.rightButton = (Button) findViewById(R.id.right_button);

        this.leftButton.setVisibility(View.GONE);
        this.rightButton.setVisibility(View.GONE);

        this.viewPager.addOnPageChangeListener(new PageChangeListener());

        final ArrowClickListener arrowClickListener = new ArrowClickListener();
        this.leftButton.setOnClickListener(arrowClickListener);
        this.rightButton.setOnClickListener(arrowClickListener);

        this.refreshHandler = new ScrollIndicateHandler(PageIndicatorViewUrlSitios.this);
    }

    /**
     * get ViewPager object
     */
    public ViewPager getViewPager() {
        return viewPager;
    }

    /**
     * get current index
     */
    public int getCurrentIndex() {
        return this.currentIndex;
    }

    /**
     * git view count
     */
    public int getTotalCount() {
        return this.totelCount;
    }

    /**
     * get latest scroll time
     */
    public long getRefreshTime() {
        return this.refreshTime;
    }

    /**
     * add single View
     *
     * @param view
     */
    public void addViewItem(View view) {
        final int position = viewList.size();
        view.setOnClickListener(new ItemClickListener(position));
        this.viewList.add(view);
    }

    /**
     * set ItemClickListener
     */
    private class ItemClickListener implements OnClickListener {
        private int position = 0;

        public ItemClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null) {
                onItemClickListener.OnItemClick(view, position);
            }
        }
    }

    /**
     * set Drawable array
     *
     * @param resArray Drawable array
     */
    public void setupLayoutByDrawable(final String resArray[]) {
        if (resArray == null)
            throw new NullPointerException();

        this.setupLayoutByDrawable(Arrays.asList(resArray));
    }

    /**
     * set Drawable list
     *
     * @param resList Drawable list
     */
    public void setupLayoutByDrawable(final List<String> resList) {
        if (resList == null)
            throw new NullPointerException();

        final int len = resList.size();
        if (len > 0) {
            for (int index = 0; index < len; index++) {

                JSONObject json = null;
                try {
                    json = new JSONObject(resList.get(index));
                    Log.v("items", json.get("nombre").toString());


                    LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                    llp.setMargins(10, 10, 10, 10); // llp.setMargins(left, top, right, bottom);

                    final LinearLayout linearLayout = new LinearLayout(getContext());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));


                    final ImageView imagen = new ImageView(getContext());
                    imagen.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 250));
                    imagen.setImageResource(R.drawable.imagen_no_disponible);

                    if(android.os.Build.VERSION.SDK_INT > 15)
                    {
                        // for API above 15
                        imagen.setBackground(getResources().getDrawable(R.drawable.imageviewind));
                    }
                    else
                    {
                        // for API below 15
                        imagen.setBackgroundDrawable(getResources().getDrawable(R.drawable.imageviewind));
                    }

                    LoadImageFromURL asyncTask =new LoadImageFromURL(new LoadImageFromURL.AsyncResponse() {

                        @Override
                        public void processFinish(final Bitmap[] output) {
                            if(output[0]==null){

                                imagen.setImageResource(R.drawable.imagen_no_disponible);
                            }else{
                                imagen.setImageBitmap(output[0]);
                            }
                        }
                    });

                    String[] myTaskParams ={json.get("imagen").toString()};
                    asyncTask.execute(myTaskParams);


                    final TextView nombre = new TextView(getContext());
                    nombre.setTextSize(20);
                    nombre.setTypeface(null, Typeface.BOLD);
                    nombre.setText(json.get("nombre").toString());
                    nombre.setTextColor(Color.BLACK);
                    nombre.setGravity(Gravity.CENTER_HORIZONTAL);
                    nombre.setLayoutParams(llp);

                    final TextView categoria = new TextView(getContext());
                    categoria.setTextSize(15);
                    categoria.setTextColor(Color.BLACK);
                    categoria.setText("Categoría: " + json.get("categoria").toString());
                    categoria.setLayoutParams(llp);

                    final TextView descripcion = new TextView(getContext());
                    descripcion.setTextSize(15);
                    descripcion.setTextColor(Color.BLACK);
                    descripcion.setText(json.get("descripcion_corta").toString());
                    descripcion.setLayoutParams(llp);

                    final TextView telefono = new TextView(getContext());
                    telefono.setTextSize(15);
                    telefono.setTextColor(Color.BLACK);
                    telefono.setText("Teléfono: " + json.get("telefono").toString());
                    telefono.setLayoutParams(llp);

                    final TextView direccion = new TextView(getContext());
                    direccion.setTextSize(15);
                    direccion.setTextColor(Color.BLACK);
                    direccion.setText("Dirección: " + json.get("direccion").toString());
                    direccion.setLayoutParams(llp);


                    final TextView email = new TextView(getContext());
                    email.setTextSize(15);
                    email.setTextColor(Color.BLACK);
                    email.setText("Email: " + json.get("email").toString());
                    email.setLayoutParams(llp);

                    final TextView web = new TextView(getContext());
                    web.setTextSize(15);
                    web.setTextColor(Color.BLACK);
                    web.setText("Página Web: " + json.get("pagina_web").toString());
                    web.setLayoutParams(llp);


                    if(!"".equals(json.get("imagen").toString()) && !"NULL".equals(json.get("imagen").toString()) && !json.get("imagen").toString().equals(null) && !"null".equals(json.get("imagen").toString())){
                        linearLayout.addView(imagen);
                    }else{
                        imagen.setImageResource(R.drawable.imagen_no_disponible);
                        linearLayout.addView(imagen);
                    }

                    if(!"".equals(json.get("nombre").toString()) && !"NULL".equals(json.get("nombre").toString()) && !json.get("nombre").toString().equals(null) && !"null".equals(json.get("nombre").toString())){
                        linearLayout.addView(nombre);
                    }
                    if(!"".equals(json.get("categoria").toString()) && !"NULL".equals(json.get("categoria").toString()) && !json.get("categoria").toString().equals(null) && !"null".equals(json.get("categoria").toString())){
                        linearLayout.addView(categoria);
                    }
                    if(!"".equals(json.get("descripcion_corta").toString()) && !"NULL".equals(json.get("descripcion_corta").toString()) && !json.get("descripcion_corta").toString().equals(null) && !"null".equals(json.get("descripcion_corta").toString())){
                        linearLayout.addView(descripcion);
                    }
                    if(!"".equals(json.get("telefono").toString()) && !"NULL".equals(json.get("telefono").toString()) && !json.get("telefono").toString().equals(null) && !"null".equals(json.get("telefono").toString())){
                        linearLayout.addView(telefono);
                    }
                    if(!"".equals(json.get("direccion").toString()) && !"NULL".equals(json.get("direccion").toString()) && !json.get("direccion").toString().equals(null) && !"null".equals(json.get("direccion").toString())){
                        linearLayout.addView(direccion);
                    }
                    if(!"".equals(json.get("email").toString()) && !"NULL".equals(json.get("email").toString()) && !json.get("email").toString().equals(null) && !"null".equals(json.get("email").toString())){
                        linearLayout.addView(email);
                    }
                    if(!"".equals(json.get("pagina_web").toString()) && !"NULL".equals(json.get("pagina_web").toString()) && !json.get("pagina_web").toString().equals(null) && !"null".equals(json.get("email").toString())){
                        linearLayout.addView(web);
                    }


                    ScrollView myScrollview;

                    myScrollview = new ScrollView(getContext());

                    myScrollview.addView(linearLayout);

                    addViewItem(myScrollview);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * set show item current
     *
     * @param index postion
     */
    public void setCurrentItem(int index) {
        this.currentIndex = index;
    }

    /**
     * set anchor style, default INDICATOR_ARROW_ROUND_STYLE
     *
     * @param style INDICATOR_USERGUIDE_STYLE or INDICATOR_ARROW_ROUND_STYLE
     */
    public void setIndicateStyle(int style) {
        this.indicatorStyle = style;
    }

    /**
     * add OnItemChangeListener
     *
     * @param onItemChangeListener callback
     */
    public void setOnItemChangeListener(OnItemChangeListener onItemChangeListener) {
        if (onItemChangeListener == null) {
            throw new NullPointerException();
        }
        this.onItemChangeListener = onItemChangeListener;
    }

    /**
     * add setOnItemClickListener
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * show
     */
    public void show() {
        this.totelCount = viewList.size();

        //init anchor
        for (int index = 0; index < this.totelCount; index++) {
            final View indicater = new ImageView(getContext());
        }
        this.refreshHandler.sendEmptyMessage(currentIndex);
        // set data for viewpager
        this.viewPager.setAdapter(new MyPagerAdapter(this.viewList));
        this.viewPager.setCurrentItem(currentIndex, false);
    }

    /**
     * deal clicked event
     */
    private class ArrowClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (view == leftButton) {
                if (currentIndex >= (totelCount - 1)) {
                    return;
                } else {
                    viewPager.setCurrentItem(currentIndex + 1, true);
                }
            } else {
                if (totelCount <= 0) {
                    return;
                } else {
                    viewPager.setCurrentItem(currentIndex - 1, true);
                }
            }
        }
    }

    /**
     * deal page change
     */
    private class PageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int index) {
            currentIndex = index;
            refreshHandler.sendEmptyMessage(index);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }

    /**
     * refresh indicate anchor
     */
    protected void refreshIndicateView() {
        this.refreshTime = System.currentTimeMillis();

        for (int index = 0; index < totelCount; index++) {

        }

        if (INDICATE_USERGUIDE_STYLE == this.indicatorStyle) {// no arrow when user guide style
            this.leftButton.setVisibility(View.GONE);
            this.rightButton.setVisibility(View.GONE);
        } else {// set arrow style
            if (totelCount <= 1) {
                leftButton.setVisibility(View.GONE);
                rightButton.setVisibility(View.GONE);
            } else if (totelCount == 2) {
                if (currentIndex == 0) {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.GONE);
                } else {
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.VISIBLE);
                }
            } else {
                if (currentIndex == 0) {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.GONE);
                } else if (currentIndex == (totelCount - 1)) {
                    leftButton.setVisibility(View.GONE);
                    rightButton.setVisibility(View.VISIBLE);
                } else {
                    leftButton.setVisibility(View.VISIBLE);
                    rightButton.setVisibility(View.VISIBLE);
                }
            }
        }
        if (this.onItemChangeListener != null) {// notify item state changed
            try {
                this.onItemChangeListener.onPosition(this.currentIndex, this.totelCount);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ScrollIndicateHandler
     */
    private static class ScrollIndicateHandler extends Handler {
        private final WeakReference<PageIndicatorViewUrlSitios> scrollIndicateViewRef;

        public ScrollIndicateHandler(PageIndicatorViewUrlSitios scrollIndicateView) {
            this.scrollIndicateViewRef = new WeakReference<PageIndicatorViewUrlSitios>(
                    scrollIndicateView);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PageIndicatorViewUrlSitios scrollIndicateView = scrollIndicateViewRef.get();
            if (scrollIndicateView != null) {
                scrollIndicateView.refreshIndicateView();
            }
        }
    }

    private class MyPagerAdapter extends PagerAdapter {
        private List<View> pageViews = new ArrayList<View>();

        public MyPagerAdapter(List<View> pageViews) {
            this.pageViews = pageViews;
        }

        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(pageViews.get(arg1));
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

}
