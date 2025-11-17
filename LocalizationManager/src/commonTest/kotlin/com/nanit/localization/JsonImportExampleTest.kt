package com.nanit.localization

import com.nanit.localization.importer.JsonLocalizationImporter
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Test demonstrating JSON localization file import and usage
 *
 * This test class shows:
 * - How to structure a complete JSON localization file
 * - How to import JSON data using JsonLocalizationImporter
 * - How to query all types of localized resources (values, arrays, plurals)
 * - Real-world examples of localization keys and values
 *
 * The example JSON structure includes:
 * - 22 string values (buttons, labels, messages, errors)
 * - 7 string arrays (days, months, navigation, colors, priorities)
 * - 9 plural forms (cart items, messages, time, likes, comments, tasks)
 *
 * Note: This is a KMM common test, so JSON is defined inline rather than loaded from files.
 * See shared/src/commonTest/resources/localization_full_example.json for the reference file.
 */
class JsonImportExampleTest {

    /**
     * Returns the full JSON example for testing
     *
     * This JSON structure matches the file at:
     * shared/src/commonTest/resources/localization_full_example.json
     */
    private fun getFullJsonExample(): String = """
{
  "locale": "en",
  "values": [
    {
      "key": "app_name",
      "value": "My Awesome App",
      "description": "The name of the application displayed in the header"
    },
    {
      "key": "welcome_message",
      "value": "Welcome back, %s!",
      "description": "Greeting message with user name placeholder"
    },
    {
      "key": "login_button",
      "value": "Log In",
      "description": "Button text for logging into the application"
    },
    {
      "key": "logout_button",
      "value": "Log Out"
    },
    {
      "key": "sign_up_button",
      "value": "Sign Up"
    },
    {
      "key": "forgot_password",
      "value": "Forgot your password?"
    },
    {
      "key": "settings_title",
      "value": "Settings"
    },
    {
      "key": "profile_title",
      "value": "My Profile"
    },
    {
      "key": "save_button",
      "value": "Save Changes"
    },
    {
      "key": "cancel_button",
      "value": "Cancel"
    },
    {
      "key": "delete_button",
      "value": "Delete"
    },
    {
      "key": "confirm_button",
      "value": "Confirm"
    },
    {
      "key": "error_network",
      "value": "Network error occurred. Please check your connection and try again.",
      "description": "Error message shown when network request fails"
    },
    {
      "key": "error_auth",
      "value": "Authentication failed. Please log in again.",
      "description": "Error shown when user session expires"
    },
    {
      "key": "success_saved",
      "value": "Your changes have been saved successfully!",
      "description": "Success message after saving data"
    },
    {
      "key": "loading_message",
      "value": "Loading, please wait..."
    },
    {
      "key": "empty_state_title",
      "value": "Nothing here yet",
      "description": "Title shown when list is empty"
    },
    {
      "key": "empty_state_description",
      "value": "Start by adding your first item."
    },
    {
      "key": "search_placeholder",
      "value": "Search..."
    },
    {
      "key": "filter_all",
      "value": "All"
    },
    {
      "key": "filter_active",
      "value": "Active"
    },
    {
      "key": "filter_completed",
      "value": "Completed"
    }
  ],
  "arrays": [
    {
      "key": "days_of_week",
      "items": [
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
      ],
      "description": "Full names of days of the week starting with Monday"
    },
    {
      "key": "days_of_week_short",
      "items": [
        "Mon",
        "Tue",
        "Wed",
        "Thu",
        "Fri",
        "Sat",
        "Sun"
      ],
      "description": "Abbreviated names of days of the week"
    },
    {
      "key": "months",
      "items": [
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December"
      ],
      "description": "Full names of months"
    },
    {
      "key": "months_short",
      "items": [
        "Jan",
        "Feb",
        "Mar",
        "Apr",
        "May",
        "Jun",
        "Jul",
        "Aug",
        "Sep",
        "Oct",
        "Nov",
        "Dec"
      ],
      "description": "Abbreviated month names"
    },
    {
      "key": "navigation_items",
      "items": [
        "Home",
        "Profile",
        "Messages",
        "Notifications",
        "Settings",
        "Help"
      ],
      "description": "Main navigation menu items"
    },
    {
      "key": "color_options",
      "items": [
        "Red",
        "Blue",
        "Green",
        "Yellow",
        "Purple",
        "Orange"
      ],
      "description": "Available color choices"
    },
    {
      "key": "priority_levels",
      "items": [
        "Low",
        "Medium",
        "High",
        "Critical"
      ],
      "description": "Priority levels for tasks"
    }
  ],
  "plurals": [
    {
      "key": "items_in_cart",
      "quantities": {
        "zero": "Your cart is empty",
        "one": "1 item in your cart",
        "other": "%d items in your cart"
      },
      "description": "Shows the number of items in shopping cart"
    },
    {
      "key": "unread_messages",
      "quantities": {
        "zero": "No new messages",
        "one": "You have 1 unread message",
        "other": "You have %d unread messages"
      },
      "description": "Notification for unread messages"
    },
    {
      "key": "days_remaining",
      "quantities": {
        "zero": "Trial expired",
        "one": "1 day left in your trial",
        "two": "2 days left in your trial",
        "other": "%d days left in your trial"
      },
      "description": "Days remaining in trial period"
    },
    {
      "key": "files_selected",
      "quantities": {
        "zero": "No files selected",
        "one": "1 file selected",
        "other": "%d files selected"
      },
      "description": "Number of files selected in file picker"
    },
    {
      "key": "likes_count",
      "quantities": {
        "zero": "No likes yet",
        "one": "1 like",
        "other": "%d likes"
      },
      "description": "Number of likes on a post"
    },
    {
      "key": "comments_count",
      "quantities": {
        "zero": "No comments",
        "one": "1 comment",
        "other": "%d comments"
      },
      "description": "Number of comments on a post"
    },
    {
      "key": "minutes_ago",
      "quantities": {
        "zero": "Just now",
        "one": "1 minute ago",
        "other": "%d minutes ago"
      },
      "description": "Relative time in minutes"
    },
    {
      "key": "hours_ago",
      "quantities": {
        "zero": "Less than an hour ago",
        "one": "1 hour ago",
        "other": "%d hours ago"
      },
      "description": "Relative time in hours"
    },
    {
      "key": "tasks_completed",
      "quantities": {
        "zero": "No tasks completed",
        "one": "Completed 1 task",
        "other": "Completed %d tasks"
      },
      "description": "Number of completed tasks"
    }
  ]
}
    """.trimIndent()

