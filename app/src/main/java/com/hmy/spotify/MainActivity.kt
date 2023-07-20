package com.hmy.spotify

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.hmy.spotify.ui.theme.SpotifyTheme


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
class MainActivity : AppCompatActivity() {
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