package org.example.kotless.pages

import kotlinx.html.*
import org.example.kotless.storage.TodoData

object Todo {
    fun HTML.toDo(todoList: List<TodoData>) {
        head {
            title {
                +"Kotless-Todo"
            }
            link {
                rel = "stylesheet"
                href = "https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
            }
        }
        body {
            div {
                classes = setOf("container")
                h1 {
                    +"Kotless Todo"
                }
                div {
                    form("/todo/add", null, FormMethod.post) {
                        classes = setOf("form-inline")
                        div {
                            classes = setOf("form-group mb-2")
                            label {
                                classes = setOf("sr-only")
                                +"Todo"
                            }
                            textInput {
                                classes = setOf("form-control")
                                name = "todo_value"
                                maxLength = "50"
                                placeholder = "Write Todo"
                            }
                        }
                        submitInput {
                            classes = setOf("btn btn-primary mb-2")
                            value = "Add"
                        }
                    }
                }
                if (todoList.isNotEmpty()) {
                    div {
                        table {
                            classes = setOf("table", "table-striped")
                            thead {
                                tr {
                                    th { +"Todo" }
                                    th { +"Action" }
                                }
                            }
                            tbody {
                                for (toDo in todoList) {
                                    tr {
                                        td { +toDo.toDoValue }
                                        td {
                                            form("/todo/delete", null, FormMethod.post) {
                                                hiddenInput {
                                                    name = "todo_id"
                                                    value = toDo.toDoId.toString()
                                                }
                                                submitInput {
                                                    classes = setOf("btn", "btn-link")
                                                    value = "Delete"
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}