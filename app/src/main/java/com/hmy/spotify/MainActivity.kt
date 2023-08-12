package com.hmy.spotify

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hmy.spotify.database.DatabaseDao
import com.hmy.spotify.datamodel.Album
import com.hmy.spotify.network.NetworkApi
import com.hmy.spotify.network.NetworkModule
import com.hmy.spotify.player.PlayerBar
import com.hmy.spotify.player.PlayerViewModel
import com.hmy.spotify.ui.theme.SpotifyTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    // 因为都是inject的，所以不能确保他和Activity的生命周期保持一致
    @Inject
    lateinit var api: NetworkApi
    @Inject
    lateinit var databaseDao: DatabaseDao
    private val playerViewModel: PlayerViewModel by viewModels()

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

        val playerBar = findViewById<ComposeView>(R.id.player_bar)
        playerBar.apply {
            setContent {
                MaterialTheme(colors = darkColors()) {
                    PlayerBar(
                        playerViewModel
                    )
                }
            }
        }

        // A coroutine is a concurrency design pattern on Android to simplify code
        // that executes asynchronously. Coroutines were added to Kotlin in version
        // 1.3 and are based on established concepts from other languages

        // A coroutine is an instance of suspendable computation. It is conceptually
        // similar to a thread, in the sense that it takes a block of code to run that
        // works concurrently with the rest of the code. However, a coroutine is not
        // bound to any particular thread. It may suspend its execution in one thread and
        // resume in another one.

        // Features: lightweight, fewer memory leaks, built-in cancellation support, Jetpack integration


        // Managing State in Compose
        // https://developer.android.com/jetpack/compose/state
        // State in an app is any value that can change over time. This is a very broad definition and
        // encompasses everything from a Room database to a variable on a class
        // All Android apps display state to the user: a snackbar, blog post, ripple animations on button, stickers

        // Jetpack Compose helps you be explicit about where and how you store nad use state in an Android app.
        // Compose is declarative and as such the only way to update it is by calling the same composable
        // with new arguments. These arguments are representations of the UI state. Any time a state is updated
        // a recomposition takes place.


        // A composable that uses remember to store an object creates internal state, making the composable stateful.
        // A stateless composable is a composable that doesn't hold any state.
        // An easy way to achieve stateless is by using state hoisting



        // Test retrofit
        // 需要裹一层GlobalScope.launch(Dispatchers.IO) 否则会阻塞IO
        GlobalScope.launch(Dispatchers.IO) {
            // comment out 不需要这一行了，因为hilt帮忙inject了
            // val api = NetworkModule.provideRetrofit().create(NetworkApi::class.java)
            val response = api.getHomeFeed().execute().body()
            Log.d("Network", response.toString())
        }

        // remember it runs everytime you start the app
        // Global thread 然后转换到IO thread
        GlobalScope.launch {
            // 为什么我们可以不停插入一样的Album因为可以replace
            // 商业化APP里这是个bug，这里有意保留该专辑
            withContext(Dispatchers.IO) {
                val album = Album(
                    id = 1,
                    name =  "Hexagonal",
                    year = "2008",
                    cover = "https://upload.wikimedia.org/wikipedia/en/6/6d/Leessang-Hexagonal_%28cover%29.jpg",
                    artists = "Lesssang",
                    description = "Leessang (Korean: 리쌍) was a South Korean hip hop duo, composed of Kang Hee-gun (Gary or Garie) and Gil Seong-joon (Gil)"
                )
                databaseDao.favoriteAlbum(album) // 如果直接放到UI thread上会阻塞UI会卡 直接不允许
            }
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