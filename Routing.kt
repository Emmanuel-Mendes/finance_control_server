package br.com

import br.com.models.Task
import br.com.repositories.TaskRepository
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.receive
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.ExperimentalSerializationApi


fun Application.configureRouting() {

    val repository = TaskRepository()

    routing {
        get("/tasks") {
            call.respond(repository.task)
        }
        post(path = "/tasks"){
            val task = call.receive<Task>()
            repository.save(task)
            call.respondText("Task was created", status = HttpStatusCode.Created)
        }
    }
}
