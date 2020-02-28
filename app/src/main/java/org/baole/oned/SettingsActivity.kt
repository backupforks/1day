package org.baole.oned

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import org.baole.oned.databinding.SettingsActivityBinding


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        FirebaseAuth.getInstance().currentUser?.let {
            findPreference<Preference>("account")?.setSummary("${it.displayName}/${it.email}")
        } ?: kotlin.run {
            findPreference<Preference>("account")?.let {
                findPreference<PreferenceScreen>("settings")?.removePreference(it)
            }
        }

        findPreference<Preference>("version")?.let { preference ->
            preference.summary = "V${BuildConfig.VERSION_NAME}"
        }
    }

    override fun onPreferenceTreeClick(preference: Preference?): Boolean {
        when (preference?.key) {
            "backup" -> onBackupClick()
            "restore" -> onRestoreClick()
            "open_source" -> onOpenSourcesClick()
            "report_issues" -> onReportIssuesClick()
            "sign_out" -> onSignOutClick()
        }

        return true
    }

    private fun onRestoreClick() {
        Toast.makeText(context, "TODO: to be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun onBackupClick() {
        Toast.makeText(context, "TODO: to be implemented", Toast.LENGTH_SHORT).show()
    }

    private fun onSignOutClick() {
        activity?.let { activity ->
            AuthUI.getInstance().signOut(activity).addOnSuccessListener {
                activity.setResult(Activity.RESULT_OK)
                activity.finish()
            }.addOnFailureListener {
                it.printStackTrace()
            }
        }
    }

    private fun onReportIssuesClick() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.issue_url))))
    }

    private fun onOpenSourcesClick() {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.source_code_url))))
    }
}

class SettingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: SettingsActivityBinding = SettingsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()
    }
}