package com.nyotek.dot.admin.ui.tabs.fleets

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nyotek.dot.admin.R

class FleetsFragmentDirections private constructor() {
    companion object {
        fun actionFleetToFleetDetail(bundle: Bundle?): NavDirections {
            return object : NavDirections {
                override val actionId: Int
                    get() = R.id.action_fleetsFragment_to_fleetsDetailFragment
                override val arguments: Bundle
                    get() = bundle?:Bundle()
            }
        }
    }
}