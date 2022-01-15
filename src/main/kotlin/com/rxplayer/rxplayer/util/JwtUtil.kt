package com.rxplayer.rxplayer.util

import com.rxplayer.rxplayer.security.BearerToken
import com.rxplayer.rxplayer.security.SecurityUserAdapter
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.core.io.ClassPathResource
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.security.Key
import java.security.KeyStore
import java.security.PublicKey
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*

@Component
class JwtUtil {
    private lateinit var privateKey: Key
    private lateinit var publicKey: PublicKey

    init {
        val resource = ClassPathResource("keys/server.jks")
        val keyStore: KeyStore = KeyStore.getInstance("PKCS12")
        keyStore.load(resource.inputStream, "jkspassword".toCharArray())
        privateKey = keyStore.getKey("jwtKey", "jkspassword".toCharArray())
        val certificate = keyStore.getCertificate("jwtKey")
        publicKey = certificate.publicKey
    }

    fun create(username: String): String {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(Date.from(Instant.now()))
            .setIssuer("RX-Player")
            .signWith(privateKey, SignatureAlgorithm.RS256)
            .setExpiration(Date.from(Instant.now().plus(15, ChronoUnit.MINUTES)))
            .compact()
    }

    fun getSubject(token: BearerToken): String {
        return Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token.value)
            .body
            .subject
    }

    fun validate(token: String, user: SecurityUserAdapter?): Boolean {
        val claims = Jwts.parserBuilder().setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .body

        val hasExpired = claims.expiration.after(Date.from(Instant.now()))
        return hasExpired && (claims.subject == user?.user?.email)
    }

}