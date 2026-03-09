// data/local/db/AppDatabase.kt
// Stage 6 — SQLCipher-encrypted Room Database
package com.hacksecure.messenger.data.local.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

// ══════════════════════════════════════════════════════════════════════════════
// ENTITIES
// ══════════════════════════════════════════════════════════════════════════════

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey val id: String,
    val identityHashHex: String,
    val publicKeyBase64: String,
    val displayName: String,
    val verifiedAt: Long,
    val keyChangedAt: Long?
)

@Entity(
    tableName = "messages",
    indices = [
        Index("conversationId"),
        Index("expiryMs"),
        Index("counter")
    ]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val conversationId: String,
    val senderIdHex: String,
    val ciphertextBlob: ByteArray,          // re-encrypted with per-message storage key
    val storageKeyAlias: String,            // Android Keystore alias
    val headerJson: String,
    val timestampMs: Long,
    val counter: Long,
    val expiryMs: Long?,                    // null = no expiry
    val isDecryptable: Boolean = true,      // false after storage key deletion
    val isOutgoing: Boolean,
    val messageType: String = "TEXT",       // TEXT | VOICE_NOTE (Phase 2)
    val messageState: String = "SENT"       // mirrors MessageState enum name
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MessageEntity) return false
        return id == other.id
    }
    override fun hashCode(): Int = id.hashCode()
}

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey val id: String,             // conversationId
    val contactId: String,
    val lastMessageAt: Long,
    val unreadCount: Int
)

@Entity(tableName = "local_identity")
data class LocalIdentityEntity(
    @PrimaryKey val id: Int = 1,            // singleton
    val publicKeyBase64: String,
    val identityHashHex: String,
    val createdAt: Long
)

// ══════════════════════════════════════════════════════════════════════════════
// TYPE CONVERTERS
// ══════════════════════════════════════════════════════════════════════════════

class Converters {
    @TypeConverter
    fun fromByteArray(value: ByteArray?): String? =
        value?.let { android.util.Base64.encodeToString(it, android.util.Base64.NO_WRAP) }

    @TypeConverter
    fun toByteArray(value: String?): ByteArray? =
        value?.let { android.util.Base64.decode(it, android.util.Base64.NO_WRAP) }
}

// ══════════════════════════════════════════════════════════════════════════════
// DAOs
// ══════════════════════════════════════════════════════════════════════════════

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY displayName ASC")
    fun getAllContacts(): kotlinx.coroutines.flow.Flow<List<ContactEntity>>

    @Query("SELECT * FROM contacts WHERE id = :id")
    suspend fun getContact(id: String): ContactEntity?

    @Query("SELECT * FROM contacts WHERE identityHashHex = :hex")
    suspend fun getContactByIdentityHash(hex: String): ContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ContactEntity)

    @Update
    suspend fun updateContact(contact: ContactEntity)

    @Query("DELETE FROM contacts WHERE id = :id")
    suspend fun deleteContact(id: String)
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY counter ASC")
    fun getMessages(conversationId: String): kotlinx.coroutines.flow.Flow<List<MessageEntity>>

    @Query("SELECT * FROM messages WHERE id = :id")
    suspend fun getMessage(id: String): MessageEntity?

    @Query("SELECT * FROM messages WHERE expiryMs IS NOT NULL AND expiryMs <= :nowMs AND isDecryptable = 1")
    suspend fun getExpiredMessages(nowMs: Long): List<MessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Query("UPDATE messages SET ciphertextBlob = x'00', isDecryptable = 0 WHERE id = :id")
    suspend fun zeroizeAndMarkUnrecoverable(id: String)

    @Query("DELETE FROM messages WHERE id = :id")
    suspend fun deleteMessage(id: String)

    @Query("DELETE FROM messages WHERE conversationId = :conversationId")
    suspend fun deleteConversationMessages(conversationId: String)

    @Query("SELECT MAX(counter) FROM messages WHERE conversationId = :conversationId AND senderIdHex = :senderHex")
    suspend fun getMaxCounter(conversationId: String, senderHex: String): Long?

    /** Persists the delivery state of a message (SENT, FAILED, DELIVERED, etc.). */
    @Query("UPDATE messages SET messageState = :state WHERE id = :id")
    suspend fun updateMessageState(id: String, state: String)

    /** Returns outgoing messages in FAILED state — used by the retry mechanism. */
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId AND isOutgoing = 1 AND messageState = 'FAILED' ORDER BY counter ASC")
    suspend fun getFailedMessages(conversationId: String): List<MessageEntity>
}

@Dao
interface ConversationDao {
    @Query("SELECT * FROM conversations ORDER BY lastMessageAt DESC")
    fun getAllConversations(): kotlinx.coroutines.flow.Flow<List<ConversationEntity>>

    @Query("SELECT * FROM conversations WHERE id = :id")
    suspend fun getConversation(id: String): ConversationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(conversation: ConversationEntity)

    @Query("DELETE FROM conversations WHERE id = :id")
    suspend fun deleteConversation(id: String)

    @Query("UPDATE conversations SET unreadCount = 0 WHERE id = :id")
    suspend fun markAsRead(id: String)
}

@Dao
interface LocalIdentityDao {
    @Query("SELECT * FROM local_identity WHERE id = 1")
    suspend fun getIdentity(): LocalIdentityEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveIdentity(identity: LocalIdentityEntity)

    @Query("DELETE FROM local_identity")
    suspend fun deleteAll()
}

// ══════════════════════════════════════════════════════════════════════════════
// DATABASE
// ══════════════════════════════════════════════════════════════════════════════

// ══════════════════════════════════════════════════════════════════════════════
// MIGRATIONS
// ══════════════════════════════════════════════════════════════════════════════

/**
 * v1 → v2: Added `messageState` column to track delivery status (SENT/FAILED/DELIVERED).
 * All existing messages default to 'SENT' so historical data is preserved.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE messages ADD COLUMN messageState TEXT NOT NULL DEFAULT 'SENT'"
        )
    }
}

@Database(
    entities = [
        ContactEntity::class,
        MessageEntity::class,
        ConversationEntity::class,
        LocalIdentityEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contactDao(): ContactDao
    abstract fun messageDao(): MessageDao
    abstract fun conversationDao(): ConversationDao
    abstract fun localIdentityDao(): LocalIdentityDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, passphrase: ByteArray): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                SQLiteDatabase.loadLibs(context)
                val factory = SupportFactory(passphrase)
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "hacksecure.db"
                )
                    .openHelperFactory(factory)
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}
