package de.yanos.chat.domain.database

import androidx.room.Dao
import de.yanos.chat.data.Member
import de.yanos.corelibrary.base.BaseDao

@Dao
interface MemberDao : BaseDao<Member> {
}