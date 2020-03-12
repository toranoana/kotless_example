package org.example.kotless.storage

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
import com.amazonaws.services.dynamodbv2.model.AttributeValue
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest
import com.amazonaws.services.dynamodbv2.model.PutItemRequest
import com.amazonaws.services.dynamodbv2.model.ScanRequest
import io.kotless.PermissionLevel
import io.kotless.dsl.lang.DynamoDBTable
import org.slf4j.LoggerFactory

/** テーブル名：kotless_example_todo.*/
private const val TABLE_NAME_TODO = "kotless_example_todo"

@DynamoDBTable(TABLE_NAME_TODO, PermissionLevel.ReadWrite)
object TodoStorage {
    /** ロガー. */
    private val logger = LoggerFactory.getLogger(TodoStorage::class.java);

    private val clientBuilder = AmazonDynamoDBClientBuilder
            .standard().apply {
                region = "ap-northeast-1"
            }
    /** DynamoDbClient. */
    private val client = clientBuilder.build()

    /**
     * TODOリスト全件を取得する.
     *
     * @return TODOリスト全件
     */
    fun scan(): List<TodoData> {
        val scanRequest = ScanRequest().withTableName(TABLE_NAME_TODO)
        val scanResult = client.scan(scanRequest)

        val toDoList = mutableListOf<TodoData>()

        if (scanResult == null) {
            return toDoList
        }

        for (item in scanResult.items) {
            val toDoId = if (item["todo_id"] != null) {
                item["todo_id"]?.n ?: ""
            } else {
                ""
            }.toLongOrNull()
            val toDoValue = if (item["todo_value"] != null) {
                item["todo_value"]?.s ?: ""
            } else {
                ""
            }
            if (toDoId != null) {
                toDoList.add(TodoData(toDoId = toDoId, toDoValue = toDoValue))
            }
        }
        if (toDoList.isNotEmpty()) {
            toDoList.sortByDescending { it.toDoId }
        }
        return toDoList
    }

    /**
     * TODOを登録する.
     *
     * @param toDoValue TODO内容
     *
     * @return true:登録成功, false:登録失敗
     */
    fun createTodo(toDoValue: String): Boolean {
        try {
            val maxIdToDoData = scan().maxBy { it.toDoId }
            val nextId = if (maxIdToDoData == null) {
                0L
            } else {
                maxIdToDoData.toDoId + 1
            }

            val values = mapOf(
                    "todo_id" to
                            AttributeValue().apply { n = nextId.toString() },
                    "todo_value" to
                            AttributeValue().apply { s = toDoValue }
            )
            val request = PutItemRequest()
                    .withItem(values).withTableName(TABLE_NAME_TODO)

            client.putItem(request)
        } catch (e: Exception) {
            logger.warn("createTodo Method Error.", e)
            return false
        }
        return true
    }

    /**
     * 指定したToDoIdのTODOを削除する.
     *
     * @param toDoId ToDoID
     *
     */
    fun deleteTodo(toDoId: Long) {
        try {

            val request = DeleteItemRequest().withKey(mapOf(
                    "todo_id" to
                            AttributeValue().apply { n = toDoId.toString() }
            )).withTableName(TABLE_NAME_TODO)

            client.deleteItem(request)
        } catch (e: Exception) {
            logger.warn("deleteTodo Method Error.")
        }
    }
}
