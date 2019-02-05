package nikhil.prolock;

import android.app.Activity;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.valdesekamdem.library.mdtoast.MDToast;

public class MainActivity extends Activity {
    private DevicePolicyManager mgr=null;
    private ComponentName cn=null;
    SensorManager sManager;
    float sValue;
    View v;
    int btnvalue;
    private ShareActionProvider mShareActionProvider;
    Button onbtn,offbtn;
    TextView tv1;

    boolean doubleTap = false;
    MDToast backToast;

    private AdView mAdView;
    private InterstitialAd mOninterstitial;
    private InterstitialAd mOffinterstitial;
    private InterstitialAd mExitinterstitial;
    private InterstitialAd mResumeinterstitial;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        cn=new ComponentName(this, AdminReceiver.class);
        mgr=(DevicePolicyManager)getSystemService(DEVICE_POLICY_SERVICE);
        sManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        onbtn=findViewById(R.id.onbtn);
        offbtn=findViewById(R.id.offbtn);
        tv1=findViewById(R.id.tv1);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        onbtn.setBackgroundResource(R.drawable.ic_startred);
        offbtn.setBackgroundResource(R.drawable.ic_cancelgreen);

        sensorManager();

        //Interstitial Ads

        mOninterstitial = new InterstitialAd(this);
        mOninterstitial.setAdUnitId("ca-app-pub-9169779934207622/5390094102");
        AdRequest onrequest = new AdRequest.Builder().build();
        mOninterstitial.loadAd(onrequest);

        mOffinterstitial = new InterstitialAd(this);
        mOffinterstitial.setAdUnitId("ca-app-pub-9169779934207622/7249970680");
        AdRequest offrequest = new AdRequest.Builder().build();
        mOffinterstitial.loadAd(offrequest);

        mExitinterstitial = new InterstitialAd(this);
        mExitinterstitial.setAdUnitId("ca-app-pub-9169779934207622/9301418956");
        AdRequest exitrequest = new AdRequest.Builder().build();
        mExitinterstitial.loadAd(exitrequest);

        mResumeinterstitial = new InterstitialAd(this);
        mResumeinterstitial.setAdUnitId("ca-app-pub-9169779934207622/4599756614");
        AdRequest resumerequest = new AdRequest.Builder().build();
        mResumeinterstitial.loadAd(resumerequest);

    }

    private void sensorManager() {

        Sensor p=sManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        sManager.registerListener(new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float[] values=event.values;
                sValue=values[0];

                if(sValue==0.0&&btnvalue==1){
                    lock(v);
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        },p,SensorManager.SENSOR_DELAY_NORMAL);
    }


    public void lock(View v) {
        if (mgr.isAdminActive(cn)) {
            mgr.lockNow();
        }
        else {
            Intent intent=
                    new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, cn);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    getString(R.string.device_admin_explanation));
            startActivity(intent);
        }

    }

    public void onbtn(View v){
        btnvalue = 1;
        onbtn.setBackgroundResource(R.drawable.ic_start);
        offbtn.setBackgroundResource(R.drawable.ic_cancel);
        tv1.setText("WAVE HAND TO LOCK");

        if (mOninterstitial.isLoaded()) {
            mOninterstitial.show();
        }
    }
    public void offbtn(View v){
        btnvalue=0;
        onbtn.setBackgroundResource(R.drawable.ic_startred);
        offbtn.setBackgroundResource(R.drawable.ic_cancelgreen);
        tv1.setText(R.string.touch_to_activate);

        if (mOffinterstitial.isLoaded()) {
            mOffinterstitial.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.prolock_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.shareapp:
                Intent share =new Intent();
                share.setAction(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT,"Lock Your Phone's Screen Smartly,Download ProLock-https://play.google.com/store/apps/details?id=nikhil.prolock");
                share.setType("text/plain");
                startActivity(share);
                break;

            case R.id.rateapp:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("https://play.google.com/store/apps/details?id=nikhil.prolock")));
                break;

            case R.id.privacypolicy:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                        ("https://sites.google.com/view/prolock")));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (doubleTap) {
            super.onBackPressed();
            backToast.cancel();
        } else {
            if (mExitinterstitial.isLoaded()) {
                mExitinterstitial.show();
            }

            backToast = MDToast.makeText(MainActivity.this, "Press back again to exit", MDToast.LENGTH_SHORT, MDToast.TYPE_INFO);
            backToast.show();
            doubleTap = true;
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTap = false;
                    backToast.cancel();
                }
            }, 2000);
        }
    }
}


