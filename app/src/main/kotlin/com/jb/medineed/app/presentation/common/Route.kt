package com.jb.medineed.app.presentation.common

object Route {

    const val HOME = "home"

    const val SETTINGS_PAGE = "settings_page"

    const val APPEARANCE = "appearance"
    const val ABOUT = "about"
    const val CREDITS = "credits"
    const val LANGUAGES = "languages"
    const val DARK_THEME = "dark_theme"
    const val SETTINGS = "settings"

    const val STOCK_LIST = "stock_list"
    const val MEDICINE_ENTRY = "medicine_entry"

    const val LOW_STOCK = "low_stock"
    const val OUT_OF_STOCK = "out_of_stock"
    const val REPORTS = "reports"

    const val EDIT_MEDICINE = "edit_medicine"
    const val STOCK_UPDATE = "stock_update"

    const val MEDICINE_ID = "medicineId"
}

infix fun String.arg(arg: String) = "$this/{$arg}"
infix fun String.id(id: Long) = "$this/$id"