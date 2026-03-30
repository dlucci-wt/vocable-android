package com.willowtree.vocable.splash

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.willowtree.vocable.MainActivity
import com.willowtree.vocable.ui.splash.SplashScreen
import com.willowtree.vocable.ui.theme.VocableTheme
import org.koin.androidx.scope.ScopeActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashActivity : ScopeActivity() {

    private val viewModel: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            VocableTheme {
                SplashScreen()
            }
        }

        viewModel.exitSplash.observe(this) {
            if (it) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}
