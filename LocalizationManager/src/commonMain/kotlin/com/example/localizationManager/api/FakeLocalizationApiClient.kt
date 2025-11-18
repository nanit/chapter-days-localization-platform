package com.example.localizationManager.api

import kotlinx.coroutines.delay

/**
 * Fake implementation of LocalizationApiClient for manual testing.
 * Contains mock data for multiple locales.
 */
class FakeLocalizationApiClient(
    private val simulateDelay: Boolean = true,
    private val delayMillis: Long = 500L
) : LocalizationApiClient {

    private val mockData = mapOf(
        "en" to mapOf(
            "welcome_message" to "Welcome to our app!",
            "login_button" to "Login",
            "signup_button" to "Sign Up",
            "settings_title" to "Settings",
            "profile_title" to "Profile",
            "logout_button" to "Logout",
            "greeting" to "Hello",
            "goodbye" to "Goodbye"
        ),
        "es" to mapOf(
            "welcome_message" to "¡Bienvenido a nuestra aplicación!",
            "login_button" to "Iniciar sesión",
            "signup_button" to "Registrarse",
            "settings_title" to "Configuración",
            "profile_title" to "Perfil",
            "logout_button" to "Cerrar sesión",
            "greeting" to "Hola",
            "goodbye" to "Adiós"
        ),
        "fr" to mapOf(
            "welcome_message" to "Bienvenue dans notre application!",
            "login_button" to "Connexion",
            "signup_button" to "S'inscrire",
            "settings_title" to "Paramètres",
            "profile_title" to "Profil",
            "logout_button" to "Déconnexion",
            "greeting" to "Bonjour",
            "goodbye" to "Au revoir"
        ),
        "he" to mapOf(
            "welcome_message" to "ברוכים הבאים לאפליקציה שלנו!",
            "login_button" to "התחברות",
            "signup_button" to "הרשמה",
            "settings_title" to "הגדרות",
            "profile_title" to "פרופיל",
            "logout_button" to "התנתקות",
            "greeting" to "שלום",
            "goodbye" to "להתראות"
        ),
        "de" to mapOf(
            "welcome_message" to "Willkommen in unserer App!",
            "login_button" to "Anmelden",
            "signup_button" to "Registrieren",
            "settings_title" to "Einstellungen",
            "profile_title" to "Profil",
            "logout_button" to "Abmelden",
            "greeting" to "Hallo",
            "goodbye" to "Auf Wiedersehen"
        )
    )

    override suspend fun fetchStrings(locale: String): Map<String, String> {
        // Simulate network delay
        if (simulateDelay) {
            delay(delayMillis)
        }

        println("FakeLocalizationApiClient: Fetching strings for locale: $locale")

        // Return mock data for the requested locale, or empty map if not found
        val result = mockData[locale] ?: emptyMap()

        println("FakeLocalizationApiClient: Returning ${result.size} strings for locale: $locale")

        return result
    }

    override fun close() {
        println("FakeLocalizationApiClient: Closed")
    }

    /**
     * Add or update strings for a specific locale (useful for testing)
     */
    fun addMockStrings(locale: String, strings: Map<String, String>) {
        (mockData as MutableMap)[locale] = strings
    }

    /**
     * Get all available locales in the mock data
     */
    fun getAvailableLocales(): Set<String> = mockData.keys
}