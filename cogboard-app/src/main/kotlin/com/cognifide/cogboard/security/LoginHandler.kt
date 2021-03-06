package com.cognifide.cogboard.security

import com.cognifide.cogboard.CogboardConstants
import com.cognifide.cogboard.storage.VolumeStorageFactory
import io.knotx.server.api.handler.RoutingHandlerFactory
import io.vertx.core.Handler
import io.vertx.core.json.JsonArray
import io.vertx.core.json.JsonObject
import io.vertx.ext.auth.KeyStoreOptions
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.jwt.JWTOptions
import io.vertx.reactivex.core.Vertx
import io.vertx.reactivex.ext.auth.jwt.JWTAuth
import io.vertx.reactivex.ext.web.RoutingContext

class LoginHandler : RoutingHandlerFactory {

    private var vertx: Vertx? = null
    private var admins: MutableMap<String, String> = mutableMapOf()
    private lateinit var config: JsonObject

    override fun getName(): String = "login-handler"

    override fun create(vertx: Vertx?, config: JsonObject?): Handler<RoutingContext> {
        this.vertx = vertx
        this.config = config ?: JsonObject()
        loadAdmins(VolumeStorageFactory.admins().loadConfig().getJsonArray("admins") ?: JsonArray())
        val wrongUserMsg = config?.getString("wrongUserMsg") ?: "Please, enter correct Username"
        val wrongPassMsg = config?.getString("wrongPassMsg") ?: "Please, enter correct Password"

        return Handler { ctx ->
            ctx.bodyAsJson?.let {
                val user = it.getString("username", "")
                val password = it.getString("password", "")
                when {
                    isNotExisting(user) -> sendUnauthorized(ctx, wrongUserMsg)
                    isNotAuthorized(user, password) -> sendUnauthorized(ctx, wrongPassMsg)
                    else -> sendJWT(ctx, user)
                }
            }
        }
    }

    private fun loadAdmins(jsonArray: JsonArray) {
        jsonArray.stream()
                .map { it as JsonObject }
                .forEach {
                    admins[it.getString("name")] = it.getString("pass")
                }
    }

    private fun sendJWT(ctx: RoutingContext, user: String) {
        ctx.response().end(generateJWT(user))
    }

    private fun sendUnauthorized(ctx: RoutingContext, message: String) {
        ctx.response().setStatusMessage(message).setStatusCode(CogboardConstants.STATUS_CODE_401).end()
    }

    private fun isNotExisting(user: String): Boolean {
        return admins[user] == null
    }

    private fun isNotAuthorized(user: String, password: String): Boolean {
        return password.isBlank() || admins[user] != password
    }

    private fun generateJWT(username: String): String {
        val keyStore = KeyStoreOptions()
                .setType(config.getString("type", "jceks"))
                .setPath(config.getString("path", "keystore.jceks"))
                .setPassword(config.getString("password", "secret"))

        val config = JWTAuthOptions().setKeyStore(keyStore)
        val jwtAuth = JWTAuth.create(vertx, config)

        val token = jwtAuth?.generateToken(
                JsonObject().put("name", username),
                JWTOptions().setExpiresInSeconds(SESSION_DURATION_IN_SECONDS)
        ) ?: "no data"
        return "{\"token\":\"Bearer $token\"}"
    }

    companion object {
        private const val SESSION_DURATION_IN_SECONDS = 2 * 60 * 60 // hours * min * sec
    }
}
