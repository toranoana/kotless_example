package org.example.kotless

import io.kotless.dsl.ktor.Kotless
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.request.receiveParameters
import io.ktor.response.respondRedirect
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.post
import io.ktor.routing.routing
import org.example.kotless.pages.Todo.toDo
import org.example.kotless.storage.TodoStorage


// Kotlessクラスを継承
class Server : Kotless() {

    // prepareメソッドをオーバーライドして実処理を記述します
    override fun prepare(app: Application) {
        // ルーティング設定
        app.routing {
            // ルートURLにGetでアクセス
            get("/") {
                // Hello World!の文字列を返す
                call.respondText { "Hello World!" }
            }

            // ToDoリストのルーティング
            get("/todo") {
                val toDoList = TodoStorage.scan()
                call.respondHtml {
                    toDo(toDoList)
                }
            }

            // ToDoリスト追加のルーティング
            post("/todo/add") {
                val toDoValue = call.receiveParameters()["todo_value"] ?: ""
                if (toDoValue.isNotEmpty()) {
                    TodoStorage.createTodo(toDoValue)
                }
                call.respondRedirect("/todo")
            }

            // ToDoリスト削除のルーティング
            post("/todo/delete") {
                val toDoId = call.receiveParameters()["todo_id"] ?: ""
                if (toDoId.toLongOrNull() != null) {
                    TodoStorage.deleteTodo(toDoId.toLong())
                }
                call.respondRedirect("/todo")
            }
        }
    }
}