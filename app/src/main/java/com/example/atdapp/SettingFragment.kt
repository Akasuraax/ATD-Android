import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.atdapp.LoginActivity
import com.example.atdapp.databinding.FragmentSettingBinding

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
            SettingItem("Action") {  },
            SettingItem("Déconnexion") { logOut() },
            // Ajoutez autant d'actions que nécessaire
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
}
