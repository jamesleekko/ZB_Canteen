package com.znhst.xtzb.utils

import android.util.Base64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

val publickey = "MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBANL378k3RiZHWx5AfJqdH9xRNBmD9wGD\n" + "2iRe41HdTNF8RUhNnHit5NpMNtGL0NPTSSpPjjI1kJfVorRvaQerUgkCAwEAAQ=="

fun getPublicKey(publicKeyStr: String): PublicKey {
    val keyBytes = Base64.decode(publicKeyStr, Base64.DEFAULT)
    val keySpec = X509EncodedKeySpec(keyBytes)
    val keyFactory = KeyFactory.getInstance("RSA")
    return keyFactory.generatePublic(keySpec)
}

fun rsaEncrypt(input: String, publicKey: PublicKey): String {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.ENCRYPT_MODE, publicKey)
    val encryptedBytes = cipher.doFinal(input.toByteArray(Charsets.UTF_8))
    return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
}