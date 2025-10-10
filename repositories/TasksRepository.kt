package br.com.repositories

import kotlin.collections.mutableListOf
import br.com.models.Task
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializer


class TaskRepository {

    val task get() = _tasks.toList()

    fun save(task: Task): Boolean{
        if (task.title.isNotEmpty() and task.description.isNotEmpty() and task.userId.isNotEmpty()){
            _tasks.add(task)
            return true
        }
        else{
            return false
        }
    }
    companion object{
        private val _tasks = mutableListOf<Task>()
    }
}