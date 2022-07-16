package com.github.sensorydiary

import androidx.biometric.BiometricPrompt
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import java.util.concurrent.Executor


class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_error, errString),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    findNavController().navigate(R.id.action_loginFragment_to_menuFragment)
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_succeeded),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(
                        requireContext(), getString(R.string.authentication_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.log_in))
            .setSubtitle(getString(R.string.log_in_subtitle))
            .setAllowedAuthenticators(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)
            .setConfirmationRequired(false)
            .build()

        val buttonLogin = requireView().findViewById<Button>(R.id.buttonLogin)
        val biometricManager = BiometricManager.from(requireContext())


        buttonLogin.setOnClickListener {
            when (biometricManager.canAuthenticate(BIOMETRIC_WEAK or DEVICE_CREDENTIAL)) {
                BiometricManager.BIOMETRIC_SUCCESS ->
                    biometricPrompt.authenticate(promptInfo)
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                    Toast.makeText(
                        requireContext(), getString(R.string.biometric_error_no_hardware),
                        Toast.LENGTH_SHORT
                    ).show()
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                    Toast.makeText(
                        requireContext(), getString(R.string.biometric_error_hardware_unavailable),
                        Toast.LENGTH_SHORT
                    ).show()
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.biometric_error_none_enrolled),
                        Toast.LENGTH_SHORT
                    ).show()
                BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.biometric_error_security_update_required),
                        Toast.LENGTH_SHORT
                    ).show()
                BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.biometric_error_unsupported),
                        Toast.LENGTH_SHORT
                    ).show()
                BiometricManager.BIOMETRIC_STATUS_UNKNOWN ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.biometric_status_unknown),
                        Toast.LENGTH_SHORT
                    ).show()
            }
        }
    }
}