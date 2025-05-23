package com.anytypeio.anytype.core_models.chats

data class PushKeyUpdate(
    val encryptionKeyId: String,
    val encryptionKey: String
) {
    companion object {
        val EMPTY = PushKeyUpdate(
            encryptionKeyId = "",
            encryptionKey = ""
        )
    }
}