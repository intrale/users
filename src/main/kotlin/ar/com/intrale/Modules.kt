package ar.com.intrale

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.cognitoidentityprovider.CognitoIdentityProviderClient
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import com.typesafe.config.ConfigFactory
import net.datafaker.Faker
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable
import software.amazon.awssdk.enhanced.dynamodb.TableSchema
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

private const val LOCAL_APP_AVAILABLE_BUSINESSES = "AVAILABLE_BUISNESS"
private const val LOCAL_AWS_REGION = "REGION_VALUE"
private const val LOCAL_AWS_ACCESS_KEY_ID = "ACCESS_KEY_ID"
private const val LOCAL_AWS_SECRET_ACCESS_KEY = "SECRET_ACCESS_KEY"
private const val LOCAL_AWS_COGNITO_USER_POOL_ID = "USER_POOL_ID"
private const val LOCAL_AWS_COGNITO_CLIENT_ID = "CLIENT_ID"

private const val APP_AVAILABLE_BUSINESSES = "app.availableBusinesses"
private const val AWS_REGION = "aws.region"
private const val AWS_ACCESS_KEY_ID = "aws.accessKeyId"
private const val AWS_SECRET_ACCESS_KEY = "aws.secretAccessKey"
private const val AWS_COGNITO_USER_POOL_ID = "aws.cognito.userPoolId"
private const val AWS_COGNITO_CLIENT_ID = "aws.cognito.clientId"

val appModule = DI.Module("appModule") {

    bind<CognitoIdentityProviderClient> {
        singleton {
            val config = instance<UsersConfig>()

            CognitoIdentityProviderClient {
                region = config.region
                credentialsProvider = StaticCredentialsProvider(Credentials(
                    accessKeyId = config.accessKeyId,
                    secretAccessKey = config.secretAccessKey
                ))
            }
        }
    }

    bind <DynamoDbClient> {
        singleton {
            val configFactory = ConfigFactory.load()

            DynamoDbClient.builder()
                .region(Region.of(System.getenv(LOCAL_AWS_REGION) ?: configFactory.getString(AWS_REGION)))
                .credentialsProvider(
                    software.amazon.awssdk.auth.credentials.StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            System.getenv(LOCAL_AWS_ACCESS_KEY_ID) ?: configFactory.getString(AWS_ACCESS_KEY_ID),
                            System.getenv(LOCAL_AWS_SECRET_ACCESS_KEY) ?: configFactory.getString(AWS_SECRET_ACCESS_KEY))
                    )
                )
                .build()
        }
    }

    bind <DynamoDbEnhancedClient> {
        singleton {
            DynamoDbEnhancedClient.builder()
                .dynamoDbClient(instance())
                .build()
        }
    }

    bind <DynamoDbTable<Business>>{
        singleton {
            instance<DynamoDbEnhancedClient>().table("business", TableSchema.fromBean(Business::class.java))
        }
    }

    bind <DynamoDbTable<User>>{
        singleton {
            instance<DynamoDbEnhancedClient>().table("users", TableSchema.fromBean(User::class.java))
        }
    }

    bind <DynamoDbTable<UserBusinessProfile>>{
        singleton {
            instance<DynamoDbEnhancedClient>().table("userbusinessprofile", TableSchema.fromBean(UserBusinessProfile::class.java))
        }
    }

    bind <UsersConfig> {
        singleton {
            val configFactory = ConfigFactory.load()

            val acceptedBusinessNames: Set<String> = instance<DynamoDbTable<Business>>().scan()
                .items()
                .filter { it.state == BusinessState.APPROVED }
                .mapNotNull { it.name }
                .toSet() + setOf("intrale")

            UsersConfig(
                businesses = acceptedBusinessNames,
                region = System.getenv(LOCAL_AWS_REGION) ?: configFactory.getString(AWS_REGION),
                accessKeyId = System.getenv(LOCAL_AWS_ACCESS_KEY_ID) ?: configFactory.getString(AWS_ACCESS_KEY_ID),
                secretAccessKey = System.getenv(LOCAL_AWS_SECRET_ACCESS_KEY) ?: configFactory.getString(AWS_SECRET_ACCESS_KEY),
                awsCognitoUserPoolId = System.getenv(LOCAL_AWS_COGNITO_USER_POOL_ID) ?: configFactory.getString(AWS_COGNITO_USER_POOL_ID),
                awsCognitoClientId = System.getenv(LOCAL_AWS_COGNITO_CLIENT_ID) ?: configFactory.getString(AWS_COGNITO_CLIENT_ID),
            )
        }
    }

    bind<Faker> {
        singleton  {
            Faker()
        }
    }

    bind <Logger> {
        singleton { LoggerFactory.getLogger("AppLogger") }
    }

    bind<Function> (tag="signup") {
        singleton  { SignUp(instance(), instance(), instance()) }
    }
    bind<Function> (tag="signupPlatformAdmin") {
        singleton  { SignUpPlatformAdmin(instance(), instance(), instance()) }
    }
    bind<Function> (tag="signupDelivery") {
        singleton  { SignUpDelivery(instance(), instance(), instance()) }
    }
    bind<Function> (tag="signin") {
        singleton {  SignIn(instance(), instance(), instance()) }
    }
    bind<Function> (tag="validate") {
        singleton {  Validate(instance(), instance()) }
    }
    bind<Function> (tag="recovery") {
        singleton {  PasswordRecovery(instance(), instance(), instance()) }
    }
    bind<Function> (tag="confirm") {
        singleton {  ConfirmPasswordRecovery(instance(), instance(), instance()) }
    }
    bind<Function> (tag="profiles") {
        singleton {  Profiles(instance(), instance()) }
    }
    bind<Function> (tag="2fasetup") {
        singleton {  TwoFactorSetup(instance(), instance(), instance(), instance()) }
    }
    bind<Function> (tag="2faverify") {
        singleton {  TwoFactorVerify(instance(), instance(), instance(), instance()) }
    }
    bind<Function> (tag="registerBusiness") {
        singleton {  RegisterBusiness(instance(), instance(), instance()) }
    }
    bind<Function> (tag="reviewBusiness") {
        singleton {  ReviewBusinessRegistration(instance(), instance(), instance("2faverify"),
            instance("signup"), instance(),
            instance(), instance(),instance()) }
    }
}