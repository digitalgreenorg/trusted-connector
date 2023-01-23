/*-
 * ========================LICENSE_START=================================
 * camel-processors
 * %%
 * Copyright (C) 2023 Fraunhofer AISEC
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * =========================LICENSE_END==================================
 */
package de.fhg.aisec.ids.camel.processors.multipart

import com.google.common.collect.MapMaker
import de.fhg.aisec.ids.idscp2.daps.aisecdaps.AisecDapsDriver.Companion.toHexString
import org.apache.camel.component.http.HttpClientConfigurer
import org.apache.http.HttpResponse
import org.apache.http.conn.ManagedHttpClientConnection
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.HttpCoreContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.security.cert.Certificate

@Component("certExposingHttpClientConfigurer")
class CertExposingHttpClientConfigurer : HttpClientConfigurer {
    override fun configureHttpClient(clientBuilder: HttpClientBuilder) {
        clientBuilder.addInterceptorLast { response: HttpResponse, context ->
            val routedConnection = context.getAttribute(HttpCoreContext.HTTP_CONNECTION) as ManagedHttpClientConnection
            routedConnection.sslSession?.let { sslSession ->
                val certs = sslSession.peerCertificates
                val certHash = MessageDigest.getInstance("SHA-256").digest(certs[0].encoded).toHexString().lowercase()
                certificateMap += certHash to certs
                response.setHeader(SERVER_CERTIFICATE_HASH_HEADER, certHash)
                if (LOG.isDebugEnabled) {
                    LOG.debug("Captured server certificate with SHA256 fingerprint $certHash.")
                }
            }
        }
    }

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(CertExposingHttpClientConfigurer::class.java)
        const val SERVER_CERTIFICATE_HASH_HEADER = "ServerCertificateHash"
        val certificateMap: MutableMap<String, Array<Certificate>> = MapMaker().weakKeys().makeMap()
    }
}
