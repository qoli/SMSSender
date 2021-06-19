package com.qoli.smssender.app

object AppUnits {
     fun simStatedText(value: Int?): String {
         return when (value) {
             1 -> "Absent"
             2 -> "PinRequired"
             3 -> "PukRequired"
             4 -> "NetworkLocked"
             5 -> "Ready"
             6 -> "NotReady"
             7 -> "PermDisabled"
             8 -> "CardIoError"
             9 -> "CardRestricted"

             else -> {
                 "unknown"
             }
         }
    }
}
