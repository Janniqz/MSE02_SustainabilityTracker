package de.janniqz.sustainabilitytracker.data.db
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.janniqz.sustainabilitytracker.data.db.converter.TaskCategoryConverter
import de.janniqz.sustainabilitytracker.data.db.converter.TaskTypeConverter
import de.janniqz.sustainabilitytracker.data.db.converter.TimePeriodConverter
import de.janniqz.sustainabilitytracker.data.db.dao.GoalDao
import de.janniqz.sustainabilitytracker.data.db.dao.TaskCompletionDao
import de.janniqz.sustainabilitytracker.data.db.dao.TaskDao
import de.janniqz.sustainabilitytracker.data.model.entity.GoalEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity

/**
 * Provides Database Access for the Application
 * @see RoomDatabase
 */
@Database(
    entities = [TaskEntity::class, TaskCompletionEntity::class, GoalEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(TaskCategoryConverter::class, TaskTypeConverter::class, TimePeriodConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun task(): TaskDao
    abstract fun taskCompletion(): TaskCompletionDao
    abstract fun goal(): GoalDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Provides access to a Singleton Instance of the AppDatabase
         */
        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sustainability_database"
                    ).fallbackToDestructiveMigration(true).build()  // TODO Remove destructive migration in future versions

                    INSTANCE = instance
                }
            }

            return INSTANCE!!
        }
    }
}