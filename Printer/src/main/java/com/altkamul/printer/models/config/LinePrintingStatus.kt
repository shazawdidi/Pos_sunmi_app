package com.altkamul.printer.models.config

/**
 * LinePrintingStatus : object identify last printed line on paper status
 */

/**
 * @param printed true : if current printing model in printing queue has been printed on paper
 * @param errorMessage : null if printed = true
 *
 * @param printed false : if printer have an issue while it printing current model
 * @param errorMessage :  a string contain error reason
 */
data class LinePrintingStatus(
    var printed: Boolean = false,
    var errorMessage: String? = "UnKnown Error "
)
