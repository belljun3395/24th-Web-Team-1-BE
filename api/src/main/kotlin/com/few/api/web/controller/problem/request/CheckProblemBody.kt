package com.few.api.web.controller.problem.request

import jakarta.validation.constraints.NotBlank

data class CheckProblemBody(
    @field:NotBlank(message = "{sub.notblank}")
    val sub: String
)