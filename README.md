# REST-Voda
## Tasks
### REST API
* 23 Spotify tests.
* Usage of DataProvider for different inputs.
* GET, POST, PUT, DELETE.

### SOAP API
* 12 calculator tests.
* Usage of DataProvider for different inputs.
* POST.

### Language
* Java

### Repository
* https://github.com/Darkhert/REST-Voda

### Framework
* REST Assured

### Compile to jar
* Use `mvn package` for compilation.

### Run tests against jar
* `java -jar Test-1.0-SNAPSHOT-REST-Voda.jar`

### Run set of tests
* `java -jar Test-1.0-SNAPSHOT-REST-Voda.jar parameter`
* `parameter = {auth; calculator; content; manipulation; user}`

### Reporting
* In console. Example:
```
[TestNG] Running:
  C:\Users\mober\IdeaProjects\REST-Voda\target\testng.xml

[TestRunner] Starting executor for test Spotify with time out:2147483647 milliseconds.
PASSED: noToken
PASSED: expiredToken
PASSED: invalidToken
PASSED: correctToken
PASSED: unfollow("Ylvis", "2lEOFtf3cCyzomQcMHJGfZ")
PASSED: follow("Ylvis", "2lEOFtf3cCyzomQcMHJGfZ")
PASSED: topArtist
PASSED: unfollow("Asonance", "7bAfTFyv7hCYceSg6UqeXP")
PASSED: createPlaylist
PASSED: follow("Asonance", "7bAfTFyv7hCYceSg6UqeXP")
PASSED: topTrack
PASSED: unfollow("Queen", "1dfeR4HaWDbWqFHLkxsg1d")
PASSED: artistByID("Ylvis", "2lEOFtf3cCyzomQcMHJGfZ")
PASSED: follow("Queen", "1dfeR4HaWDbWqFHLkxsg1d")
PASSED: searchArtistId("Ylvis", "2lEOFtf3cCyzomQcMHJGfZ")
PASSED: artistByID("Asonance", "7bAfTFyv7hCYceSg6UqeXP")
PASSED: updatePlaylist
PASSED: searchArtistId("Asonance", "7bAfTFyv7hCYceSg6UqeXP")
PASSED: addToPlaylist
PASSED: artistByID("Queen", "1dfeR4HaWDbWqFHLkxsg1d")
PASSED: searchArtistId("Queen", "1dfeR4HaWDbWqFHLkxsg1d")
PASSED: removeFromPlaylist
PASSED: deletePlaylist

===============================================
    Spotify
    Tests run: 23, Failures: 0, Skips: 0
===============================================


===============================================
Spotify
Total tests run: 23, Failures: 0, Skips: 0
===============================================

[TestNG] Time taken by org.testng.reporters.jq.Main@2bfeb1ef: 32 ms
[TestNG] Time taken by org.testng.reporters.XMLReporter@435ce306: 9 ms
[TestNG] Time taken by org.testng.reporters.EmailableReporter2@1059754c: 5 ms
[TestNG] Time taken by [FailedReporter passed=0 failed=0 skipped=0]: 0 ms
[TestNG] Time taken by org.testng.reporters.JUnitReportReporter@64040287: 12 ms
[TestNG] Time taken by org.testng.reporters.SuiteHTMLReporter@4925f4f5: 19 ms
```

## Spotify credentials
* The Spotify tests are implemented against real test account (don't worry, it is not my personal account).
* You can see changes there in the real time. Feel free to log in.
* Email: _Sent in the LinkedIn message._
* Password: _Sent in the LinkedIn message._
