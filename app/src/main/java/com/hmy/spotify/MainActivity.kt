package com.hmy.spotify

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.hmy.spotify.ui.theme.SpotifyTheme


// 如果抽象成MVC的话 C(Controller)就是Activity和Fragment -- Activity有生命周期

// Composable类似JSX 是用来描述UI的 符合Kotlin语法规范的 用来描述UI的domain language
// Modifiery用来装饰UI的 类似CSS

// Fragment就是一个轻量化的Activity

// Navigation的原理就是一个图 图里是App里的每一个screen 如果要导航到另一个destination 那么就把screen连起来
// Fragment Container contain的就是图里的每一个destination！！

// 先创建navigation view，里面每一个item都是menu



// customized extend AppCompatActivity
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SpotifyTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting("Android")
                }
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