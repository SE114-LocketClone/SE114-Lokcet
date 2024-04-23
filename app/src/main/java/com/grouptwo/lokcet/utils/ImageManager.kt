import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.grouptwo.lokcet.R

class ImageManager(private val context: Context) {

    fun loadImage(url: String, imageView: ImageView) {
        Glide.with(context)
            .load(url)
            .apply(RequestOptions().placeholder(R.drawable.placeholder))
            .into(imageView)
    }
}