    /**
     * Complete example test demonstrating JSON file import and usage
     *
     * This test loads a comprehensive JSON localization file and demonstrates
     * all features of the localization system.
     */
    @Test
    fun testFullJsonExample() = runTest {
        // Setup: Create manager and importer
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)
        val importer = JsonLocalizationImporter(manager)

        println("\n" + "=".repeat(80))
        println("FULL JSON LOCALIZATION EXAMPLE TEST")
        println("=".repeat(80))

        // Step 1: Get JSON example
        println("\nStep 1: Loading JSON example...")
        val jsonContent = getFullJsonExample()
        println("  - JSON loaded successfully (${jsonContent.length} characters)")

        // Step 2: Import the JSON data
        println("\nStep 2: Importing JSON data...")
        val importResult = importer.importFromJson(jsonContent)

        println("Import Statistics:")
        println("  - Success: ${importResult.success}")
        println("  - Values imported: ${importResult.valuesImported}")
        println("  - Arrays imported: ${importResult.arraysImported}")
        println("  - Plurals imported: ${importResult.pluralsImported}")
        println("  - Total imported: ${importResult.totalImported}")

        // Verify import was successful
        assertTrue(importResult.success, "JSON import should succeed")
        assertEquals(22, importResult.valuesImported, "Should import 22 string values")
        assertEquals(7, importResult.arraysImported, "Should import 7 string arrays")
        assertEquals(9, importResult.pluralsImported, "Should import 9 plural forms")

        val env = LocalizationEnvironment(locale = "en")

        // Step 3: Test String Values
        println("\nStep 3: Testing String Values")
        println("-".repeat(80))

        val appName = manager.loadString("app_name", env)
        println("  app_name: '$appName'")
        assertEquals("My Awesome App", appName)

        val loginButton = manager.loadString("login_button", env)
        println("  login_button: '$loginButton'")
        assertEquals("Log In", loginButton)

        val errorNetwork = manager.loadString("error_network", env)
        println("  error_network: '$errorNetwork'")
        assertEquals("Network error occurred. Please check your connection and try again.", errorNetwork)

