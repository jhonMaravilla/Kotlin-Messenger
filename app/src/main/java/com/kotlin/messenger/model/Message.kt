package com.kotlin.messenger.model

data class Message(val id: String = "", val fromID: String= "", val toID: String= "", val text: String= "", val date: String= "", val time: String= "")