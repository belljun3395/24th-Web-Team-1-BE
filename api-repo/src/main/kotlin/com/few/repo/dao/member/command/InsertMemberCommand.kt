package com.few.repo.dao.member.command

import com.few.data.common.code.MemberType

data class InsertMemberCommand(
    val email: String,
    val memberType: MemberType,
)