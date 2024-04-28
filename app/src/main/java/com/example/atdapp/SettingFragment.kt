import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atdapp.LoginActivity
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
            SettingItem("Langue") { showLanguageDialog() }, // Ajoutez cette ligne
            SettingItem("Théme") { showNightModeDialog() }, // Ajoutez cette ligne
            SettingItem("Déconnexion") { logOut() },
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

        builder.setMessage("Voulez-vous vous déconnecter ?")
            .setTitle("Déconnexion")

        builder.setPositiveButton("Oui") { dialog, which ->
            val sharedPreferences = activity?.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()

            editor?.remove("auth_token")
            editor?.remove("userId")
            editor?.remove("lastLogin")
            editor?.apply()
            Toast.makeText(context, "Vous êtes déconnecté", Toast.LENGTH_SHORT).show()

            val intent = Intent(context, LoginActivity::class.java)
            startActivity(intent)

            activity?.finish()
        }
        builder.setNegativeButton("Non") { dialog, which ->

            dialog.dismiss()
        }

        builder.create().show()
    }

    private fun showNightModeDialog() {
        val modes = arrayOf("Système", "Clair", "Sombre")
        val checkedItem = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> 2
            AppCompatDelegate.MODE_NIGHT_NO -> 1
            else -> 0
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choisissez un mode")
            .setSingleChoiceItems(modes, checkedItem) { dialog, which ->
                when (which) {
                    0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Français", "English")
        val checkedItem = when (Locale.getDefault().language) {
            "fr" -> 0
            "en" -> 1
            else -> 0 // Default to French if no language is set
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choisissez une langue")
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                when (which) {
                    0 -> setLocale("fr")
                    1 -> setLocale("en")
                }
                dialog.dismiss()
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val resources = resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)

        // Redémarrer l'activité pour que les changements prennent effet
        activity?.recreate()
    }


}
