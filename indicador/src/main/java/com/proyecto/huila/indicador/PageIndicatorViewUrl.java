package com.proyecto.huila.indicador;

import android.content.ContentProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Layout;
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
import android.widget.TextView;

import org.json.JSONArray;
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
public class PageIndicatorViewUrl extends RelativeLayout {

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

    public PageIndicatorViewUrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context);
    }

    public PageIndicatorViewUrl(Context context) {
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

        this.refreshHandler = new ScrollIndicateHandler(PageIndicatorViewUrl.this);
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


                    final TextView titulo = new TextView(getContext());
                    titulo.setTextSize(20);
                    titulo.setTypeface(null, Typeface.BOLD);
                    titulo.setText(json.get("titulo").toString());
                    titulo.setTextColor(Color.WHITE);
                    titulo.setGravity(Gravity.CENTER_HORIZONTAL);
                    titulo.setLayoutParams(llp);

                    final TextView compannia = new TextView(getContext());
                    compannia.setTextSize(15);
                    compannia.setTextColor(Color.WHITE);
                    compannia.setText(json.get("compania").toString());
                    compannia.setLayoutParams(llp);

                    final TextView oferta = new TextView(getContext());
                    oferta.setTextSize(15);
                    oferta.setTextColor(Color.WHITE);
                    oferta.setText(json.get("blog_entrada").toString());
                    oferta.setLayoutParams(llp);

                    WebView mWebView = new WebView(getContext());
                    WebSettings webSettings = mWebView.getSettings();
                    webSettings.setJavaScriptEnabled(true);

                    String str = "Visitamos en el portal&nbsp;<a href=\"http://54.209.151.146/joomlaH/index.php/artesania\" target=\"_blank\">Todos al Huila</a>!!!!";
                    str = "Bienvenidos al sitio de la MiPyME Empanas Do\u00f1a Juana\n<img src=\"/files/home.jpg\" >\n\n<iframe src=\"https://www.youtube.com/embed/unMhfS1eg6c\" frameborder=\"0\"></iframe>";
                    str = json.get("contenido").toString();

                    ArrayList<String> myArray = new ArrayList<String>();

                    myArray.add("<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" ?>");
                    myArray.add("<html><body>");
                    myArray.add(str);
                    myArray.add("</body></html>");

                    String myFullString = myArray.toString().replace("[", "").replace("]", "").replace(",", "\n").replace("&lt;", "<").replace("&gt;", ">").replace("src=\"/files", "src=\"http://54.86.192.0/files").replace("src", "width=\"100%\" src");

                    mWebView.loadDataWithBaseURL("about:blank", myFullString, "text/html", "utf-8", null);

                    mWebView.setBackgroundColor(Color.parseColor("#00A8FF"));

                    linearLayout.addView(titulo);
                    linearLayout.addView(compannia);
                    linearLayout.addView(oferta);
                    linearLayout.addView(mWebView);

                    addViewItem(linearLayout);

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
        private final WeakReference<PageIndicatorViewUrl> scrollIndicateViewRef;

        public ScrollIndicateHandler(PageIndicatorViewUrl scrollIndicateView) {
            this.scrollIndicateViewRef = new WeakReference<PageIndicatorViewUrl>(
                    scrollIndicateView);

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PageIndicatorViewUrl scrollIndicateView = scrollIndicateViewRef.get();
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