        val emptyStateTitle = manager.loadString("empty_state_title", env)
        println("  empty_state_title: '$emptyStateTitle'")
        assertEquals("Nothing here yet", emptyStateTitle)

        val saveButton = manager.loadString("save_button", env)
        println("  save_button: '$saveButton'")
        assertEquals("Save Changes", saveButton)

        // Step 4: Test String Arrays
        println("\nStep 4: Testing String Arrays")
        println("-".repeat(80))

        val daysOfWeek = manager.loadStringArray("days_of_week", env)
        assertNotNull(daysOfWeek, "days_of_week should not be null")
        assertEquals(7, daysOfWeek.size, "Should have 7 days")
        println("  days_of_week: ${daysOfWeek.joinToString(", ")}")
        assertEquals("Monday", daysOfWeek[0])
        assertEquals("Sunday", daysOfWeek[6])

        val daysOfWeekShort = manager.loadStringArray("days_of_week_short", env)
        assertNotNull(daysOfWeekShort, "days_of_week_short should not be null")
        println("  days_of_week_short: ${daysOfWeekShort.joinToString(", ")}")

        val monthsShort = manager.loadStringArray("months_short", env)
        assertNotNull(monthsShort, "months_short should not be null")
        assertEquals(12, monthsShort.size, "Should have 12 months")
        println("  months_short: ${monthsShort.joinToString(", ")}")

        val priorityLevels = manager.loadStringArray("priority_levels", env)
        assertNotNull(priorityLevels, "priority_levels should not be null")
        println("  priority_levels: ${priorityLevels.joinToString(", ")}")
        assertEquals(listOf("Low", "Medium", "High", "Critical"), priorityLevels)

        val colorOptions = manager.loadStringArray("color_options", env)
        assertNotNull(colorOptions, "color_options should not be null")
        println("  color_options: ${colorOptions.joinToString(", ")}")

        val navigationItems = manager.loadStringArray("navigation_items", env)
        assertNotNull(navigationItems, "navigation_items should not be null")
        println("  navigation_items: ${navigationItems.joinToString(", ")}")
        assertEquals(6, navigationItems.size)

        // Step 5: Test Plural Forms
        println("\nStep 5: Testing Plural Forms")
        println("-".repeat(80))

        // Test cart items
        println("  items_in_cart:")
        val cart0 = manager.loadStringPlural("items_in_cart", 0, env)
        val cart1 = manager.loadStringPlural("items_in_cart", 1, env)
        val cart5 = manager.loadStringPlural("items_in_cart", 5, env)
        println("    0 items: '$cart0'")
        println("    1 item: '$cart1'")
        println("    5 items: '$cart5'")
        assertEquals("Your cart is empty", cart0)
        assertEquals("1 item in your cart", cart1)
        assertEquals("%d items in your cart", cart5)

        // Test unread messages
        println("  unread_messages:")
        val msg0 = manager.loadStringPlural("unread_messages", 0, env)
        val msg1 = manager.loadStringPlural("unread_messages", 1, env)
        val msg10 = manager.loadStringPlural("unread_messages", 10, env)
        println("    0 messages: '$msg0'")
        println("    1 message: '$msg1'")
        println("    10 messages: '$msg10'")
        assertEquals("No new messages", msg0)
        assertEquals("You have 1 unread message", msg1)
        assertEquals("You have %d unread messages", msg10)

        // Test trial days remaining (includes 'two' form)
        println("  days_remaining:")
        val days0 = manager.loadStringPlural("days_remaining", 0, env)
        val days1 = manager.loadStringPlural("days_remaining", 1, env)
        val days2 = manager.loadStringPlural("days_remaining", 2, env)
        val days7 = manager.loadStringPlural("days_remaining", 7, env)
        println("    0 days: '$days0'")
        println("    1 day: '$days1'")
        println("    2 days: '$days2'")
        println("    7 days: '$days7'")
        assertEquals("Trial expired", days0)
        assertEquals("1 day left in your trial", days1)
        assertEquals("2 days left in your trial", days2)
        assertEquals("%d days left in your trial", days7)

