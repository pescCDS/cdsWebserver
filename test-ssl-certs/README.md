This directory contains a keystore with certificates that are intended to be used by EDExchange senders where the sender will use a private key to sign a document before sending it.  The SSL certificate that contains the paired public key, will be stored on the Directory server, so that when a receiver receives the document, the SSL certificate and public key can be used to verify the authenticity of the document and that the sender is a valid EDExchange member. 

I used the following commands, using openssl and the Java keytool to create test keys and self signed certificate for testing...

openssl req -x509 -newkey rsa:2048 -keyout test_key.pem -out test_certifiate.pem -days 3650
openssl pkcs12 -export -in test_certificate.pem -inkey test_key.pem -out test.p12 -name test
keytool -importkeystore -destkeystore test.jks -srckeystore test.p12 -srcstoretype pkcs12 -alias test

The resulting "test.jks" keystore contains the private key and associated certficate intended to be used to sign documents.


