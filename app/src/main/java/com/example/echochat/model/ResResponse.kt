package com.example.echochat.model


data class ResResponse<T>(
    val statusCode: Int,
    val error: String,
    val data: T,
    val message: Any
)

data class ResponseWrapper(
    val listResponse: List<ResponseList>
)

data class ResponseList(
    val responses: List<SingleResponse>
)

data class SingleResponse(
    val response: String
)
