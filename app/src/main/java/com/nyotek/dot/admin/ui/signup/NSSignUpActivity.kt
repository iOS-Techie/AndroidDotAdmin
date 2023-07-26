package com.nyotek.dot.admin.ui.signup

import android.os.Bundle
import com.nyotek.dot.admin.common.NSActivity
import com.nyotek.dot.admin.databinding.NsActivitySignupBinding

class NSSignUpActivity : NSActivity() {
    private lateinit var signupBinding: NsActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = NsActivitySignupBinding.inflate(layoutInflater)
        setContentView(signupBinding.root)
        loadInitialFragment()
    }

    /**
     * To initialize signUp fragment
     *
     */
    private fun loadInitialFragment() {
        replaceCurrentFragment(NSSignUpFragment.newInstance(), false, signupBinding.signupContainer.id)
    }
}