package com.example.adaptivebannerexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.widget.FrameLayout
import com.google.android.gms.ads.*

private const val TAG = "MyActivity"
class MainActivity : AppCompatActivity() {
    private lateinit var adView: AdView

    private var initialLayoutComplete = false
    private lateinit var ad_view_container: FrameLayout
    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.
    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = ad_view_container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ad_view_container = findViewById(R.id.ad_view_container)
        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
        )

        adView = AdView(this)
        ad_view_container.addView(adView)
        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        ad_view_container.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }
    }
    /** Called when leaving the activity */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called when returning to the activity */
    public override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called before the activity is destroyed */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun loadBanner() {
        adView.adUnitId = AD_UNIT_ID

        adView.setAdSize(adSize)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    companion object {
        // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
        private val AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    }
}