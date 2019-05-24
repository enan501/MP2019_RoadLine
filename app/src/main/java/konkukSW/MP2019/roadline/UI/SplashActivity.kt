package konkukSW.MP2019.roadline.UI

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import konkukSW.MP2019.roadline.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startActivity(Intent(this, MainListActivity::class.java))
        finish()
    }
}

