package br.com.models

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
class Task(
    val title: String,
    val description: String,
    val UserId: Long
)