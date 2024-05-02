import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atdapp.HomeActivity
import com.example.atdapp.LoginActivity
import com.example.atdapp.R
import com.example.atdapp.databinding.FragmentSettingBinding
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingsList = listOf(
            SettingItem(getString(R.string.language)) { showLanguageDialog() },
            SettingItem(getString(R.string.theme)) { showNightModeDialog() },
            SettingItem(getString(R.string.logout)) { logOut() }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = SettingsAdapter(settingsList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun logOut() {
        val builder = AlertDialog.Builder(context)

        builder.setMessage(R.string.logoutConfirm)
            .setTitle(R.string.logout)

        builder.setPositiveButton(R.string.yes) { dialog, which ->
            val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()

            editor?.remove("auth_token")
            editor?.remove("userId")
            editor?.remove("lastLogin")
            editor?.apply()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showNightModeDialog() {

        val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences?.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        val modes = arrayOf(getString(R.string.system), getString(R.string.light), getString(R.string.dark))
        val checkedItem = when (nightMode) {
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            else -> 0
        }
        Log.d("SettingsFragment", "Activité: ${activity?.title}")

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.choose_mode)
            .setSingleChoiceItems(modes, checkedItem) { dialog, which ->
                when (which) {
                    0 -> {
                        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                        saveNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    1 -> {
                        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        saveNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                    }
                    2 -> {
                        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        saveNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun showLanguageDialog() {

        val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val language = sharedPreferences?.getString("language", "fr") // Utilisez "fr" comme valeur par défaut

        val languages = arrayOf(getString(R.string.french), getString(R.string.english))
        val checkedItem = when (language) {
            "fr" -> 0
            "en" -> 1
            else -> 0 // Default to French if no language is set
        }

        AlertDialog.Builder(requireContext())
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                when (which) {
                    0 -> {
                        setLocale("fr")
                        saveLanguage("fr")
                    }
                    1 -> {
                        setLocale("en")
                        saveLanguage("en")
                    }
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        //activity?.recreate()
    }

    private fun saveNightMode(mode: Int) {
        val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putInt("nightMode", mode)
        editor?.apply()
    }

    private fun saveLanguage(languageCode: String) {
        val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("language", languageCode)
        editor?.apply()
    }

    private fun recreateActivity() {
        val intent = Intent(activity, HomeActivity::class.java)
        activity?.recreate()
    }
}
