package br.com.models

import kotlinx.serialization.Serializable

@Serializable
class Task(
    val title: String,
    val description: String,
    val userId: String,
    val dataCreated: String,
    val dataUpdate: String,
    val type: String
)