package io.keepcoding.eh_ho.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import io.keepcoding.eh_ho.R
import io.keepcoding.eh_ho.data.SignInModel
import io.keepcoding.eh_ho.inflate
import kotlinx.android.synthetic.main.fragment_sign_in.*
import kotlinx.android.synthetic.main.fragment_sign_in.inputPassword
import kotlinx.android.synthetic.main.fragment_sign_in.inputUsername
import kotlinx.android.synthetic.main.fragment_sign_in.labelCreateAccount
import kotlinx.android.synthetic.main.fragment_sign_up.*

class SignInFragment: Fragment() {

    var signInInteractionListener: SignInInteractionListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is SignInInteractionListener)
            signInInteractionListener = context
        else
            throw IllegalArgumentException("Context doesn't implement ${SignInInteractionListener::class.java.canonicalName}")
    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_sign_in)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        labelCreateAccount.setOnClickListener {
            signInInteractionListener?.onGoToSignUp()
        }

        buttonLogin.setOnClickListener {
            if (isFormValid()) {
                val signInModel = SignInModel(inputUsername.text.toString(), inputPassword.text.toString())
                signInInteractionListener?.onSignIn(signInModel)
            } else {
                showErrors() }

        }
    }



    override fun onDetach() {
        super.onDetach()
        signInInteractionListener = null
    }

    interface SignInInteractionListener {
        fun onGoToSignUp()
        fun onSignIn(signInModel: SignInModel)
    }

    private fun isFormValid () =
        inputUsername.text.toString().isNotEmpty()
                && inputPassword.text.toString().isNotEmpty()

    private fun showErrors(){
        if (inputUsername.text.toString().isEmpty()) {
            inputUsername.error = getString(R.string.error_empty)
        }
        if (inputPassword.text.toString().isEmpty()) {
            inputPassword.error = getString(R.string.error_empty)
        }
        Toast.makeText(context, "Rellene los campos indicados", Toast.LENGTH_LONG).show()

    }

}