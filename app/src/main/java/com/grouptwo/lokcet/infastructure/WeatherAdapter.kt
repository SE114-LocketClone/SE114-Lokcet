class WeatherAdapter : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(this.window, false)
        super.onCreate(savedInstanceState)
        setContent {
            // A surface container using the 'background' color from the theme
            LokcetApp()
        }
    }
}