package com.altkamul.printer.core



object LineUtils {
    private const val SPACE =  " "
    internal const val ASTERISK: String = "*"
    internal const val DASH: String = "-"
    internal const val EMPTY_LINE: String = "\n"

    fun getLineOfChar(lineCount: Int, char: String): String {
        var line = ""
        for (i in 0 until lineCount) line += char
        return line 
    }

    fun  convertTextToLine(textToConvert: String, maxCharCountInLine: Int): MutableList<String> {
        val lineList = mutableListOf<String>()
        if (textToConvert.length > maxCharCountInLine) {
            // new line starting index ( last separated text end index)
            var startIndex = 0
            // while text still have more
            while (startIndex < textToConvert.length) {
                // let endIndex = last separated text end index + maxCharCountInLine
                var endIndex = startIndex + maxCharCountInLine
                // make sure that end index to greater than textToConvert length
                endIndex = if (endIndex > textToConvert.length) textToConvert.length else endIndex
                lineList.add(textToConvert.substring(startIndex, endIndex))
                // to get next line : make new line starting index = previous line end index
                startIndex = endIndex
            }
        } else {
           lineList.add(textToConvert)
        }
        return lineList

    }
    @JvmStatic
    fun getCenterdLine(text: String , maxCharCountInLine: Int): String {
        // if text more than line or equal then return same text
       val trimedText = text.trim()
        if (trimedText.length>=maxCharCountInLine)
            return trimedText
      // get available space in line
        val avSpace = maxCharCountInLine - trimedText.length
        // half available space + text + half available space
        return getSpace(avSpace/2 ) + trimedText + getSpace(avSpace/2)
    }


    private fun  getSpace(count :Int): String {
        var line = ""
        for (i in 0 until count - 1) line += SPACE
        return line
    }
}