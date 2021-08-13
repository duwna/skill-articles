package ru.skillbranch.skillarticles.data.local

import androidx.room.TypeConverter
import ru.skillbranch.skillarticles.data.repositories.MarkdownElement
import ru.skillbranch.skillarticles.data.repositories.MarkdownParser
import java.util.*

object DateConverter {
    @TypeConverter
    fun timestampToDate(timestamp: Long): Date = Date(timestamp)

    @TypeConverter
    fun dateToTimestamp(date: Date): Long = date.time
}

object MarkdownConverter {
    @TypeConverter
    fun toMarkdown(content: String?): List<MarkdownElement>? {
        return content?.let { MarkdownParser.parse(it) }
    }
}

object TagsConverter {
    @TypeConverter
    fun concatTagsToList(concatTags: String?): List<String> {
        return concatTags?.split(",") ?: emptyList()
    }
}

