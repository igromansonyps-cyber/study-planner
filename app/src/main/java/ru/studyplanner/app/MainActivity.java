package ru.studyplanner.app;
import android.app.*;import android.os.*;import android.content.*;import android.webkit.*;import android.speech.RecognizerIntent;
public class MainActivity extends Activity {
 WebView web;
 public void onCreate(Bundle b){super.onCreate(b);web=new WebView(this);setContentView(web);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.addJavascriptInterface(new Bridge(),"NativePlanner");web.setWebViewClient(new WebViewClient(){public void onPageFinished(WebView v,String u){v.evaluateJavascript("if(!window.nativeReady){window.nativeReady=true; const old=saveTasks; saveTasks=function(){old();NativePlanner.sync(JSON.stringify(state.tasks));}; NativePlanner.sync(JSON.stringify(state.tasks)); toggleVoiceRecognition=function(){NativePlanner.voice();}; enablePushNotifications=function(){NativePlanner.settings();}; triggerTestPushNotification=function(){NativePlanner.test();}; document.getElementById('installHeaderBtn').style.display='none';}",null);}});web.loadUrl("file:///android_asset/index.html");Reminder.channel(this);}
 class Bridge {
 @JavascriptInterface public void sync(String s){runOnUiThread(()->Reminder.sync(MainActivity.this,s));}
 @JavascriptInterface public void test(){runOnUiThread(()->Reminder.notifyUser(MainActivity.this,1,"Проверка уведомлений","Уведомления работают"));}
 @JavascriptInterface public void settings(){runOnUiThread(()->startActivity(new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,getPackageName())));}
 @JavascriptInterface public void voice(){runOnUiThread(()->{try{startActivityForResult(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM).putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ru-RU"),7);}catch(Exception e){android.widget.Toast.makeText(MainActivity.this,"Распознавание речи недоступно",0).show();}});}
 }
 protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d);if(r==7&&c==RESULT_OK&&d!=null){java.util.ArrayList<String> a=d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(a!=null&&!a.isEmpty())web.evaluateJavascript("document.getElementById('taskInput').value="+org.json.JSONObject.quote(a.get(0))+";",null);}}
}