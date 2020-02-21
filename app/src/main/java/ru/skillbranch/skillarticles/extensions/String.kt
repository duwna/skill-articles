package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    if (this == null || substr.isBlank()) return emptyList()
    val list = mutableListOf<Int>()
    var currentIdx = 0
    var nextIdx: Int
    while (true) {
        nextIdx = indexOf(substr, currentIdx, ignoreCase)
        if (nextIdx == -1) break
        list.add(nextIdx)
        currentIdx = nextIdx + substr.length
    }
    return list
}