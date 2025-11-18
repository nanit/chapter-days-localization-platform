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

    private val mockData: Map<String, Map<String, String>> = buildMap {
        // Generate 1000 key-value pairs for 3 languages (en, es, fr)
        val count = 1000

        // English
        put("en", buildMap {
            for (i in 0 until count) {
                put("key_$i", "English text for key $i - This is a sample localization string")
            }
            // Keep original keys for compatibility
            put("welcome_message", "Welcome to our app!")
            put("login_button", "Login")
            put("signup_button", "Sign Up")
            put("settings_title", "Settings")
            put("profile_title", "Profile")
            put("logout_button", "Logout")
            put("greeting", "Hello")
            put("goodbye", "Goodbye")
        })

        // Spanish
        put("es", buildMap {
            for (i in 0 until count) {
                put("key_$i", "Texto en español para la clave $i - Esta es una cadena de localización de ejemplo")
            }
            // Keep original keys for compatibility
            put("welcome_message", "¡Bienvenido a nuestra aplicación!")
            put("login_button", "Iniciar sesión")
            put("signup_button", "Registrarse")
            put("settings_title", "Configuración")
            put("profile_title", "Perfil")
            put("logout_button", "Cerrar sesión")
            put("greeting", "Hola")
            put("goodbye", "Adiós")
        })

        // French
        put("fr", buildMap {
            for (i in 0 until count) {
                put("key_$i", "Texte français pour la clé $i - Ceci est un exemple de chaîne de localisation")
            }
            // Keep original keys for compatibility
            put("welcome_message", "Bienvenue dans notre application!")
            put("login_button", "Connexion")
            put("signup_button", "S'inscrire")
            put("settings_title", "Paramètres")
            put("profile_title", "Profil")
            put("logout_button", "Déconnexion")
            put("greeting", "Bonjour")
            put("goodbye", "Au revoir")
        })

        // Keep other languages with original data
        put("he", mapOf(
            "welcome_message" to "ברוכים הבאים לאפליקציה שלנו!",
            "login_button" to "התחברות",
            "signup_button" to "הרשמה",
            "settings_title" to "הגדרות",
            "profile_title" to "פרופיל",
            "logout_button" to "התנתקות",
            "greeting" to "שלום",
            "goodbye" to "להתראות"
        ))

        put("de", mapOf(
            "welcome_message" to "Willkommen in unserer App!",
            "login_button" to "Anmelden",
            "signup_button" to "Registrieren",
            "settings_title" to "Einstellungen",
            "profile_title" to "Profil",
            "logout_button" to "Abmelden",
            "greeting" to "Hallo",
            "goodbye" to "Auf Wiedersehen"
        ))
    }

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