package no.nav.security.mock.oauth2.templates

import freemarker.cache.ClassTemplateLoader
import freemarker.template.Configuration
import no.nav.security.mock.oauth2.OAuth2Config
import no.nav.security.mock.oauth2.extensions.toTokenEndpointUrl
import no.nav.security.mock.oauth2.http.OAuth2HttpRequest
import no.nav.security.mock.oauth2.http.objectMapper
import okhttp3.HttpUrl
import java.io.StringWriter

data class HtmlContent(
    val template: String,
    val model: Any?
)

class TemplateMapper(
    private val config: Configuration
) {

    fun loginHtml(oAuth2HttpRequest: OAuth2HttpRequest, userTokens: List<OAuth2Config.userToken>): String {
        val userConfig = getUserConfig(userTokens);
        return asString(
            HtmlContent(
                "login.ftl",
                mapOf(
                    "request_url" to oAuth2HttpRequest.url.newBuilder().query(null).build().toString(),
                    "query" to OAuth2HttpRequest.Parameters(oAuth2HttpRequest.url.query).map,
                    "user_config" to userConfig,
                ),
            ),
        )
    }

    fun debuggerCallbackHtml(tokenRequest: String, tokenResponse: String): String {
        return asString(
            HtmlContent(
                "debugger_callback.ftl",
                mapOf(
                    "token_request" to tokenRequest,
                    "token_response" to tokenResponse
                )
            )
        )
    }

    fun debuggerErrorHtml(debuggerUrl: HttpUrl, stacktrace: String) =
        asString(
            HtmlContent(
                "error.ftl",
                mapOf(
                    "debugger_url" to debuggerUrl,
                    "stacktrace" to stacktrace
                )
            )
        )

    fun debuggerFormHtml(url: HttpUrl, clientAuthMethod: String): String {
        val urlWithoutQuery = url.newBuilder().query(null)
        return asString(
            HtmlContent(
                "debugger.ftl",
                mapOf(
                    "url" to urlWithoutQuery,
                    "token_url" to url.toTokenEndpointUrl(),
                    "query" to OAuth2HttpRequest.Parameters(url.query).map,
                    "client_auth_method" to clientAuthMethod
                )
            )
        )
    }

    fun authorizationCodeResponseHtml(redirectUri: String, code: String, state: String): String =
        asString(
            HtmlContent(
                "authorization_code_response.ftl",
                mapOf(
                    "redirect_uri" to redirectUri,
                    "code" to code,
                    "state" to state
                )
            )
        )

    private fun getUserConfig(userTokens: List<OAuth2Config.userToken>): String {
        if (userTokens.isEmpty()) {
            return "[]";
        }
        return objectMapper.writeValueAsString(userTokens)
    }

    private fun asString(htmlContent: HtmlContent): String =
        StringWriter().apply {
            config.getTemplate(htmlContent.template).process(htmlContent.model, this)
        }.toString()

    companion object {
        fun create(configure: Configuration.() -> Unit): TemplateMapper {
            val config = Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
                .apply {
                    templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
                }
                .apply(configure)
            return TemplateMapper(config)
        }
    }
}
