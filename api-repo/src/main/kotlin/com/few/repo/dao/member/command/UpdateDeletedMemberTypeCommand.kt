package com.few.repo.dao.member.command

import com.few.data.common.code.MemberType

data class UpdateDeletedMemberTypeCommand(
    val id: Long,
    val memberType: MemberType,
)