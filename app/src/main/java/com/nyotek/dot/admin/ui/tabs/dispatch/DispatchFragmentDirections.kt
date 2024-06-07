package com.nyotek.dot.admin.ui.tabs.dispatch

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nyotek.dot.admin.R

class DispatchFragmentDirections private constructor() {
    companion object {
        fun actionDispatchToDispatchDetail(bundle: Bundle?): NavDirections {
            return object : NavDirections {
                override val actionId: Int
                    get() = R.id.action_dispatchFragment_to_dispatchDetailFragment
                override val arguments: Bundle
                    get() = bundle?:Bundle()
            }
        }
    }
}