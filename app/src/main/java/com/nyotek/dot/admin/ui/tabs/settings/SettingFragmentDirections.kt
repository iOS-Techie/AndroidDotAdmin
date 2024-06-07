package com.nyotek.dot.admin.ui.tabs.settings

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nyotek.dot.admin.R

class SettingFragmentDirections private constructor() {
    companion object {
        fun actionSettingsToUserDetail(bundle: Bundle?): NavDirections {
            return object : NavDirections {
                override val actionId: Int
                    get() = R.id.action_settingFragment_to_userDetailFragment
                override val arguments: Bundle
                    get() = bundle?:Bundle()
            }
        }
    }
}