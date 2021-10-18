package com.prince.wally.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.prince.wally.model.local.DatabaseMigration.V1.Favorite.tableName

@Entity(tableName = tableName)
data class Favorite(
    @PrimaryKey
    var url: String,

    @ColumnInfo(name = Favorite.Column.hit)
    var hit: String?
) {

    object Favorite {
        const val tableName = "favorites"

        object Column {
            const val url = "url"
            const val hit = "hit"
        }
    }
}