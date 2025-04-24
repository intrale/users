package ar.com.intrale

data class UsersConfig(
    override val businesses: Set<String>,
    val region: String,
    val accessKeyId: String,
    val secretAccessKey: String,
    val awsCognitoUserPoolId: String,
    val awsCognitoClientId: String) : Config (businesses = businesses)
