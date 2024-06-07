package com.nyotek.dot.admin.ui.tabs.fleets.employee

import android.os.Bundle
import androidx.navigation.NavDirections
import com.nyotek.dot.admin.R

class EmployeeFragmentDirections private constructor() {
    companion object {
        fun actionFleetDetailToDriverDetail(bundle: Bundle?): NavDirections {
            return object : NavDirections {
                override val actionId: Int
                    get() = R.id.action_fleetsDetailFragment_to_driverDetailFragment
                override val arguments: Bundle
                    get() = bundle?:Bundle()
            }
        }

        fun actionFleetDetailToVehicleDetail(bundle: Bundle?): NavDirections {
            return object : NavDirections {
                override val actionId: Int
                    get() = R.id.action_fleetsDetailFragment_to_vehicleDetailFragment
                override val arguments: Bundle
                    get() = bundle?:Bundle()
            }
        }
    }
}