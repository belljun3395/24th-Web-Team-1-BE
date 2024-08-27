package com.few.repo.dao.member.query

data class SelectMemberByEmailNotConsiderDeletedAtQuery(
    val email: String,
)