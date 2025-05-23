package ar.com.intrale

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.sdk.kotlin.services.cognitoidentityprovider.model.ListUsersRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import net.datafaker.Faker
import org.slf4j.Logger

class SignUpPlatformAdmin(override val config: UsersConfig, override val faker: Faker, override val logger: Logger) :
                SignUp(config = config, faker = faker, logger = logger) {

    override fun getProfile() : String {
        return PROFILE_PLATFORM_ADMIN
    }


    override suspend fun execute(
        business: String,
        function: String,
        headers: Map<String, String>,
        textBody: String
    ): Response {
        // Validamos si ya existe algun usuario y lanzamos un error
        // Solo se permite utilizar en la creacion del primer usuario
        CognitoIdentityProviderClient {
            region = config.region
            credentialsProvider = StaticCredentialsProvider(Credentials(
                accessKeyId = config.accessKeyId,
                secretAccessKey = config.secretAccessKey
            ))
        }.use { identityProviderClient ->
           val response = identityProviderClient.listUsers(ListUsersRequest{
               userPoolId = config.awsCognitoUserPoolId
           })
           if (response.users?.isEmpty() == true){
               return super.execute(business, function, headers, textBody)
           }
        }
        return UnauthorizeExeption()
    }


}