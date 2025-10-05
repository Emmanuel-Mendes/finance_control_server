package br.com.repositories

import kotlin.collections.mutableListOf
import br.com.models.Task
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer


class TaskRepository {

    val task get() = _tasks.toList()

    fun save(task: Task){
        _tasks.add(task)
    }
    companion object{
        private val _tasks = mutableListOf<Task>()
    }
}