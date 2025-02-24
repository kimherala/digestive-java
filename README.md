# digestive-java
Digestive(java version) is a script style program without a GUI.
Digestive digests files and produces hashes of different types.

Digestive is most useful when you need to get a file digest in a specific encoding(raw, hex, base64 or octal).
Digests are usually presented in hex encoding.

On a Linux systems it's simple to get file hashes of some types(sha256sum and others).
Digestive aims to bring most of the functionality, that can be achieved with a simple shell script, into a easy to use program.

### Planned features
#### Supported algorithms:
* ✅ MD5([RFC 1321](https://datatracker.ietf.org/doc/html/rfc1321))
* ✅ SHA1 and SHA2([FIPS PUB 180-4](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.180-4.pdf))
* ✅ SHA3([FIPS PUB 202](https://nvlpubs.nist.gov/nistpubs/FIPS/NIST.FIPS.202.pdf))
* ❌ BLAKE2([RFC 7693](https://datatracker.ietf.org/doc/html/rfc7693)) and BLAKE3([spec](https://github.com/BLAKE3-team/BLAKE3-specs/blob/master/blake3.pdf))

#### Supported encodings:
* ✅ int(raw bytes as integers)
* ✅ bin(raw bytes as binary strings)
* ✅ octal(base 8)
* ✅ hex
* ✅ base64

#### Other major features
* ✅ multithreading by default
* ✅ digesting whole directories at a time
* ❌ saving digests to a file
* ❌ saving digest files in a specific structured data format, like JSON or XML

### Basic usage
#### Hash current working directory:
java -jar digestive.jar -a SHA-256 -e base64

#### Hash custom path:
java -jar digestive.jar -a SHA-256 -e base64 -p ~/Downloads/

#### Hash single file(Coming soon(TM)):
java -jar digestive.jar -a SHA-256 -e base64 -f ~/Downloads/your.file

