package me.foolishchow.autoparamdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import me.foolishchow.android.annotation.IntentParam

open class MainActivity : AppCompatActivity() {
    @JvmField
    @IntentParam
    protected var ints: List<Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val controller = navController
        //NavigationManager.attach(controller, R.navigation.main)
        //val navDestinations = MainActivity(controller)
        //val inflate = controller!!.navInflater.inflate(R.navigation.main)
        //val displayName = inflate.navigatorName
    }

    private val navController: NavController?
        get() {
            try {
                val loginNavHost = supportFragmentManager.findFragmentByTag("login_nav_host") as NavHostFragment?
                return loginNavHost?.navController
            } catch (ignored: Exception) {
            }
            return null
        }
}