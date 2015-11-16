package detroneduc.mauricio.ch.dtrnerleduc;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;


import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.internal.me;
import com.jirbo.adcolony.AdColony;
import com.jirbo.adcolony.AdColonyAd;
import com.jirbo.adcolony.AdColonyAdAvailabilityListener;
import com.jirbo.adcolony.AdColonyAdListener;
import com.jirbo.adcolony.AdColonyVideoAd;


public class MainActivity extends Activity implements View.OnTouchListener,AdColonyAdListener, AdColonyAdAvailabilityListener  {


    private MaSurface maSurface;
    private AdColonyVideoAd ad;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        super.onCreate(savedInstanceState);
        AdColony.configure(this, "version:" + Constantes.VERSION + ",store:google", Constantes.APPID, Constantes.ZONEID);

        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        Tracker t = analytics.newTracker(R.xml.global_tracker);
        ad = new AdColonyVideoAd(Constantes.ZONEID);

        Button btnPlay=(Button) findViewById(R.id.btnPlay);
        btnPlay.setTypeface(Typeface.createFromAsset(getAssets(),"fontplay.ttf"));

    }

    public void onClickJouer(View v){
        maSurface= new MaSurface(MainActivity.this);
        setContentView(maSurface);

   }



    @Override
    protected void onResume() {
        super.onResume();
        AdColony.resume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AdColony.pause();

    }



    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onAdColonyAdAvailabilityChange(boolean b, String s) {

    }



    @Override
    public void onAdColonyAdAttemptFinished(AdColonyAd adColonyAd) {

    }

    @Override
    public void onAdColonyAdStarted(AdColonyAd adColonyAd) {

        Log.d("AdColony", "onAdColonyAdStarted");
    }


}
