package com.jyh.byzb;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.jyh.byzb.common.constant.SpConstant;
import com.jyh.byzb.common.utils.SPUtils;
import com.jyh.byzb.common.utils.ScanningImageTools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;

public class WebActivity extends Activity {
	private WebView webView;
	private TextView tv;

	private LinearLayout title,self_out_img;
	private TextView title_tv;

	private String summary_url;
	private boolean isKF;

	private ProgressBar progressBar;

	private String url;
	private String imageUrl;
    private boolean isGoBack;
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		summary_url = SPUtils.getString(this, SpConstant.APPINFO_SUMMARY_URL);
		url = getIntent().getStringExtra("url");
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
		setTheme(R.style.BrowserThemeDefault);

		if (!summary_url.equals(url)) {
			isKF = true;
			setContentView(R.layout.activity_hq2);
		} else {
			isKF = false;
			setContentView(R.layout.activity_hq);
		}

		title = (LinearLayout) findViewById(R.id.title);
		title_tv = (TextView) findViewById(R.id.title_tv);
		title.setVisibility(View.GONE);
		self_out_img= (LinearLayout) findViewById(R.id.self_out_img);
		progressBar= (ProgressBar) findViewById(R.id.web_progressBar);
		self_out_img.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});
		if (null != getIntent().getStringExtra("from") && "main".equals(getIntent().getStringExtra("from"))) {
			title.setVisibility(View.VISIBLE);
			findViewById(R.id.self_fk_img).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(isGoBack){
						if(webView.canGoBack()){
							webView.goBack();
						}else{
							finish();
						}
					}else{
						finish();
					}

				}
			});
		}

		if (null != getIntent().getStringExtra("title")) {
			title_tv.setText(getIntent().getStringExtra("title"));
		}

		webView = (WebView) findViewById(R.id.webView);
		tv = (TextView) findViewById(R.id.tvId);
		webView.setVisibility(View.VISIBLE);
		tv.setVisibility(View.GONE);
		WebSettings webSeting = webView.getSettings();
		webSeting.setJavaScriptEnabled(true);
		webSeting.setLoadsImagesAutomatically(true);
		webSeting.setLoadWithOverviewMode(true);
		webSeting.setUseWideViewPort(true);
		webSeting.setDisplayZoomControls(false);
		webSeting.setBuiltInZoomControls(true);
		webView.removeJavascriptInterface("searchBoxJavaBridge_");
		if (isKF)
			webView.addJavascriptInterface(new isQQ(), "QQ");
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// TODO Auto-generated method stub
				// Log.i("kefu", "shouldOverrideUrlLoading" + url);
				isGoBack=true;
				self_out_img.setVisibility(View.VISIBLE);
				return super.shouldOverrideUrlLoading(view, url);
			}

			@Override
			public void onPageFinished(WebView v, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(v, url);
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
			}

			@Override
			public void onReceivedError(WebView v, int errorCode, String description, String failingUrl) {
				// view.findViewById(R.id.dialog_view).setVisibility(View.GONE);
				if (isKF) {
					v.loadUrl(url);
				} else {
					tv.setVisibility(View.VISIBLE);
					webView.setVisibility(View.GONE);
				} 
			}

			@SuppressWarnings("deprecation")
			@SuppressLint("NewApi")
			@Override
			public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
				if (url.startsWith("http") || url.startsWith("https")) {
					return super.shouldInterceptRequest(view, url);
				} else {
					Intent in = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
					startActivity(in);
					return null;
				}
			}
		});
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public void onProgressChanged(WebView view, int newProgress) {
				super.onProgressChanged(view, newProgress);
				if (newProgress != 100) {
					if (progressBar.getVisibility() == View.GONE)
						progressBar.setVisibility(View.VISIBLE);
					progressBar.setProgress(newProgress);
				} else {
					progressBar.setVisibility(View.GONE);
				}
			}
		});

		webView.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				WebView.HitTestResult result = ((WebView) v).getHitTestResult();
				if (null != result) {
					int type = result.getType();
					if (type == WebView.HitTestResult.IMAGE_TYPE
							|| type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
						imageUrl = result.getExtra();
						Log.e("TAG", "image -- " + imageUrl);
						showPopupWindow(webView, imageUrl);
					}
				}
				return false;
			}
		});

		webView.loadUrl(url);

	}

	class isQQ {
		@JavascriptInterface
		public boolean isAppInstalled() {
			PackageInfo packageInfo;
			try {
				packageInfo = getPackageManager().getPackageInfo("com.tencent.mobileqq", 0);
			} catch (Exception e) {
				packageInfo = null;
				e.printStackTrace();
			}
			if (packageInfo == null) {
				// System.out.println("没有安装");
				return false;
			} else {
				// System.out.println("已经安装");
				return true;
			}
		}
	}

	private TextView EQCodeView,SaveBimap,popuwoindow_cancel;
	private RelativeLayout bgWai;
	private String EQResult = "";
	private Bitmap bitmaps = null;
	private void showPopupWindow(View view, String url) {
		View contentView = View.inflate(this, R.layout.popuwoindow_item, null);
		final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
		EQCodeView = (TextView) contentView.findViewById(R.id.popuwoindow_eqCode);
		SaveBimap=(TextView) contentView.findViewById(R.id.saveBimap);
		bgWai= (RelativeLayout) contentView.findViewById(R.id.bgWai);
		popuwoindow_cancel=(TextView) contentView.findViewById(R.id.popuwoindow_cancel);
		EQCodeView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (EQResult == null || "".equals(EQResult))
					return;
				Log.e("TAG", "二维码的地址 -- " + EQResult);
				if(EQResult.startsWith("http://weixin.qq.com/r")){
					Intent intent = null;
					try {
						intent = Intent.parseUri("weixin://", Intent.URI_INTENT_SCHEME);
						startActivity(intent);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}catch (Exception e){
						e.printStackTrace();
						Toast.makeText(getApplicationContext(),"未安装此应用",Toast.LENGTH_SHORT).show();
					}
				}else{
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(EQResult));
					startActivity(intent);
				}
				popupWindow.dismiss();
