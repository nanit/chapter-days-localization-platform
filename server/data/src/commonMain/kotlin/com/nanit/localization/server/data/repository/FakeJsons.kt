package com.nanit.localization.server.data.repository

internal val enJsonMock: String = """
    {
      "locale": "en",
      "values": [
        {
          "key": "app_name",
          "value": "Localization Platform",
          "description": "Application name"
        },
        {
          "key": "welcome_message",
          "value": "Welcome to our application!",
          "description": "Main welcome message"
        },
        {
          "key": "login_button",
          "value": "Log In"
        },
        {
          "key": "logout_button",
          "value": "Log Out"
        },
        {
          "key": "settings_title",
          "value": "Settings"
        },
        {
          "key": "save_button",
          "value": "Save"
        },
        {
          "key": "cancel_button",
          "value": "Cancel"
        },
        {
          "key": "error_network",
          "value": "Network error occurred. Please try again.",
          "description": "Network error message"
        }
      ],
      "arrays": [
        {
          "key": "days_of_week",
          "items": ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"],
          "description": "Days of the week"
        },
        {
          "key": "months",
          "items": ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"],
          "description": "Months of the year"
        },
        {
          "key": "navigation_items",
          "items": ["Home", "Profile", "Settings", "Help"],
          "description": "Main navigation menu items"
        }
      ],
      "plurals": [
        {
          "key": "items_in_cart",
          "quantities": {
            "zero": "No items in cart",
            "one": "1 item in cart",
            "other": "%d items in cart"
          },
          "description": "Shopping cart item count"
        },
        {
          "key": "unread_messages",
          "quantities": {
            "zero": "No unread messages",
            "one": "1 unread message",
            "other": "%d unread messages"
          },
          "description": "Unread message count"
        },
        {
          "key": "days_remaining",
          "quantities": {
            "zero": "No days remaining",
            "one": "1 day remaining",
            "two": "2 days remaining",
            "other": "%d days remaining"
          },
          "description": "Days remaining in trial"
        },
        {
          "key": "files_selected",
          "quantities": {
            "zero": "No files selected",
            "one": "1 file selected",
            "other": "%d files selected"
          }
        }
      ]
    }
""".trimIndent()

internal val esJsonMock: String = """
    {
      "locale": "es",
      "values": [
        {
          "key": "app_name",
          "value": "Plataforma de Localización",
          "description": "Nombre de la aplicación"
        },
        {
          "key": "welcome_message",
          "value": "¡Bienvenido a nuestra aplicación!",
          "description": "Mensaje de bienvenida principal"
        },
        {
          "key": "login_button",
          "value": "Iniciar Sesión"
        },
        {
          "key": "logout_button",
          "value": "Cerrar Sesión"
        },
        {
          "key": "settings_title",
          "value": "Configuración"
        },
        {
          "key": "save_button",
          "value": "Guardar"
        },
        {
          "key": "cancel_button",
          "value": "Cancelar"
        },
        {
          "key": "error_network",
          "value": "Error de red. Por favor, inténtelo de nuevo.",
          "description": "Mensaje de error de red"
        }
      ],
      "arrays": [
        {
          "key": "days_of_week",
          "items": ["Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"],
          "description": "Días de la semana"
        },
        {
          "key": "months",
          "items": ["Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"],
          "description": "Meses del año"
        },
        {
          "key": "navigation_items",
          "items": ["Inicio", "Perfil", "Configuración", "Ayuda"],
          "description": "Elementos del menú de navegación principal"
        }
      ],
      "plurals": [
        {
          "key": "items_in_cart",
          "quantities": {
            "zero": "No hay artículos en el carrito",
            "one": "1 artículo en el carrito",
            "other": "%d artículos en el carrito"
          },
          "description": "Número de artículos en el carrito"
        },
        {
          "key": "unread_messages",
          "quantities": {
            "zero": "No hay mensajes sin leer",
            "one": "1 mensaje sin leer",
            "other": "%d mensajes sin leer"
          },
          "description": "Número de mensajes sin leer"
        },
        {
          "key": "days_remaining",
          "quantities": {
            "zero": "No quedan días",
            "one": "Queda 1 día",
            "two": "Quedan 2 días",
            "other": "Quedan %d días"
          },
          "description": "Días restantes en la prueba"
        },
        {
          "key": "files_selected",
          "quantities": {
            "zero": "No hay archivos seleccionados",
            "one": "1 archivo seleccionado",
            "other": "%d archivos seleccionados"
          }
        }
      ]
    }
""".trimIndent()

internal val frJsonMock: String = """
    {
      "locale": "fr",
      "values": [
        {
          "key": "app_name",
          "value": "Plateforme de Localisation",
          "description": "Nom de l'application"
        },
        {
          "key": "welcome_message",
          "value": "Bienvenue dans notre application!",
          "description": "Message de bienvenue principal"
        },
        {
          "key": "login_button",
          "value": "Se Connecter"
        },
        {
          "key": "logout_button",
          "value": "Se Déconnecter"
        },
        {
          "key": "settings_title",
          "value": "Paramètres"
        },
        {
          "key": "save_button",
          "value": "Enregistrer"
        },
        {
          "key": "cancel_button",
          "value": "Annuler"
        },
        {
          "key": "error_network",
          "value": "Erreur réseau. Veuillez réessayer.",
          "description": "Message d'erreur réseau"
        }
      ],
      "arrays": [
        {
          "key": "days_of_week",
          "items": ["Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"],
          "description": "Jours de la semaine"
        },
        {
          "key": "months",
          "items": ["Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"],
          "description": "Mois de l'année"
        },
        {
          "key": "navigation_items",
          "items": ["Accueil", "Profil", "Paramètres", "Aide"],
          "description": "Éléments du menu de navigation principal"
        }
      ],
      "plurals": [
        {
          "key": "items_in_cart",
          "quantities": {
            "zero": "Aucun article dans le panier",
            "one": "1 article dans le panier",
            "other": "%d articles dans le panier"
          },
          "description": "Nombre d'articles dans le panier"
        },
        {
          "key": "unread_messages",
          "quantities": {
            "zero": "Aucun message non lu",
            "one": "1 message non lu",
            "other": "%d messages non lus"
          },
          "description": "Nombre de messages non lus"
        },
        {
          "key": "days_remaining",
          "quantities": {
            "zero": "Aucun jour restant",
            "one": "1 jour restant",
            "two": "2 jours restants",
            "other": "%d jours restants"
          },
          "description": "Jours restants dans l'essai"
        },
        {
          "key": "files_selected",
          "quantities": {
            "zero": "Aucun fichier sélectionné",
            "one": "1 fichier sélectionné",
            "other": "%d fichiers sélectionnés"
          }
        }
      ]
    }
""".trimIndent()
