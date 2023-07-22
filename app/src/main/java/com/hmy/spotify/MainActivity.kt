package com.hmy.spotify

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hmy.spotify.network.NetworkApi
import com.hmy.spotify.network.NetworkModule
import com.hmy.spotify.ui.theme.SpotifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject


// 如果抽象成MVC的话 C(Controller)就是Activity和Fragment -- Activity有生命周期

// Composable类似JSX 是用来描述UI的 符合Kotlin语法规范的 用来描述UI的domain language
// Modifiery用来装饰UI的 类似CSS

// Fragment就是一个轻量化的Activity

// Navigation的原理就是一个图 图里是App里的每一个screen 如果要导航到另一个destination 那么就把screen连起来
// Fragment Container contain的就是图里的每一个destination！！

// 先创建navigation view，里面每一个item都是menu

// 命名习惯是 如果main class是MainActivity 那么layout就是activity_main.xml

// graph是data model，FragmentView是View，NavHost里的NavController就是Controller

// customized extend AppCompatActivity

// flow: retrofit -> endpoint -> OKHTTP -> Service(0.0.0.0:8080) -> JSON -> GsonCon -> Kotlin: List<Section> -- UI

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // MainActivity是谁new的呢？因为MainActivity是系统来new的
    // android里有一系列的东西 Android Components 都是被系统new出来的
    // hilt会找到new出来的object，然后再给他传参
    // 因为是之后再init 所以是lateinit

    // class name @Inject constructor() 就不需要写provide了，可以直接inject进来
    // 自己写class这样可以告诉hilt让hilt认识

    // Inject表明了这个参数是用问工厂要来的，不需要指明工厂
    // 要的方式是调用provide方法，hilt帮忙自动实现
    // hilt帮我们注入进来的
    @Inject
    lateinit var api: NetworkApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContent {
//            SpotifyTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }

        setContentView(R.layout.activity_main)

        // findViewById拿到大的安卓的view 这里是BottomNavigationView
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)

        // controller
        val navHostFragment =supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Main thread / UI thread 所以Fragment和Activity不指名都是在这个thread 负责UI绘制

        // data model
        navController.setGraph(R.navigation.nav_graph)

        // 把view和Controller联系起来了
        NavigationUI.setupWithNavController(navView, navController)

        // 把MenuItem也和Nav Controller联系到了一起  MenuItem就是Home和Favorite
        // https://stackoverflow.com/questions/70703505/navigationui-not-working-correctly-with-bottom-navigation-view-implementation
        navView.setOnItemSelectedListener{
            NavigationUI.onNavDestinationSelected(it, navController)
            navController.popBackStack(it.itemId, inclusive = false)
            true
        }

        // Test retrofit
        GlobalScope.launch(Dispatchers.IO) {
            // comment out 不需要这一行了，因为hilt帮忙inject了
            // val api = NetworkModule.provideRetrofit().create(NetworkApi::class.java)
            val response = api.getHomeFeed().execute().body()
            Log.d("Network", response.toString())
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SpotifyTheme {
        Surface {
            Greeting("Android")
        }
    }
}