//				Toast.makeText(WebActivity.this, EQResult, Toast.LENGTH_LONG).show();
			}
		});
		SaveBimap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (null != imageUrl) {
					popupWindow.dismiss();
					new SaveImage().execute(); // Android 4.0以后要使用线程来访问网络
				}
			}
		});
		popuwoindow_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
		bgWai.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				popupWindow.dismiss();
			}
		});
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		//popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.selectmenu_bg_downward));
		popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
		ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
			public void onLoadingStarted(String imageUri, View view) {
			}
			public void onLoadingFailed(String imageUri, View view,
										FailReason failReason) {
			}
			public void onLoadingComplete(String imageUri, View view,
										  Bitmap loadedImage) {
				// TODO Auto-generated method stub
				if (loadedImage == null)
					return;
				bitmaps = loadedImage;
				ScanningImageTools.scanningImage(loadedImage,
						new ScanningImageTools.IZCodeCallBack() {
							public void ZCodeCallBackUi(Result result) {
								if (result == null) {
									handler.sendEmptyMessage(0);
								} else {
									handler.sendEmptyMessage(1);
									EQResult = ScanningImageTools.recode(result.toString());
								}
							}
						});
			}
			public void onLoadingCancelled(String imageUri, View view) {
			}
		});
	}

	public void setVISIBLE(View v, boolean falg) {
		if (falg) {
			if (View.GONE == v.getVisibility()) {
				v.setVisibility(View.VISIBLE);
			}
		} else {
			if (View.VISIBLE == v.getVisibility()) {
				v.setVisibility(View.GONE);
			}
		}
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (EQCodeView == null)
				return;
			switch (msg.what) {
				case 0:
					setVISIBLE(EQCodeView, false);
					break;
				case 1:
					setVISIBLE(EQCodeView, true);
					break;
				case 2:
					break;
				default:
					break;
			}
		}
	};
	/***
	 * 功能：用线程保存图片
	 *
	 * @author wangyp
	 *
	 */
	private class SaveImage extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			String result = "";
			try {
				String sdcard = Environment.getExternalStorageDirectory()
						.toString();
				File file = new File(sdcard + "/Download");
				if (!file.exists()) {
					file.mkdirs();
				}
				int idx = imageUrl.lastIndexOf(".");
				String ext = imageUrl.substring(idx);
				file = new File(sdcard + "/Download/" + new Date().getTime()
						+ ext);
				InputStream inputStream = null;
				URL url = new URL(imageUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(20000);
				if (conn.getResponseCode() == 200) {
					inputStream = conn.getInputStream();
				}
				byte[] buffer = new byte[4096];
				int len = 0;
				FileOutputStream outStream = new FileOutputStream(file);
				while ((len = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, len);
				}
				outStream.close();
				result = "图片已保存至：" + file.getAbsolutePath();
				sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
						Uri.fromFile(file)));
			} catch (Exception e) {
				result = "保存失败！" + e.getLocalizedMessage();
			}
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onBackPressed() {
		if(isGoBack){
			if(webView!=null){
				if(webView.canGoBack()){
					webView.goBack();
				}else{
					super.onBackPressed();
				}

			}
		}else{
			super.onBackPressed();
		}

	}
}