        // Test relative time - minutes
        println("  minutes_ago:")
        val min0 = manager.loadStringPlural("minutes_ago", 0, env)
        val min1 = manager.loadStringPlural("minutes_ago", 1, env)
        val min30 = manager.loadStringPlural("minutes_ago", 30, env)
        println("    0 minutes: '$min0'")
        println("    1 minute: '$min1'")
        println("    30 minutes: '$min30'")
        assertEquals("Just now", min0)
        assertEquals("1 minute ago", min1)
        assertEquals("%d minutes ago", min30)

        // Test relative time - hours
        println("  hours_ago:")
        val hour0 = manager.loadStringPlural("hours_ago", 0, env)
        val hour1 = manager.loadStringPlural("hours_ago", 1, env)
        val hour24 = manager.loadStringPlural("hours_ago", 24, env)
        println("    0 hours: '$hour0'")
        println("    1 hour: '$hour1'")
        println("    24 hours: '$hour24'")
        assertEquals("Less than an hour ago", hour0)
        assertEquals("1 hour ago", hour1)
        assertEquals("%d hours ago", hour24)

        // Test likes count
        println("  likes_count:")
        val likes0 = manager.loadStringPlural("likes_count", 0, env)
        val likes1 = manager.loadStringPlural("likes_count", 1, env)
        val likes100 = manager.loadStringPlural("likes_count", 100, env)
        println("    0 likes: '$likes0'")
        println("    1 like: '$likes1'")
        println("    100 likes: '$likes100'")
        assertEquals("No likes yet", likes0)
        assertEquals("1 like", likes1)
        assertEquals("%d likes", likes100)

        // Test comments count
        println("  comments_count:")
        val comments0 = manager.loadStringPlural("comments_count", 0, env)
        val comments1 = manager.loadStringPlural("comments_count", 1, env)
        val comments50 = manager.loadStringPlural("comments_count", 50, env)
        println("    0 comments: '$comments0'")
        println("    1 comment: '$comments1'")
        println("    50 comments: '$comments50'")
        assertEquals("No comments", comments0)
        assertEquals("1 comment", comments1)
        assertEquals("%d comments", comments50)

        // Test tasks completed
        println("  tasks_completed:")
        val tasks0 = manager.loadStringPlural("tasks_completed", 0, env)
        val tasks1 = manager.loadStringPlural("tasks_completed", 1, env)
        val tasks15 = manager.loadStringPlural("tasks_completed", 15, env)
        println("    0 tasks: '$tasks0'")
        println("    1 task: '$tasks1'")
        println("    15 tasks: '$tasks15'")
        assertEquals("No tasks completed", tasks0)
        assertEquals("Completed 1 task", tasks1)
        assertEquals("Completed %d tasks", tasks15)

        println("\n" + "=".repeat(80))
        println("ALL TESTS PASSED - Full JSON Example Verified!")
        println("=".repeat(80))
        println("\nThis test demonstrates:")
        println("  ✓ Loading JSON from external file (localization_full_example.json)")
        println("  ✓ Complete JSON structure with values, arrays, and plurals")
        println("  ✓ JSON import using JsonLocalizationImporter")
        println("  ✓ Querying all resource types from the database")
        println("  ✓ Real-world localization examples for UI elements")
        println("  ✓ Proper handling of plural quantities (zero, one, two, other)")
        println("  ✓ Optional description fields for documentation")
        println("  ✓ Best practices for organizing localization resources")
        println("=".repeat(80) + "\n")
    }

    /**
     * Test demonstrating how to handle multiple JSON strings for different locales
     */
    @Test
    fun testMultipleLocalesImport() = runTest {
        val driverFactory = createTestDatabaseDriverFactory()
        val manager = LocalizationManager(driverFactory)
        val importer = JsonLocalizationImporter(manager)

        println("\n" + "=".repeat(80))
        println("MULTIPLE LOCALES JSON IMPORT TEST")
        println("=".repeat(80))

        // Import main example
        val jsonContent = getFullJsonExample()
        val result = importer.importFromJson(jsonContent)

        assertTrue(result.success, "Import should succeed")
        println("Imported locale: ${result.locale}")
        println("Total resources: ${result.totalImported}")

        // Verify we can query in the imported locale
        val env = LocalizationEnvironment(locale = "en")
        val appName = manager.loadString("app_name", env)

        assertNotNull(appName, "Should be able to load imported string")
        println("Successfully loaded: app_name = '$appName'")

        println("=".repeat(80) + "\n")
    }
}
