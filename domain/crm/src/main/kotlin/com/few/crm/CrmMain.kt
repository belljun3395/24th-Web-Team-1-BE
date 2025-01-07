package com.few.crm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CrmMain

fun main(args: Array<String>) {
    runApplication<CrmMain>(*args)
}