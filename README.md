# RedditSearchImages
An application that allows users to search /r/pics. Returns the links for the top 3 most popular posts, ranked by number of comments.
A URL request is sent to the reddit API, using the search parameters that the user entered. The returned JSON response is parsed into
JSON objects and an array to retrieve the links for the top 3 viral images. These links are displayed to the user via a GUI.
