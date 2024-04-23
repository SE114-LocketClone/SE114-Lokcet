import kotlin.random.Random

object FriendMock {

    data class GeoPoint(val latitude: Double, val longitude: Double)

    data class User(
        val id: String = "",
        val firstName: String = "",
        val lastName: String = "",
        val location: GeoPoint = GeoPoint(0.0, 0.0),
        val email: String = "",
        val password: String = "",
        val phoneNumber: String = "",
        val profilePicture: String = "",
        val friends: List<String> = emptyList()
    )

    val mockUsers = listOf(
        User(
            id = "1",
            firstName = "Alice",
            lastName = "Smith",
            location = GeoPoint(40.7128, -74.0060), // New York City coordinates
            email = "alice@gmail.com",
            password = "password123",
            phoneNumber = "123-456-7890",
            profilePicture = "path/to/alice.jpg",
            friends = listOf("2", "3")
        ),
        User(
            id = "2",
            firstName = "Bob",
            lastName = "Johnson",
            location = GeoPoint(34.0522, -118.2437), // Los Angeles coordinates
            email = "bob@yahoo.com",
            password = "password456",
            phoneNumber = "456-789-0123",
            profilePicture = "path/to/bob.jpg",
            friends = listOf("1", "3")
        ),
        User(
            id = "3",
            firstName = "Charlie",
            lastName = "Williams",
            location = GeoPoint(51.5074, -0.1278), // London coordinates
            email = "charlie@hotmail.com",
            password = "password789",
            phoneNumber = "789-012-3456",
            profilePicture = "path/to/charlie.jpg",
            friends = listOf("1", "2")
        )
    )
}