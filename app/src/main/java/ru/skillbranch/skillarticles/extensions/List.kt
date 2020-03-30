package ru.skillbranch.skillarticles.extensions

fun List<Pair<Int, Int>>.groupByBounds(bounds: List<Pair<Int, Int>>): List<MutableList<Pair<Int, Int>>> =
    bounds.map { bound ->
        this.filter { it.second > bound.first && it.first < bound.second }
            .map { searchPosition ->
                when {
                    searchPosition.first < bound.first -> bound.first to searchPosition.second
                    searchPosition.second > bound.second -> searchPosition.first to bound.second
                    else -> searchPosition
                }
            }.toMutableList()
    }