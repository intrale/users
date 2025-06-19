package ar.com.intrale

data class ChangePasswordRequest(val previousPassword: String, val proposedPassword: String)
