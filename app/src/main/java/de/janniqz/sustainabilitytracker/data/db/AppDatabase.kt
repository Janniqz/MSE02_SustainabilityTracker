
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.janniqz.sustainabilitytracker.data.db.converter.TaskCategoryConverter
import de.janniqz.sustainabilitytracker.data.db.converter.TaskTypeConverter
import de.janniqz.sustainabilitytracker.data.db.dao.TaskCompletionDao
import de.janniqz.sustainabilitytracker.data.db.dao.TaskDao
import de.janniqz.sustainabilitytracker.data.model.entity.TaskCompletionEntity
import de.janniqz.sustainabilitytracker.data.model.entity.TaskEntity

@Database(
    entities = [TaskEntity::class, TaskCompletionEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(TaskCategoryConverter::class, TaskTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // DAOs
    abstract fun task(): TaskDao
    abstract fun taskCompletion(): TaskCompletionDao

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(this) {
                    val instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "sustainability_database"
                    ).build()

                    INSTANCE = instance
                }
            }

            return INSTANCE!!
        }
    }
}