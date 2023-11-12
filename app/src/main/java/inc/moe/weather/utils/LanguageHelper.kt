package inc.moe.weather.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.Locale

class  LanguageHelper{

    fun setLocale(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val displayMetrics = resources.displayMetrics
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, displayMetrics)
//        val config = Configuration()
//        config.setLocale(locale)

        context.resources.updateConfiguration(
            configuration,
            context.resources.displayMetrics
        )
    }

    fun getLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }
}
