package com.few.repo.dao.member.record

data class MemberIdAndIsDeletedRecord(
    val memberId: Long,
    val isDeleted: Boolean,
)