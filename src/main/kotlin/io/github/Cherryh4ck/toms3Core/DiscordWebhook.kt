package io.github.Cherryh4ck.toms3Core

import java.net.HttpURLConnection
import java.net.URI

// this shit is so fucking vibecoded
// my ass cant really make this shit, i fucking hate this >:(
object DiscordWebhook {
    fun sendDiscordWebhook(webhookUrl: String, message: String) {
        val uri = URI(webhookUrl)
        val url = uri.toURL()
        val connection = url.openConnection() as HttpURLConnection

        connection.apply {
            requestMethod = "POST"
            doOutput = true
            addRequestProperty("Content-Type", "application/json")
            addRequestProperty("User-Agent", "Java-Discord-Webhook")
        }

        val jsonBody = """
        {
            "content": "$message"
        }
        """.trimIndent()

        try {
            connection.outputStream.use { it.write(jsonBody.toByteArray()) }
            val responseCode = connection.responseCode
            if (responseCode !in 200..299) {
                println("Error webhook - $responseCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection.disconnect()
        }
    }
}