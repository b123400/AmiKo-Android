package com.ywesee.amiko;

import android.content.Context;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class ExpertInfoView {

	private static final String TAG = "ExpertInfoView";
	
	private WebView mWebView = null;
	
	public ExpertInfoView(Context context, WebView webView) {
		mWebView = webView;

		// Override web client to open all links in same webview
		// mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
				
		mWebView.setInitialScale(1);
		mWebView.setPadding(0, 0, 0, 0);		
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);	
		mWebView.setScrollbarFadingEnabled(true);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.requestFocus(WebView.FOCUS_DOWN);
		
		// Activate JavaScriptInterface in given context
		mWebView.addJavascriptInterface(new JSInterface(context), "jsInterface");				
		
		WebSettings wsettings = mWebView.getSettings();		    		
		// Sets whether WebView loads pages in overview mode
		wsettings.setLoadWithOverviewMode(true);
		// Tells WebView to use a wide viewport
		wsettings.setUseWideViewPort(true);
		// Sets whether WebView should use its built-in zoom mechanisms
		wsettings.setBuiltInZoomControls(true);
		// Sets whether WebView should display on-screen zoom controls
		wsettings.setDisplayZoomControls(false);
		// Sets default zoom density of the page
		// wsettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
		wsettings.setLayoutAlgorithm(LayoutAlgorithm.NORMAL);//SINGLE_COLUMN);
		// Enable javascript
		wsettings.setJavaScriptEnabled(true);
		// TODO
		wsettings.setLoadsImagesAutomatically(true);		
	}
	
	public WebView getWebView() {
		return mWebView;
	}
	
	/**
	 * Customizes web view client to open links from your own site in the same web view otherwise
	 * just open the default browser activity with the URL
	 * @author Max
	 * 
	 */
	private class MyWebViewClient extends WebViewClient {
				
	    @Override
	    public boolean shouldOverrideUrlLoading(WebView view, String url) {
	   	 	view.loadUrl(url);
	   	 	view.requestFocus();
	    	return true;
	    }
	    
	    @Override
	    public void onPageFinished(WebView view, String url) {
	    	super.onPageFinished(view, url);
	    }
	}
}