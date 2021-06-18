package com.qoli.smssender

object appUnits {
     fun simStatetoText(value: Int?): String {
        when (value) {
            1 -> return "Absent"
            2 -> return "PinRequired"
            3 -> return "PukRequired"
            4 -> return "NetworkLocked"
            5 -> return "Ready"
            6 -> return "NotReady"
            7 -> return "PermDisabled"
            8 -> return "CardIoError"
            9 -> return "CardRestricted"

            else -> {
                return "unknown"
            }
        }
    }
}
