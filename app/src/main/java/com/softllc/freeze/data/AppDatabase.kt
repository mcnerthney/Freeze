
package com.softllc.freeze.data

import android.content.Context
import android.content.ContextWrapper
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.softllc.freeze.utilities.DATABASE_NAME
import androidx.room.migration.Migration



/**
 * The Room database for this app
 */
@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }



        // Create and pre-populate the database. See this article for more details:
        // https://medium.com/google-developers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context): AppDatabase {
            val MIGRATION_1_2: Migration = object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    val cw = ContextWrapper(context)
                    val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
                    directory.delete()

                }
            }

            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigrationOnDowngrade()
                    .addMigrations(MIGRATION_1_2)
                    .build()
        }
    }
}
